package com.hoddmimes.distributor.samples.bdxgwy;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.samples.AuxXml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.util.*;


public class BdxGwyPublisher {
	Logger cLogger = LogManager.getLogger("BdxGwyPublisher");

	private Distributor					mDistributor;
	private List<BdxGwyInboundEntry> 	mInGateways;
	private List<McaEntry>				mMulicastGroups;
	private List<SubjectEntry>			mSubjects;
	private Map<Integer,DistributorPublisherIf> mPublishers;


	private String mConfigurationFile = "./pubsub/BdxGwyPublisher.xml";
	private String mEthDevice = "eth0";
	private String mApplicationName;

	private String mLocalBdxGwyHost;
	private int mLocalBdxGwyPort;

	private int mMaxSize = 1024;
	private int mMinSize = 1024;

	private int mIpBufferSize = 128000;
	private int mHoldback = 10;
	private int mHoldbackThreshold = 300;
	private int mSegmentSize = 8192;
	
	private int mUpdateDisplayFactor = 10;
	
	private double  mRate = 10.0;
	private int 	mSendBatchFactor = 0;
	private long 	mSendWait = 0;
	private int 	mFakeErrorRate = 0;
	
	private int mLogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
			                 DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		BdxGwyPublisher publisher = new BdxGwyPublisher();
		publisher.parseArguments( pArgs );
		publisher.loadAndParseConfiguration();
		publisher.setup();
		publisher.runPublisher();
	}
	
	
	
	private void parseArguments( String[] pArgs ) {
		int i = 0;
		while( i < pArgs.length) {
			if (pArgs[i].startsWith("-config")) {
				mConfigurationFile = pArgs[i+1];
				i++;
			}
			i++;
		}
	}

	private void loadAndParseConfiguration() {
		try {
			Element tRoot = AuxXml.loadXMLFromFile( mConfigurationFile ).getDocumentElement();
			mApplicationName = AuxXml.getStringAttribute( tRoot, "application", "Publisher");
			mEthDevice = AuxXml.getStringAttribute( tRoot, "device", "eth0");
			mLocalBdxGwyHost = AuxXml.getStringAttribute( tRoot, "bdxgwyHost", "127.0.0.1");
			mLocalBdxGwyPort = AuxXml.getIntAttribute( tRoot, "bdxgwyPort", 11900);

			// Parse Multicast Groups
			mMulicastGroups = new ArrayList<>();
			List<Element> tElements = AuxXml.getChildrenElement( tRoot, "MulticastGroups" );
			for( int i = 0; i < tElements.size(); i++ ) {
				Element tMca = (Element) tElements.get(i);
			   	mMulicastGroups.add( new McaEntry( AuxXml.getStringAttribute( tMca,"address", null),
						                           AuxXml.getIntAttribute( tMca,"port", 0), (i+1)));
			}

			// Parse Subjects
			mSubjects = new ArrayList<>();
			tElements = AuxXml.getChildrenElement( tRoot, "Subjects" );
			for( int i = 0; i < tElements.size(); i++ ) {
				Element tSubj = (Element) tElements.get(i);
				mSubjects.add( new SubjectEntry( AuxXml.getStringAttribute( tSubj,"name", null),
						                         AuxXml.getIntAttribute( tSubj,"mcaId", 0)));
			}

			// Parse Inbound Gateways, if any
			tElements = AuxXml.getChildrenElement( tRoot, "InboundBroadcastGateways" );
			if ((tElements != null) && (tElements.size() > 0)) {
			    mInGateways = new ArrayList<>();
				for( int i = 0; i < tElements.size(); i++ ) {
					Element tGwy = (Element) tElements.get(i);
					mInGateways.add( new BdxGwyInboundEntry( AuxXml.getStringAttribute( tGwy,"name", null),
									                  AuxXml.getStringAttribute( tGwy,"ipAddress", null)));
				}
			} else {
				mInGateways = null;
			}

			// Parse Publisher Parameters
			Element tPubParams = AuxXml.getElement( tRoot,"PublisherParameters");
			mRate = AuxXml.getDoubleAttribute( tPubParams,"sendRate", 10.0d);
			mMinSize = AuxXml.getIntAttribute( tPubParams,"minMsgSize", 1024);
			mMaxSize = AuxXml.getIntAttribute( tPubParams,"maxMsgSize", 1024);
			mIpBufferSize = AuxXml.getIntAttribute( tPubParams,"ipBufferSize", 128000);
			mHoldback = AuxXml.getIntAttribute( tPubParams,"holdback", 10);
			mHoldbackThreshold = AuxXml.getIntAttribute( tPubParams,"holdbackTreshold", 300);
			mSegmentSize = AuxXml.getIntAttribute( tPubParams,"segmentSize", 8192);
			mUpdateDisplayFactor = AuxXml.getIntAttribute( tPubParams,"updateDisplayFactor", 10);
			mFakeErrorRate = AuxXml.getIntAttribute( tPubParams,"sendFakeError", 0);


		}
		catch( Exception e ){
			e.printStackTrace();
			System.exit(0);
		}
	}


	private void setup() {
		try {
			// Create Distributor App Entry
			DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration(mApplicationName);
			tApplConfig.setEthDevice(mEthDevice);
			tApplConfig.setLogFlags(mLogFlags);

			mDistributor = new Distributor(tApplConfig);

			// Create publisher entries
			mPublishers = new HashMap<>();

			for (McaEntry tMcaEntry : mMulicastGroups) {
				DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration(tMcaEntry.getMca(), tMcaEntry.getPort());
				tConnConfig.setIpBufferSize(mIpBufferSize);
				tConnConfig.setSendHoldbackDelay(mHoldback);
				tConnConfig.setSendHoldbackThreshold(mHoldbackThreshold);
				tConnConfig.setFakeRcvErrorRate(mFakeErrorRate);
				tConnConfig.setSegmentSize(mSegmentSize);

				DistributorConnectionIf tDistributorConnection = mDistributor.createConnection(tConnConfig);
				DistributorPublisherIf tPublisher = mDistributor.createPublisher(tDistributorConnection, new DistributorEventCallbackHandler("PUBLISHER-" + tMcaEntry.getMca()));
				mPublishers.put(tMcaEntry.getId(), tPublisher);
			}

		} catch (DistributorException e) {
			e.printStackTrace();
		}
	}
		
	private void runPublisher() 
	{

		ByteBuffer tBuffer = ByteBuffer.allocate( mMaxSize );
		Random tRandom = new Random();
		long mLoopCount = 0l, mMcaSendTime = 0l;
		parseSenderRate();

		// Initialize the send buffer
		for( int i = Long.BYTES; i < mMaxSize; i++) {
			tBuffer.put(i, (byte) ( 65 + tRandom.nextInt(12 ) & 0xff));
		}
		tBuffer.position( mMaxSize );

		try {
			while( true ) {
				for( int i = 0; i < mSendBatchFactor; i++) {
					mLoopCount++;
					SubjectEntry tSubj = mSubjects.get( tRandom.nextInt(mSubjects.size()));

					tBuffer.putLong(0, tSubj.getSeqNo());
					int tMsgSize = (mMaxSize == mMinSize) ? tBuffer.position() : (mMinSize + Math.abs(tRandom.nextInt() % (mMaxSize - mMinSize)));

					DistributorPublisherIf tPublisher = mPublishers.get( tSubj.getMcaId());
					mMcaSendTime += tPublisher.publish( tSubj.getSubject(), tBuffer.array(), tMsgSize);


					if ((mLoopCount % mUpdateDisplayFactor) == 0) {
						long mFreeMem = Runtime.getRuntime().freeMemory();
						long mTotalMem = Runtime.getRuntime().totalMemory();
						long mUsedMem = (mTotalMem - mFreeMem) / 1024L;
						long tAvgSendTime = mMcaSendTime / mLoopCount;

						cLogger.info( " updates sent: " + mLoopCount + " avg xta time: " + tAvgSendTime + " memory used: " + mUsedMem + " (KB)");
					}
				}
				try  { Thread.currentThread().sleep(mSendWait); }
				catch(InterruptedException e) {};
			}
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
	
	
	
	void parseSenderRate() {
		
		if (mRate == 0)
		{
			mSendBatchFactor = 5000;
			mSendWait = (long) 1L;
			return;
		}
			
		if (mRate < 1.0) {
			mSendBatchFactor = 1;
			mSendWait = (long) (1000 / mRate);
		}
		else if (mRate  <= 100 ) {
			mSendBatchFactor = 1;
			mSendWait = (long) (1000 / mRate);		
		}
		else if (mRate <= 200) {
			mSendBatchFactor = 2;
			mSendWait = (long) (2000 / mRate);
		}
		else if (mRate <= 800) {
			mSendBatchFactor = 5;
			mSendWait = (long) (5000 / mRate);
		}
		else if ( mRate  <= 2000 )
		{
			mSendBatchFactor = 20;
			mSendWait = (long) (20000 / mRate);
		}
		else if (mRate <= 5000) {
			mSendBatchFactor = 80;
			mSendWait = (long) (80000 / mRate);
		}
		else if (mRate <= 20000) {
			mSendBatchFactor = 200;
			mSendWait = (long) (200000 / mRate);
		}	
		else if (mRate <= 50000) {
			mSendBatchFactor = 500;
			mSendWait = (long) (500000 / mRate);
		}	
		else if (mRate <= 100000) {
			mSendBatchFactor = 1000;
			mSendWait = (long) (1000000 / mRate);
		}
		else {
			mSendBatchFactor = 1000;
			mSendWait = 5;
		}		
	}
	

	
	
	
	class DistributorEventCallbackHandler implements DistributorEventCallbackIf 
	{
		String mType;
		
		DistributorEventCallbackHandler( String pType ) {
			cLogger.info("Distributor Event Callback [" + mType + "]\n" );
			mType = pType;
		}

		@Override
		public void distributorEventCallback(DistributorEvent pDistributorEvent) 
		{
			cLogger.info("Distributor Application Event [" + mType + "]\n" + pDistributorEvent.toString() );
		}
	}


	class BdxGwyInboundEntry
	{
		private String 	mName;
		private String 	mHost;

		public String getHost() {
			return mHost;
		}

		public String getName() {
			return mName;
		}

		BdxGwyInboundEntry( String pHost, String pName ) {
			mHost = pHost;
			mName = pName;
		}
	}

	class SubjectEntry
	{

		private String  mSubject;
		private int	    mMcaId;
		private long mSeqNo;

		public SubjectEntry( String pSubject, int pMcaId ) {
			mMcaId = pMcaId;
			mSubject = pSubject;
			mSeqNo = 0l;
		}

		public long getSeqNo() {
			return (++mSeqNo);
		}



		public String getSubject() {
			return mSubject;
		}

		public int getMcaId() {
			return mMcaId;
		}


	}

	class McaEntry {
		private String mMca;
		private int mPort;
		private int mId;


		McaEntry( String pMca, int pPort, int pId ) {
			mId = pId;
			mMca = pMca;
			mPort = pPort;
		}

		public String getMca() {
			return mMca;
		}

		public int getId() {
			return mId;
		}

		public int getPort() {
			return mPort;
		}
	}


	

	
	

	
}
