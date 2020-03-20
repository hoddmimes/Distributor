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
	 * <ul>
	 * <A HREF="DistributorCommunicationErrorEvent.html">DistributorCommunicationErrorEvent</A>
	 * <A HREF="DistributorConnectionClosedErrorEvent.html">DistributorConnectionClosedErrorEvent</A>
	 * <A HREF="DistributorNaggingErrorEvent.html">DistributorNaggingErrorEvent</A>
	 * <A HREF="DistributorNewRemoteConnectionEvent.html">DistributorNewRemoteConnectionEvent</A>
	 * <A HREF="DistributorRemoveRemoteConnectionEvent.html">DistributorRemoveRemoteConnectionEvent</A>
	 * <A HREF="DistributorRetransmissionNAKErrorEvent.html">DistributorRetransmissionNAKErrorEvent</A>
	 * <A HREF="DistributorTooManyRetransmissionRetriesErrorEvent.html">DistributorTooManyRetransmissionRetriesErrorEvent</A>
	 * <br>
	 * @param pDistributorEvent distributor application 
	 */
	public void distributorEventCallback(DistributorEvent pDistributorEvent);
}
