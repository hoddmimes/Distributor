package com.hoddmimes.distributor;

/**
 * Callback interface through which the distributor will pass unsolicited events
 * back to subscribers and publishers
 * 
 * @author POBE
 * 
 */

public interface DistributorEventCallbackIf {
	/**
	 * Callback method being called when the distributor has an event to pass to
	 * the subscriber / publisher. The following events can be passed back to the application:
	 * <br>
	 * <ul>
	 * <a HREF="DistributorCommunicationErrorEvent.html">DistributorCommunicationErrorEvent</a>
	 * <a HREF="DistributorConnectionClosedErrorEvent.html">DistributorConnectionClosedErrorEvent</a>
	 * <a HREF="DistributorNaggingErrorEvent.html">DistributorNaggingErrorEvent </a>
	 * <a HREF="DistributorNewRemoteConnectionEvent.html">DistributorNewRemoteConnectionEvent</a>
	 * <a HREF="DistributorRemoveRemoteConnectionEvent.html">DistributorRemoveRemoteConnectionEvent</a>
	 * <a HREF="DistributorRetransmissionNAKErrorEvent.html">DistributorRetransmissionNAKErrorEvent</a>
	 * <a HREF="DistributorTooManyRetransmissionRetriesErrorEvent.html">DistributorTooManyRetransmissionRetriesErrorEvent</a>
	 * </ul>
	 * <br>
	 * @param pDistributorEvent distributor application 
	 */
	public void distributorEventCallback(DistributorEvent pDistributorEvent);
}
