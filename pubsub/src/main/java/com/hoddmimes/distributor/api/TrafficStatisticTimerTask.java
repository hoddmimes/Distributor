package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorPublisherStatisticsIf;
import com.hoddmimes.distributor.DistributorSubscriberStatisticsIf;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;
import com.hoddmimes.distributor.generated.messages.*;



class TrafficStatisticTimerTask extends DistributorTimerTask implements DistributorPublisherStatisticsIf, DistributorSubscriberStatisticsIf {
	long mLastTimeStamp;
	long mLastTimeStamp_1_min;
	long mLastTimeStamp_5_min;
	long mStartTime;

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

	AtomicLong mXtaTotalBytes;			// total bytes send of all kinds
	AtomicLong mXtaTotalSegments;		// total segments/messages sent of all kinds

	AtomicLong mXtaUpdSendTimeUsec;		// total xta I/O time of user update UDP segments/messages

	AtomicLong mXtaUpdFillTotalBufferAllocateSize;	// total buffer allocation size for user update segments/messages
	AtomicLong mXtaUpdFillTotalUpdateBytes;			// total number of user data bytes sent
	AtomicLong mXtaUpdPackages;						// total number of user data segments/messages sent
	AtomicLong mXtaTotalUserDataUpdates;			// total number of user data updates sent



	AtomicLong mRcvTotalBytes;
	AtomicLong mRcvTotalUserDataUpdates;
	AtomicLong mRcvTotalSegments;


	private DataRateItem transform(CounterElement pCE) {
		DataRateItem tDR = new DataRateItem();
		tDR.setAttribute( pCE.getAttributeName() );
		tDR.setCurrValue(pCE.getValueSec());
		tDR.setPeakValue(pCE.getPeakPerSec());
		tDR.setPeakTime(pCE.getPeakTime());
		tDR.setTotal(pCE.getTotal());
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
		return mXtaTotalUserDataUpdates.get();
	}

	long getTotalRcvBytes() {
		return mRcvTotalBytes.get();
	}

	long getTotalRcvSegments() {
		return mRcvTotalSegments.get();
	}

	long getTotalRcvUpdates() {
		return mRcvTotalUserDataUpdates.get();
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
		mStartTime = System.currentTimeMillis();
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
		mXtaTotalUserDataUpdates = new AtomicLong(0);
		mXtaTotalSegments = new AtomicLong(0);
		mRcvTotalBytes = new AtomicLong(0);
		mRcvTotalUserDataUpdates = new AtomicLong(0);
		mRcvTotalSegments = new AtomicLong(0);
		mXtaUpdSendTimeUsec = new AtomicLong(0);
		mXtaUpdFillTotalBufferAllocateSize = new AtomicLong(0);
		mXtaUpdFillTotalUpdateBytes = new AtomicLong(0);
		mXtaUpdPackages = new AtomicLong(0);
	}

	void updateXtaStatistics(XtaSegment pSegment, long pXtaTimeUsec) {
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
			mXtaTotalUserDataUpdates.getAndAdd(tUpdateCount);
			mXtaUpdPackages.incrementAndGet();
			mXtaUpdSendTimeUsec.getAndAdd( pXtaTimeUsec );
			mXtaUpdFillTotalUpdateBytes.getAndAdd(pSegment.getSize());
			mXtaUpdFillTotalBufferAllocateSize.getAndAdd( pSegment.getBufferAllocationSize());
		} else if (pSegment.isUpdateMessage()) {
			mXtaUpdPackages.incrementAndGet();
			mXtaUpdSendTimeUsec.getAndAdd( pXtaTimeUsec );
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
			mRcvTotalUserDataUpdates.getAndAdd(tUpdateCount);
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
			tDiff = System.currentTimeMillis() - mLastTimeStamp_5_min;
			mLastTimeStamp_5_min = System.currentTimeMillis();

			mXtaMsgs5min.calculate(tDiff);
			mXtaBytes5min.calculate(tDiff);
			mXtaUpdates5min.calculate(tDiff);

			mRcvMsgs5min.calculate(tDiff);
			mRcvBytes5min.calculate(tDiff);
			mRcvUpdates5min.calculate(tDiff);
		}

	}

	public MgmtConnectionTrafficInfo getMgmtTrafficInfo() {
		MgmtConnectionTrafficInfo tMsg = new MgmtConnectionTrafficInfo();

		tMsg.setRcvBytes( this.getRcvBytesInfo());
		tMsg.setRcvSegments( this.getRcvMsgsInfo());
		tMsg.setRcvUpdates( this.getRcvUpdatesInfo());

		tMsg.setRcvBytes1min( this.getRcvBytes1MinInfo());
		tMsg.setRcvBytes5min( this.getRcvBytes5MinInfo());

		tMsg.setRcvSegments1min( this.getRcvMsgs1MinInfo());
		tMsg.setRcvSegments5min( this.getRcvMsgs5MinInfo());

		tMsg.setRcvUpdates1min( this.getRcvUpdates1MinInfo());
		tMsg.setRcvUpdates5min( this.getRcvUpdates5MinInfo());

		tMsg.setRcvTotalBytes( this.mRcvTotalBytes.get());
		tMsg.setRcvTotalSegments( this.mRcvTotalSegments.get());
		tMsg.setRcvTotalUpdates( this.mRcvTotalUserDataUpdates.get());

		// Xta Info
		tMsg.setXtaBytes( this.getXtaBytesInfo());
		tMsg.setXtaSegments( this.getXtaMsgsInfo());
		tMsg.setXtaUpdates( this.getXtaUpdatesInfo());

		tMsg.setXtaBytes1min( this.getXtaBytes1MinInfo());
		tMsg.setXtaBytes5min( this.getXtaBytes5MinInfo());

		tMsg.setXtaSegments1min( this.getXtaMsgs1MinInfo());
		tMsg.setXtaSegments5min( this.getXtaMsgs5MinInfo());

		tMsg.setXtaUpdates1min( this.getXtaUpdates1MinInfo());
		tMsg.setXtaUpdates5min( this.getXtaUpdates5MinInfo());

		tMsg.setRcvTotalBytes(this.mRcvTotalBytes.get());
		tMsg.setXtaTotalBytes( this.mXtaTotalBytes.get());

		tMsg.setXtaTotalSegments( this.mXtaTotalSegments.get());
		tMsg.setRcvTotalSegments( this.mRcvTotalSegments.get());

		tMsg.setXtaTotalUpdates( this.mXtaTotalUserDataUpdates.get());
		tMsg.setRcvTotalUpdates( this.mRcvTotalUserDataUpdates.get());

		return tMsg;
	}



	/**
	 *===================================================================================
	 * DistributorPublisherStatisticsIf
	 * ==================================================================================
	 */
	@Override
	public double getXtaAvgMessageFillRate() {
		if (mXtaUpdFillTotalBufferAllocateSize.get() == 0) {
			return 0;
		}
		long x = (mXtaUpdFillTotalUpdateBytes.get() * 10000L) / mXtaUpdFillTotalBufferAllocateSize.get();
		double tRatio =  (double) x / 100.0f;
		return tRatio;
	}

	@Override
	public double getXtaAvgUpdatesPerMessage() {
		if (mXtaUpdPackages.get() == 0) {
			return 0;
		}
		long x = (mXtaTotalUserDataUpdates.get() * 100L) / mXtaUpdPackages.get();
		double tRatio =  (double) x / 100.0f;
		return tRatio;
	}

	@Override
	public double getXtaAvgIOXTimeUsec() {
		return (mXtaTotalSegments.get() == 0) ? 0 : (int) (mXtaUpdSendTimeUsec.get() / mXtaTotalSegments.get());
	}

	@Override
	public long getXtaTotalNumberOfUpdates() {
		return mXtaTotalUserDataUpdates.get();
	}

	@Override
	public long getXtaTotalNumberOfMessages() {
		return mXtaTotalSegments.get();
	}

	@Override
	public long getXtaTotalNumberOfBytes() {
		return mXtaTotalBytes.get();
	}

	@Override
	public CounterElement getXta1MinUpdates() {
		return mXtaUpdates1min;
	}

	@Override
	public CounterElement getXta1MinMessages() {
		return mXtaMsgs1min;
	}

	@Override
	public CounterElement getXta1MinBytes() {
		return mXtaBytes1min;
	}





	/**
	 *===================================================================================
	 * DistributorSubscriberStatisticsIf
	 * ==================================================================================
	 */

	@Override
	public long getRcvTotalNumberOfUpdates() {
		return this.mRcvTotalUserDataUpdates.get();
	}

	@Override
	public long getRcvTotalNumberOfMessages() {
		return this.mRcvTotalSegments.get();
	}

	@Override
	public long getRcvTotalNumberOfBytes() {
		return this.mRcvTotalBytes.get();
	}

	@Override
	public CounterElement getRcv1MinUpdates() {
		return mRcvUpdates1min;
	}

	@Override
	public CounterElement getRcv1MinMessages() {
		return mRcvMsgs1min;
	}

	@Override
	public CounterElement getRcv1MinBytes() {
		return mRcvBytes1min;
	}

	@Override
	public String getInitTime() {
		SimpleDateFormat tSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return tSDF.format( mStartTime );
	}

	@Override
	public int getSecondsSinceInit() {
		long tSec = (System.currentTimeMillis() - mStartTime) / 1000L;
		return (int) (tSec & 0xffffffff);
	}


}
