package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * Distributor error event being signaled back to subscriber and publisher event callback routines.
 * In case an send or receive error occurs on the IP socket used by a distributor connection this event is 
 * sent back to the callback routines for the publisher and subscriber using the distributor connection.
 * <br>
 * This event / signal is fatal for the subcribers/publishers. The connection is no longer fully 
 * functional and data will not be received and/or sent. ion restart and recreation of publisher/subscribers may 
 * be possible. 
 * 
 * 
 * @author POBE
 * 
 */
public class DistributorCommunicationErrorEvent extends DistributorErrorEvent {

	private InetAddress mMcAddress;
	private int			mMcPort;

	/**
	 * Constructor routine for creating a <i>DistributorCommunicationErrorEvent</i>
	 * @param pDirectionString indicate whatever there is a "SEND" or "RECEIVE" error
	 * @param pMcAddress the IP multicast address used by the distributor connection
	 * @param pMcPort the UDP port used by the distributor connection
	 * @param pReason the exception reason message i.e. IOException reason.
	 */
	public DistributorCommunicationErrorEvent(String pDirectionString, InetAddress pMcAddress, int pMcPort, String pReason) {
		super(DistributorEventSignal.COMMUNICATION_FAILURE );
		mMcAddress = pMcAddress;
		mMcPort = pMcPort;
	
		setMessage( pDirectionString + " Connection communication error McAddress: " + mMcAddress.toString() + " McPort " + mMcPort +
				   " reason: " + pReason );
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
