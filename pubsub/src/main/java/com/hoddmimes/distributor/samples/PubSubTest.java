package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Random;

/*
	This sample is a combined pub/sub application acting as publisher and subscriber
	It illustrates the basic usage of the Distributor. It is likely a good starting point for those how
	would like to understand the Distributor mechanics.
 */


public class PubSubTest {

	private SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");


	Distributor mDistributor; 							// Distributor handle
	DistributorConnectionIf mDistributorConnection;		// Connection communication handle
	DistributorPublisherIf mPublisher;					// Publisher instance
	DistributorSubscriberIf mSubscriber;				//  Subscriber instance
	

	String mEthDevice = "eth0"; 				// Ethernet device to use for communication
	String mIpAddress = "224.10.10.42";			// Class D IP multicast group to use (224.0.0.0 to 239.255.255.255)
	int mIpPort = 5656;							// IP UDP port to use
	
	int mMessageMinSize = 1024;  					// Max message size
	int mMessageMaxSize = 12842;					// Max message size
	
	String mSubjectName = "/pubsubtest";
	String mApplicationName = "PUBSUBTST";
	int mIpBufferSize = 128000;
	int mXtaHoldback = 10;						// Transmit holdback timer to apply if message rate becomes excessive
	int mXtaHoldbackThreshold = 300;			// Apply the transmitter holdback timer is message rate > 'mHoldbackThreshold' / sec

	int mFakeRcvErrorRate = 0;				// Simulate package lost by dropping packages on the subscriber side
											// at a frequency of 'mFakeRcvErrorRate' / 1000
	
	int mUpdateDisplayFactor = 100;			// Print statistics message every 'mUpdateDisplayFactor'
	
	double  mMessageRate = 10;			// Transmit message rate 100 msgs/sec

	Random mRandom = new Random();

	/*
     * Program entry point
	 * @param args, ethernet device to use for communicate over
	 */
	public static void main(String[] args) {
		PubSubTest pubsubtst = new PubSubTest();
		pubsubtst.setup( args ); 	// setup and initialize program
		pubsubtst.runPublisher();	// run publisher in background
	}

	/*
		Initialize the program, create distributor, publisher and subscriber

	 */
	private void setup( String[] pArgs ) 
	{
		if (pArgs.length > 0) {
			mEthDevice = pArgs[0];
			System.out.println("Setting the Ethernet device to " + mEthDevice);
		}


		try {
			// Create a distrbution configuration
			DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration( mApplicationName );
			tApplConfig.setEthDevice( mEthDevice );
			tApplConfig.setLogFlags( DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
					                 DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
									 //DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_XTA +
							         //DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV +
					                 DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS );
									 //DistributorApplicationConfiguration.LOG_SEGMENTS_EVENTS );

			// Create a Distributor instance
			mDistributor = new Distributor(tApplConfig);

			// Create connection confguration
			DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( mIpAddress, mIpPort );
			tConnConfig.setIpBufferSize( mIpBufferSize );
			tConnConfig.setSendHoldbackDelay(mXtaHoldback);
			tConnConfig.setSendHoldbackThreshold(mXtaHoldbackThreshold);
			tConnConfig.setFakeRcvErrorRate( mFakeRcvErrorRate );

			// Create a connection communication instance used to communicate over
			mDistributorConnection = mDistributor.createConnection( tConnConfig );

			// Create publisher
			mPublisher = mDistributor.createPublisher( mDistributorConnection, 		// Connection to use when publishing
					                                   new DistributorEventCallbackHandler("PUBLISHER"));	// Publisher event callback handler

			// Create subscriber
			mSubscriber = mDistributor.createSubscriber( mDistributorConnection, 	// Connection to use for receiving updates on
													 	 new DistributorEventCallbackHandler("SUBSCRIBER"),	// Subscriber event callback handler
													     new DistributorUpdateCallbackHandler());	// Subscriber handler to receive updates on

		
			mSubscriber.addSubscription(mSubjectName, "subscription-callback-parameter"); // Seetup a subscription for the topic we have an interest in

		
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}

	byte[] createMessage( long sequenceNumber ) {
		int tSize = mMessageMinSize + mRandom.nextInt(mMessageMaxSize - mMessageMinSize );
		ByteBuffer bb = ByteBuffer.allocate( tSize );
		bb.putLong(sequenceNumber);
		bb.putLong(System.nanoTime());
		return bb.array();
	}

	/**
	 * Run the publisher
	 */
		
	private void runPublisher() 
	{

		long sequenceNumber = 1;
		XtaParams xtaParams = parseSenderRate( mMessageRate );

		try {
			// This loop will publish messages at the specified 'mMessageRate' (or close enough)
			while( true ) {
				for(int i = 0; i < xtaParams.batchFactor; i++) {
					byte[] msgBuffer = createMessage( sequenceNumber++ );
					mPublisher.publish(mSubjectName, msgBuffer);	// Publish the update
				}
				try  { Thread.currentThread().sleep(xtaParams.dismiss); }
				catch(InterruptedException e) {};
			
			}
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}


	/*
		Subscription Update handler. This class implements the interface 'DistributorUpdateCallbackIf'
		that is called when an update the subscriber has an interest in is received
	 */

	
	class DistributorUpdateCallbackHandler implements DistributorUpdateCallbackIf
	{
		long mStartTime = 0;
		long mTotalUpdates = 0;
		long mTotalBytes = 0;
		long mLastSequenceNumber = 0;
		
		DistributorUpdateCallbackHandler() {
			
		}

		/**
		 * Invoked when there is an update for the subscriber
		 * @param pSubjectName subject/topic being updated
		 * @param pData new update data
		 * @param pCallbackParameter callback parameter associated with the subscription
		 * @param pAppId Distributor application id
		 * @param pDeliveryQueueLength  number of messages in queue for being delivered to the  application.
		 */
		@Override
		public void distributorUpdate(String pSubjectName, byte[] pData,
				Object pCallbackParameter, int pAppId, int pDeliveryQueueLength) {

			// Extract the application sequence number from the update. Is the first long in the update
			ByteBuffer bb = ByteBuffer.wrap( pData );
			long tSeqno = bb.getLong();
			
			if (mStartTime == 0) {
				mStartTime = System.currentTimeMillis();
			}
			
			mTotalUpdates++;
			mTotalBytes += pData.length;
			
			if (mLastSequenceNumber == 0) {
				mLastSequenceNumber = tSeqno;
			} else {
				/*
					here, just for sanity we check that messages are in sequence
					is not necessary. But if we simulate losr messages by setting 'mFakeRcvError'
					it is nice to verify that the recovery works.
				 */
				if ((mLastSequenceNumber + 1) != tSeqno) {
					Exception tException = new Exception("Out of sequence expected: " + (mLastSequenceNumber +1) + " got: " + tSeqno );
					tException.printStackTrace();
				}
				mLastSequenceNumber = tSeqno;
			}

			// From time to time display some statistics
			if ((mUpdateDisplayFactor <= 1) || ((mTotalUpdates % mUpdateDisplayFactor) == 0)) {
				System.out.println( cSDF.format( System.currentTimeMillis()) +
								"[UPDATE-CALLBACK] total updates: " + mTotalUpdates +
								" message rates: " + ((mTotalUpdates * 1000) / ((System.currentTimeMillis() - mStartTime) + 1)) +
								" update event queue length: " + pDeliveryQueueLength);

			}


			// Monitor that we do not have any significan memory leaks
			if ((mTotalUpdates % 5000) == 0) {
				
				System.gc();
				
				long mFreeMem = Runtime.getRuntime().freeMemory();
				long mTotalMem = Runtime.getRuntime().totalMemory();
				long mMaxMem = Runtime.getRuntime().maxMemory();
				long mUsedMem = mTotalMem - mFreeMem;




				
				long tTimeDiff = System.currentTimeMillis() - mStartTime;
				long tUpdateRate =  (mTotalUpdates * 1000L) / tTimeDiff;
				long tByteRate = (mTotalBytes * 1000L) / tTimeDiff;
				System.out.println( cSDF.format( System.currentTimeMillis()) + "  UPDATE-STAT TotalUpds: " + mTotalUpdates +
						" UpdateRate: " + tUpdateRate + " ByteRate: " + tByteRate + " memory: " + (mUsedMem/1024) + " Kb" );
				
			}
		}
		
		
	}

	/**
	 * The distributor beside calling the application with received updates
	 * also will notify the applications about various events like changes in the network connections
	 * coming and going etc.,
	 *
	 */
	class DistributorEventCallbackHandler implements DistributorEventCallbackIf 
	{
		String mType;
		
		DistributorEventCallbackHandler( String pType ) {
			mType = pType;
		}

		@Override
		public void distributorEventCallback(DistributorEvent pDistributorEvent) 
		{
			System.out.println("Distributor Application Event [" + mType + "]\n" + pDistributorEvent.toString() );
		}
	}

	class XtaParams {
		int batchFactor;
		long dismiss;

		XtaParams( double dismiss, int batchFactor) {
			this.batchFactor = batchFactor;
			this.dismiss = (long) dismiss;
		}
	}

	/**
	 * This method calculate teh batching factor and wait-time
	 * needed for accomplish the publishing rate
	 */

	XtaParams parseSenderRate( double pMessageRate) {

		if (mMessageRate == 0)
		{
			return new XtaParams( 1, 5000);
		}

		if (mMessageRate <= 1.0) {
			return new XtaParams( (1000 / mMessageRate), 1);
		}
		else if (mMessageRate  <= 100 ) {
			return new XtaParams( (1000 / mMessageRate), 1);
		}
		else if (mMessageRate <= 200) {
			return new XtaParams(  (2000 / mMessageRate), 2);
		}
		else if (mMessageRate <= 800) {
			return new XtaParams( (5000 / mMessageRate), 5);
		}
		else if ( mMessageRate  <= 2000 )
		{
			return new XtaParams( (20000 / mMessageRate), 20);
		}
		else if (mMessageRate <= 5000) {
			return new XtaParams(  (80000 / mMessageRate), 80);
		}
		else if (mMessageRate <= 20000) {
			return new XtaParams((200000 / mMessageRate), 200);
		}
		else if (mMessageRate <= 50000) {
			return new XtaParams( (500000 / mMessageRate), 500);
		}
		else {
			return new XtaParams( 1, 5000);
		}
	}
}
