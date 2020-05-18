package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * An event indicating that a remote application has closed down a
 * distributor connection.
 * 
 * @author POBE
 */
public class DistributorRemoveRemoteConnectionEvent extends DistributorEvent{

	private InetAddress mRemoteAddress;
	private int 		mSenderId;
	private InetAddress mMcAddress;
	private int 		mMcPort;
	private String 		mApplicationName;
	private int			mAppId;

	/**
	 * Constructor for creating a <i>DistributorRemoveRemoteConnectionEvent</i>
	 * @param pRemoteAddress IP address of the node on which the distributor connection is created.
	 * @param pSenderId the sender id used on the remote host 
	 * @param pMcAddress distributor connection IP multicast InetAddress
	 * @param pMcaPort distributor connection UDP port used.
	 * @param pApplicationName remote application name.
	 * @param pAppId remote pubslisher app id
	 */
	public DistributorRemoveRemoteConnectionEvent(InetAddress pRemoteAddress,
			int pSenderId, InetAddress pMcAddress, int pMcaPort, String pApplicationName, int pAppId)
	{
		super( DistributorEventSignal.REMOTE_CONNECTION_REMOVED );
		mRemoteAddress = pRemoteAddress;
		mMcAddress = pMcAddress;
		mMcPort = pMcaPort;
		mSenderId = pSenderId;
		mAppId = pAppId;
		mApplicationName = pApplicationName;
		
		setMessage("Remote Connection Disconnected McAddress: " + mMcAddress.toString() 
				+ " McPort: " + mMcPort + " Remote Address: " + mRemoteAddress.toString()
				+ " SenderId: " + Integer.toHexString(mSenderId ) 
				+ " Application: " + pApplicationName
				+ " App Id: " + pAppId );
	}

	public int getSenderId() {
		return mSenderId;
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
	 * Return UDP address being used
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
	 * Returns the application "name" closing the connection
	 * 
	 * @return String application "name".
	 */
	public String getRemoteApplicationName() {
		return mApplicationName;
	}

	/**
	 * Method returning the remote (global) application id existing
	 * @return, remote application is
	 */
	public int getAppId() { return mAppId; };
}
