package com.hoddmimes.distributor.api;



public class TrafficFlowTask extends DistributorTimerTask 
{
	long	 	mDistributorConnectionId;
	long		mLastEvalTime;
	double 		mLastRelativeTimeFactor;
	long		mRecalculateInterval;
	double		mBitsRateIncrement;
	double		mUpdateRateIncrement;
	double		mSecondTimeFactor;

	double 	mCurrentBitRate;
	double	mCurrentUpdateRate;
	double	mMaxBandwidthUtilizationBitPerInterval;

	TrafficFlowTask(long pDistributorConnectionId, 
					long pRecalculateInterval,
					double pMaxBandwidthUtilizationMBitPerSec )
	{
	   super(pDistributorConnectionId);
	   mSecondTimeFactor =  (1000.0 / (double) pRecalculateInterval);
	   mMaxBandwidthUtilizationBitPerInterval = (Math.max(0.25,pMaxBandwidthUtilizationMBitPerSec) * 1000000.0) / mSecondTimeFactor;
	   mLastRelativeTimeFactor = 1.0;
	   mDistributorConnectionId = pDistributorConnectionId;
	   mLastEvalTime = System.currentTimeMillis();

	   mRecalculateInterval = pRecalculateInterval;
		mBitsRateIncrement = 0;
		mUpdateRateIncrement = 0;
		mCurrentBitRate = 0;
		mCurrentUpdateRate = 0;
	}

	@Override
	public void execute(DistributorConnection pDistributorConnection) {
		long tNow = System.currentTimeMillis();
		mLastRelativeTimeFactor = ((double) mRecalculateInterval / (double) (tNow - mLastEvalTime));
		mCurrentBitRate = mLastRelativeTimeFactor * mBitsRateIncrement;
		mCurrentUpdateRate = mLastRelativeTimeFactor * mUpdateRateIncrement;
		mBitsRateIncrement = 0;
		mUpdateRateIncrement = 0;
		mLastEvalTime = tNow;
	}
		
	void increment( int pSegmentSize ) {
		mBitsRateIncrement += (double) (pSegmentSize * 8);
		mUpdateRateIncrement++;
	}

	int getUpdateRate() {
		int tUpdateRatePerSecond = (int) (mCurrentUpdateRate * mSecondTimeFactor * mLastRelativeTimeFactor) + 1;
		return tUpdateRatePerSecond;
	}
	
	void calculateWaitTime( TrafficFlowClientContext pContext)
	{
		// If bandwidth control is not enforced just return
		// and set wait time to zero
		if (mMaxBandwidthUtilizationBitPerInterval == 0) {
				pContext.mWaitTime = 0;
				return;
		}

		// If not been here before just return and set wait time to sero
		if (pContext.mLastTimestamp == 0) {
		  pContext.mLastTimestamp = mLastEvalTime;
		  pContext.mWaitTime = 0;
		  return;
		}

		// If called within an interval, check if total bandwidth is exceeded.
		// calculate wait time
		if (pContext.mLastTimestamp == mLastEvalTime)
		{
			if (mBitsRateIncrement > mMaxBandwidthUtilizationBitPerInterval) {
				  double tClientBitRatePerInterval = pContext.mBitsSentInInterval * mLastRelativeTimeFactor;
				  double tBitPercentage = Math.min(1.0, (tClientBitRatePerInterval / mMaxBandwidthUtilizationBitPerInterval));
				  double tTimeRemaining = (double) (System.currentTimeMillis() - mLastEvalTime);
				  pContext.mWaitTime = (long) (tBitPercentage * tTimeRemaining);
				  //std::cout << "waittime: " <<  pContext->mWaitTime << std::endl;
				  return;
			  } else {
				  pContext.mWaitTime = 0;
			  }
			  return;
			}

			/**
			 * We have ended a interval period
			 */
			long tTimeDiff = mLastEvalTime - pContext.mLastTimestamp;
			if (tTimeDiff >= (2 * mRecalculateInterval)) {
			   pContext.mLastTimestamp = mLastEvalTime;
			   pContext.mBitsSentInInterval = 0;
			   pContext.mWaitTime = (mBitsRateIncrement > mMaxBandwidthUtilizationBitPerInterval) ? 1 : 0;
			   return;
			}

			if (mCurrentBitRate < mMaxBandwidthUtilizationBitPerInterval) {
				  pContext.mLastTimestamp = mLastEvalTime;
				  pContext.mWaitTime = 0;
				  pContext.mBitsSentInInterval = 0;
				  return;
			}


			double tClientBitRatePerInterval = pContext.mBitsSentInInterval * mLastRelativeTimeFactor;
			double tBitPercentage = Math.min(1.0, (tClientBitRatePerInterval / mCurrentBitRate));

			double tWaitTime = ((((mCurrentBitRate - mMaxBandwidthUtilizationBitPerInterval) * (double) mRecalculateInterval) / mMaxBandwidthUtilizationBitPerInterval) * tBitPercentage);

			pContext.mWaitTime = (long) tWaitTime;
			pContext.mLastTimestamp = mLastEvalTime;
			pContext.mBitsSentInInterval = 0;
	}
	

}
