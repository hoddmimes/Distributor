package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class RetransmissionCache {
	DistributorConnection mConnection;
	DistributorConnectionConfiguration mConfiguration;
	ConnectionSender mSender;
	LinkedList<RetransQueItm> mQueue;
	int mCacheSize;
	CleanRetransmissionQueueTask mCleanCacheTask;
	volatile boolean mIsDead;

	RetransmissionCache(ConnectionSender pSender) {
		mConnection = pSender.mConnection;
		mConfiguration = pSender.mConnection.mConfiguration;
		mSender = pSender;
		mIsDead = false;
		mQueue = new LinkedList<RetransQueItm>();
		mCacheSize = 0;
		mCleanCacheTask = new CleanRetransmissionQueueTask(mConnection.mConnectionId);

		long tInterval = mConfiguration.getRetransmissionCacheCleanInterval();
		DistributorTimers.getInstance().queue( tInterval, tInterval, mCleanCacheTask );

	}

	void close() {
		mCleanCacheTask.cancel();
		mQueue.clear();
	}

	void addSentUpdate(XtaSegment pSegment) {
		synchronized (mQueue) {
			mQueue.add(new RetransQueItm(pSegment, pSegment.getSeqno()));
			mCacheSize += pSegment.getLength();
			microClean();
		}
	}

	private void microClean() {
		ListIterator<RetransQueItm> mItr = mQueue.listIterator();

		while (mItr.hasNext()) {
			RetransQueItm tQueItm = mItr.next();

			if (mCacheSize <= mConfiguration.getRetransmissionMaxCacheSize()) {
				return;
			} else {
				if (!tQueItm.mInProgress) {
					mItr.remove();
					mCacheSize -= tQueItm.mSegment.getLength();
					tQueItm.mSegment = null;
					tQueItm = null;
				}
			}
		}
	}

	private ArrayList<Integer> getRetransmissionNAKSequenceNumbers( int pLowestRequestedSeqNo, int pLowestSeqNoInCache) {
		int tSize = 0;
		ArrayList<Integer> pNAKSeqNumbers = null;

		if (pLowestRequestedSeqNo < pLowestSeqNoInCache) {
			return null;
		}

		tSize = pLowestSeqNoInCache - pLowestRequestedSeqNo;
		pNAKSeqNumbers = new ArrayList<Integer>( tSize );

		for (int i = pLowestRequestedSeqNo; i < pLowestSeqNoInCache; i++) {
			pNAKSeqNumbers.add( i );
		}

		return pNAKSeqNumbers;
	}

	private void sendRetransmissionNAK(List<Integer> pNAKSeqNumbers) {
		NetMsgRetransmissionNAK tNAKMsg = new NetMsgRetransmissionNAK(
				new XtaSegment(mConfiguration.getSmallSegmentSize()));
	
		tNAKMsg.setHeader(Segment.MSG_TYPE_RETRANSMISSION_NAK,
				(byte) (Segment.FLAG_M_SEGMENT_END + Segment.FLAG_M_SEGMENT_START),
				mSender.mLocalAddress,
				mSender.mSenderId,
				(int) (mSender.mConnectionStartTime & 0xffffffff),
				mSender.mConnection.mDistributor.getAppId());

		tNAKMsg.set(mSender.mMca.mInetAddress, mSender.mMca.mPort, mSender.mSenderId);
		tNAKMsg.setNakSeqNo(pNAKSeqNumbers);

		tNAKMsg.encode();
		
		mSender.sendSegment((XtaSegment)tNAKMsg.mSegment);
		tNAKMsg = null;
	}

	void sendRetransmissions(List<RetransQueItm> mRetransList) {
		ListIterator<RetransQueItm> tItr;

		tItr = mRetransList.listIterator(mRetransList.size());
		while (tItr.hasPrevious()) {
			RetransQueItm tQueItm = tItr.previous();

			tQueItm.mResentCount++;
			if (mConnection
					.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
				mConnection.log("RETRANSMISSION: XTA RE-SENDING Segment ["
						+ tQueItm.mSeqNo + "] ResentCount: "
						+ tQueItm.mResentCount);
			}

			mSender.sendSegment(tQueItm.mSegment);
			tQueItm.mInProgress = false;
		}
	}

	void retransmit(int pLowSeqNo, int pHighSeqNo) {
		ListIterator<RetransQueItm> tItr;
		List<RetransQueItm> tRetransList = new LinkedList<RetransQueItm>();
		List<Integer> tNAKSequenceNumbers = null;

		synchronized (mQueue) {
			if (mConnection
					.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
				if (mQueue.size() == 0) {
					mConnection
							.log("RETRANSMISSION: RCV Request for resending Segment ["
									+ pLowSeqNo
									+ ":"
									+ pHighSeqNo
									+ "] Cache is EMPTY!");
				} else {
					mConnection
							.log("RETRANSMISSION: RCV Request for resending Segment ["
									+ pLowSeqNo
									+ ":"
									+ pHighSeqNo
									+ "] Cache First Segment: "
									+ mQueue.get(0).mSeqNo
									+ " Last Segment: "
									+ mQueue.get(mQueue.size() - 1).mSeqNo);
				}
			}

			if (mQueue.size() == 0) {
				tNAKSequenceNumbers = getRetransmissionNAKSequenceNumbers( pLowSeqNo, pHighSeqNo);
			} else {
				if (pLowSeqNo < mQueue.get(0).mSeqNo) {
					tNAKSequenceNumbers = getRetransmissionNAKSequenceNumbers( pLowSeqNo, mQueue.get(0).mSeqNo);
				}

				tItr = mQueue.listIterator(mQueue.size());
				while (tItr.hasPrevious()) {
					RetransQueItm tQueItm = tItr.previous();
					if (tQueItm.mSeqNo < pLowSeqNo) {
						break;
					}
					if ((tQueItm.mSeqNo >= pLowSeqNo)
							&& (tQueItm.mSeqNo <= pHighSeqNo)) {
						tQueItm.mInProgress = true;
						tRetransList.add(tQueItm);
					}
				}
			}
		} // synchronized( mQueue )

		if (tNAKSequenceNumbers != null) {
			sendRetransmissionNAK(tNAKSequenceNumbers);
		}

		if ((tRetransList.size() > 0) && (mConfiguration.getRetransmissionServerHoldback() > 0)) {
			QueueRetransmissionListTask tTask = new QueueRetransmissionListTask( mConnection.mConnectionId, tRetransList);
			DistributorTimers.getInstance().queue(mConfiguration.getRetransmissionServerHoldback(), tTask );
		} else if ((tRetransList.size() > 0) && (mConfiguration.getRetransmissionServerHoldback() <= 0)) {
			sendRetransmissions(tRetransList);
		}

	}

	class QueueRetransmissionListTask extends DistributorTimerTask {
		List<RetransQueItm> mRetransList = null;

		QueueRetransmissionListTask(long pDistributoConnectionId, List<RetransQueItm> pRetransList) {
			super(pDistributoConnectionId);
			mRetransList = pRetransList;
		}

		@Override
		public void execute( DistributorConnection pConnection) {
			if (pConnection.mConnectionSender.mRetransmissionCache.mIsDead) {
				return;
			}
			pConnection.mConnectionSender.mRetransmissionCache.sendRetransmissions(mRetransList);
			mRetransList = null;
		}
	}

	class CleanRetransmissionQueueTask extends DistributorTimerTask {
		long tCacheThresholdTime;
		ListIterator<RetransQueItm> mItr;


		CleanRetransmissionQueueTask(long pDistributorConnectionId) {
			super(pDistributorConnectionId);
		}

		@Override
		public void execute( DistributorConnection pConnection) {
			int tRemoveElements = 0;
			tCacheThresholdTime = DistributorTimers.getSecondTicks() - pConnection.mConfiguration.getRetransmissionCacheCleanInterval();
			
			RetransmissionCache mCache = pConnection.mConnectionSender.mRetransmissionCache;
			

			if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_CACHE))
			{
				if (mQueue.size() > 0) {
					RetransQueItm tFirstItem = mQueue.getFirst();
					RetransQueItm tLastItem = mQueue.getLast();
					
					long tTimeDiff = tLastItem.mQueueTime - tFirstItem.mQueueTime; // Diff in seconds
					
					
					mConnection.log("RETRANSMISSON CACHE STATISTICS Connection: " + mConnection.mIpmg.toString() + "\n" +
								"    size: " + mCacheSize + " elements: " + mQueue.size() + " time-span: " + tTimeDiff + " (sec)");
				} else {
					mConnection.log("RETRANSMISSON CACHE STATISTICS Connection: " + mConnection.mIpmg.toString() + "\n" +
							"    size: 0 elements: 0 time-span: 0 (sec)");
				}
			}
			

			mItr = mCache.mQueue.listIterator();
			while (mItr.hasNext()) {
				RetransQueItm tQueItm = mItr.next();
					// System.out.println("Queue Size: " + mCache.mQueue.size()
					// + " Que Time: " + tQueItm.mQueueTime + " > T0: " +
					// tCacheThresholdTime + "   diff = " + (tQueItm.mQueueTime
					// - tCacheThresholdTime));
				if ((mCache.mConfiguration.getRetransmissionCacheLifeTime() == 0)
							&& (mCache.mCacheSize <= mCache.mConfiguration.getRetransmissionMaxCacheSize())) {
						// mCache.mConnection.log("Removed " + tRemoveElements +
						// " from retransmission cache QueueLength: " +
						// mCache.mQueue.size() + " CacheSize: " +
						// mCache.mCacheSize.get());
					return;
				} else if ((tQueItm.mQueueTime > tCacheThresholdTime)
							&& (mCache.mConfiguration.getRetransmissionCacheLifeTime() != 0)
							&& (mCache.mCacheSize <= mCache.mConfiguration.getRetransmissionMaxCacheSize())) {
						// mCache.mConnection.log("Removed " + tRemoveElements +
						// " from retransmission cache QueueLength: " +
						// mCache.mQueue.size() + " CacheSize: " +
						// mCache.mCacheSize.get());
						return;
				} else {
					if (!tQueItm.mInProgress) {
						mItr.remove();
						tRemoveElements++;
						mCache.mCacheSize -= tQueItm.mSegment.getLength();
						tQueItm.mSegment = null;
						tQueItm = null;
					}
				}
			} // synchronized
		}
	}

	class RetransQueItm {
		int mSeqNo;
		XtaSegment mSegment;
		int mQueueTime;
		volatile boolean mInProgress;
		volatile int mResentCount;

		RetransQueItm(XtaSegment pSegment, int pSeqNo) {
			mQueueTime = DistributorTimers.getSecondTicks();
			mSegment = pSegment;
			mSeqNo = pSeqNo;
			mInProgress = false;
			mResentCount = 0;
			mSegment.setHeaderMessageType(Segment.MSG_TYPE_RETRANSMISSION);
		}
	}
}