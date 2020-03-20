package com.hoddmimes.distributor.bdxgwy;

import java.util.HashMap;
import java.util.Map;

import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.api.DistributorSubscriptionFilter;
import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterestItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BdxGwySubscriptionFilter 
{
	private static final Logger cLogger = LogManager.getLogger( BdxGwySubscriptionFilter.class.getSimpleName());

	private DistributorSubscriptionFilter mFilter;
	/**
	 * The mCrossMap maps between remote subscription handlers and local
	 * subscription handlers a remote subscription handle is
	 * "multicastgroupid" + "handle" . The local representation is the local
	 * handle
	 */
	private Map<String, Object> mCrossMap;

	BdxGwySubscriptionFilter() {
		mFilter = new DistributorSubscriptionFilter();
		mCrossMap = new HashMap<String, Object>();
	}

	void addSubscription(DistBdxGwySubscrInterestItem pInterest) {
		Object tLocalHandle = null;
		try {
			tLocalHandle = mFilter.add(pInterest.getSubject(), null, null);
			mCrossMap.put(String.valueOf(pInterest.getMulticastGroupId()) + ":" + pInterest.getHandler(), tLocalHandle);
		} catch (DistributorException e) {
			cLogger.error(e);
		}
	}

	void removeSubscription(DistBdxGwySubscrInterestItem pInterest) {
		Object tLocalHandle = mCrossMap.get(String.valueOf(pInterest.getMulticastGroupId()) + ":" + pInterest.getHandler());
		if (tLocalHandle != null) {
			mFilter.remove(tLocalHandle);
		}
	}

	boolean matchAny(String pSubject) {
		return mFilter.matchAny(pSubject);
	}
}
