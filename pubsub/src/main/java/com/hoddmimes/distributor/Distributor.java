package com.hoddmimes.distributor;

import com.hoddmimes.distributor.api.DistributorConnection;
import com.hoddmimes.distributor.api.DistributorConnectionController;
import com.hoddmimes.distributor.api.DistributorManagementController;
import com.hoddmimes.distributor.api.DistributorTimers;
import com.hoddmimes.distributor.auxillaries.Application;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;
import com.hoddmimes.distributor.bdxgwy.BdxGwyDistributorClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * In order to send and receive <I>broadcast</I> data a Distributor instance has to
 * be created. When having a Distributor instance Connections, Publishers and
 * Subscribers can be created. A connection is physical multicast transport
 * entity i.e a multicast group, class D address and UDP port. A publisher is a
 * component publishing data to one or more subscribers. Publishers and
 * subscribers are always associated with a <I>connection</I>.
 * 
 * @author POBE
 * 
 */
public class Distributor {
	final static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");

	final DistributorApplicationConfiguration mApplicationConfiguration;
	final Logger 							  mLogger = LogManager.getLogger( this.getClass().getSimpleName());
	final long 							      mDistributorId;
	final String 						      mStartTimeString;
	final int								  mAppId;
	final BdxGwyDistributorClient             mBdxGwyConnection;
	final DistributorManagementController 	  mMgmtController;
	final InetAddress						  mLocalInetAddr;


	/**
	 * Constructor, invoked when creating a Distributor instance. The parameter
	 * <i>DistributorApplicationConfiguration</i> defines the global parameter that
	 * will be used by distributor components.
	 * 
	 * @param pApplicationConfiguration  application configuration parameter context
	 * @throws DistributorException  exception throw if failed to create a Distributor instance
	 */
	public Distributor( DistributorApplicationConfiguration pApplicationConfiguration) throws DistributorException 
	{
		mDistributorId = UUIDFactory.getId();
		mStartTimeString = cSDF.format(new Date());
		mApplicationConfiguration = pApplicationConfiguration;

		
		String tComponentName = pApplicationConfiguration.getApplicationName().replaceAll("[\\/:*?\"<>|]","_");

		mLocalInetAddr = mApplicationConfiguration.getLocalInetAddress();
		mAppId = Application.getId( mLocalInetAddr );


		mLogger.info("Initialized distributor application " + mApplicationConfiguration.getApplicationName() +
					"\n    DistributorId: " + Long.toHexString(mDistributorId));
		DistributorTimers.createTimers(mApplicationConfiguration.getTimerThreads());

		if (mApplicationConfiguration.isMgmtControlEnabled()) {
			mMgmtController = new DistributorManagementController(this, pApplicationConfiguration);
		} else {
			mMgmtController = null;
		}

		if (mApplicationConfiguration.isBroadcastGateayUseEnabled()) {
			mBdxGwyConnection = new BdxGwyDistributorClient(
					mApplicationConfiguration.getBroadcastGatewayAddress(),
					mApplicationConfiguration.getBroadcastGatewayPort());
		} else {
			mBdxGwyConnection = null;
		}


	}




	/**
	 * Creates a transport connection i.e. multicast group. When having a connection
	 * Subscribers and Publishers can associate with the connection for
	 * subscribing and publishing data over the the connection. A connection is a UDP transport
	 * connection, a class D address and UDP port.
	 * 
	 * @param pConnectionConfiguration object defining the parameter settings used by the connection transport
	 * @return DistributorConnectionIf a reference to the connection interface
	 * @throws DistributorException exception thrown if failed to create a distributor connection instance 
	 */
	public DistributorConnectionIf createConnection( DistributorConnectionConfiguration pConnectionConfiguration) throws DistributorException {
		return DistributorConnectionController.createConnection(this, pConnectionConfiguration, mApplicationConfiguration);
	}

	/**
	 * Creates a publisher. When having a publisher the application can publish
	 * data on the <i>connection</i> being passed to all <i>subscribers</i>
	 * associated with the <I>connection</I>.
	 * 
	 * @param pDistributorConnection the connection with which the publisher will be associated.
	 * @param pEventCallback event callback routine receiving application notification and error events
	 * @return DistributorPublisherIf reference to the publisher interface
	 * @throws DistributorException exception thrown if failed to create a publisher instance 
	 */
	public DistributorPublisherIf createPublisher( DistributorConnectionIf pDistributorConnection, 
												   DistributorEventCallbackIf pEventCallback) throws DistributorException 
	{
		if (pDistributorConnection == null) {
			throw new DistributorException("Connection parameter must not be null");
		}
		
		long tDistributorConnectionId = pDistributorConnection.getConnectionId();
		
		DistributorConnection tConnection = null;
		 
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(tDistributorConnectionId);
			if (tConnection == null) {
				throw new DistributorException("Distributor connects is closed or no longer valid");
			}
			tConnection.checkStatus();
			return tConnection.createPublisher(pEventCallback);
		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}
	}

	/**
	 * Creates a subscriber. When having a subscriber the application can
	 * subscribe to data being published on the <i>connection</i>.
	 * 
	 * @param pDistributorConnection the connection that the subscriber will receive updates on.
	 * @param pEventCallback event callback routine receiving application notification and error events.
	 * @param pUpdateCallback update callback routine invoked when data being subscribed to has been updated and published.
	 * @return DistributorSubscriberIf reference to the subscriber interface.
	 * @throws DistributorException exception thrown if failed to create a subscriber instance 
	 */

	public DistributorSubscriberIf createSubscriber(
			DistributorConnectionIf pDistributorConnection,
			DistributorEventCallbackIf pEventCallback,
			DistributorUpdateCallbackIf pUpdateCallback)
			throws DistributorException 
	{
		if (pDistributorConnection == null) {
			throw new DistributorException("Connection parameter must not be null");
		}
		
		long tDistributorConnectionId = pDistributorConnection.getConnectionId();
		
		DistributorConnection tConnection = null;
		
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(tDistributorConnectionId);
			if (tConnection == null) {
				throw new DistributorException("Distributor connects is closed or no longer valid");
			}
			tConnection.checkStatus();
			return tConnection.createSubscriber(pEventCallback, pUpdateCallback, this.mBdxGwyConnection);
		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}
	}

	/**
	 * Method for updating the events being logged. Can be set and modified in real time
	 * The value is a mask i.e. an OR for the event values being of interest
	 * See DistributorApplicationConfiguration.LOG_*
	 * <table>
	 *  <tr><th>Event</th><th>Mask</th><th>Description</th></tr>
	 * 	<tr><td>LOG_ERROR_EVENTS</td><td>1</td><td>trace when application error events are passed to the publisher / subscriber event callback routines</td></tr>
	 * 	<tr><td>LOG_CONNECTION_EVENTS</td><td>2</td><td>trace the creation and deletion of distributor connection instances</td></tr>
	 * 	<tr><td>LOG_RMTDB_EVENTS</td><td>4</td><td>trace the creation and deletion of <i>remote</i> distributor connection</tr>
	 * 	<tr><td>LOG_RETRANSMISSION_EVENTS</td><td>8</td><td>trace retransmission requests and recovery events</td></tr>
	 * 	<tr><td>LOG_SUBSCRIPTION_EVENTS</td><td>16</td><td>trace when subscribers add and remove subscriptions</td></tr>
	 * 	<tr><td>LOG_STATISTIC_EVENTS</td><td>32</td><td>periodically out statistics information to the log file in case statistic tracing is enabled.</td></tr>
	 * 	<tr><td>LOG_SEGMENTS_EVENTS</td><td>64</td><td>trace when sending and receiving packages (can be extensive).</td></tr>
	 * 	<tr><td>LOG_DATA_PROTOCOL_RCV</td><td>128</td><td>trace distributor protocol messages received.</td></tr>
	 * 	<tr><td>LOG_DATA_PROTOCOL_XTA</td><td>256</td><td>trace distributor protocol messages sent.</td></tr>
	 * 	<tr><td>LOG_RETRANSMISSION_CACHE</td><td>512</td><td>periodically trace the size and time span of the messages in the retransmission cache</td></tr>
	 * </table>
	 *
	 */
	public void setLogging( int pLogMask ) {
		this.mApplicationConfiguration.setLogFlags( pLogMask );
	}

	/**
	 * Method for receiving the time when the distributor instance was created. 
	 * @return creation time having the following format "HH:mm:ss.SSS"
	 */
	public String getStartTime() {
		return mStartTimeString;
	}
	
	/**
	 * Method returning the global Distributor instance identification.
	 * @return Distributor id <i>(used internally in the Distributor to identify the application and distributor).</i>
	 */
	public long getDistributorId() {
		return mDistributorId;
	}

	/**
	 * Method to return the dsitributed application id
	 * @return, application id
	 */
	public int getAppId() {
		return mAppId;
	}

	/**
	 * Method returning the IP V4 local interface address used when
	 * sending / receiving Distributor messages
	 * @return IP V4 interface address
	 */
	public InetAddress getLocalInetAddr() {
		return mLocalInetAddr;
	}
}
