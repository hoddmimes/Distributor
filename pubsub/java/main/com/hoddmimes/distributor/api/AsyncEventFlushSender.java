package com.hoddmimes.distributor.api;

public class AsyncEventFlushSender implements AsyncEvent 
{
	long 		 	 mCurrentFlushSeqno;
	
	AsyncEventFlushSender( long pCurrentFlushSeqno)
	{
		mCurrentFlushSeqno = pCurrentFlushSeqno;
	}


	@Override
	public void execute( DistributorConnection pConnection) {
		pConnection.mConnectionSender.flushHoldback(mCurrentFlushSeqno);
	}

	@Override
	public String getTaskName() {
		return this.getClass().getSimpleName();
	}
	
	public String toString() {
		return  "[" + this.getClass().getSimpleName() + "] flush-seqno: " + mCurrentFlushSeqno;
	}

}
