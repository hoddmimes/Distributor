package com.hoddmimes.distributor.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CounterElement {
    private static SimpleDateFormat cSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String mAttributeName;
    private AtomicLong mTotal;
    private AtomicInteger mCurrValueSec;
    private  int mValueSec;
    private  int mMaxValueSec;
    private long mMaxValueSecTime;

    CounterElement(String pAttributeName) {
        mAttributeName = pAttributeName;
        mTotal = new AtomicLong(0);
        mCurrValueSec = new AtomicInteger(0);
        mValueSec = 0;
        mMaxValueSec = 0;
        mMaxValueSecTime = 0;
    }

    /**
     * returns the counter aggregated total value
     * @return total value
     */
    public long getTotal()
    {
        return mTotal.get();
    }

    /**
     * returns the aggregated peak value under one second
     * @return aggreagated peak value under one second
     */
    public int getPeakPerSec()
    {
        return mMaxValueSec;
    }

    /**
     * returns the peak time
     * @return, peak time
     */
    public String getPeakTime()
    {
        if (mMaxValueSecTime == 0) {
          return  "00:00:00.000";
        }

        return cSDF.format(mMaxValueSecTime);
    }

    int getValueSec() {
        return this.mValueSec;
    }

    void update(int pValue) {
        mTotal.getAndAdd(pValue);
        mCurrValueSec.getAndAdd(pValue);
    }

    void calculate(long pTimeDiff) {
        if (pTimeDiff > 0) {
            mValueSec = (mCurrValueSec.getAndSet(0) * 1000)
                    / (int) pTimeDiff;
            if (mValueSec > mMaxValueSec) {
                mMaxValueSec = mValueSec;
                mMaxValueSecTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public String toString() {
        String tTimeString;
        if (mMaxValueSecTime == 0) {
            tTimeString = "00:00:00.000";
        } else {
            tTimeString = cSDF.format(new Date(mMaxValueSecTime));
        }
        return mAttributeName + " Total: " + mTotal.get() + " "
                + mAttributeName + "/Sec : " + mValueSec + " Max "
                + mAttributeName + "/Sec : " + mMaxValueSec + " Max Time: "
                + tTimeString;
    }

}
