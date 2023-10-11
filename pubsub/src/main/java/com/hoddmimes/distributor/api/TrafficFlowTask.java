package com.hoddmimes.distributor.api;


import java.text.DecimalFormat;

public class TrafficFlowTask extends DistributorTimerTask
{
	long	 	mDistributorConnectionId;
	int			mMaxBandwidthKbitSec;				// Max bandwidth kbit/sec. A value of '0' is unlimited bandwidth
	int			mMaxBandwidthWithinInterval;
	long		mRecalcIntervalMs;					// Recalculate interval in ms
	double 		mIntervalFactor;
	double		mBitsRateIncrement;
	double		mUpdateRateIncrement;
	double 		mLastRelativeTimeFactor;
	long		mLastRecalcTimestamp;

	long 		mCurrentBitRate;
	long 		mCurrentUpdateRate;

	public TrafficFlowTask(long pDistributorConnectionId,
					long pRecalculateInterval,
					int pMaxBandwidthUtilizationKBitPerSec )
	{
	   super(pDistributorConnectionId);
	   this.mMaxBandwidthKbitSec = pMaxBandwidthUtilizationKBitPerSec;
	   this.mRecalcIntervalMs = pRecalculateInterval;
	   this.mLastRelativeTimeFactor = 1.0d;
	   this.mIntervalFactor = 1000.0d / pRecalculateInterval;
	   this.mMaxBandwidthWithinInterval = (int) ((pMaxBandwidthUtilizationKBitPerSec * 1024 *  8) / this.mIntervalFactor);
	   this.mUpdateRateIncrement = 0;
	   this.mBitsRateIncrement = 0;
	   this.mLastRecalcTimestamp = System.currentTimeMillis();
	}

	@Override
	public void execute(DistributorConnection pDistributorConnection) {
		long tNow = System.currentTimeMillis();
		mLastRelativeTimeFactor = ((double) this.mRecalcIntervalMs / (double) (tNow - this.mLastRecalcTimestamp));
		this.mCurrentBitRate = Math.round(mLastRelativeTimeFactor * mBitsRateIncrement);
		this.mCurrentUpdateRate = Math.round(mLastRelativeTimeFactor * mUpdateRateIncrement);
		//System.out.println("[execcute] timefactor: " + mLastRelativeTimeFactor + " bit-rate: " + mCurrentBitRate + " update-rate: " + mCurrentUpdateRate );

		mBitsRateIncrement = 0;
		mUpdateRateIncrement = 0;
		mLastRecalcTimestamp = tNow;
	}
		
	public void increment( int pSegmentSize ) {
		mBitsRateIncrement += (double) (pSegmentSize * 8);
		mUpdateRateIncrement++;
	}

	int getUpdateRate() {
		int tUpdateRatePerSecond = (int) (this.mUpdateRateIncrement * this.mIntervalFactor * this.mLastRelativeTimeFactor) + 1;
		return tUpdateRatePerSecond;
	}
	
	public long calculateWaitTime() {
		// If bandwidth control is not enforced just return wait time eq zero
		if (this.mMaxBandwidthKbitSec == 0) {
			return 0;
		}

		if (this.mBitsRateIncrement > (this.mMaxBandwidthWithinInterval * this.mLastRelativeTimeFactor)) {
			DecimalFormat df = new DecimalFormat("#.###");
			// Bandwidth exceeded calculate suspend time
			double tRatio = this.mBitsRateIncrement / ((double) this.mMaxBandwidthWithinInterval * this.mLastRelativeTimeFactor);
			long tWaitTime = (long) ((double) this.mRecalcIntervalMs * tRatio);
			//System.out.println("interval: " + Math.round(this.mBitsRateIncrement) + " max-in-interval: " + this.mMaxBandwidthWithinInterval +
			//		" wait-ms: " + tWaitTime + " ratio: " + df.format(tRatio) + " relative-time-factor: " + df.format(this.mLastRelativeTimeFactor));
			return tWaitTime;
		}
		return 0L;
	}

}
