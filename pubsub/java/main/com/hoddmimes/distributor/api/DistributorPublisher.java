package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.DistributorPublisherIf;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;


public class DistributorPublisher implements DistributorPublisherIf {
	long mDistributorConnectionId;
	boolean mIsFloodRegulated;
	DistributorEventCallbackIf mEventCallback;
	long mId;
	TrafficFlowClientContext mFlowContext;

	public DistributorPublisher( long pDistributorConnectionId,  
								 boolean pIsFloodRegulated,
								 DistributorEventCallbackIf pEventCallback)
	{
		mDistributorConnectionId = pDistributorConnectionId;
		mIsFloodRegulated = pIsFloodRegulated;
		mEventCallback = pEventCallback;
		mId = UUIDFactory.getId();
		
		if (mIsFloodRegulated) {
			mFlowContext = new TrafficFlowClientContext();
		} else {
			mFlowContext = null;
		}
		
		if (pEventCallback != null) {
			ClientDeliveryController.getInstance().addEventListener(mDistributorConnectionId, pEventCallback);
		}	
	}

	public int publish(String pSubjectName, byte[] pData) throws DistributorException
	{
		 if (pData == null) {
			 return publish( pSubjectName, pData, 0);
		 }
		 return publish( pSubjectName, pData, pData.length);
	}


	@SuppressWarnings("static-access")
	public int publish(String pSubjectName, byte[] pData, int pLength) throws DistributorException
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
			tXtaUpdate = new XtaUpdate( pSubjectName, pData, pLength, false);
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
	public double getUpdatesPerMessage() {
		DistributorConnection tConnection = null;
		try {
		   tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			return  (tConnection != null) ? tConnection.getUpdatesPerMessage() : 0;
		}
		finally {
			DistributorConnectionController.unlockDistributor(tConnection);
		}
	}

	@Override
	public double getBufferFillRate() {
		DistributorConnection tConnection = null;
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			return  (tConnection != null) ? tConnection.getPackageFillRate() : 0;
		}
		finally {
				DistributorConnectionController.unlockDistributor(tConnection);
		}
	}

	@Override
	public int getAvgXtaTime() {
		DistributorConnection tConnection = null;
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			return  (tConnection != null) ? (int) tConnection.getAvgXtaTime(): 0;
		}
		finally {
			DistributorConnectionController.unlockDistributor(tConnection);
		}
	}
}
