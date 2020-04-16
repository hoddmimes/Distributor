package com.hoddmimes.distributor.api;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Random;


import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorCommunicationErrorEvent;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.DistributorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


class ConnectionSender {
	static final Logger cLogger = LogManager.getLogger( ConnectionSender.class.getSimpleName() );

	DistributorConnection 		mConnection; 			// Parent connection
	InetAddress				    mLocalAddress; 			// Local host address
	DistributorConnectionConfiguration 	mConfiguration; 		// Connection configuration
	Ipmg 						mMca; 					// MCA ip multicast group instance
	volatile int 				mConnectionStartTime; 	// Time when this sender was started
	volatile int 				mSenderId; 				// Unique sender id within this host
	volatile boolean 			mErrorSignaled;

	
	volatile NetMsgUpdate 		mCurrentUpdate = null; // Current update message
	long 						mLastUpdateFlushSeqno; // Last update message seqno used for flush dragging updates

	int 						mCurrentSeqNo; 			// Sequence number for this connection

	TrafficFlowTask 			mTrafficFlowTask;
	SendHeartbeatTask			mHeartbeatTimerTask;
	SendConfigurationTask		mConfigurationTimerTask;
	
	
	
	RetransmissionCache 		mRetransmissionCache;
	Random 						mRand;
	private ServerSocket 		mSenderIdSocket;

	
	 
	
	
	ConnectionSender(DistributorConnection pConnection) throws DistributorException 
	{
		synchronized (this) {
			mErrorSignaled = false;
			mConnection = pConnection;
			mConfiguration = pConnection.mConfiguration;

			mRand = new Random(System.currentTimeMillis());
			mConnectionStartTime = (int) (System.currentTimeMillis() & 0xffffffff);
			mLastUpdateFlushSeqno = 0;

			// Pickup local address
		    mLocalAddress = getLocalAddress();
	

			if (mConfiguration.getSenderIdPort() == 0) {
				mSenderId = getSenderId(mConfiguration.getSenderIdPortOffset());
			} else {
				mSenderId = mConfiguration.getSenderIdPort();
			}

			mCurrentSeqNo = 0;

			// Initialize retransmission cache
			mRetransmissionCache = new RetransmissionCache( this );

			// Open MCA
			mMca = pConnection.mIpmg;

			// Create flow regulator
			long tInterval = mConfiguration.getFlowRateRecalculateInterval();
			mTrafficFlowTask = new TrafficFlowTask(mConnection.mConnectionId, tInterval, mConfiguration.getMaxBandwidth() );
			DistributorTimers.getInstance().queue(tInterval, tInterval, mTrafficFlowTask);
			
			// Create heartbeat timer task
			tInterval = mConfiguration.getHearbeatInterval();
			mHeartbeatTimerTask = new SendHeartbeatTask(mConnection.mConnectionId);
			DistributorTimers.getInstance().queue(tInterval, tInterval, mHeartbeatTimerTask);
			
			// Create configuration timer task
			tInterval = mConfiguration.getConfigurationInterval();
			mConfigurationTimerTask = new SendConfigurationTask(mConnection.mConnectionId);
			DistributorTimers.getInstance().queue(tInterval, tInterval, mConfigurationTimerTask);
				
			mCurrentUpdate = null;
			getNewCurrentUpdate();
		}
	}
	
	
	InetAddress getLocalAddress() throws DistributorException
	{
		// Check if local host address is configured, if so just use it as being defined.
		InetAddress tLocalHostAddress = null;
		try {

			if ((mConnection.mApplicationConfiguration.getLocalHostAddress() != null)  && (mConnection.mApplicationConfiguration.getLocalHostAddress() .length() > 0)) {
				tLocalHostAddress = InetAddress.getByName(mConnection.mApplicationConfiguration.getLocalHostAddress() );
				return tLocalHostAddress;
			}
		}
		catch (UnknownHostException e) {
			throw new DistributorException( "Invalid localhost address ("+ mConnection.mApplicationConfiguration.getLocalHostAddress()  + ") UnknownHostException: " + e.getMessage());
		}
		
			
		// Get IP address from device specified
		String tEthDevice = mConnection.mApplicationConfiguration.getEthDevice(); 
		if ((tEthDevice == null) || (tEthDevice.length() == 0)) {
			throw new DistributorException( "Parameter EthDevice must be defined for the connection" );	
		}
		
		try {
			Enumeration<NetworkInterface> tEnumInterface = NetworkInterface.getNetworkInterfaces();
			while( tEnumInterface.hasMoreElements()) {
				NetworkInterface tInterface = tEnumInterface.nextElement();
				if (tInterface.getName().equals( tEthDevice )) {
					Enumeration<InetAddress> tEnumInetAddr = tInterface.getInetAddresses();
					while(tEnumInetAddr.hasMoreElements()) {
						InetAddress tAdr = tEnumInetAddr.nextElement();
						if (tAdr instanceof Inet4Address) {
							tLocalHostAddress = tAdr;
							return tLocalHostAddress;
						}
					}
				}
			}
		}
		catch( SocketException e) {
			throw new DistributorException( "Failed to get local ip host address for ethernet device ("+ tEthDevice + ") UnknownHostException: " + e.getMessage());
		}

		throw new DistributorException( "Failed to get local ip host address for ethernet device ("+ tEthDevice + ") ");
	
	}
	
    void evalTrafficFlow( TrafficFlowClientContext pClientFlowContext ) 
    {
    	mTrafficFlowTask.calculateWaitTime( pClientFlowContext );
    }
    
    
	private int getSenderId(int pPortOffset) throws DistributorException {

		int tPort = pPortOffset;

		while (tPort < 65535) {
			try {
				mSenderIdSocket = new ServerSocket(tPort);
				return tPort;
			} catch (IOException e) {
				tPort++;
			}
		}
		throw new DistributorException( "Could not find any free server id port, start from (" + pPortOffset + ")");
	}

	private boolean randomError(int pPromille) {
		if ((Math.abs(mRand.nextInt()) % 1000) <= pPromille) {
			return true;
		} else {
			return false;
		}
	}

	void flushHoldback( long pFlushRequestSeqno ) 
	{
		if (mCurrentUpdate != null) 
		{
			if ((pFlushRequestSeqno == mCurrentUpdate.mFlushSequenceNumber) && (mCurrentUpdate.mUpdateCount > 0)) 
			{
				//System.out.println("flushHoldback kicked in SeqnoStamp: " + mCurrentUpdate.mFlushSequenceNumber);
				queueCurrentUpdate(Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END);
			}
		}
	}
	

	void close() 
	{
		mHeartbeatTimerTask.cancel();
		mConfigurationTimerTask.cancel();

		try { mSenderIdSocket.close();} 
		catch (Throwable e) {}
		
		mSenderIdSocket = null;
		
		mRetransmissionCache.close();
	}

	void retransmit(NetMsgRetransmissionRqst pMsg) {
		if ((pMsg.getSenderStartTime() == mConnectionStartTime) && (pMsg.getSenderId() == mSenderId)) {
			mConnection.updateInRetransmissionStatistics(mMca, pMsg, true);
			mRetransmissionCache.retransmit(pMsg.getLowSeqNo(), pMsg.getHighSeqNo());
		} else {
			mConnection.updateInRetransmissionStatistics(mMca, pMsg, false);
		}
	}

	/**
	 * Invoked when a publisher wants to publish an update
	 * 
	 * @param pXtaUpdate
	 */
	int publishUpdate(XtaUpdate pXtaUpdate)  throws DistributorException {
		return updateToSegment(pXtaUpdate);
	}

	

	void getNewCurrentUpdate() 
	{
		if (mCurrentUpdate != null) {
			throw new RuntimeException("Illegal state: mCurrentUpdate is not null");
		}
		mCurrentUpdate = new NetMsgUpdate(new XtaSegment(mConfiguration.getSegmentSize()));
		mCurrentUpdate.setHeader( Segment.MSG_TYPE_UPDATE, Segment.FLAG_M_SEGMENT_START, 
								  mLocalAddress, mSenderId, mConnectionStartTime);
	}

	int queueCurrentUpdate(int pSegmentFlags) {
		// Check is the sender is running in the context of an
		// broadcast gateway if so tag the segment as being published
		// by a broadcast gateway. Used to prevent recursive loop
		
		if (mCurrentUpdate.mUpdateCount == 0) {
			return 0; // No updates in segment we can continue to use this one
		}
		

		if (mConnection.mApplicationConfiguration.isBroadcastGateway()) {
			pSegmentFlags += Segment.FLAG_M_SEGMENT_BDXGWY;
		}

		mCurrentUpdate.setHeaderSegmentFlags((byte) (pSegmentFlags & 0xff));
		mCurrentUpdate.setSequenceNumber(++mCurrentSeqNo);

		mCurrentUpdate.encode();
		
		int tSendDelay = sendSegment((XtaSegment) mCurrentUpdate.getSegment());
		mCurrentUpdate = null;
		getNewCurrentUpdate();
		
		return tSendDelay;
	}

	void smallUpdateToSegment(XtaUpdate pXtaUpdate) throws DistributorException {
		if (!mCurrentUpdate.addUpdate(pXtaUpdate)) {
			queueCurrentUpdate(Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END);
			if (!mCurrentUpdate.addUpdate(pXtaUpdate)) {
			   throw new DistributorException("Update did not fit into segment (" + pXtaUpdate.getSize() + " bytes)");
			}
		}
	}

	void largeUpdateToSegments(XtaUpdate pXtaUpdate) {
		int pOffset = 0;
		int pSegmenCount = 0;

		/**
		 * If we have updates in the current segment, queue and get a new
		 * segment
		 */
		queueCurrentUpdate(Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END);

		mCurrentUpdate.addLargeUpdateHeader(pXtaUpdate.mSubjectName, pXtaUpdate.mData.length );

		while (pOffset < pXtaUpdate.mData.length) {
			pOffset += mCurrentUpdate.addLargeData(pXtaUpdate.mData, pOffset);
			mCurrentUpdate.mUpdateCount = 1;
			if (pSegmenCount == 0) {
				queueCurrentUpdate(Segment.FLAG_M_SEGMENT_START);
			} else if (pOffset == pXtaUpdate.mData.length) {
				queueCurrentUpdate(Segment.FLAG_M_SEGMENT_END);
			} else {
				queueCurrentUpdate(Segment.FLAG_M_SEGMENT_MORE);
			}
			pSegmenCount++;
		}
	}

	int updateToSegment(XtaUpdate pXtaUpdate)  throws DistributorException {

		if (pXtaUpdate.getSize() > (mConnection.mConfiguration.getSegmentSize() - NetMsgUpdate.getMinUpdateHeaderSize())) {
			largeUpdateToSegments(pXtaUpdate);
		} else {
			smallUpdateToSegment(pXtaUpdate);
		}

		if ((mConfiguration.getSendHoldbackDelay() > 0) && // Yes, we are having holdback enabled
				(mTrafficFlowTask.getUpdateRate() > mConfiguration.getSendHoldbackThreshold()) && // Yes, we are over the holdback rate
				((System.currentTimeMillis() - mCurrentUpdate.mCreateTime) < mConfiguration.getSendHoldbackDelay())) // yes, the holdback has not expired
		{
			if (mLastUpdateFlushSeqno < mCurrentUpdate.mFlushSequenceNumber) {
				mLastUpdateFlushSeqno = mCurrentUpdate.mFlushSequenceNumber;
				SenderHoldbackTimerTask tTimerTask = new SenderHoldbackTimerTask(mConnection.mConnectionId, mLastUpdateFlushSeqno);
				DistributorTimers.getInstance().queue(mConfiguration.getSendHoldbackDelay(), tTimerTask);
			}
			return 0;
		}

		/**
		 * Send message
		 */
		return queueCurrentUpdate(Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END);
	}

	private void logProtocolData(XtaSegment pSegment)
	{
		int tMsgType = pSegment.getHeaderMessageType();
		
	
		ByteBuffer bb = pSegment.getEncoder().getByteBuffer();
		byte[] tBuffer = new byte[bb.position()];
		System.arraycopy(bb.array(), 0, tBuffer, 0, bb.position());
		ByteBuffer tb = ByteBuffer.wrap(tBuffer);
		tb.position( bb.position());
		tb.mark();

		
		RcvSegment tSegment = new RcvSegment(tb);


		switch( tMsgType ) 
		{
			case Segment.MSG_TYPE_CONFIGURATION:
			{
				NetMsgConfiguration tMsg = new NetMsgConfiguration( tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] " + tMsg.toString());
			}
			break;
			
			case Segment.MSG_TYPE_HEARTBEAT:
			{
				NetMsgHeartbeat tMsg = new NetMsgHeartbeat( tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] " + tMsg.toString());
			}
			break;
			
			case Segment.MSG_TYPE_RETRANSMISSION:
			{
				NetMsgUpdate tMsg = new NetMsgUpdate(  tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] <retrans>" + tMsg.toString());
			}
			break;
			
			case Segment.MSG_TYPE_RETRANSMISSION_NAK:
			{
				NetMsgRetransmissionNAK tMsg = new NetMsgRetransmissionNAK( tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] " + tMsg.toString());
			}
			break;
			
			case Segment.MSG_TYPE_RETRANSMISSION_RQST:
			{
				NetMsgRetransmissionRqst tMsg = new NetMsgRetransmissionRqst(  tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] " + tMsg.toString());
			}
			break;
			
			case Segment.MSG_TYPE_UPDATE:
			{
				NetMsgUpdate tMsg = new NetMsgUpdate(  tSegment );
				tMsg.decode();
				cLogger.info( "PROTOCOL [XTA] " + tMsg.toString());
			}
			break;
			
			default:
			{
				cLogger.info( "PROTOCOL [XTA] unknown message: " + tMsgType  );
				break;
			}
		}
		tb = null;
	}


	private boolean isLoggingEnabled( int pLogFlag ) 
	{
		if (mConnection.isLogFlagSet(pLogFlag)) {
		  return true;
		}
		return false;
	}

    int sendSegment(XtaSegment pSegment) {
		int tSendTime = 0;
		
		if (!mErrorSignaled) {
			
			if (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE) {
				mTrafficFlowTask.increment(pSegment.getLength());
			}

			if (isLoggingEnabled( DistributorApplicationConfiguration.LOG_SEGMENTS_EVENTS )) {
				cLogger.info("XTA Segment: " + pSegment.toString());
			}

			if (isLoggingEnabled( DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_XTA )) {
				logProtocolData( pSegment ); 
			}



			if ((mConfiguration.getFakeXtaErrorRate() > 0) 
					&& (randomError(mConfiguration.getFakeXtaErrorRate()))
					&& (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE)) {
				cLogger.info("SIMULATED XTA Error Segment [" + pSegment.getSeqno() + "] dropped");
			} else {
				try {
					tSendTime = mMca.send(pSegment.getEncoder().getByteBuffer());
					mConnection.mTrafficStatisticsTask.updateXtaStatistics(pSegment);
				} catch (Exception e) {
					mErrorSignaled = true;
					cLogger.info("Failed to send segment, reason: " + e.getMessage());
					mConnection.logThrowable( e);
					
					DistributorCommunicationErrorEvent tEvent = new DistributorCommunicationErrorEvent("[SEND]", mMca.mInetAddress, mMca.mPort, e.getMessage());
					ClientDeliveryController.getInstance().queueEvent(mConnection.mConnectionId, tEvent);
				}
			}

			if (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE) {
				mRetransmissionCache.addSentUpdate(pSegment);
				mHeartbeatTimerTask.dataHasBeenPublished();
			} else {
				pSegment = null;
			}
		} else {
			pSegment = null;
		}
		
		return (int) tSendTime;
	}



	


	class SendHeartbeatTask extends DistributorTimerTask {
		long mDistributorConnectionId;
		volatile boolean mConnectionIsSending;

		SendHeartbeatTask(long pDistributorConnectionId) {
			super(pDistributorConnectionId);
			mDistributorConnectionId = pDistributorConnectionId;
			mConnectionIsSending = false;
		}
		
		void dataHasBeenPublished() {
			mConnectionIsSending = true;
		}
		
		
		@Override
		public void execute( DistributorConnection pConnection) {
			if (pConnection.mTimeToDie) {
				cancel(); // cancel this timer task
				return;
			}

			if ((pConnection.mPublishers.isEmpty()) && (!pConnection.mIsCmaConnection)) {
				return;
			}

			if (!mConnectionIsSending) {
				sendHeartbeat(pConnection);
			}

			mConnectionIsSending = false;
		}

		private void sendHeartbeat(DistributorConnection pConnection) {

			NetMsgHeartbeat tHeartbeat = new NetMsgHeartbeat( new XtaSegment(pConnection.mConfiguration.getSmallSegmentSize()));
			tHeartbeat.setHeader(
					Segment.MSG_TYPE_HEARTBEAT,
					(byte) (Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END),
					pConnection.mConnectionSender.mLocalAddress,
					pConnection.mConnectionSender.mSenderId,
					pConnection.mConnectionSender.mConnectionStartTime);

			tHeartbeat.set(pConnection.mIpmg.mInetAddress,
					pConnection.mIpmg.mPort,
					pConnection.mConnectionSender.mSenderId,
					pConnection.mConnectionSender.mCurrentSeqNo);

			tHeartbeat.encode();
			pConnection.mConnectionSender.sendSegment((XtaSegment) tHeartbeat.mSegment);
			tHeartbeat = null;
		}
	}

	class SendConfigurationTask extends DistributorTimerTask {

		SendConfigurationTask(long pDistributorConnectionId) {
			super( pDistributorConnectionId );
		}

		void sendConfiguration( DistributorConnection pConnection) {
		    pConnection.pushOutConfiguration();
		    pConnection.pushOutConfiguration();
		}

		@Override
		public void execute( DistributorConnection pConnection ) {
			if (mConnection.mTimeToDie) {
				cancel(); // Cancel this timer task
				return;
			}

			if ((mConnection.mPublishers.isEmpty()) && (!mConnection.mIsCmaConnection)) {
				return;
			}

			sendConfiguration(pConnection);
		}
	}


}
