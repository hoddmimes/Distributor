package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;
import com.hoddmimes.distributor.generated.messages.MgmtConnection;
import com.hoddmimes.distributor.generated.messages.MgmtConnectionView;
import com.hoddmimes.distributor.generated.messages.MgmtDistributorBdx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


public class DistributorConnection extends Thread implements DistributorConnectionIf {
	enum RunningStateType {
		INIT, RUNNING, CLOSED, ERROR
	};

	private final Logger 					mLogger;
	private ReentrantLock 					mMutext;
	private LinkedBlockingQueue<AsyncEvent> mAsynchEventQueue;
	
	
	Distributor 						mDistributor; // Parent distributor
	DistributorConnectionConfiguration 	mConfiguration; // Local connection configuration
	Ipmg 								mIpmg; // Multicast group connection
	
	ConnectionSender 					mConnectionSender; // Local connection sender
	ConnectionReceiver 					mConnectionReceiver; // Local connection receiver
	
	RetransmissionController			mRetransmissionController; // Retransmission controller for this connection
	
	long 								mConnectionId;

	RetransmissionStatistics 			mRetransmissionStatistics;
	RunningStateType 					mState;
	String 								mLastKnownError = null;

	LinkedList<DistributorPublisher>  	mPublishers;
	LinkedList<DistributorSubscriber> 	mSubscribers;

	DistributorSubscriptionFilter		mSubscriptionFilter;
	volatile boolean 					mTimeToDie;
	
	
	LogStatisticsTimerTask 				mLogStatisticTask;
	TrafficStatisticTimerTask			mTrafficStatisticsTask;
	DistributorApplicationConfiguration mApplicationConfiguration;
	

	



	DistributorConnection(Distributor pDistributor,DistributorConnectionConfiguration pConfiguration, DistributorApplicationConfiguration pApplicationConfiguration) throws DistributorException {
		mLogger = LogManager.getLogger( this.getClass().getSimpleName());
		mMutext = new ReentrantLock();
		mState = RunningStateType.INIT;
		mConnectionId = UUIDFactory.getId();
		mTimeToDie = false;
		mDistributor = pDistributor;
		mConfiguration = pConfiguration;
		mApplicationConfiguration = pApplicationConfiguration; 
		mAsynchEventQueue = new LinkedBlockingQueue<AsyncEvent>();
		
		mRetransmissionStatistics = new RetransmissionStatistics();

		mPublishers  = new LinkedList<DistributorPublisher>();
		mSubscribers = new LinkedList<DistributorSubscriber>();

		// Start sender statistics timer task
		mTrafficStatisticsTask = new TrafficStatisticTimerTask( mConnectionId );
		DistributorTimers.getInstance().queue( 1000, 1000, mTrafficStatisticsTask );

		// Open MCA
		mIpmg = new Ipmg(pConfiguration.getMca(),pConfiguration.getMcaNetworkInterface(), pConfiguration.getMcaPort(), pConfiguration.getIpBufferSize(), pConfiguration.getTTL());

		// Start connection receiver
		mConnectionReceiver = new ConnectionReceiver(this);

		// Start connection sender
		mConnectionSender = new ConnectionSender(this);

		// Create retransmission controller
		mRetransmissionController = new RetransmissionController(this);

		mSubscriptionFilter = new DistributorSubscriptionFilter();
		ClientDeliveryController.getInstance().addSubscriptionFilter(mConnectionId, mSubscriptionFilter);

		if (mConfiguration.getStatisticsLogInterval() > 0) {
			mLogStatisticTask = new LogStatisticsTimerTask(mConnectionId);
			DistributorTimers.getInstance().queue( mConfiguration.getStatisticsLogInterval(),
					                               mConfiguration.getStatisticsLogInterval(),
					                               mLogStatisticTask);
		} else {
			mLogStatisticTask = null;
		}

		if (isLogFlagSet(DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS)) {
			log("Open Connection (" + mIpmg.toString() + ") using sender id: " + mConnectionSender.mSenderId);
		}
		mState = RunningStateType.RUNNING;
		this.start();
	}
	
	
	public void pushOutConfiguration()
	{
		NetMsgConfiguration tMsg = new NetMsgConfiguration( new XtaSegment(mConfiguration.getSmallSegmentSize()));
		tMsg.setHeader(Segment.MSG_TYPE_CONFIGURATION,
				(byte)(Segment.FLAG_M_SEGMENT_START + Segment.FLAG_M_SEGMENT_END),
				mConnectionSender.mLocalAddress,
				mConnectionSender.mSenderId,
				(int) (mConnectionSender.mConnectionStartTime & 0xffffffff),
				mDistributor.getAppId());

		tMsg.set( mIpmg.mInetAddress,
				mIpmg.mPort,
				mConnectionSender.mSenderId,
				mConnectionSender.mConnectionStartTime,
				(int) mConfiguration.getHearbeatInterval(),
				(int) mConfiguration.getConfigurationInterval(),
				mConnectionSender.mLocalAddress,
				mApplicationConfiguration.getApplicationName());
		tMsg.encode();

		mConnectionSender.sendSegment( (XtaSegment) tMsg.mSegment);
		tMsg = null;
	}
	
	
	public DistributorSubscriberIf createSubscriber(  DistributorEventCallbackIf pEventCallback,
													  DistributorUpdateCallbackIf pUpdateCallback) throws DistributorException
	{
		DistributorSubscriber tSubscriber =  new DistributorSubscriber(this, pEventCallback, pUpdateCallback);
		mSubscribers.add(tSubscriber);
		if (pEventCallback != null) {
			ClientDeliveryController.getInstance().addEventListener(mConnectionId, pEventCallback);
			this.mConnectionReceiver.triggerRemoteConfigurationNotifications( pEventCallback );
		}
		return tSubscriber;
	}
	
	public DistributorPublisherIf createPublisher(  DistributorEventCallbackIf pEventCallback ) throws DistributorException
	{
		boolean tFloodRegulated = (mConfiguration.getMaxBandwidthKbit() > 0) ? true : false;

		DistributorPublisher tPublisher = new DistributorPublisher(mConnectionId, this.mDistributor.getAppId(), tFloodRegulated, pEventCallback);
		mPublishers.add(tPublisher);
		pushOutConfiguration();

		if (pEventCallback != null) {
			ClientDeliveryController.getInstance().addEventListener(mConnectionId, pEventCallback);
			this.mConnectionReceiver.triggerRemoteConfigurationNotifications( pEventCallback );
		}
		return tPublisher;
	}
	
	void lock() {
		mMutext.lock();
	}
	
	void unlock() {
		mMutext.unlock();
	}


	public void updateMgmtDistributorBdx(MgmtDistributorBdx bdx )
	{
		bdx.setPublishers( bdx.getPublishers() + this.mPublishers.size());
		bdx.setSubscribers( bdx.getSubscribers()+ this.mSubscribers.size());
		bdx.setSubscriptions(bdx.getSubscriptions() + this.mSubscriptionFilter.getActiveSubscriptions());
		bdx.setRetransIn( bdx.getRetransIn() + this.mRetransmissionStatistics.mTotalIn);
		bdx.setRetransOut( bdx.getRetransOut() + this.mRetransmissionStatistics.mTotalOut);
	}

	public MgmtConnection getMgmtConnection() {
		MgmtConnection mgmtConn = new MgmtConnection();
		mgmtConn.setConnectionId( this.mConnectionId );
		mgmtConn.setMcaPort( this.getMcPort());
		mgmtConn.setMcaAddress( this.getMcAddress());
		mgmtConn.setPublishers( this.mPublishers.size());
		mgmtConn.setSubscribers( this.mSubscribers.size());
		mgmtConn.setSubscriptions( this.mSubscriptionFilter.getActiveSubscriptions() );
		mgmtConn.setInRetransmissions( this.mRetransmissionStatistics.mTotalIn );
		mgmtConn.setOutRetransmissions( this.mRetransmissionStatistics.mTotalOut );
		mgmtConn.setUpdateIn( this.mTrafficStatisticsTask.mRcvUpdates.getTotal() );
		mgmtConn.setUpdateOut( this.mTrafficStatisticsTask.mXtaUpdates.getTotal() );
		return mgmtConn;
	}

	public MgmtConnectionView getMgmtConnectionDetails() {
		MgmtConnectionView tMsg = new MgmtConnectionView();
		tMsg.setConnectionId( this.getConnectionId());
		tMsg.setPublishers( this.mPublishers.size() );
		tMsg.setSubscribers( this.mSubscribers.size());
		tMsg.setSubscriptions( this.mSubscriptionFilter.getActiveSubscriptions() );
		tMsg.setMcaPort( this.getMcPort());
		tMsg.setMcaAddress( this.getMcAddress());
		tMsg.setTrafficInfo( this.mTrafficStatisticsTask.getMgmtTrafficInfo());
		tMsg.setDeliverUpdateQueue( ClientDeliveryController.getInstance().getQueueSize());
		tMsg.setRetransmissionInfo( this.mRetransmissionStatistics.geRetransmissionStatistics());
		tMsg.setSubscriptionTopic( this.mSubscriptionFilter.getActiveSubscriptionsStrings());
		return tMsg;
	}


	synchronized public void close() {
		try {
			lock();

			if (mTimeToDie) {
				return;
			}
			
			ClientDeliveryController.getInstance().removeSubscriptionFilter(mConnectionId, mSubscriptionFilter);
			DistributorConnectionController.removeConnection(mConnectionId);

			

			mState = RunningStateType.CLOSED;
			mTimeToDie = true;

			mLogStatisticTask.cancel();
			mTrafficStatisticsTask.cancel();

			if (isLogFlagSet(DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS)) {
				log("Close Connection (" + mIpmg.toString() + ")  using sender id: "
						+ this.mConnectionSender.mSenderId);
			}

			// Clear retransmission controller
			mRetransmissionController.close();
			mRetransmissionController = null;

			// Close connection sender
			mConnectionSender.close();
			mConnectionSender = null;

			// Close connection receiver
			mConnectionReceiver.close();
			mConnectionReceiver = null;

			// Close Ipmg
			mIpmg.close();
			mIpmg = null;

			mSubscriptionFilter.remove(null);
			mSubscriptionFilter = null;
		}
		finally {
			unlock();
		}
	}

	public void checkStatus() throws DistributorException {
		if (mState == RunningStateType.RUNNING) {
			return;
		} else if (mState == RunningStateType.CLOSED) {
			throw new DistributorException("Connection is not in a running state, has been closed.");
		} else if (mState == RunningStateType.ERROR) {
			if (mLastKnownError != null) {
				throw new DistributorException( "Connection in error state, not in a trustworthy state, last error signale: \n   " + mLastKnownError);
			} else {
				throw new DistributorException( "Connection in error state, not in a trustworthy state");
			}
		}

	}

	public String getMcAddress() {
		return mIpmg.mInetAddress.getHostAddress();
	}

	public int getMcPort() {
		return mIpmg.mPort;
	}

	public long getConnectionId() {
		return mConnectionId;
	}

	void flushHoldback( long pFlushHoldbackSeqno ) {
		mConnectionSender.flushHoldback(pFlushHoldbackSeqno);
	}
	
	/**
	 * Invoked when a publisher wants to publish an update
	 * 
	 * @param pXtaUpdate
	 */
	int publishUpdate(XtaUpdate pXtaUpdate) throws DistributorException
	{
		return mConnectionSender.publishUpdate(pXtaUpdate);
	}

	void logThrowable(Throwable e) {
		mLogger.error("[Distributor mca: " + mIpmg.toString() + "]", e);
	}

	boolean isLogFlagSet(int pLogFlag) {
		return mApplicationConfiguration.isLogFlagEnabled( pLogFlag );
	}

	void log(String pMessage) {
		mLogger.info("[mca: " + mIpmg.toString() + "] " + pMessage);
	}


	void deliveryUpdate(RcvUpdate pUpdate, int pQueueLength) {
		if (mTimeToDie) {
			return;
		}

		mSubscriptionFilter.match(pUpdate.getSubjectName(), pUpdate.getData(), pUpdate.getAppId(), pQueueLength);
		pUpdate = null;
	}


	
	
	public void evalTrafficFlow( TrafficFlowClientContext pClientFlowContext ) {
		mConnectionSender.evalTrafficFlow();
	}
	


	void removePublisher(DistributorPublisher pPublisher) throws DistributorException {
		if (mTimeToDie) {
			throw new DistributorException("Distrbutor Connection (" + mIpmg.toString() + ") has been closed.");
		}
		mPublishers.remove(pPublisher);
		if (pPublisher.mEventCallback != null) {	
			ClientDeliveryController.getInstance().removeEventListener(mConnectionId, pPublisher.mEventCallback);
		}
	}

	void removeSubscriber(DistributorSubscriber pSubscriber) 	throws DistributorException {
		if (mTimeToDie) {
			throw new DistributorException("Connection ("	+ mIpmg.toString() + ") has been closed.");
		}
		mSubscribers.remove(pSubscriber);
		if (pSubscriber.mEventCallback != null) {	
			ClientDeliveryController.getInstance().removeEventListener(mConnectionId, pSubscriber.mEventCallback);
		}
	}

	Object addSubscription(DistributorSubscriber pSubscriber, String pSubjectName, Object pCallbackObject) throws DistributorException {
		if (mTimeToDie) {
			throw new DistributorException("Connection (" + mIpmg.toString() + ") has been closed.");
		}
		
		if (isLogFlagSet(DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS)) {
			log("ADD Subscription: " + pSubjectName + " connection: " + mIpmg.toString());
		}
		
		return mSubscriptionFilter.add(pSubjectName,
				pSubscriber.mUpdateCallback, pCallbackObject);
		
		
	}

	void removeSubscription(Object pHandle, String pSubjectName ) throws DistributorException {
		if (mTimeToDie) {
			throw new DistributorException("Connection (" + mIpmg.toString() + ") has been closed.");
		}
		
		if (isLogFlagSet(DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS)) {
			log("REMOVE Subscription: " + pSubjectName + " connection: " + mIpmg.toString());
		}
		
		mSubscriptionFilter.remove(pHandle);
	}

	void updateOutRetransmissionStatistics(Ipmg pMca, InetAddress pDestinationAddress) {
		mRetransmissionStatistics.updateOutStatistics(pMca.mInetAddress, pMca.mPort, pDestinationAddress);
	}

	void updateInRetransmissionStatistics(Ipmg pMca, NetMsgRetransmissionRqst tMsg, boolean pToThisNode) {
		mRetransmissionStatistics.updateInStatistics(pMca.mInetAddress, pMca.mPort, tMsg.getHeaderLocalSourceAddress(), pToThisNode);
	}

	
	public void queueAsyncEvent( AsyncEvent pAsyncEvent ) {
		mAsynchEventQueue.add(pAsyncEvent);
	}
	
	public void run() 
	{
		AsyncEvent 					tAsyncEvent = null;
		ArrayList<AsyncEvent>		tEventList = new ArrayList<AsyncEvent>(60);
		
		setName("AsynchThread: " + mIpmg.toString());
		while( (mState == RunningStateType.RUNNING) || (mState == RunningStateType.INIT)) {
			try { tAsyncEvent = mAsynchEventQueue.take(); }
			catch( InterruptedException e) {}
			
			mMutext.lock();
			try {
				if (mState != RunningStateType.RUNNING) {
					mAsynchEventQueue.clear();
					return;
				}
			
			
				if (tAsyncEvent != null) {
					tAsyncEvent.execute(this);
				}
			
				if (!mAsynchEventQueue.isEmpty()) {
					tEventList.clear();
					mAsynchEventQueue.drainTo(tEventList, 60);
					for( int i = 0; i < tEventList.size(); i++ ) {
						if (mState != RunningStateType.RUNNING) {
							mAsynchEventQueue.clear();
							return;
						} 
						tEventList.get(i).execute(this);
					}
				}
			}
			finally {
				mMutext.unlock();
			}
		}
	}
	
	
	class LogStatisticsTimerTask extends DistributorTimerTask {
		long mDistributorConnectionId;


		String getLogFileName(  DistributorConnection tConnection ) {
			String tFilename;

			SimpleDateFormat tSDF = new SimpleDateFormat("-yyyy-MM-dd-HHmmss");
			if (tConnection.mConfiguration.getStatisticFilename() == null) {
				tFilename = tConnection.mApplicationConfiguration.getApplicationName().replaceAll("[\\/:*?\"<>|]","_") 
						+ "-Statistics-"
						+ tConnection.mIpmg.mInetAddress.getHostAddress()
						+ "_"
						+ tConnection.mIpmg.mPort
						+ tSDF.format(new Date())
						+ ".log";
			} else {
				int tIndex = tConnection.mConfiguration.getStatisticFilename().lastIndexOf(".");
				if (tIndex > 0) {
					tFilename = tConnection.mConfiguration.getStatisticFilename().substring(0, tIndex)
							+ "-"
							+ tSDF.format(new Date())
							+ tConnection.mIpmg.mInetAddress.getHostAddress()
							+ "_"
							+ tConnection.mIpmg.mPort
							+ tSDF.format(new Date())
							+ tConnection.mConfiguration.getStatisticFilename().substring(tIndex, tConnection.mConfiguration.getStatisticFilename().length());
				} else {
					tFilename = tConnection.mConfiguration.getStatisticFilename();
				}
			}
			return tFilename;
		}

		LogStatisticsTimerTask( long DistributorConnectionId ) {
			super(DistributorConnectionId);
		}

		@Override
		public void execute( DistributorConnection tConnection ) {
			StringBuilder tSB = new StringBuilder(512);
			if (tConnection.mTimeToDie) {
				return;
			}

			try {
				tSB.append("Traffic Statistics MCA " + tConnection.mIpmg.toString() + "\n");
				tSB.append("\t XTA Byte Rate          " + tConnection.mTrafficStatisticsTask.mXtaBytes.getValueSec() + "\n");
				tSB.append("\t XTA Segment Rate       " + tConnection.mTrafficStatisticsTask.mXtaMsgs.getValueSec() + "\n");
				tSB.append("\t XTA Update Rate        " + tConnection.mTrafficStatisticsTask.mXtaUpdates.getValueSec() + "\n");
				tSB.append("\t XTA Total Segments     " + tConnection.mTrafficStatisticsTask.getTotalXtaSegments() + "\n");
				tSB.append("\t XTA Total Updates      " + tConnection.mTrafficStatisticsTask.getTotalXtaUpdates() + "\n");
				tSB.append("\t XTA Avg snd time (usec)" + tConnection.mTrafficStatisticsTask.getXtaAvgIOXTimeUsec() + "\n");
				tSB.append("\t RCV Byte Rate          " + tConnection.mTrafficStatisticsTask.mRcvBytes.getValueSec() + "\n");
				tSB.append("\t RCV Segment Rate       " + tConnection.mTrafficStatisticsTask.mRcvMsgs.getValueSec() + "\n");
				tSB.append("\t RCV Update Rate        " + tConnection.mTrafficStatisticsTask.mRcvUpdates.getValueSec() + "\n");
				tSB.append("\t RCV Total Segments     " + tConnection.mTrafficStatisticsTask.getTotalRcvSegments() + "\n");
				tSB.append("\t RCV Total Updates      " + tConnection.mTrafficStatisticsTask.getTotalRcvUpdates() + "\n");
				tSB.append("\t Delivery Queue         " + ClientDeliveryController.getInstance().getQueueLength() + "\n");
				tSB.append("\t Maximum Memory         " + Runtime.getRuntime().maxMemory() + "\n");
				tSB.append("\t Total Memory           " + Runtime.getRuntime().totalMemory() + "\n");
				tSB.append("\t Free Memory            " + Runtime.getRuntime().freeMemory() + "\n");
				tSB.append("\t Used Memory            " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + "\n");
				mLogger.info(tSB.toString());
			} catch (Exception e) {
				System.out .println("Opps failed to log statistics data, reason:  " + e.getMessage());
			}

		}

	}

}
