package com.hoddmimes.distributor.api;

abstract public class DistributorTimerTask 
{
	long mDistributorConnectionId;
	private boolean mCanceled;
	
	public DistributorTimerTask( long pDistributorConnectionId ) {
		mDistributorConnectionId = pDistributorConnectionId;
		mCanceled = false;
	}


	
	abstract public void execute( DistributorConnection pDistributorConnection );

	public void cancel() {
		mCanceled = true;
	}

	public boolean isCanceled() { return mCanceled; }
}
