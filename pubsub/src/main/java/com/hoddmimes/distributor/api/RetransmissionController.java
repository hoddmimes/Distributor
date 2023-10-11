package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


class RetransmissionController {
	private static final Logger cLogger = LogManager.getLogger( RetransmissionController.class.getSimpleName());
	DistributorConnection mConnection;
	NaggingMonitorTask mNaggingMonitor;
	List<RetransmissionRequestItem> mRetransmissionRequestQueue = Collections
			.synchronizedList(new LinkedList<RetransmissionRequestItem>());

	RetransmissionController(DistributorConnection pConnection) {
		mConnection = pConnection;
		mNaggingMonitor = new NaggingMonitorTask(pConnection.mConnectionId, mConnection.mConfiguration );
		if (pConnection.mConfiguration.getNaggingWindowInterval() > 0) {
			long tInterval = pConnection.mConfiguration.getNaggingWindowInterval();
			DistributorTimers.getInstance().queue(tInterval, tInterval, mNaggingMonitor );
		}
	}

	void close() {
		if (mRetransmissionRequestQueue.isEmpty()) {
			return;
		}

		synchronized (mRetransmissionRequestQueue) {
			Iterator<RetransmissionRequestItem> tItr = mRetransmissionRequestQueue
					.iterator();
			while (tItr.hasNext()) {
				tItr.next().cancel();
			}
			mRetransmissionRequestQueue.clear();
		}
		mNaggingMonitor.cancel();
	}

	void createRetransmissionRequest(RemoteConnection pRemoteConnection, int pLowSeqNo, int pHighSeqNo) {
		RetransmissionRequestItem tRqstTask = new RetransmissionRequestItem( mConnection.mConnectionId, pRemoteConnection.mRemoteConnectionId, pLowSeqNo, pHighSeqNo);
		mRetransmissionRequestQueue.add(tRqstTask);
		DistributorTimers.getInstance().queue(0, mConnection.mConfiguration.getRetransmissionTimeout(), tRqstTask);
	}

	void updateRetransmissions(RcvSegment pSegment) {

		if (mRetransmissionRequestQueue.isEmpty()) {
			return;
		}

		synchronized (mRetransmissionRequestQueue) {
			Iterator<RetransmissionRequestItem> tItr = mRetransmissionRequestQueue
					.iterator();
			while (tItr.hasNext()) {
				tItr.next().adjustSeqNo(pSegment.getSeqno());
			}
		}
	}

	void processRetransmissionNAK( RcvSegment pSegment) {
		if (mRetransmissionRequestQueue.isEmpty()) {
			return;
		}

		NetMsgRetransmissionNAK tMsg = new NetMsgRetransmissionNAK(pSegment);
		tMsg.decode();
		
		List<Integer> tNakSeqNo = tMsg.getNakSeqNo();
		RetransmissionRequestItem tRqst = null;

		synchronized (mRetransmissionRequestQueue) {
			for (int i = 0; i < tNakSeqNo.size(); i++) {
				Iterator<RetransmissionRequestItem> tItr = mRetransmissionRequestQueue
						.iterator();
				while (tItr.hasNext()) {
					tRqst = tItr.next();
					if ((tNakSeqNo.get(i) >= tRqst.mLowSeqNo) && (tNakSeqNo.get(i) <= tRqst.mHighSeqNo)) {
						RemoteConnection tRemoteConnection = mConnection.mConnectionReceiver.mRemoteConnectionController.getConnection(pSegment);
						tRqst.requestNakSmoked(tRemoteConnection, tNakSeqNo.get(i));
						tItr.remove();
					}
				}
			}
		} // End synchronized
	}

	/*

	 */
	class RetransmissionRequestItem extends DistributorTimerTask {
		long mRemoteConnectionId;
		int mLowSeqNo;
		int mHighSeqNo;
		int mRetries;
		int[] mServedList;
		int mServedListIndex;

		RetransmissionRequestItem(long pDistributorConnectionId, long pRemoteConnectionId,  int pLowSeqNo, int pHighSeqNo) {
			super( pDistributorConnectionId );
			mRemoteConnectionId = pRemoteConnectionId;
			mLowSeqNo = pLowSeqNo;
			mHighSeqNo = pHighSeqNo;
			mRetries = 0;
			mServedList = new int[pHighSeqNo - pLowSeqNo + 1];
			mServedListIndex = 0;
		}

		void queueRetransmissionRqstMessage(DistributorConnection pConnection, RemoteConnection pRemoteConnection) {
			
			
			NetMsgRetransmissionRqst tRqstMsg;
			tRqstMsg = new NetMsgRetransmissionRqst(new XtaSegment(mConnection.mConfiguration.getSmallSegmentSize()));
			tRqstMsg.setHeader(
							Segment.MSG_TYPE_RETRANSMISSION_RQST,
							(byte) (Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END),
							pConnection.mConnectionSender.mLocalAddress,
							pConnection.mConnectionSender.mSenderId,
					        (int) (pConnection.mConnectionSender.mConnectionStartTime & 0xffffffff),
							pConnection.mDistributor.getAppId());

			tRqstMsg.set(   pConnection.mConnectionSender.mLocalAddress,
							mLowSeqNo,
							mHighSeqNo,
							pConnection.mConnectionSender.mLocalAddress.getHostName(),
							pConnection.mApplicationConfiguration.getApplicationName(),
							pRemoteConnection.mRemoteSenderId,
							(int) (pRemoteConnection.mRemoteStartTime&0xffffffff));

			pConnection.mRetransmissionStatistics.updateOutStatistics(
					pConnection.mIpmg.mInetAddress, mConnection.mIpmg.mPort,
					pRemoteConnection.mRemoteHostInetAddress);
			
			tRqstMsg.encode();
			pConnection.mConnectionSender.sendSegment((XtaSegment)tRqstMsg.mSegment);
		}

		void requestNakSmoked(RemoteConnection tRemoteConnection, int pNakSeqNo) {
			DistributorRetransmissionNAKErrorEvent tEvent;
			if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
				mConnection.log("RETRANSMISSION: RCV NAK (NakSeqNo: "
						+ pNakSeqNo + ")  Remote Addr: "
						+ tRemoteConnection.mRemoteHostAddressString
						+ " Remote Sender Id: "
						+ tRemoteConnection.mRemoteSenderId + " LowSeqNo: "
						+ mLowSeqNo + " HighSeqNo: "
						+ tRemoteConnection.mHighiestSeenSeqNo);
			}

			tEvent = new DistributorRetransmissionNAKErrorEvent( mConnection.mIpmg.mInetAddress,
																 mConnection.mIpmg.mPort );
					
			ClientDeliveryController.getInstance().queueEvent(mConnection.mConnectionId, tEvent);
		}

		void adjustSeqNo(int pSeqNo) {
			if ((pSeqNo > mHighSeqNo) || (pSeqNo < mLowSeqNo)) {
				return;
			}

			for (int i = 0; i < mServedListIndex; i++) {
				if (mServedList[i] == pSeqNo) {
					return;
				}
			}

			mServedList[mServedListIndex++] = pSeqNo;

			if (pSeqNo == mHighSeqNo) {
				mHighSeqNo--;
			}

			if (pSeqNo == mLowSeqNo) {
				mLowSeqNo++;
			}
		}

		@Override
		public void execute( DistributorConnection pConnection) {
			boolean tResend = false;
			boolean tFailed = false;

			RemoteConnection tRemoteConnection = pConnection.mConnectionReceiver.mRemoteConnectionController.getRemoteConnection(mRemoteConnectionId);
			
			try {
				if (pConnection.mTimeToDie) {
					cancel();
					return;
				}
				
				if (mServedListIndex == mServedList.length) {
				  mRetransmissionRequestQueue.remove(this);
				  cancel();
				  return; // All done
				} else if (mRetries > mConnection.mConfiguration.getRetransmissionRetries()) {
					tFailed = true;
				} else {
					tResend = true;
				}
			

				if (tResend) {
					if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
						mConnection.log("RETRANSMISSION: XTA REQUEST RETRANSMISSION Segments ["
										+ mLowSeqNo
										+ ":"
										+ mHighSeqNo
										+ "] Retry: ["
										+ mRetries
										+ "] Remote Addr: "
										+ tRemoteConnection.mRemoteHostAddressString
										+ " Remote Sender Id: "
										+ tRemoteConnection.mRemoteSenderId
										+ "  ("
										+ mServedListIndex
										+ ") served out of ("
										+ mServedList.length + ") requested");
					}
	
					queueRetransmissionRqstMessage( pConnection, tRemoteConnection);
					mRetries++;
				} else if (tFailed) {
					cancel();
					synchronized (mRetransmissionRequestQueue) {
						mRetransmissionRequestQueue.remove(this);
					}

					DistributorTooManyRetransmissionRetriesErrorEvent tEvent = new DistributorTooManyRetransmissionRetriesErrorEvent(
																								mConnection.mIpmg.mInetAddress,
																								mConnection.mIpmg.mPort );


					if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
						mConnection.log("RETRANSMISSION: RCV TO MANY RETRANSMISSION  Remote Addr: "
										+ tRemoteConnection.mRemoteHostAddressString
										+ " Remote Sender Id: "
										+ tRemoteConnection.mRemoteSenderId
										+ " LowSeqNo: "
										+ mLowSeqNo
										+ " HighSeqNo: " + mHighSeqNo);
					}

					ClientDeliveryController.getInstance().queueEvent(mConnection.mConnectionId, tEvent);
				}
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	class NaggingMonitorTask extends DistributorTimerTask {

		int mIntervalCount;
		int mLastIntervalCount;
		int mConsequtiveTicks;

		int mCfgWindowInterval;
		int mCfgCheckInterval;
		int mCfgMaxRetransmissions;

		NaggingMonitorTask(long DistributorConnectionId, DistributorConnectionConfiguration pConfiguration ) {
			super(DistributorConnectionId);
			mCfgWindowInterval = pConfiguration.getNaggingWindowInterval();
			mCfgCheckInterval = pConfiguration.getNaggingCheckInterval();
			mCfgMaxRetransmissions = pConfiguration.getNaggingMaxRetransmissions();
			mIntervalCount = 0;
			mLastIntervalCount = 0;
			mConsequtiveTicks = 0;
		}

		private void clear() {
			mIntervalCount = 0;
			mLastIntervalCount = 0;
			mConsequtiveTicks = 0;
		}

		@Override
		public void execute( DistributorConnection pConnection) {
			try {
				if (mIntervalCount > mLastIntervalCount) // an increase in number of retransmission compared with previous check
				{
					mConsequtiveTicks += mCfgWindowInterval;
					if (mConsequtiveTicks >= mCfgCheckInterval) {
						if ((mCfgMaxRetransmissions == 0) || 
							((mCfgMaxRetransmissions > 0) && 
							(mIntervalCount >= mCfgMaxRetransmissions))) {
							   DistributorNaggingErrorEvent tEvent = new DistributorNaggingErrorEvent( pConnection.mIpmg.mInetAddress, 
									   																   pConnection.mIpmg.mPort );
							   ClientDeliveryController.getInstance().queueEvent(pConnection.mConnectionId, tEvent);

						} else {
							clear();
						}
					}
				} else {
					clear();
				}
			} catch (Exception e) {
				mConnection.logThrowable(e);
			}
		}
	}

}
