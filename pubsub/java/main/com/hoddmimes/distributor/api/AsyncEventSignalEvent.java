package com.hoddmimes.distributor.api;


import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorErrorEvent;

import com.hoddmimes.distributor.DistributorEvent;

public class AsyncEventSignalEvent implements AsyncEvent 
{
	DistributorEvent			mEvent;
	
	AsyncEventSignalEvent( DistributorEvent pEvent)
	{
		mEvent = pEvent;
	}
	
	
	@Override
	public void execute(DistributorConnection pConnection) 
	{
		if (pConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_ERROR_EVENTS)) {
		  if (mEvent instanceof DistributorErrorEvent) {
			 pConnection.log("APPLICATION ERROR EVENT Event: " + mEvent.toString() ); 
		  }
		}
		
		ClientDeliveryController.getInstance().queueEvent(pConnection.mConnectionId, mEvent);
	}

	@Override
	public String getTaskName() {
		return this.getClass().getSimpleName();
	}
	
	public String toString() {
		return  "[" + this.getClass().getSimpleName() + "] " + mEvent.toString();
	}

}
