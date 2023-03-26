package com.hoddmimes.distributor;

import java.net.InetAddress;


/**
 * An error event being signaled to the publisher/subscriber when a missed
 * message could not be recovered i.e. the distributor from where the missed
 * message originates does not have the message in its retransmission cache i.e.
 * has expired and been removed.
 * <br>
 * This event / signal is fatal for the subcribers/publishers. The connection is no longer fully 
 * functional and data will not be received and/or sent. ion restart and recreation of publisher/subscribers may 
 * be possible. 
 * 
 * @author POBE
 * 
 */
public class DistributorRetransmissionNAKErrorEvent extends DistributorErrorEvent 
{
	private InetAddress mMcAddress;
	private int			mMcPort;

	/**
	 * Constructor for creating a <i>DistributorRetransmissionNAKErrorEvent</i>
	 * @param pMcAddress the IP multicast address used by the distributor connection
	 * @param pMcPort the UDP port used by the distributor connection
	 */
	public DistributorRetransmissionNAKErrorEvent(InetAddress pMcAddress, int pMcPort) {
		super(DistributorEventSignal.CONNECTION_CLOSING);
		mMcAddress = pMcAddress;
		mMcPort = pMcPort;
		
		setMessage( "Distributor Connection, failed to recover lost message. Message not in remote cache \n McAddress: " 
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
