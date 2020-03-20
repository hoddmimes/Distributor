package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * Event to be passed to publishers/ subscribers when a distributor connection is created on a remote host
 * This event is just of informational nature.
 * @author POBE
 *
 */

public class DistributorNewRemoteConnectionEvent extends DistributorEvent 
{
	private InetAddress mMcAddress;
	private int			mMcPort;
	
	private InetAddress mRemoteAddress;
	private int		    mRemotePort;
	
	private String 		mApplicationName;
	

	/**
	 * Constructor for creating a <i>DistributorNewRemoteConnectionEvent</i>
	 * @param pSourceAddress IP address of the node on which the distributor connection is created.
	 * @param pSourcePort the sender id port used on the remote host 
	 * @param pMcAddress distributor connection IP multicast InetAddress
	 * @param pMcPort distributor connection UDP port used.
	 * @param pApplicationName remote application name.
	 */
	public DistributorNewRemoteConnectionEvent(int pSourceAddress, int pSourcePort, int pMcAddress, 
										int pMcPort, String pApplicationName) 
	{
		super( DistributorEventSignal.REMOTE_CONNECTION_CREATED );
		mMcAddress = netAddressToInet4Address(pMcAddress);
		mMcPort = pMcPort;
		mRemoteAddress = netAddressToInet4Address(pSourceAddress);
		mRemotePort = pSourcePort;
		mApplicationName = pApplicationName;
				
		setMessage("New Remote Connection MCA-ADDR: " + mMcAddress.toString() + " MCA-Port: "
				+ mMcPort + " Remote Address: " + mRemoteAddress.toString()
				+ " Source Port: " + mRemotePort + " Application: " + pApplicationName);
	}

	/**
	 * Returns the remote host address
	 * 
	 * @return String host address
	 */
	public InetAddress getRemoteAddress() {
		return mRemoteAddress;
	}

	/**
	 * Returns the source address
	 * 
	 * @return int remote source port being used.
	 */
	public int getremotePort() {
		return mRemotePort;
	}

	/**
	 * Return IP multicast inet address being used
	 * 
	 * @return String UDP address
	 */
	public InetAddress getMcAddress() {
		return mMcAddress;
	}

	/**
	 * Returns the UDP port being used
	 * 
	 * @return int UDP port
	 */
	int getMcPort() {
		return mMcPort;
	}

	/**
	 * Returns the application "name" creating the connection
	 * 
	 * @return String application "name".
	 */
	public String getRemoteApplicationName() {
		return mApplicationName;
	}

}
