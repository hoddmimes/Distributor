package com.hoddmimes.distributor.api;

public class TrafficFlowClientContext 
{
	long 		mLastTimestamp;
	double  	mBitsSentInInterval;
	long		mWaitTime;
	
	TrafficFlowClientContext() 
	{
		mLastTimestamp = 0;
		mBitsSentInInterval = 0;
		mWaitTime = 0;
	}
}
