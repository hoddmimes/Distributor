package com.hoddmimes.distributor.samples.bdxgwy;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.samples.AuxXml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class BdxGwySubscriber {
	Logger cLogger = LogManager.getLogger("BdxGwySubscriber");

	private String mConfigurationFile = "./pubsub/BdxGwySubscriber.xml";

	private Distributor							mDistributor;
	private List<BdxGwyOutboundEntry> 			mOutGateways;
	private List<McaEntry>						mMulicastGroups;
	private Map<String, SubjectEntry>			mSubjectMap;
	private List<DistributorSubscriberIf>		mSubscribers;

	private String			mLocalBdxGwyHost;
	private int				mLocalBdxGwyPort;
	private String			mApplicationName;
	private boolean 		mTrace = false;

	AtomicLong mTotalUpdates = new AtomicLong(0);
	AtomicLong mTotalBytes = new AtomicLong(0);

	private String mEthDevice;



	private int mIpBufferSize = 128000;
	private int mSegmentSize = 8192;
	
	private int 	mUpdateDisplayFactor = 10;
	private int 	mFakeErrorRate = 0;
	
	private int mLogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
							 //DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_XTA +
							 //DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		
		BdxGwySubscriber subscriber = new BdxGwySubscriber();
		subscriber.parseArguments( pArgs );
		subscriber.loadAndParseConfiguration();
		subscriber.setup();
		subscriber.cLogger.info("BdxGwySubscriber successfully started");
		while( true ) {
			subscriber.cLogger.info("BdxGwySubscriber main loop");
			try {  Thread.sleep( 15000L ); }
			catch( InterruptedException e) {}
		}
	}
	
	
	
	private void parseArguments( String[] pArgs ) {
		int i = 0;
		while( i < pArgs.length) {
			if (pArgs[i].startsWith("-config")) {
				mEthDevice = pArgs[i+1];
				i++;
			}
			
			i++;
		}

		
	}

	private void loadAndParseConfiguration() {
		try {
			Element tRoot = AuxXml.loadXMLFromFile( mConfigurationFile ).getDocumentElement();
			mApplicationName = AuxXml.getStringAttribute( tRoot, "application", "Subscriber");
			mEthDevice = AuxXml.getStringAttribute( tRoot, "device", "eth0");
			mLocalBdxGwyHost = AuxXml.getStringAttribute( tRoot, "bdxgwyHost", "127.0.0.1");
			mLocalBdxGwyPort = AuxXml.getIntAttribute( tRoot, "bdxgwyPort", 11900);
			mTrace = AuxXml.getBooleanAttribute( tRoot, "trace", false);

			// Parse Multicast Groups
			mMulicastGroups = new ArrayList<>();
			List<Element> tElements = AuxXml.getChildrenElement( tRoot, "MulticastGroups" );
			for( int i = 0; i < tElements.size(); i++ ) {
				Element tMca = (Element) tElements.get(i);
				mMulicastGroups.add( new McaEntry( AuxXml.getStringAttribute( tMca,"address", null),
						                           AuxXml.getIntAttribute( tMca,"port", 0)));
			}

			// Parse Subjects
			mSubjectMap = new HashMap<>();
			tElements = AuxXml.getChildrenElement( tRoot, "Subjects" );
			for( int i = 0; i < tElements.size(); i++ ) {
				Element tSubj = (Element) tElements.get(i);
				String tSubjectId = AuxXml.getStringAttribute( tSubj,"name", null);
				mSubjectMap.put( tSubjectId, new SubjectEntry( tSubjectId ));
			}

			// Parse Outbound Gateways
			tElements = AuxXml.getChildrenElement( tRoot, "OutboundBroadcastGateways" );
			if ((tElements != null) && (tElements.size() > 0)) {
				mOutGateways = new ArrayList<>();
				for( int i = 0; i < tElements.size(); i++ ) {
					Element tGwy = (Element) tElements.get(i);
					mOutGateways.add( new BdxGwyOutboundEntry( AuxXml.getStringAttribute( tGwy,"name", null),
														       AuxXml.getStringAttribute( tGwy,"ipHost", null),
							                                   AuxXml.getIntAttribute( tGwy,"ipPort", 0)));
				}
			}

			// Parse Publisher Parameters
			Element tPubParams = AuxXml.getElement( tRoot,"SubscriberParameters");
			mIpBufferSize = AuxXml.getIntAttribute( tPubParams,"ipBufferSize", 128000);
			mSegmentSize = AuxXml.getIntAttribute( tPubParams,"segmentSize", 8192);
			mUpdateDisplayFactor = AuxXml.getIntAttribute( tPubParams,"updateDisplayFactor", 10);
			mFakeErrorRate = AuxXml.getIntAttribute( tPubParams,"sendFakeError", 0);


		}
		catch( Exception e ){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void setup() 
	{
		try {
			// setup distributor
		 DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration( mApplicationName );
		 tApplConfig.setEthDevice( mEthDevice );
		 tApplConfig.setLogFlags( mLogFlags );
		 tApplConfig.setBroadcastGatewayAddress( mLocalBdxGwyHost );
		 tApplConfig.setBroadcastGatewayPort( mLocalBdxGwyPort );
    	
		 mDistributor = new Distributor(tApplConfig);

		 // Create subscribers
			mSubscribers = new ArrayList<>();
			for( McaEntry tMcaEntry : mMulicastGroups ) {
				DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( tMcaEntry.getMca(), tMcaEntry.getPort() );
				tConnConfig.setIpBufferSize( mIpBufferSize );
				tConnConfig.setFakeRcvErrorRate(mFakeErrorRate);
				tConnConfig.setSegmentSize(mSegmentSize);

				DistributorConnectionIf tDistributorConnection = mDistributor.createConnection( tConnConfig );
				DistributorSubscriberIf tSubscriberIf = mDistributor.createSubscriber(
						tDistributorConnection,
						new DistributorEventCallbackHandler("SUBSCRIBER-" + tMcaEntry.getMca()),
						new DistributorUpdateCallbackHandler());

				mSubscribers.add( tSubscriberIf );

				// Setup subscriptions
				for( SubjectEntry tSubj : mSubjectMap.values() ) {
					tSubscriberIf.addSubscription(tSubj.getSubject(), tMcaEntry);
				}
			}
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
		

	

	
	class DistributorUpdateCallbackHandler implements DistributorUpdateCallbackIf
	{
		long mStartTime = 0;
		
		DistributorUpdateCallbackHandler() {
			
		}

		@Override
		public void distributorUpdate(String pSubjectName, byte[] pData,
				Object pCallbackParameter, int pAppId, int pDeliveryQueueLength) {


			long tSeqno = buffer2Long(pData, 0);
			
			if (mStartTime == 0) {
				mStartTime = System.currentTimeMillis();
			}

			McaEntry tMcaEntry = (McaEntry) pCallbackParameter;

			long tTotUpds = mTotalUpdates.incrementAndGet();
			long tTotBytes = mTotalBytes.getAndAdd( pData.length);

			SubjectEntry tSubjEntry = mSubjectMap.get( pSubjectName );
			if ( tSubjEntry != null) {
				if (!tSubjEntry.checkSequenceNo( tSeqno )) {
					Exception tException = new Exception( " Out of sequence expected: " + ( tSubjEntry.expected() ) + " got: " + tSeqno );
					cLogger.warn("Message out of sequence [" + pSubjectName + "]", tException);
				}

			}

			if (mTrace) {
				cLogger.info(" Subject: " + pSubjectName + " seqno: " + tSeqno + "  ( " + tMcaEntry + " )");
			}
			
			if ((tTotUpds % mUpdateDisplayFactor) == 0) {
				
				System.gc();

				long mFreeMem = Runtime.getRuntime().freeMemory();
				long mTotalMem = Runtime.getRuntime().totalMemory();
				long mUsedMem = mTotalMem - mFreeMem;
				
				
				
				long tTimeDiff = System.currentTimeMillis() - mStartTime;
				long tUpdateRate =  (tTotUpds * 1000L) / tTimeDiff;
				long tByteRate = (tTotBytes * 1000L) / tTimeDiff;
				cLogger.info( "  UPDATE-STAT TotUpds: " + mTotalUpdates + " delivery-queue: " + pDeliveryQueueLength + " UpdRate: " + tUpdateRate +
						      " (upds/s) ByteRate: " + tByteRate + " (B/s) memory used: " + (mUsedMem/1024) + " (KB)" );
			}
		}
		
		
	}
	
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



	class BdxGwyOutboundEntry
	{
		private String 	mName;
		private String 	mHost;

		private int		mPort;

		public String getHost() {
			return mHost;
		}

		public String getName() {
			return mName;
		}

		public int getPort() {
			return mPort;
		}

		BdxGwyOutboundEntry( String pName, String pHost, int pPort ) {
			mHost = pHost;
			mName = pName;
			mPort = pPort;
		}
	}

	class SubjectEntry
	{
		private String  mSubject;
		private long	mLastSeqNoSeen;
		private long 	isExpected;

		public SubjectEntry( String pSubject) {
			mLastSeqNoSeen = 0L;
			mSubject = pSubject;
		}

		long expected() {
			return isExpected;
		}

		boolean checkSequenceNo( long pSeqNo ) {
			boolean ok = true;
			if (mLastSeqNoSeen == 0) {
				mLastSeqNoSeen = pSeqNo;
			} else {
				isExpected = (mLastSeqNoSeen + 1);
				ok = (isExpected == pSeqNo) ? true : false;
				mLastSeqNoSeen = pSeqNo;
			}
			return ok;
		}


		public String getSubject() {
			return mSubject;
		}

	}

	class McaEntry {
		private String mMca;
		private int mPort;


		McaEntry( String pMca, int pPort ) {
			mMca = pMca;
			mPort = pPort;
		}

		public String getMca() {
			return mMca;
		}

		public int getPort() {
			return mPort;
		}

		@Override
		public String toString() {
			return "Mca: " + mMca + " Port: " + mPort;
		}
	}

	static long buffer2Long(byte[] pBuffer, int pOffset )
	{
		long tValue = 0;
		tValue += (long) ((long)(pBuffer[ pOffset + 0] & 0xff) << 56);
		tValue += (long) ((long)(pBuffer[ pOffset + 1] & 0xff) << 48);
		tValue += (long) ((long)(pBuffer[ pOffset + 2] & 0xff) << 40);
		tValue += (long) ((long)(pBuffer[ pOffset + 3] & 0xff) << 32);
		tValue += (long) ((long)(pBuffer[ pOffset + 4] & 0xff) << 24);
		tValue += (long) ((long)(pBuffer[ pOffset + 5] & 0xff) << 16);
		tValue += (long) ((long)(pBuffer[ pOffset + 6] & 0xff) << 8);
		tValue += (long) ((long)(pBuffer[ pOffset + 7] & 0xff) << 0);
		return tValue;
	}

}
