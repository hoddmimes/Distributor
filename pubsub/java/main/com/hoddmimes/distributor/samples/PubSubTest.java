package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.*;

import java.text.SimpleDateFormat;
import java.util.Random;





public class PubSubTest {

	private SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	Distributor mDistributor;
	DistributorConnectionIf mDistributorConnection;
	DistributorPublisherIf mPublisher;
	DistributorSubscriberIf mSubscriber;
	

	String mEthDeive = "eth0";
	String mIpAddress = "224.10.10.42";
	int mIpPort = 5656;
	
	int mMaxSize = 1024;
	int mMinSize = 12842;
	
	String mSubjectName = "/pubsubtest";
	String mApplicationName = "PUBSUBTST";
	int mIpBufferSize = 128000;
	int mHoldback = 10;
	int mHoldbackThreshold = 300;
	
	int mUpdateDisplayFactor = 10;
	
	double  mRate = 100.0;
	int 	mSendBatchFactor = 0;
	long 	mSendWait = 0;
	
	
	
	private void setup( String[] pArgs ) 
	{
		try {
		DistributorApplicationConfiguration tApplConfig = new DistributorApplicationConfiguration( mApplicationName );
		tApplConfig.setEthDevice( mEthDeive );
    	tApplConfig.setLogFlags( DistributorApplicationConfiguration.LOG_CONNECTION_EVENTS + 
		 DistributorApplicationConfiguration.LOG_RMTDB_EVENTS + 
//		 DistributorApplicationConfiguration.LOG_RETRANSMISSION_EVENTS + 
//		 DistributorApplicationConfiguration.LOG_SEGMENTS_EVENTS +
//		 DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_RCV + 
//		 DistributorApplicationConfiguration.LOG_DATA_PROTOCOL_XTA + 
//		 DistributorApplicationConfiguration.LOG_RETRANSMISSION_CACHE +
		 DistributorApplicationConfiguration.LOG_ERROR_EVENTS );
		
		mDistributor = new Distributor(tApplConfig);
		
		DistributorConnectionConfiguration tConnConfig = new DistributorConnectionConfiguration( mIpAddress, mIpPort );
		tConnConfig.setIpBufferSize( mIpBufferSize );
		tConnConfig.setSendHoldbackDelay( mHoldback );
		tConnConfig.setSendHoldbackThreshold( mHoldbackThreshold );
		tConnConfig.setFakeRcvErrorRate(35);
		
		mDistributorConnection = mDistributor.createConnection( tConnConfig );
		mPublisher = mDistributor.createPublisher( mDistributorConnection, new DistributorEventCallbackHandler("PUBLISHER"));
		mSubscriber = mDistributor.createSubscriber( mDistributorConnection, 
													 new DistributorEventCallbackHandler("PUBLISHER"),
													 new DistributorUpdateCallbackHandler());
		
		mSubscriber.addSubscription(mSubjectName, null);
		
		}
		catch( DistributorException e) {
			e.printStackTrace();
		}
	}
		
	private void runPublisher() 
	{
		byte[] tBuffer;
		Random mRandom = new Random();
		long mSequenceNumber = 1;
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
					long2Buffer(mSequenceNumber++, tBuffer, 0);
					mPublisher.publish(mSubjectName, tBuffer);
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
		else {
			mSendBatchFactor = 1000;
			mSendWait = 5;
		}		
	}
	
	public static void main(String[] args) {
		PubSubTest pubsubtst = new PubSubTest();
		pubsubtst.setup( args );
		pubsubtst.runPublisher();
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
				Object pCallbackParameter, int pDeliveryQueueLength) {
			
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
			
			if (mUpdateDisplayFactor <= 1) {
				System.out.println( cSDF.format( System.currentTimeMillis()) + " UPDATE subj: " + pSubjectName +
						" length: " + pData.length + " seqno: " + tSeqno + " queuelen: " + pDeliveryQueueLength );
			}
			
			if ((mTotalUpdates % 1000) == 0) {
				
				System.gc();
				
				
				long mFreeMem = Runtime.getRuntime().freeMemory();
				long mTotalMem = Runtime.getRuntime().totalMemory();
				long mMaxMem = Runtime.getRuntime().maxMemory();
				long mUsedMem = mTotalMem - mFreeMem;
				
				
				
				long tTimeDiff = System.currentTimeMillis() - mStartTime;
				long tUpdateRate =  (mTotalUpdates * 1000L) / tTimeDiff;
				long tByteRate = (mTotalBytes * 1000L) / tTimeDiff;
				System.out.println( cSDF.format( System.currentTimeMillis()) + "  UPDATE-STAT TotUpds: " + mTotalUpdates + " UpdRate: " + tUpdateRate + " BytRate: " + tByteRate + " memory: " + mUsedMem );
				
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
