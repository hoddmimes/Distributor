package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.DistributorNewRemoteConnectionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Publisher extends Thread
{
    int         mAppId;
    String      mAppName;
    String      mStartTime;
    String      mRemoteIpAddress;
    String      mMcaAddr;
    int         mMcaPort;
    AtomicLong        mTotBytes;
    AtomicLong        mTotKBytes;
    AtomicLong        mTotUpdates;

    AtomicLong mSecTimestamp, mMinTimestamp;
    AtomicLong mSecBytes, mMinBytes, mSecUpdates, mMinUpdates;
    double mDisplayKBytesSec, mDisplayKBytesMin, mDisplayUpdatesSec, mDisplayUpdatesMin;

    Map<String,Statistic> mSubjects;



    Publisher(DistributorNewRemoteConnectionEvent pEvent ) {
        mAppId = pEvent.getAppId();
        mAppName = pEvent.getRemoteApplicationName();
        mStartTime = pEvent.getSenderStartTime();
        mRemoteIpAddress = pEvent.getRemoteAddress().getHostAddress();
        mMcaAddr = pEvent.getMcAddress().getHostAddress();
        mMcaPort = pEvent.getMcPort();
        mSubjects = new HashMap<>();
        long tTimeStamp = System.currentTimeMillis();
        mSecTimestamp = new AtomicLong(tTimeStamp);
         mMinTimestamp = new AtomicLong(tTimeStamp);
        mSecBytes = new AtomicLong(0);
        mSecUpdates = new AtomicLong(0);
        mMinUpdates = new AtomicLong(0);
        mMinBytes = new AtomicLong(0);

        mTotBytes = new AtomicLong(0);
        mTotUpdates = new AtomicLong( 0);
        mTotKBytes = new AtomicLong(0);
        this.start();

    }




    public void distributorUpdate( String pSubject, byte[] pPayLoad ) {
        mTotBytes.getAndAdd(pPayLoad.length);
        mTotUpdates.getAndIncrement();
        mTotKBytes.set(mTotBytes.get() / 1000L);

        mMinBytes.addAndGet(pPayLoad.length);
        mSecBytes.addAndGet(pPayLoad.length);
        mMinUpdates.getAndIncrement();
        mSecUpdates.getAndIncrement();


        Statistic tStat = mSubjects.get( pSubject );
        if (tStat == null) {
            tStat = new Statistic( pSubject );
            mSubjects.put( pSubject, tStat);
        }
        tStat.mBytes.getAndAdd(pPayLoad.length);
        tStat.mUpdates.getAndIncrement();
    }

    double getKBytesSec() {
        return mDisplayKBytesSec;
    }

    double getKBytesMin() {
        return mDisplayKBytesMin;
    }

    double getUpdatesSec() {
        return mDisplayUpdatesSec;
    }

    double getUpdatesMin() {
        return mDisplayUpdatesMin;
    }

    @Override
    public void run() {
        this.setName("Counters-" + mAppName);
        while( true ) {
            try { Thread.sleep( 1000L );}
            catch( InterruptedException e) {}

            long tDeltaTime = System.currentTimeMillis() - mSecTimestamp.get();

            //System.out.println("delta: " + tDeltaTime + " bytes: " +mSecBytes.get() + " updates: " + mSecUpdates.get());

            mDisplayKBytesSec = (mSecBytes.get() == 0) ? 0.0d : round(((double) mSecBytes.get() / (double) tDeltaTime));
            mDisplayUpdatesSec = (mSecUpdates.get() == 0) ? 0.0d : round( (double)(mSecUpdates.get() * 1000.0d) / (double) tDeltaTime);

            mSecBytes.set(0);mSecUpdates.set(0);
            mSecTimestamp.set(System.currentTimeMillis());

            tDeltaTime = System.currentTimeMillis() - mMinTimestamp.get();
            if (tDeltaTime >= 60000L) {
                mDisplayKBytesMin = (mMinBytes.get() == 0) ? 0.0d : round((double) (mMinBytes.get()) / (double) tDeltaTime);
                mDisplayUpdatesMin = (mMinUpdates.get() == 0) ? 0.0d : round( (mMinUpdates.get() * 1000.0d) / (double) tDeltaTime);
                mMinBytes.set(0); mMinUpdates.set(0);
                mMinTimestamp.set(System.currentTimeMillis());
            }
        }
    }

    private double round( double pValue ) {
        int x = (int) Math.round( (pValue * 100.0d));
        double d = (x / 100.0d );
        return d;
    }



    public static class Statistic
    {
        String mSubject;
        AtomicLong mUpdates;
        AtomicLong mBytes;

        Statistic( String pSubject ) {
            mSubject = pSubject;
            mUpdates = new AtomicLong(0);
            mBytes = new AtomicLong(0);
        }
    }
}
