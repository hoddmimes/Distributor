package com.hoddmimes.distributor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hoddmimes.distributor.api.DistributorConnection;
import com.hoddmimes.distributor.api.DistributorConnectionController;
import com.hoddmimes.distributor.api.DistributorManagementController;
import com.hoddmimes.distributor.api.DistributorTimers;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;
import com.hoddmimes.distributor.bdxgwy.BdxGwyDistributorClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

	final BdxGwyDistributorClient 			mBdxGwyConnction;
	final DistributorManagementController 	mMgmtController;



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

		
		mLogger.info("Initialized distributor application " + mApplicationConfiguration.getApplicationName() +
					"\n    DistributorId: " + Long.toHexString(mDistributorId));
		DistributorTimers.createTimers(mApplicationConfiguration.getTimerThreads());

		if (mApplicationConfiguration.isMgmtControlEnabled()) {
			mMgmtController = new DistributorManagementController(this, pApplicationConfiguration);
		} else {
			mMgmtController = null;
		}

		if (mApplicationConfiguration.isBroadcastGateayUseEnabled()) {
			mBdxGwyConnction = new BdxGwyDistributorClient(
					mApplicationConfiguration.getBroadcastGatewayAddress(),
					mApplicationConfiguration.getBroadcastGatewayPort());
		} else {
			mBdxGwyConnction = null;
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
			return tConnection.createSubscriber(pEventCallback, pUpdateCallback, this.mBdxGwyConnction );
		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}
	}

	/**
	 * Method for updating the events being logged.
	 * See DistributorApplicationConfiguration.LOG_*
	 * @param pLogMask, mask with the events being captured and logged
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
}
