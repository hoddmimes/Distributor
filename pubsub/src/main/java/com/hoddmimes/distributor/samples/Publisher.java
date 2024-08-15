package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.auxillaries.AuxLog4J;
import com.hoddmimes.distributor.auxillaries.InetAddressConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;






public class Publisher {

	private final static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private Distributor mDistributor;
	private List<DistributorPublisherIf> mPublishers;
	private Random mRandom;


	private int mPublisherInstances = 1;
	private int mSubjectInstances = 1;

	private String mEthDevice = "eth0";
	private String mIpAddress = "224.10.10.44";
	private int mIpPort = 5656;
	
	private int mMaxSize = 1024;
	private int mMinSize = 1024;
	
	private final String mSubjectName = "/test-subject-name";
	private final String mApplicationName = "PUBLISHER";
	private int mIpBufferSize = 128000;
	private int mHoldback = 10;
	private int mHoldbackThreshold = 300;
	private int mSegmentSize = 8192;
	
	private int mUpdateDisplayFactor = 10;
	
	private double  mRate = 10.0;
	private int 	mSendBatchFactor = 0;
	private long 	mSendWait = 0;
	private int 	mFakeErrorRate = 0;
	private boolean mMaximize = false;

	private long	mStatUpdsSent = 0;
	private long 	mStatLastStat = 0;


	private List<String> mSubjectNames;

	private int mLogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		AuxLog4J.initialize("publisher.log", true);
		Publisher publisher = new Publisher();
		publisher.parseArguments( pArgs );
		publisher.setup();
		publisher.runPublisher();
	}
	
	
	
	private void parseArguments( String[] pArgs ) {
		int i = 0;
		while( i < pArgs.length) {
			if (pArgs[i].equals("-minSize")) {
				mMinSize = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-maximize")) {
				mMaximize = Boolean.parseBoolean(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-maxSize")) {
				mMaxSize = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-rate")) {
				mRate= Double.parseDouble(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-device")) {
				mEthDevice = pArgs[i+1];
				i++;
			}
			if (pArgs[i].equals("-address")) {
				mIpAddress = pArgs[i+1];
				i++;
			}
			if (pArgs[i].equals("-port")) {
				mIpPort = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-holdback")) {
				mHoldback = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-holdbackTreshold")) {
				mHoldbackThreshold = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-publisherInstances")) {
				mPublisherInstances = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-subjectNames")) {
				mSubjectInstances = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-ipBuffer")) {
				mIpBufferSize = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-segment")) {
				mSegmentSize = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-logFlags")) {
				mLogFlags = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-fakeErrors")) {
				mFakeErrorRate = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			if (pArgs[i].equals("-displayFactor")) {
				mUpdateDisplayFactor = Integer.parseInt(pArgs[i+1]);
				i++;
			}
			
			i++;
		}
		System.out.println("-publisherInstances  " + mPublisherInstances );
		System.out.println("-subjectNames        " + mSubjectInstances );
		System.out.println("-minSize             " + mMinSize );
		System.out.println("-maxSize             " + mMaxSize );
		System.out.println("-rate                " + mRate );
		System.out.println("-address             " + mIpAddress );
		System.out.println("-port                " + mIpPort );
		System.out.println("-device              " + mEthDevice );
		System.out.println("-holdback            " + mHoldback );
		System.out.println("-holdbackTreshold    " + mHoldbackThreshold );
		System.out.println("-maximize		     " + mMaximize );
		System.out.println("-ipBuffer            " + mIpBufferSize );
		System.out.println("-segment             " + mSegmentSize );
		System.out.println("-logFlags            " + "0x0" + Integer.toHexString(mLogFlags) );
		System.out.println("-fakeErrors          " + mFakeErrorRate);
		System.out.println("-displayFactor       " + mUpdateDisplayFactor);
		System.out.println("-------------------------------------------------------\n\n");
		
	}


	
	private void setup() 
	{
		mRandom = new Random();
		mSubjectNames = new ArrayList<>();
		if (mSubjectInstances == 1) {
			mSubjectNames.add( mSubjectName );
		} else {
			for( int i = 0; i < mSubjectInstances; i++) {
				mSubjectNames.add( mSubjectName + "-" + String.valueOf(i));
			}
		}

		try {
			DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration(mApplicationName);
			tApplConfig.setEthDevice(mEthDevice);
			tApplConfig.setLogFlags(mLogFlags);


			mDistributor = new Distributor(tApplConfig);

			mPublishers = new ArrayList<>();
			for (int i = 0; i < mPublisherInstances; i++) {
				String tIpAddress = getIpAddress(mIpAddress, i);
				DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration(tIpAddress, mIpPort);
				tConnConfig.setIpBufferSize(mIpBufferSize);
				tConnConfig.setSendHoldbackDelay(mHoldback);
				tConnConfig.setSendHoldbackThreshold(mHoldbackThreshold);
				tConnConfig.setFakeRcvErrorRate(mFakeErrorRate);
				tConnConfig.setSegmentSize(mSegmentSize);


				DistributorConnectionIf tDistributorConnection = mDistributor.createConnection(tConnConfig);
				DistributorPublisherIf tPublisher = mDistributor.createPublisher(tDistributorConnection, new DistributorEventCallbackHandler("PUBLISHER"));
				mPublishers.add( tPublisher );
			}
		}
		catch(DistributorException e){
			e.printStackTrace();
		}
	}

	String getIpAddress( String pIpAddressBase, int pOffset ){
		int tIntAddr = InetAddressConverter.stringToIntAddr( pIpAddressBase );
		return InetAddressConverter.intToAddrString( tIntAddr + pOffset );
	}

	long getUpdatesPerSecond( long tTotalUpdates, long pStartTime ) {
		long x = (tTotalUpdates * 1000L) / (System.currentTimeMillis() - pStartTime);
		return x;
	}
	private void runPublisher() 
	{
		Statistics tStat = (mPublisherInstances >= 1) ? new Statistics() : null;

		byte[] tBuffer;
		Random mRandom = new Random();
		long mSequenceNumber = 0, tXtaTime = 0;
		mStatLastStat = System.currentTimeMillis();
		long tStartTime = System.currentTimeMillis();
		parseSenderRate();

		if (mPublisherInstances >= 1)
		try {
			while( true ) {
				for( int i = 0; i < mSendBatchFactor; i++) {
					if (mMaxSize == mMinSize) {
						tBuffer = new byte[ mMaxSize ];
					} else {
						int tSize = mMinSize + Math.abs(mRandom.nextInt() % (mMaxSize - mMinSize));
						tBuffer = new byte[ tSize ];
					} 
					long2Buffer(++mSequenceNumber, tBuffer, 0);


					DistributorPublisherIf tPublisher = getPublisher();
					tXtaTime = tPublisher.publish(getSubject(), tBuffer);
					if (tStat != null) {
						tStat.update( tXtaTime, tBuffer.length);
					}



					if ((mSequenceNumber % mUpdateDisplayFactor) == 0) {
						long mFreeMem = Runtime.getRuntime().freeMemory();
						long mTotalMem = Runtime.getRuntime().totalMemory();
						long mUsedMem = (mTotalMem - mFreeMem) / 1024L;
						if (tStat == null) {

							long tUpdateRate = (mSequenceNumber * 1000L) / (System.currentTimeMillis() - tStartTime);
							mStatUpdsSent = mSequenceNumber;

							DistributorPublisherStatisticsIf tPubStat = tPublisher.getStatistics();

							System.out.println(cSDF.format(System.currentTimeMillis()) + " Avg upds/sec: " + tUpdateRate +
									"  avg upds/msg: " + tPubStat.getXtaAvgUpdatesPerMessage() +
									"  avg xta time: " + tPubStat.getXtaAvgIOXTimeUsec() +
									" (usec)   buffer fill rate: " + tPubStat.getXtaAvgMessageFillRate() +
									"  memory used: " + mUsedMem + " (KB)");
							mStatLastStat = System.currentTimeMillis();
						} else {
							System.out.println(cSDF.format(System.currentTimeMillis()) + " Avg upds/sec: " + tStat.getUpdatesSec() +
									"  KBytes/sec: " + tStat.getKbytesSec() +
									"  avg xta time: " + tStat.getAvgXtaTime() +
									"  memory used: " + mUsedMem + " (KB)");
						}
					}
				}
				if (!mMaximize) {
				   try  { Thread.currentThread().sleep(mSendWait); }
				   catch(InterruptedException e) {};
				}
			}
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
	
	private DistributorPublisherIf getPublisher() {
		if (mPublishers.size() == 1) {
			return mPublishers.get(0);
		}
		return mPublishers.get( mRandom.nextInt( mPublishers.size()));
	}

	private String getSubject() {
		if (mSubjectNames.size() == 1) {
			return mSubjectNames.get(0);
		}
		return mSubjectNames.get( mRandom.nextInt( mSubjectNames.size()));
	}


	void parseSenderRate() {
		
		if ((mRate == 0) || (mMaximize))
		{
			mSendBatchFactor = 5000;
			mSendWait = (long) 1L;
			return;
		}
			
		if (mRate < 1.0) {
			mSendBatchFactor = 1;
			mSendWait = (long) (1 / mRate);
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
			mType = pType;
		}

		@Override
		public void distributorEventCallback(DistributorEvent pDistributorEvent) 
		{
			System.out.println("Distributor Application Event [" + mType + "]\n" + pDistributorEvent.toString() );
		}
	}
	
	static void long2Buffer( long pValue, byte[] pBuffer, int pOffset ) 
	{
		pBuffer[ pOffset + 0] = (byte) (pValue >>> 56);
		pBuffer[ pOffset + 1] = (byte) (pValue >>> 48);
		pBuffer[ pOffset + 2] = (byte) (pValue >>> 40);
		pBuffer[ pOffset + 3] = (byte) (pValue >>> 32);
		pBuffer[ pOffset + 4] = (byte) (pValue >>> 24);
		pBuffer[ pOffset + 5] = (byte) (pValue >>> 16);
		pBuffer[ pOffset + 6] = (byte) (pValue >>> 8);
		pBuffer[ pOffset + 7] = (byte) (pValue >>> 0);
	}


	class Statistics
	{
		long mUpdates;
		long mBytes;
		long mXtaTime;
		long mStartTime;

		void update( long pXtaTime, long pBytes ) {
			mBytes += pBytes;
			mUpdates++;
			mXtaTime += pXtaTime;
		}

		long getAvgXtaTime() {
			if (mUpdates == 0) {
				return 0;
			}
			return mXtaTime / mUpdates;
		}

		double getUpdatesSec() {
			long x = (mUpdates * 10000) / (System.currentTimeMillis() - mStartTime);
			return (double) x / 10.0d;
		}

		double getKbytesSec() {
			long x = (mBytes * 10) / (System.currentTimeMillis() - mStartTime);
			return (double) x / 10.0d;
		}
		Statistics(){
			mStartTime = System.currentTimeMillis();
		}
	}

	

	
}
