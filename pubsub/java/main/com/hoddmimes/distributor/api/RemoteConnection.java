package com.hoddmimes.distributor.api;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.DistributorRemoveRemoteConnectionEvent;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;


class RemoteConnection {
	static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");

	DistributorConnection mConnection; // Parent connection
	RemoteConnectionController mRemoteConnectionController; // Remote connection
	// controller
	RetransmissionController mRetransmissionController;

	DistributorConnectionConfiguration mConfiguration; // Local configuration for
	Ipmg mMca; // MCA instance

	String mRemoteHostName;
	String mRemoteApplicationName;
	String mRemoteHostAddressString;
	InetAddress mRemoteHostInetAddress;
	int mRemoteStartTime;
	long mHeartbeatInterval;
	long mConfigurationInterval;
	String mConnectTime;
	int mRemoteSenderId;
	int mHashCode;
	long mRemoteConnectionId;

	InetSocketAddress mRetransmissionDestinationAddress;

	/**
	 * Local used attribute
	 */
	boolean mStartSynchronized;
	int mNextExpectedSeqno;
	int mHighiestSeenSeqNo;
	volatile boolean mHbIsActive;
	volatile boolean mCfgIsActive;
	volatile boolean isDead;
	RcvSegmentBatch mRcvSegmentBatch;
	

	List<RcvSegment> mPendingReceiverQueue = new LinkedList<RcvSegment>();

	CheckHeartbeatTask mCheckHeartbeatTask;
	CheckConfigurationTask mCheckConfigurationTask;

	RemoteConnection(Segment pSegment, RemoteConnectionController pController,
			DistributorConnection pConnection) {
		synchronized (this) {
			mRemoteConnectionId = UUIDFactory.getId();
			mConnection = pConnection;
			mHashCode = pSegment.hashCode();
			NetMsgConfiguration tMsg = new NetMsgConfiguration(pSegment);
			tMsg.decode();
			mConnectTime = cSDF.format(new Date());
			mRemoteHostAddressString = tMsg.getHostAddress().toString();
			mRemoteHostInetAddress = tMsg.getHostAddress();
			mRemoteSenderId = tMsg.getSenderId();
			mRemoteStartTime = tMsg.getHeaderSenderStartTime();
			mHeartbeatInterval = tMsg.getHeartbeatInterval();
			mConfigurationInterval = tMsg.getConfigurationInterval();
			mRemoteApplicationName = tMsg.getApplicationName();
			mRemoteHostName = tMsg.getHostAddress().getHostName(); // Watch out for DNS issues !!!
			mConfiguration = pConnection.mConfiguration;
			mMca = pConnection.mIpmg;
			mRemoteConnectionController = pController;
			mRetransmissionController = pConnection.mRetransmissionController;
			mRcvSegmentBatch = null;

			mHbIsActive = true;
			mCfgIsActive = true;

			mStartSynchronized = false;
			mNextExpectedSeqno = 0;
			mHighiestSeenSeqNo = 0;

			isDead = false;
			mCheckHeartbeatTask = new CheckHeartbeatTask(mConnection.mConnectionId, mRemoteConnectionId);
			mCheckConfigurationTask = new CheckConfigurationTask(mConnection.mConnectionId, mRemoteConnectionId);

			long tInterval = ((mConfiguration.getHearbeatMaxLost() + 1) * mHeartbeatInterval);
			DistributorTimers.getInstance().queue( tInterval, tInterval, mCheckHeartbeatTask );

			tInterval = ((mConfiguration.getConfigurationMaxLost() + 1) * mConfigurationInterval);
			DistributorTimers.getInstance().queue( tInterval, tInterval, mCheckConfigurationTask );
		}
	}

	@Override
	public String toString() {
		return "[ Host: " + mRemoteHostAddressString + " Name: "
				+ mRemoteHostName + " SndrId: " + mRemoteSenderId + " Appl: "
				+ mRemoteApplicationName + "\n        StartTime: "
				+ Integer.toHexString(mRemoteStartTime) + " ConnTime: "
				+ mConnectTime + " HashCode: " + Integer.toHexString(mHashCode)
				+ "\n         HbIntvl: " + mHeartbeatInterval + " CfgIntvl: "
				+ mConfigurationInterval + " LclMca : " + mMca + "]";

	}

	void processHeartbeatMsg(RcvSegment pSegment) {
		NetMsgHeartbeat tMsg = new NetMsgHeartbeat(pSegment);
		tMsg.decode();
		
		mHbIsActive = true;
		// mConnection.log("Received heartbeat Application: " +
		// this.mRemoteApplicationName + " HashCode: " + Integer.toHexString(
		// this.mHashCode ));
		if ((mStartSynchronized) && (mHighiestSeenSeqNo < tMsg.getSeqNo())) {
			mRetransmissionController.createRetransmissionRequest(this,
					mHighiestSeenSeqNo + 1, tMsg.getSeqNo());
			mHighiestSeenSeqNo = tMsg.getSeqNo();
		}
	}

	private NetMsg.SequenceNumberActions checkMessageSequence(NetMsgUpdate tMsg) {
		if (mStartSynchronized) {
			int tRcvSeqno = tMsg.getSequenceNumber();
			if (tRcvSeqno == mNextExpectedSeqno) {
				return NetMsg.SequenceNumberActions.SYNCH;
			} else if (tRcvSeqno > mNextExpectedSeqno) {
				return NetMsg.SequenceNumberActions.HIGHER;
			} else {
				return NetMsg.SequenceNumberActions.LOWER;
			}
		} else {
			if ((tMsg.getHeaderSegmentFlags() & Segment.FLAG_M_SEGMENT_START) != 0) {
				mStartSynchronized = true;
				mHighiestSeenSeqNo = tMsg.getSequenceNumber() - 1;
				mNextExpectedSeqno = mHighiestSeenSeqNo + 1;
				return NetMsg.SequenceNumberActions.SYNCH;
			} else {
				return NetMsg.SequenceNumberActions.IGNORE;
			}
		}
	}

	private void segmentToRcvSegmentBatch(RcvSegment pSegment) {
		if (mRcvSegmentBatch == null) {
			mRcvSegmentBatch = new RcvSegmentBatch(pSegment);
		} else {
			mRcvSegmentBatch.addSegment(pSegment);
		}

		if (pSegment.isEndSegment()) {
			mConnection.mConnectionReceiver.processReceiveSegmentBatch(mRcvSegmentBatch);
			mRcvSegmentBatch = null;
		}
	}

	private void processPendingReceiverQueue() {
		NetMsgUpdate tMsg;
		RcvSegment tRcvSegment;

		while (true) {
			if (mPendingReceiverQueue.isEmpty()) {
				return;
			}

			tMsg = new NetMsgUpdate(mPendingReceiverQueue.get(0));
			tMsg.decode();

			if (tMsg.getSequenceNumber() == mNextExpectedSeqno) {
				mNextExpectedSeqno++;
				tRcvSegment = mPendingReceiverQueue.remove(0);

				if (mConnection
						.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
					mConnection
							.log("RETRANSMISSION: RCV Process message from pending queue Segment ["
									+ tRcvSegment.getSeqno()
									+ "] QueueSize: "
									+ mPendingReceiverQueue.size());
				}

				segmentToRcvSegmentBatch(tRcvSegment);
			} else {
				return;
			}
		}
	}

	private void segmentToPendingReceiverQueue(RcvSegment pSegment) {
		RcvSegment tSeg;

		if (mPendingReceiverQueue.isEmpty()) {
			mPendingReceiverQueue.add(pSegment);
			return;
		}

		ListIterator<RcvSegment> tItr = mPendingReceiverQueue.listIterator(mPendingReceiverQueue.size());
		while (tItr.hasPrevious()) {
			tSeg = tItr.previous();
			if (tSeg.getSeqno() == pSegment.getSeqno()) {
				return;
			} else if (pSegment.getSeqno() > tSeg.getSeqno()) {
				tItr.next();
				tItr.add(pSegment);
				return;
			}
		}

		mPendingReceiverQueue.add(0, pSegment);
	}

	void processUpdateSegment(RcvSegment pSegment) {
		
		
		/**
		 * If no subscribers connected do not process the message 
		 * and do not update sequence number schema.
		 */
		if (mConnection.mSubscribers.isEmpty()) {
			mStartSynchronized = false;
			mHighiestSeenSeqNo = 0;
			return;
		}
		
		
		
		NetMsgUpdate tMsg = new NetMsgUpdate(pSegment);
		tMsg.decode();
		//System.out.println("PROCESS RCV-SEGMENT " + tMsg.toString());
		

		
		NetMsg.SequenceNumberActions tAction;

		if (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_RETRANSMISSION) {
			mRetransmissionController.updateRetransmissions(pSegment);
		}

		tAction = checkMessageSequence(tMsg);

		/**
		 * A segment is received having a sequence number being the next one
		 * that we expect. Update mNextExpectedSeqno and possibly
		 * mHighiestSeenSeqNo.
		 */
		if (tAction == NetMsg.SequenceNumberActions.SYNCH) {
			if ((pSegment.getHeaderMessageType() == Segment.MSG_TYPE_RETRANSMISSION)
					&& (mConnection
							.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS))) {
				mConnection.log("RETRANSMISSION: RCV Retransmission Segment ["
						+ pSegment.getSeqno() + "]");
			}

			mNextExpectedSeqno++;
			if (pSegment.getSeqno() > mHighiestSeenSeqNo) {
				mHighiestSeenSeqNo = pSegment.getSeqno();
			}

			segmentToRcvSegmentBatch(pSegment);

			if (!mPendingReceiverQueue.isEmpty()) {
				processPendingReceiverQueue();
			}
		}

		/**
		 * Not synchronized startup just return
		 */
		else if (tAction == NetMsg.SequenceNumberActions.IGNORE) {
			return;
		} else if (tAction == NetMsg.SequenceNumberActions.LOWER) {
			return;
		} else if (tAction == NetMsg.SequenceNumberActions.HIGHER) {
			if (pSegment.getSeqno() > (mHighiestSeenSeqNo + 1)) {
				mRetransmissionController.createRetransmissionRequest(this,
						mHighiestSeenSeqNo + 1, pSegment.getSeqno() - 1);
			}
			mHighiestSeenSeqNo = pSegment.getSeqno();

			if (mConnection
					.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
				mConnection
						.log("RETRANSMISSION: RCV Message To Pending Queue Segment ["
								+ pSegment.getSeqno() + "]");
			}

			mRetransmissionController.updateRetransmissions(pSegment);
			segmentToPendingReceiverQueue(pSegment);
		}
	}

	class CheckHeartbeatTask extends DistributorTimerTask 
	{
		private long mRemoteConnectionId;
		
		CheckHeartbeatTask(long pDistributorConnectionId, long pRemoteConnectionId) {
			super(pDistributorConnectionId);
			mRemoteConnectionId = pRemoteConnectionId;
		}

		@Override
		public void execute( DistributorConnection pConnection) {
			RemoteConnection tRemoteConnection = pConnection.mConnectionReceiver.mRemoteConnectionController.getRemoteConnection( mRemoteConnectionId );
			
			if (tRemoteConnection == null) {
				this.cancel();
				return;
			}
			
			try {
				if (tRemoteConnection.isDead) {
					cancel();
					return;
				}
				if (pConnection.mTimeToDie) {
					cancel();
					return;
				}
				if (!tRemoteConnection.mHbIsActive) {
					if (pConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RMTDB_EVENTS)) {
						pConnection.log("Remote connction disconnected (no heartbeats) \n        "
										+ tRemoteConnection.toString());
					}
					DistributorRemoveRemoteConnectionEvent tEvent = new DistributorRemoveRemoteConnectionEvent(
									tRemoteConnection.mRemoteHostInetAddress,
									tRemoteConnection.mRemoteSenderId,
									tRemoteConnection.mMca.mInetAddress,
									tRemoteConnection.mMca.mPort,
									tRemoteConnection.mRemoteApplicationName );
					ClientDeliveryController.getInstance().queueEvent(pConnection.mConnectionId, tEvent);
					cancel();
					mRemoteConnectionController.removeRemoteConnection(RemoteConnection.this);
				} else {
					tRemoteConnection.mHbIsActive = false;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	class CheckConfigurationTask extends DistributorTimerTask {
		private long mRemoteConnectionId;

		CheckConfigurationTask( long pDistributorConnectionId, long pRemoteConnectionId ) {
			super( pDistributorConnectionId );
			mRemoteConnectionId = pRemoteConnectionId;
		}

		@Override
		public void execute( DistributorConnection pConnection ) {
			RemoteConnection tRemoteConnection = pConnection.mConnectionReceiver.mRemoteConnectionController.getRemoteConnection( mRemoteConnectionId );
			
			if (tRemoteConnection == null) {
				this.cancel();
				return;
			}
			
			try {

				if (tRemoteConnection.isDead) {
					cancel();
					return;
				}

				if (pConnection.mTimeToDie) {
					cancel();
					return;
				}

				if (!tRemoteConnection.mCfgIsActive) {
					tRemoteConnection.isDead = true;
					tRemoteConnection.mRemoteConnectionController.removeRemoteConnection(tRemoteConnection);
					if (pConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RMTDB_EVENTS)) {
						pConnection.log("Remote connction disconnected (no configuration heartbeats) \n        "
										+ tRemoteConnection.toString());
					}

					
					DistributorRemoveRemoteConnectionEvent tEvent = new DistributorRemoveRemoteConnectionEvent(
							tRemoteConnection.mRemoteHostInetAddress,
							tRemoteConnection.mRemoteSenderId,
							tRemoteConnection.mMca.mInetAddress,
							tRemoteConnection.mMca.mPort,
							tRemoteConnection.mRemoteApplicationName );
					ClientDeliveryController.getInstance().queueEvent(pConnection.mConnectionId, tEvent);
					cancel();
				} else {
					tRemoteConnection.mCfgIsActive = false;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

}
