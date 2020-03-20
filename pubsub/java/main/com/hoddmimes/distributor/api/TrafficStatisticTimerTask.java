package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.generated.messages.DataRateItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;




class TrafficStatisticTimerTask extends DistributorTimerTask {
	static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	long mLastTimeStamp;
	long mLastTimeStamp_1_min;
	long mLastTimeStamp_5_min;

	CounterElement mXtaBytes;
	CounterElement mXtaMsgs;
	CounterElement mXtaUpdates;

	CounterElement mXtaBytes1min;
	CounterElement mXtaMsgs1min;
	CounterElement mXtaUpdates1min;

	CounterElement mXtaBytes5min;
	CounterElement mXtaMsgs5min;
	CounterElement mXtaUpdates5min;

	CounterElement mRcvBytes;
	CounterElement mRcvMsgs;
	CounterElement mRcvUpdates;

	CounterElement mRcvBytes1min;
	CounterElement mRcvMsgs1min;
	CounterElement mRcvUpdates1min;

	CounterElement mRcvBytes5min;
	CounterElement mRcvMsgs5min;
	CounterElement mRcvUpdates5min;

	AtomicLong mXtaTotalBytes;
	AtomicLong mXtaTotalUpdates;
	AtomicLong mXtaTotalSegments;
	AtomicLong mRcvTotalBytes;
	AtomicLong mRcvTotalUpdates;
	AtomicLong mRcvTotalSegments;

	private DataRateItem transform(CounterElement pCE) {
		DataRateItem tDR = new DataRateItem();
		tDR.setCurrValue(pCE.mValueSec);
		tDR.setPeakValue(pCE.mMaxValueSec);
		if (pCE.mMaxValueSecTime > 0) {
			tDR.setPeakTime(cSDF.format(pCE.mMaxValueSecTime));
		} else {
			tDR.setPeakTime("00:00:00.000");
		}

		tDR.setTotal(pCE.mTotal.get());
		return tDR;
	}

	DataRateItem getXtaBytesInfo() {
		return transform(mXtaBytes);
	}

	DataRateItem getXtaMsgsInfo() {
		return transform(mXtaMsgs);
	}

	DataRateItem getXtaUpdatesInfo() {
		return transform(mXtaUpdates);
	}

	DataRateItem getXtaBytes1MinInfo() {
		return transform(mXtaBytes1min);
	}

	DataRateItem getXtaMsgs1MinInfo() {
		return transform(mXtaMsgs1min);
	}

	DataRateItem getXtaUpdates1MinInfo() {
		return transform(mXtaUpdates1min);
	}

	DataRateItem getXtaBytes5MinInfo() {
		return transform(mXtaBytes5min);
	}

	DataRateItem getXtaMsgs5MinInfo() {
		return transform(mXtaMsgs5min);
	}

	DataRateItem getXtaUpdates5MinInfo() {
		return transform(mXtaUpdates5min);
	}

	DataRateItem getRcvBytesInfo() {
		return transform(mRcvBytes);
	}

	DataRateItem getRcvMsgsInfo() {
		return transform(mRcvMsgs);
	}

	DataRateItem getRcvUpdatesInfo() {
		return transform(mRcvUpdates);
	}

	DataRateItem getRcvBytes1MinInfo() {
		return transform(mRcvBytes1min);
	}

	DataRateItem getRcvMsgs1MinInfo() {
		return transform(mRcvMsgs1min);
	}

	DataRateItem getRcvUpdates1MinInfo() {
		return transform(mRcvUpdates1min);
	}

	DataRateItem getRcvBytes5MinInfo() {
		return transform(mRcvBytes5min);
	}

	DataRateItem getRcvMsgs5MinInfo() {
		return transform(mRcvMsgs5min);
	}

	DataRateItem getRcvUpdates5MinInfo() {
		return transform(mRcvUpdates5min);
	}

	long getTotalXtaBytes() {
		return mXtaTotalBytes.get();
	}

	long getTotalXtaSegments() {
		return mXtaTotalSegments.get();
	}

	long getTotalXtaUpdates() {
		return mXtaTotalUpdates.get();
	}

	long getTotalRcvBytes() {
		return mRcvTotalBytes.get();
	}

	long getTotalRcvSegments() {
		return mRcvTotalSegments.get();
	}

	long getTotalRcvUpdates() {
		return mRcvTotalUpdates.get();
	}

	String[] getStatistics() {
		String[] tResult = new String[6];
		tResult[0] = mXtaBytes.toString();
		tResult[1] = mXtaUpdates.toString();
		tResult[2] = mXtaMsgs.toString();
		tResult[3] = mRcvBytes.toString();
		tResult[4] = mRcvUpdates.toString();
		tResult[5] = mRcvMsgs.toString();
		return tResult;
	}

	TrafficStatisticTimerTask(long pDistributorConnectionId) {
		super(pDistributorConnectionId);
		mLastTimeStamp = System.currentTimeMillis();
		mLastTimeStamp_1_min = System.currentTimeMillis();
		mLastTimeStamp_5_min = System.currentTimeMillis();

		mXtaBytes = new CounterElement("XtaBytes");
		mXtaMsgs = new CounterElement("XtaMsgs");
		mXtaUpdates = new CounterElement("XtaUpdates");

		mXtaBytes1min = new CounterElement("XtaBytes_1_min");
		mXtaMsgs1min = new CounterElement("XtaMsgs_1_min");
		mXtaUpdates1min = new CounterElement("XtaUpdates_1_min");

		mXtaBytes5min = new CounterElement("XtaBytes_5_min");
		mXtaMsgs5min = new CounterElement("XtaMsgs_5_min");
		mXtaUpdates5min = new CounterElement("XtaUpdates_5_min");

		mRcvBytes = new CounterElement("RcvBytes");
		mRcvMsgs = new CounterElement("RcvMsgs");
		mRcvUpdates = new CounterElement("RcvUpdates");

		mRcvBytes1min = new CounterElement("RcvBytes_1_min");
		mRcvMsgs1min = new CounterElement("RcvMsgs_1_min");
		mRcvUpdates1min = new CounterElement("RcvUpdates_1_min");

		mRcvBytes5min = new CounterElement("RcvBytes_5_min");
		mRcvMsgs5min = new CounterElement("RcvMsgs_5_min");
		mRcvUpdates5min = new CounterElement("RcvUpdates_5_min");

		mXtaTotalBytes = new AtomicLong(0);
		mXtaTotalUpdates = new AtomicLong(0);
		mXtaTotalSegments = new AtomicLong(0);
		mRcvTotalBytes = new AtomicLong(0);
		mRcvTotalUpdates = new AtomicLong(0);
		mRcvTotalSegments = new AtomicLong(0);
	}

	void updateXtaStatistics(XtaSegment pSegment) {
		mXtaMsgs.update(1);
		mXtaMsgs1min.update(1);
		mXtaMsgs5min.update(1);
		mXtaBytes.update(pSegment.getSize());
		mXtaBytes1min.update(pSegment.getSize());
		mXtaBytes5min.update(pSegment.getSize());

		mXtaTotalBytes.getAndAdd(pSegment.getSize());
		mXtaTotalSegments.getAndIncrement();

		if (pSegment.isUpdateMessage()
				&& ((pSegment.getHeaderSegmentFlags() & Segment.FLAG_M_SEGMENT_START) == Segment.FLAG_M_SEGMENT_START)
				&& (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE)) {
			int tUpdateCount = pSegment.getUpdateUpdateCount();
			mXtaUpdates.update(tUpdateCount);
			mXtaUpdates1min.update(tUpdateCount);
			mXtaUpdates5min.update(tUpdateCount);
			mXtaTotalUpdates.getAndAdd(tUpdateCount);
					
		}
	}

	void updateRcvStatistics(RcvSegment pSegment) {
		mRcvMsgs.update(1);
		mRcvMsgs1min.update(1);
		mRcvMsgs5min.update(1);
		mRcvBytes.update(pSegment.getLength());
		mRcvBytes1min.update(pSegment.getLength());
		mRcvBytes5min.update(pSegment.getLength());

		mRcvTotalBytes.getAndAdd(pSegment.getLength());
		mRcvTotalSegments.getAndIncrement();

		if (pSegment.isUpdateMessage()
				&& ((pSegment.getHeaderSegmentFlags() & Segment.FLAG_M_SEGMENT_START) == Segment.FLAG_M_SEGMENT_START)
				&& (pSegment.getHeaderMessageType() == Segment.MSG_TYPE_UPDATE)) {
			int tUpdateCount = pSegment.getUpdateUpdateCount();
			
			mRcvUpdates.update(tUpdateCount);
			mRcvUpdates1min.update(tUpdateCount);
			mRcvUpdates5min.update(tUpdateCount);
			mRcvTotalUpdates.getAndAdd(tUpdateCount);
		}
	}

	@Override
	public void execute( DistributorConnection pConnection) {
		long tCurrentTime = System.currentTimeMillis();
		long tDiff = tCurrentTime - mLastTimeStamp;
		mLastTimeStamp = tCurrentTime;

		mXtaMsgs.calculate(tDiff);
		mXtaBytes.calculate(tDiff);
		mXtaUpdates.calculate(tDiff);

		mRcvMsgs.calculate(tDiff);
		mRcvBytes.calculate(tDiff);
		mRcvUpdates.calculate(tDiff);

		if (System.currentTimeMillis() <= (mLastTimeStamp_1_min + 60000)) {
			tDiff = System.currentTimeMillis() - mLastTimeStamp_1_min;
			mLastTimeStamp_1_min = System.currentTimeMillis();

			mXtaMsgs1min.calculate(tDiff);
			mXtaBytes1min.calculate(tDiff);
			mXtaUpdates1min.calculate(tDiff);

			mRcvMsgs1min.calculate(tDiff);
			mRcvBytes1min.calculate(tDiff);
			mRcvUpdates1min.calculate(tDiff);
		}

		if (System.currentTimeMillis() <= (mLastTimeStamp_5_min + 300000)) {
			tDiff = System.currentTimeMillis() - mLastTimeStamp_1_min;
			mLastTimeStamp_5_min = System.currentTimeMillis();

			mXtaMsgs5min.calculate(tDiff);
			mXtaBytes5min.calculate(tDiff);
			mXtaUpdates5min.calculate(tDiff);

			mRcvMsgs5min.calculate(tDiff);
			mRcvBytes5min.calculate(tDiff);
			mRcvUpdates5min.calculate(tDiff);
		}

	}

	class CounterElement {

		String mAttributeName;
		AtomicLong mTotal;
		AtomicInteger mCurrValueSec;
		int mValueSec;
		int mMaxValueSec;
		long mMaxValueSecTime;

		CounterElement(String pAttributeName) {
			mAttributeName = pAttributeName;
			mTotal = new AtomicLong(0);
			mCurrValueSec = new AtomicInteger(0);
			mValueSec = 0;
			mMaxValueSec = 0;
			mMaxValueSecTime = 0;
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

}
