package com.hoddmimes.distributor.api;


public class AsyncEventReceiveSegment implements AsyncEvent 
{
	RcvSegment 					mRcvSegment;
	
	AsyncEventReceiveSegment( RcvSegment pRcvSegment )
	{
		mRcvSegment = pRcvSegment;
	}
	
	@Override
	public void execute(DistributorConnection pConnection) 
	{
		pConnection.mTrafficStatisticsTask.updateRcvStatistics(mRcvSegment);
		pConnection.mConnectionReceiver.processReceivedSegment( mRcvSegment );
	}

	@Override
	public String getTaskName() {
		return this.getClass().getSimpleName();
	}
	
	public String toString() {
		return  "[" + this.getClass().getSimpleName() + "] " + mRcvSegment.toString();
	}

}
