package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * This event is passed back to connected publisher/subscribers when distributor connection is being closed. 
 * A distributor close is always triggered by the application invoking a specific distributor connection close.
 * Typically an application should never receive this event since publisher / connection should be closed before 
 * the distributor connection close is called.
 * <br>
 * This event / signal is fatal for the subcribers/publishers. The connection is no longer present.
 * and data will not be received and/or sent. 
 *  
 *  
 * @author POBE
 *
 */
public class DistributorConnectionClosedErrorEvent extends DistributorErrorEvent {

	private InetAddress mMcAddress;
	private int			mMcPort;

	/**
	 * Constructor for creating a <i>DistributorConnectionClosedErrorEvent</i>
	 * @param pMcAddress the IP multicast address used by the distributor connection
	 * @param pMcPort the UDP port used by the distributor connection
	 */
	public DistributorConnectionClosedErrorEvent(int pMcAddress, int pMcPort) {
		super(DistributorEventSignal.CONNECTION_CLOSING);
		mMcAddress = netAddressToInet4Address(pMcAddress);
		mMcPort = pMcPort;
	
		setMessage( "Distributor Connection Closing McAddress: " + mMcAddress.toString() + " McPort " + mMcPort );
	}
	
	/**
	 * return the IP multicast address used by the distributor connection
	 * @return IP multicast INET address
	 */
	public InetAddress getMcAddress() {
		return mMcAddress;
	}
	
	/**
	 * Returns the UDP port used by the distributor connection. 
	 * @return UDP port 
	 */
	public int getMcPort() {
		return mMcPort;
	}
}
