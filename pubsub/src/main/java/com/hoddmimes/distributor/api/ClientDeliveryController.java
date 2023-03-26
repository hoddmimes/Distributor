package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorEvent;
import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.generated.messages.QueueSizeItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;


public class ClientDeliveryController extends Thread  
{
	private static final SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private static ClientDeliveryController cInstance = null;

	LinkedList<SubscriptionFiltersCntx> 	mSubscriptionFiltersList;
	LinkedList<EventCallbackCntx> 			mEventCallbackListeners;
	LinkedBlockingDeque<ClientEvent>        mQueue;
	AtomicInteger							mEventQueueLength;
	int 									mPeakSize;
	String 									mPeakTime;
	
	private ClientDeliveryController() 
	{
		mEventQueueLength = new AtomicInteger(0);
		mSubscriptionFiltersList = new LinkedList<SubscriptionFiltersCntx>();
		mEventCallbackListeners = new LinkedList<EventCallbackCntx>();
		mQueue = new LinkedBlockingDeque<ClientEvent>();
		mPeakSize = 0;
		mPeakTime = "00:00:00.000";
		start();
	}
	
	public static  ClientDeliveryController getInstance() {
		if (cInstance == null) {
			cInstance = new ClientDeliveryController();
		}
		return cInstance;
	}
	
	public void addSubscriptionFilter( long pDistributorConnectionId, DistributorSubscriptionFilter pFilter ) 
	{
		synchronized( this ) 
		{
		  mSubscriptionFiltersList.add( new SubscriptionFiltersCntx(pDistributorConnectionId, pFilter));
		}
	}
	
	public void removeSubscriptionFilter( long pDistributorConnectionId, DistributorSubscriptionFilter pFilter )
	{
		synchronized( this ) 
		{
			Iterator<SubscriptionFiltersCntx> tItr = mSubscriptionFiltersList.iterator();
			while( tItr.hasNext() ) {
				SubscriptionFiltersCntx tCntx = tItr.next();
				if ((tCntx.mSubscriptionFilters == pFilter) && (tCntx.mDistributorConnectionId == pDistributorConnectionId)) {
					tItr.remove();
					return;
				}
			}
		}		
	}
	
	public void addEventListener( long pDistributorConnectionId, DistributorEventCallbackIf pCallbackIf ) 
	{
		synchronized( this ) 
		{
			EventCallbackCntx tCallbackCntx = new EventCallbackCntx(pDistributorConnectionId, pCallbackIf);
		    mEventCallbackListeners.add( tCallbackCntx );
		}
	}	
	
	
	public void removeEventListener(  long pDistributorConnectionId, DistributorEventCallbackIf pCallbackIf )
	{
		synchronized( this ) 
		{
			Iterator<EventCallbackCntx> tItr = mEventCallbackListeners.iterator();
			while( tItr.hasNext() ) {
				EventCallbackCntx tCntx = tItr.next();
				if ((tCntx.mEventCallbackInterface == pCallbackIf) && (tCntx.mDistributorConnectionId == pDistributorConnectionId)) {
					tItr.remove();
					return;
				}
			}
		}
	}
	
	void queueUpdate( long  pDistributorConnectionId, List<RcvUpdate> pRcvUpdateList ) 
	{
		mEventQueueLength.addAndGet(pRcvUpdateList.size());
		if (mEventQueueLength.get() > mPeakSize) {
			mPeakSize = mEventQueueLength.get();
			mPeakTime = cSDF.format(System.currentTimeMillis());
		}
		mQueue.add( new ClientUpdateEvent(pDistributorConnectionId, pRcvUpdateList));
	}
	
	void queueUpdate( long pDistributorConnectionId, RcvUpdate pRcvUpdate ) {
		mEventQueueLength.incrementAndGet();
		if (mEventQueueLength.get() > mPeakSize) {
			mPeakSize = mEventQueueLength.get();
			mPeakTime = cSDF.format(System.currentTimeMillis());
		}
		mQueue.add( new ClientUpdateEvent(pDistributorConnectionId, pRcvUpdate));
	}

	void queueEvent( long pDistributorConnectionId, DistributorEvent pEvent, DistributorEventCallbackIf pCallback ) {
		mQueue.add( new ClientDedicatedAppEvent(pDistributorConnectionId, pEvent, pCallback));
	}
	
	void queueEvent( long pDistributorConnectionId, DistributorEvent pEvent ) 
	{
		mEventQueueLength.incrementAndGet();
		if (mEventQueueLength.get() > mPeakSize) {
			mPeakSize = mEventQueueLength.get();
			mPeakTime = cSDF.format(System.currentTimeMillis());
		}
		mQueue.add( new ClientAppEvent(pDistributorConnectionId, pEvent));
	}

	int	getQueueLength()
	{
		return mEventQueueLength.get();
	}
	
	QueueSizeItem getQueueSize()
	{
		QueueSizeItem tItem = new QueueSizeItem();
		tItem.setPeakSize(mPeakSize);
		tItem.setPeakTime(mPeakTime);
		tItem.setSize(mEventQueueLength.get());
		return tItem;
	}
	
	
	private DistributorSubscriptionFilter getSubscriptionFilter( long pDistributorConnectionId ) 
	{
		Iterator<SubscriptionFiltersCntx> tItr = mSubscriptionFiltersList.iterator();
		while( tItr.hasNext() ) {
			SubscriptionFiltersCntx tCntx = tItr.next();
			if (tCntx.mDistributorConnectionId == pDistributorConnectionId) {
				return tCntx.mSubscriptionFilters;
			}
		}
		return null;
	}
	
	private void processEvent( ClientEvent pClientEvent ) 
	{
		if (pClientEvent.mEventType == ClientEvent.UPDATE)
		{
			synchronized( this )
			{
				DistributorSubscriptionFilter tFilter = getSubscriptionFilter( pClientEvent.mDistributorConnectionId );
				ClientUpdateEvent tUpdateEvent = (ClientUpdateEvent) pClientEvent;
				if (tFilter != null) {
				   if (tUpdateEvent.mRcvUpdate != null) {
					 tFilter.match( tUpdateEvent.mRcvUpdate.getSubjectName(), tUpdateEvent.mRcvUpdate.getData(),
							        tUpdateEvent.mRcvUpdate.getAppId(), mEventQueueLength.get() - 1);
				   } else if (tUpdateEvent.mRcvUpdateList != null) {
					   Iterator<RcvUpdate> tItr = tUpdateEvent.mRcvUpdateList.iterator();
					   int tCount = 0;
					   while(tItr.hasNext()) 
					   {
						   RcvUpdate tRcvUpd = tItr.next();
						   tFilter.match( tRcvUpd.getSubjectName(), tRcvUpd.getData(),  tRcvUpd.getAppId(), (mEventQueueLength.get() - (++tCount)));
					   }
				   }
			    }
			}
		}

		// If the an application event notify all registetered listeners
		if (pClientEvent.mEventType == ClientEvent.APPEVENT)
		{
			ClientAppEvent tAppEvent = (ClientAppEvent) pClientEvent;
			synchronized( this )
			{
				Iterator<EventCallbackCntx> tItr = mEventCallbackListeners.iterator();
				while( tItr.hasNext()) {
					EventCallbackCntx tEventCntx = tItr.next();
					if (tEventCntx.mDistributorConnectionId == tAppEvent.mDistributorConnectionId) {
					  if (tEventCntx.mEventCallbackInterface != null) {
						tEventCntx.mEventCallbackInterface.distributorEventCallback( tAppEvent.mEvent );
					  }
					}
				}
			}
		}

		if (pClientEvent.mEventType == ClientEvent.DEDICATED_APPEVENT) {
			ClientDedicatedAppEvent tAppEvent = (ClientDedicatedAppEvent) pClientEvent;
			tAppEvent.mEventCallbackIf.distributorEventCallback( tAppEvent.mEvent );
		}
		
		mEventQueueLength.addAndGet(-pClientEvent.getEventElements());
	}
	
	
	
	public void run() 
	{
		ArrayList<ClientEvent> tEventList = new ArrayList<ClientEvent>(30);
		ClientEvent tEvent;

		setName("ClientDeliveryThread");
		while( true ) {
			tEvent = null;
			try { tEvent = mQueue.take(); }
			catch( InterruptedException e) {}

			if (tEvent != null) {
				processEvent(tEvent);
			}
			
			if (mQueue.size() > 0) {
				tEventList.clear();
				mQueue.drainTo(tEventList, 30);
				for( int i = 0; i < tEventList.size(); i++) {
					processEvent(tEventList.get(i));
				}
			}
		}
	}	

	
	abstract class ClientEvent
	{
		public static final int UPDATE = 1;
		public static final int APPEVENT = 2;
		public static final int DEDICATED_APPEVENT = 3;
	
		long				mDistributorConnectionId;
		int					mEventType;

		ClientEvent( int pType, long pDistributorConnectionId ) {
			mEventType = pType;
			mDistributorConnectionId = pDistributorConnectionId;
		}
	
		abstract int getEventElements();
	};
	
	class ClientUpdateEvent extends ClientEvent 
	{
		ArrayList<RcvUpdate> 	mRcvUpdateList;
		RcvUpdate				mRcvUpdate;
		
		ClientUpdateEvent( long pDistributorConnectionId, List<RcvUpdate> pRcvUpdateList )
		{
			super(ClientEvent.UPDATE, pDistributorConnectionId);
			mRcvUpdateList = new ArrayList<RcvUpdate>(pRcvUpdateList.size());
			mRcvUpdateList.addAll(pRcvUpdateList);
			mRcvUpdate = null;
		}
		
		ClientUpdateEvent( long pDistributorConnectionId, RcvUpdate pRcvUpdate ) 
		{
			super(ClientEvent.UPDATE, pDistributorConnectionId);
			mRcvUpdateList = null;
			mRcvUpdate = pRcvUpdate;
		}

		int getEventElements() {
			if (mRcvUpdateList != null) {
				return mRcvUpdateList.size();
			}
			return 1;
		}
	}
	
	class ClientAppEvent extends ClientEvent
	{
		DistributorEvent mEvent;

		ClientAppEvent( long pDistributorConnectionId, DistributorEvent pEvent )
		{
			super( ClientEvent.APPEVENT, pDistributorConnectionId);
			mEvent = pEvent;
		}

		int getEventElements() {
			return 1;
		}
	}

	class ClientDedicatedAppEvent extends ClientEvent
	{
		DistributorEvent 				mEvent;
		DistributorEventCallbackIf		mEventCallbackIf;

		ClientDedicatedAppEvent( long pDistributorConnectionId, DistributorEvent pEvent, DistributorEventCallbackIf pCallback )
		{
			super( ClientEvent.DEDICATED_APPEVENT, pDistributorConnectionId);
			mEvent = pEvent;
			mEventCallbackIf = pCallback;
		}

		int getEventElements() {
			return 1;
		}
	}
	
	class SubscriptionFiltersCntx
	{
		long							mDistributorConnectionId;
		DistributorSubscriptionFilter	mSubscriptionFilters;

		SubscriptionFiltersCntx( long pDistributorConnectionId, DistributorSubscriptionFilter pFilter ) 
		{
			mDistributorConnectionId = pDistributorConnectionId;
			mSubscriptionFilters = pFilter;
		}
	}

	class EventCallbackCntx
	{
		long						mDistributorConnectionId;
		DistributorEventCallbackIf	mEventCallbackInterface;
		
		EventCallbackCntx(  long pDistributorConnectionId, DistributorEventCallbackIf pEventCallbackInterface ) {
			mDistributorConnectionId = pDistributorConnectionId;
			mEventCallbackInterface = pEventCallbackInterface;
		}
	}
	
}
