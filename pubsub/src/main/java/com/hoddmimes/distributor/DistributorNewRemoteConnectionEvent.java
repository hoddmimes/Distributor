package com.hoddmimes.distributor;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;

import java.net.InetAddress;
import java.text.SimpleDateFormat;

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
	
	private String 		mApplicationName;
	private int			mAppId;
	private int			mSenderId;
	private long		mSendStartTime;
	private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Constructor for creating a <i>DistributorNewRemoteConnectionEvent</i>
	 * @param pSourceAddress IP address of the node on which the distributor connection is created.
	 * @param pMcAddress distributor connection IP multicast InetAddress
	 * @param pMcPort distributor connection UDP port used.
	 * @param pApplicationName remote application name.
	 * @param pAppId distributed global App id
	 * @param pSenderId, remote sender id
	 * @param pSenderStartTime, time when the sender was started
	 */
	public DistributorNewRemoteConnectionEvent(int pSourceAddress, int pMcAddress,
										int pMcPort, String pApplicationName, int pAppId, int pSenderId, long pSenderStartTime )
	{
		super( DistributorEventSignal.REMOTE_CONNECTION_CREATED );
		mMcAddress = InetAddressConverter.intToInetAddr(pMcAddress);
		mMcPort = pMcPort;
		mRemoteAddress = InetAddressConverter.intToInetAddr(pSourceAddress);
		mApplicationName = pApplicationName;
		mSenderId = pSenderId;
		mSendStartTime =  pSenderStartTime;
		mAppId = pAppId;



		setMessage("New Remote Connection MCA-ADDR: " + mMcAddress.toString() + " MCA-Port: "
				+ mMcPort + " Remote Address: " + mRemoteAddress.toString() +
				" Application: " + pApplicationName + " App Id: " + mAppId +
				" sender id:" + mSenderId + " sender start: " + mSdf.format( mSendStartTime ));
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
	public int getMcPort() {
		return mMcPort;
	}

	/**
	 * Method returning the distributed app id for the remote publisher
	 * @return app id
	 */
	public int getAppId() {
		return mAppId;
	}
	/**
	 * Returns the application "name" creating the connection
	 * 
	 * @return String application "name".
	 */
	public String getRemoteApplicationName() {
		return mApplicationName;
	}

	/**
	 * Returns the remote id for the sender
	 * @return, remote sender id
	 */
	public int getRemoteSenderId() {
		return mSenderId;
	}

	/**
	 * Return time when the sender was started (omn the remote node)
	 * @return, time string "yyyy-MM-dd HH:mm.ss"
	 */
	public String getSenderStartTime() {
		return mSdf.format( mSendStartTime );
	}
}
