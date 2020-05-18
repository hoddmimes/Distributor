package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.text.SimpleDateFormat;


public class Subscriber {

	private final static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private Distributor mDistributor;
	private DistributorConnectionIf mDistributorConnection;
	private DistributorSubscriberIf mSubscriber;


	private String mEthDevice = "eth0";
	private String mIpAddress = "224.10.10.44";
	private int mIpPort = 5656;
	

	
	private final String mSubjectName = "/test-subject-name";
	private final String mApplicationName = "SUBSCRIBER";
	private int mIpBufferSize = 128000;
	private int mSegmentSize = 8192;
	
	private int 	mUpdateDisplayFactor = 10;
	private int 	mFakeErrorRate = 0;
	
	private int mLogFlags =  DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS +
	                         DistributorApplicationConfiguration.LOG_RMTDB_EVENTS +
					         DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS;
	
	
	
	public static void main(String[] pArgs) {
		
		Subscriber subscriber = new Subscriber();
		subscriber.parseArguments( pArgs );
		subscriber.setup();
	}
	
	
	
	private void parseArguments( String[] pArgs ) {
		int i = 0;
		while( i < pArgs.length) {
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
		
		System.out.println("-address             " + mIpAddress );
		System.out.println("-port                " + mIpPort );
		System.out.println("-device              " + mEthDevice );
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
		tConnConfig.setFakeRcvErrorRate(mFakeErrorRate);
		tConnConfig.setSegmentSize(mSegmentSize);
		
		
		mDistributorConnection = mDistributor.createConnection( tConnConfig );
		mSubscriber = mDistributor.createSubscriber( mDistributorConnection, 
													 new DistributorEventCallbackHandler("SUBSCRIBER"),
													 new DistributorUpdateCallbackHandler());
		
		mSubscriber.addSubscription(mSubjectName, null);

		
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
		

	

	
	class DistributorUpdateCallbackHandler implements DistributorUpdateCallbackIf
	{
		long mStartTime = 0;
		long mTotalUpdates = 0;
		long mTotalBytes = 0;
		long mLastSequenceNumber = 0;
		
		DistributorUpdateCallbackHandler() {
			
		}

		@Override
		public void distributorUpdate(String pSubjectName, byte[] pData,
				Object pCallbackParameter, int pAppId,  int pDeliveryQueueLength) {
			
			long tSeqno = buffer2Long(pData, 0);
			
			if (mStartTime == 0) {
				mStartTime = System.currentTimeMillis();
			}
			
			mTotalUpdates++;
			mTotalBytes += pData.length;
			
			if (mLastSequenceNumber == 0) {
				mLastSequenceNumber = tSeqno;
			} else {
				if ((mLastSequenceNumber +1) != tSeqno) {
					Exception tException = new Exception("Out of sequence expected: " + (mLastSequenceNumber +1) +
														 " got: " + tSeqno );
					tException.printStackTrace();
				}
				mLastSequenceNumber = tSeqno;
			}
			
			
			if ((mTotalUpdates % mUpdateDisplayFactor) == 0) {
				
				System.gc();
				
				
				long mFreeMem = Runtime.getRuntime().freeMemory();
				long mTotalMem = Runtime.getRuntime().totalMemory();
				long mUsedMem = mTotalMem - mFreeMem;
				
				
				
				long tTimeDiff = System.currentTimeMillis() - mStartTime;
				long tUpdateRate =  (mTotalUpdates * 1000L) / tTimeDiff;
				long tByteRate = (mTotalBytes * 1000L) / tTimeDiff;
				System.out.println( cSDF.format( System.currentTimeMillis()) + "  UPDATE-STAT TotUpds: " + mTotalUpdates + " UpdRate: " + tUpdateRate + " (upds/s) ByteRate: " + tByteRate + " (B/s) memory used: " + (mUsedMem/1024) + " (KB)" );
				
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
