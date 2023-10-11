package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.DistributorSubscriberIf;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;
import com.hoddmimes.distributor.auxillaries.UUIDFactory;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


class DistributorSubscriber implements DistributorSubscriberIf {
	long mDistributorConnectionId;
	Map<Object, SubscriptionHandleContext> mSubscriptions;
	DistributorUpdateCallbackIf mUpdateCallback;
	DistributorEventCallbackIf mEventCallback;


	long mId;

	DistributorSubscriber(DistributorConnection pConnection,
			DistributorEventCallbackIf pEventCallback,
			DistributorUpdateCallbackIf pUpdateCallback) throws DistributorException {
		mId = UUIDFactory.getId();
		mDistributorConnectionId = pConnection.mConnectionId;
		mEventCallback = pEventCallback;
		mUpdateCallback = pUpdateCallback;
		mSubscriptions = Collections.synchronizedMap(new HashMap<Object, SubscriptionHandleContext>());
	}

	public Object addSubscription(String pSubjectName, Object pCallbackParameter) throws DistributorException {
		
		DistributorConnection tConnection = null;
		
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			if (tConnection == null) {
				throw new DistributorException("Distributor connects is closed or no longer valid");
			}
			tConnection.checkStatus();
			Object tHandle = tConnection.addSubscription(this, pSubjectName, pCallbackParameter);
			mSubscriptions.put(tHandle, new SubscriptionHandleContext(pSubjectName, tHandle));
			return tHandle;
		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}		
	}

	public void removeSubscription(Object pHandle) throws DistributorException {
		
		DistributorConnection tConnection = null;
		
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			if (tConnection == null) {
				throw new DistributorException("Distributor connects is closed or no longer valid");
			}
			tConnection.checkStatus();
			SubscriptionHandleContext tCntx = mSubscriptions.remove(pHandle);
			if (tCntx != null) {
				tConnection.removeSubscription(pHandle, tCntx.mSubjectName);
			} else {
				tConnection.removeSubscription(pHandle, "<unknown>");
			}

		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}		
	}

	synchronized public void close()
	{
		DistributorConnection tConnection = null;
		
		try {
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorConnectionId);
			if (tConnection == null) {
				throw new DistributorException("Distributor connects is closed or no longer valid");
			}
			tConnection.checkStatus();
			Iterator<SubscriptionHandleContext> tItr = mSubscriptions.values().iterator();
			while (tItr.hasNext()) {
				SubscriptionHandleContext tCntx = tItr.next();
				tConnection.removeSubscription(tCntx.mHandle, tCntx.mSubjectName );
			}
			tConnection.removeSubscriber(this);
		}
		catch( DistributorException e) {
			System.out.println("Close Subscriber: " + e.getMessage());
		}
		finally {
			if (tConnection != null) {
				DistributorConnectionController.unlockDistributor(tConnection);
			}
		}
	}

	class SubscriptionHandleContext 
	{
		Object mHandle;
		String mSubjectName;
		
		SubscriptionHandleContext( String pSubjectName, Object pHandle ) {
			mHandle = pHandle;
			mSubjectName = pSubjectName;
		}
	}
	
	public long getId() {
		return mId;
	}
}
