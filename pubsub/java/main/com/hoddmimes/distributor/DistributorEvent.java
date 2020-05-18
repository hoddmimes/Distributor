package com.hoddmimes.distributor;

import com.hoddmimes.distributor.auxillaries.NumberConvert;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Base class for all application events that could be passed to subscriber / publisher event routines.
 * 
 * @author POBE
 *
 */
abstract public class DistributorEvent 
{
	public enum DistributorEventSignal { NAGGING_EXCEPTION, REMOTE_CONNECTION_CREATED, REMOTE_CONNECTION_REMOVED,
		   RETRANSMISSION_NAK, TOO_MANY_RETRIES, COMMUNICATION_FAILURE, CONNECTION_CLOSING };

	private DistributorEventSignal mSignal;
	private String mMessage;
	
	/**
	 * Constructor for creating a <i>DistributorEvent</i>
	 * @param pSignal, text string specifying the type of event event message 
	 */
	DistributorEvent( DistributorEventSignal pSignal ) {
		mSignal = pSignal;
		mMessage = "not set";
	}
	
	/**
	 * Routine for setting the type of event event message  specifically
	 * @param pMessage
	 */
	void setMessage( String pMessage ) {
		mMessage = pMessage;
	}
	
	/**
	 * Returns the text message reveling the nature and information of the event.
	 * @return event message string
	 */
	public String toString() {
		return mMessage;
	}
	
	
	/**
	 * Return the event type
	 * @return, event type
	 */
	DistributorEventSignal getEventType() {
		return mSignal;
	}	

}
