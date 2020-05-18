package com.hoddmimes.distributor;

/**
 * Subscriber call back interface. When creating a subscriber the application
 * must specify an callback that the distributor will invoke when data the
 * subscriber has an interest in are updated.
 * 
 * @author POBE
 * 
 */
public interface DistributorUpdateCallbackIf {
	/**
	 * Method invoked when an subject/topic has been updated.
	 * 
	 * @param pSubjectName subject being updated
	 * @param pPayload new data
	 * @param pCallbackParameter callback parameter associated with the subscription
	 * @param, pRemoteAppId, id of the remote application publishing the data
	 * @param pDeliveryQueueLength  number of messages in queue for being delivered to the  application.
	 */
	public void distributorUpdate(String pSubjectName,
								  byte[] pPayload,
								  Object pCallbackParameter,
								  int pRemoteAppId,
								  int pDeliveryQueueLength);
}
