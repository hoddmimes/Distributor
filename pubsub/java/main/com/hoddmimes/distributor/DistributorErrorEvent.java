package com.hoddmimes.distributor;

/**
 * This class serve is an abstract class and server as base for all application events 
 * being of fatal nature. All events inheriting from this class indicate that the event is an error 
 * event and that the error is fatal.
 * 
 * @author POBE
 *
 */
public abstract class DistributorErrorEvent extends DistributorEvent 
{

	/**
	 * Constructor for creating a <i>DistributorErrorEvent</i>
	 * @param pSignal, text string specifying the error type and reason 
	 */
	DistributorErrorEvent(DistributorEventSignal pSignal) {
		super(pSignal);
	}

}
