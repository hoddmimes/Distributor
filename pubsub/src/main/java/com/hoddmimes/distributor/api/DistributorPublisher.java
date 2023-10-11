package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.DistributorPublisherIf;
import com.hoddmimes.distributor.DistributorPublisherStatisticsIf;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;


public class DistributorPublisher implements DistributorPublisherIf {
	long mDistributorConnectionId;
	boolean mIsFloodRegulated;
	DistributorEventCallbackIf mEventCallback;
	long mId;
	int  mAppId;
	TrafficFlowClientContext mFlowContext;

	public DistributorPublisher( long pDistributorConnectionId,
								 int pAppId,
								 boolean pIsFloodRegulated,
								 DistributorEventCallbackIf pEventCallback)
	{
		mDistributorConnectionId = pDistributorConnectionId;
		mIsFloodRegulated = pIsFloodRegulated;
		mEventCallback = pEventCallback;
		mAppId = pAppId;
		mId = UUIDFactory.getId();
		
		if (mIsFloodRegulated) {
			mFlowContext = new TrafficFlowClientContext();
		} else {
			mFlowContext = null;
		}
	}


	@SuppressWarnings("static-access")
	public int publish(String pSubjectName, byte[] pData) throws DistributorException
	{
		XtaUpdate tXtaUpdate = null;
		DistributorConnection tConnection = null;
		int tSendDelay = 0;
		
		tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
		
		if (tConnection == null) {
			throw new DistributorException("Distributon connection is closed or no longer valid");
		}
		
		try {
			tConnection.checkStatus();
			tXtaUpdate = new XtaUpdate( pSubjectName, pData);
			tSendDelay = tConnection.publishUpdate(tXtaUpdate);
			
			if (mIsFloodRegulated) {
				mFlowContext.mBitsSentInInterval += (tXtaUpdate.getSize() * 8);
				tConnection.evalTrafficFlow( mFlowContext );
			}
		}
		finally {
			DistributorConnectionController.unlockDistributor(tConnection);
		}
		
		if ((mIsFloodRegulated) && (mFlowContext.mWaitTime > 0)) {
			try { Thread.currentThread().sleep(mFlowContext.mWaitTime); }
			catch( InterruptedException e) {};
		}
		return tSendDelay;
	}

	public void close() throws DistributorException 
	{
		DistributorConnection tConnection = null;
		
		tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
		
		if (tConnection == null) {
			throw new DistributorException("Distributon connection is closed or no longer valid");
		}
		
		try {
			tConnection.checkStatus();
			tConnection.removePublisher(this);
		}
		finally {
			DistributorConnectionController.unlockDistributor(tConnection);
		}
	}

	public long getId() {
		return mId;
	}

	@Override
	public DistributorPublisherStatisticsIf getStatistics() throws DistributorException {
		DistributorConnection tConnection = null;

		tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);

		if (tConnection == null) {
			throw new DistributorException("Distributon connection is closed or no longer valid");
		}

		try {
			tConnection.checkStatus();
			return tConnection.mTrafficStatisticsTask;
		}
		finally {
			DistributorConnectionController.unlockDistributor(tConnection);
		}
	}

}
