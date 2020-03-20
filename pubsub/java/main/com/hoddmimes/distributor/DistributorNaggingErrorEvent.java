package com.hoddmimes.distributor;



import java.net.InetAddress;


/**
 * An error event being signaled when the connection can not keep up with
 * remote connection and constantly generates retransmissions i.e. is "nagging"
 * 
 * @author POBE
 * 
 */
public class DistributorNaggingErrorEvent extends DistributorErrorEvent 
{
	private InetAddress	mMcAddress;
	private int			mMcPort;
	
	
	/**
	 * Constructor for creating a <i>DistributorNaggingErrorEvent</i>
	 * @param pMcAddress distributor connection IP multicast InetAddress
	 * @param pMcPort distributor UDP port used
	 */
	public DistributorNaggingErrorEvent(InetAddress pMcAddress, int pMcPort) {
		super(DistributorEvent.DistributorEventSignal.NAGGING_EXCEPTION);
		mMcPort = pMcPort;
		mMcAddress = pMcAddress;

		setMessage( "This connection McAddress: " + mMcAddress.toString() + " McPort " + mMcPort + " is nagging!" ); 
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
