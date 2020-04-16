package com.hoddmimes.distributor.api;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorCommunicationErrorEvent;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class ConnectionReceiver {
	static final SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	static final Logger cLogger = LogManager.getLogger( ConnectionReceiver.class.getSimpleName() );
	Ipmg mMca;
	DistributorConnectionConfiguration mConfiguration;
	DistributorConnection mConnection;
	RemoteConnectionController mRemoteConnectionController;

	ReceiverThread[] mReceiverThreads = null;
	Random mRand = null;

	ConnectionReceiver(DistributorConnection pConnection) {
		mRand = new Random(System.currentTimeMillis());
		mConnection = pConnection;
		mConfiguration = pConnection.mConfiguration;
		mMca = pConnection.mIpmg;
		mRemoteConnectionController = new RemoteConnectionController(pConnection);

		
		mReceiverThreads = new ReceiverThread[mConfiguration.getReadThreads()];
		for (int i = 0; i < mConfiguration.getReadThreads(); i++) {
			mReceiverThreads[i] = new ReceiverThread(mConnection.mConnectionId, (i + 1), mConfiguration.getSegmentSize() );
			mReceiverThreads[i].setPriority(Thread.NORM_PRIORITY + 1);
			mReceiverThreads[i].start();
		}
	}

	private boolean randomError(int pPromille) {
		if ((Math.abs(mRand.nextInt()) % 1000) <= pPromille) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkVersion(Segment pSegment) {
		int tMajorVersion = ((NetMsg.VERSION >> 8) & 0xff);
		int tMinorVersion = (NetMsg.VERSION & 0xff);

		int tMajorMsgVersion = ((pSegment.getHeaderVersion() >> 8) & 0xff);
		int tMinorMsgVersion = (pSegment.getHeaderVersion() & 0xff);

		if (tMajorVersion != tMajorMsgVersion) {
			cLogger.warn("Received a segment with incompatible version Segment: "
							+ tMajorMsgVersion
							+ "."
							+ tMinorMsgVersion
							+ " Distributor: "
							+ tMajorVersion
							+ "."
							+ tMinorVersion);
			return false;
		} else {
			return true;
		}
	}

	void close() {
		for (int i = 0; i < mReceiverThreads.length; i++) {
			synchronized (mReceiverThreads[i]) {
				mReceiverThreads[i].interrupt();
			}
		}
	}
	
	private boolean isLogFlagSet( int pLogFlag ) {
		if (mConnection.isLogFlagSet(pLogFlag)) {
			return true;
		}
		return false;
	}
	

	void processConfigurationMsg(Segment pSegment) {
		mRemoteConnectionController.processConfigurationMessage(pSegment);
		if (isLogFlagSet( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV)) {
			NetMsgConfiguration tMsg = new NetMsgConfiguration( pSegment );
			tMsg.decode();
			cLogger.info( "PROTOCOL [RCV] " + tMsg.toString());
		}
	}

	void processHeartbeatMsg(RcvSegment pSegment) {
		mRemoteConnectionController.processHeartbeatMessage(pSegment);
		if (isLogFlagSet( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV)) {
			NetMsgHeartbeat tMsg = new NetMsgHeartbeat( pSegment );
			tMsg.decode();
			cLogger.info( "PROTOCOL [RCV] " + tMsg.toString());
		}
	}

	void processUpdateMsg(RcvSegment pSegment) {
		if ((mConfiguration.getFakeRcvErrorRate() > 0) && (randomError(mConfiguration.getFakeRcvErrorRate()))) {
			if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS)) {
				mConnection.log("RETRANSMISSION:  RCV SIMULATED  Error Segment [" + pSegment.getSeqno() + "] dropped");
			}
		} else {
			mRemoteConnectionController.processUpdateSegment(pSegment);
			
			
			if (isLogFlagSet( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV)) {
				NetMsgUpdate tMsg = new NetMsgUpdate( pSegment );
				tMsg.decode();
				if (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE) {
					cLogger.info( "PROTOCOL [RCV] <UPDATE>" + tMsg.toString());
				} else {
					cLogger.info( "PROTOCOL [RCV] <RETRANSMISSION>" + tMsg.toString());
				}
			}
		}
	}

	void processRetransmissionNAK(RcvSegment pSegment) {
		mConnection.mRetransmissionController.processRetransmissionNAK(pSegment);
		if (isLogFlagSet( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV)) {
			NetMsgRetransmissionNAK tMsg = new NetMsgRetransmissionNAK( pSegment );
			tMsg.decode();
			cLogger.info( "PROTOCOL [RCV] " + tMsg.toString());
		}
	}

	void processRetransmissionRqst(RcvSegment pSegment) {
		NetMsgRetransmissionRqst tMsg = new NetMsgRetransmissionRqst(pSegment);
		tMsg.decode();
		mConnection.mConnectionSender.retransmit(tMsg);
		if (isLogFlagSet( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV)) {
			tMsg.decode();
			cLogger.info( "PROTOCOL [RCV] " + tMsg.toString());
		}
	}

	synchronized void processReceivedSegment(RcvSegment pSegment) {
		if (pSegment.getHeaderVersion() != NetMsg.VERSION) {
			if (!checkVersion(pSegment)) {
				return;
			}
		}

		if (isLogFlagSet(DistributorApplicationConfiguration.LOG_SEGMENTS_EVENTS)) {
			NetMsg tNetMsg = new NetMsg(pSegment);
			tNetMsg.decode();
			cLogger.info("RCV Segment: " + tNetMsg.toString() );
		}

		switch (pSegment.getHeaderMessageType()) {
		case Segment.MSG_TYPE_CONFIGURATION:
			processConfigurationMsg(pSegment);
			break;
		case Segment.MSG_TYPE_HEARTBEAT:
			processHeartbeatMsg(pSegment);
			break;
		case Segment.MSG_TYPE_RETRANSMISSION:
			processUpdateMsg(pSegment);
			break;
		case Segment.MSG_TYPE_RETRANSMISSION_NAK:
			processRetransmissionNAK(pSegment);
			break;
		case Segment.MSG_TYPE_RETRANSMISSION_RQST:
			processRetransmissionRqst(pSegment);
			break;
		case Segment.MSG_TYPE_UPDATE:
			processUpdateMsg(pSegment);
			break;
		default:

		}
	}

	void processReceiveSegmentBatch( RcvSegmentBatch pRcvSegmentBatch ) 
	{
		RcvUpdate[] tUpdates = pRcvSegmentBatch.getUpdates( mConnection.mConnectionId );

		// If the update is originate from the local broadcast and this server is
		// the broadcast gateway suppress the update this for circumvent a recursive loop
		if (tUpdates.length == 1) {
			ClientDeliveryController.getInstance().queueUpdate( mConnection.mConnectionId, tUpdates[0] );
		}else {
			ArrayList<RcvUpdate> tRcvUpdateList = new ArrayList<RcvUpdate>(tUpdates.length);
			for(int i = 0; i < tUpdates.length; i++) {
				tRcvUpdateList.add(tUpdates[i]);
			}
			
			ClientDeliveryController.getInstance().queueUpdate( mConnection.mConnectionId, tRcvUpdateList );
		}
	}

	

	

	class ReceiverThread extends Thread {
		int mIndex;
		int mSegmentSize;
		long mDistributorConnectionId;

		ReceiverThread(long pDistributorConnectionId, int pIndex, int pSegmentSize ) {
			mIndex = pIndex;
			mSegmentSize = pSegmentSize;
			mDistributorConnectionId = pDistributorConnectionId;
		}

		@Override
		public void run() 
		{
			InetSocketAddress tInetSocketAddress = null;
			setName("DIST_RECEIVER_" + mIndex + ":" + mMca.toString());
			while (true) {
				ByteBuffer tByteBuffer = ByteBuffer.allocate( mSegmentSize );

				try {
					tInetSocketAddress = (InetSocketAddress) mMca.receive(tByteBuffer);
				} catch (Exception e) {
					if (mConnection.mTimeToDie) {
						return;
					}
					mConnection.log("Failed to receive segment, reason: "+ e.getMessage());
					mConnection.logThrowable(e);
					
					DistributorCommunicationErrorEvent tEvent = new DistributorCommunicationErrorEvent("[RECEIVE]",  mMca.mInetAddress, mMca.mPort, e.getMessage());
					AsyncEventSignalEvent tAsyncEvent = new AsyncEventSignalEvent( tEvent );
					DistributorConnectionController.queueAsyncEvent(mDistributorConnectionId, tAsyncEvent);
				}

				if (mConnection.mTimeToDie) {
					return;
				}



				//System.out.println("RCV-0 SEGMENT pos: " + tByteBuffer.position() + " lim: " + tByteBuffer.limit() + " cap: " + tByteBuffer.capacity());				
				RcvSegment tRcvSegment = new RcvSegment( tByteBuffer );
				tRcvSegment.setFromAddress(tInetSocketAddress.getAddress());
				tRcvSegment.setFromPort(tInetSocketAddress.getPort());
				tRcvSegment.decode();
				
//				System.out.println("RCV-1 SEGMENT pos: " + tByteBuffer.position() + " lim: " + tByteBuffer.limit() + 
//				                   " cap: " + tByteBuffer.capacity() + "\n" + tRcvSegment.toString());
				
				
				AsyncEventReceiveSegment tAsyncEvent = new AsyncEventReceiveSegment(tRcvSegment);
				DistributorConnectionController.queueAsyncEvent(mDistributorConnectionId, tAsyncEvent);
			}
		}

	}

}
