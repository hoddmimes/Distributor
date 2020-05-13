package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.text.SimpleDateFormat;
import java.util.Random;






public class Publisher {

	private final static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private Distributor mDistributor;
	private DistributorConnectionIf mDistributorConnection;
	private DistributorPublisherIf mPublisher;


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
	
	private int mLogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		
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
		try {
		 DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration( mApplicationName );
		 tApplConfig.setEthDevice( mEthDevice );
		 tApplConfig.setLogFlags( mLogFlags );
    	
		mDistributor = new Distributor(tApplConfig);
		
		DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( mIpAddress, mIpPort );
		tConnConfig.setIpBufferSize( mIpBufferSize );
		tConnConfig.setSendHoldbackDelay( mHoldback );
		tConnConfig.setSendHoldbackThreshold( mHoldbackThreshold );
		tConnConfig.setFakeRcvErrorRate(mFakeErrorRate);
		tConnConfig.setSegmentSize(mSegmentSize);
		
		
		mDistributorConnection = mDistributor.createConnection( tConnConfig );
		mPublisher = mDistributor.createPublisher( mDistributorConnection, new DistributorEventCallbackHandler("PUBLISHER"));

		
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
		
	private void runPublisher() 
	{
		byte[] tBuffer;
		Random mRandom = new Random();
		long mSequenceNumber = 0, tXtaTime = 0;
		mStatLastStat = System.currentTimeMillis();
		parseSenderRate();

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
					tXtaTime = mPublisher.publish(mSubjectName, tBuffer);
					if ((mSequenceNumber % mUpdateDisplayFactor) == 0) {
						long mFreeMem = Runtime.getRuntime().freeMemory();
						long mTotalMem = Runtime.getRuntime().totalMemory();
						long mUsedMem = (mTotalMem - mFreeMem) / 1024L;
						long tDeltaTime = System.currentTimeMillis() - mStatLastStat;
						long  tRate = ((mSequenceNumber - mStatUpdsSent) * 1000L) / tDeltaTime;
						mStatUpdsSent = mSequenceNumber;
						
						System.out.println( cSDF.format(System.currentTimeMillis()) + " Upds/sec: " + tRate  +
								                       "  upds/msg: " + mPublisher.getUpdatesPerMessage() +
													   "  avg xta time: " + mPublisher.getAvgXtaTime() +
								                       " (usec)   buffer fill rate: " + mPublisher.getBufferFillRate() +
								                       "  memory used: " + mUsedMem + " (KB)");
						mStatLastStat = System.currentTimeMillis();
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
	
	

	
}
