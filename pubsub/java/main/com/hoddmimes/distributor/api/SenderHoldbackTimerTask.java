package com.hoddmimes.distributor.api;

public class SenderHoldbackTimerTask extends DistributorTimerTask 
{
	long				mTimerFlushSeqno;
	
	
	SenderHoldbackTimerTask(long pDistributorConnectionId, long pTimerFlushSeqno) {
		super(pDistributorConnectionId);
		mTimerFlushSeqno = pTimerFlushSeqno;
	}

	@Override
	public void execute(DistributorConnection pDistributorConnection) 
	{
		pDistributorConnection.flushHoldback( mTimerFlushSeqno );
	}

}
