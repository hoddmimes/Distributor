package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * An event being signaled to the publisher/subscriber when a lost
 * message could not be recovered i.e. maximum number of retransmission
 * requests has been exceeded.
 * <br>
 * This event / signal is fatal for the subcribers/publishers. The connection is no longer fully 
 * functional and data will not be received and/or sent. ion restart and recreation of publisher/subscribers may 
 * be possible. 
 * 
 * 
 * @author POBE
 * 
 */
public class DistributorTooManyRetransmissionRetriesErrorEvent extends DistributorErrorEvent 
{
	private InetAddress mMcAddress;
	private int			mMcPort;
	
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for creating a <i>DistributorTooManyRetransmissionRetriesErrorEvent</i>
	 * @param pMcAddress the IP multicast address used by the distributor connection
	 * @param pMcPort the UDP port used by the distributor connection
	 */
	public DistributorTooManyRetransmissionRetriesErrorEvent(InetAddress pMcAddress, int pMcPort) {
		super(DistributorEventSignal.TOO_MANY_RETRIES);
		mMcAddress = pMcAddress;
		mMcPort = pMcPort;
		
		setMessage( "Distributor Connection, failed to recover lost message. Too many recovery retries \n McAddress: " 
				+ mMcAddress.toString() + " McPort " + mMcPort );
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
