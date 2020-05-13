package com.hoddmimes.distributor;

/**
 * A DistributorConnectionIf is an interface wrapping an UDP multicast transport
 * i.e. an UDP socket. Over the <i>connections</i> application can publish i.e.
 * send out data in a true one-to-many fashion, to one or more subscribers
 * receiving the data.
 * 
 * @author POBE
 * 
 */
public interface DistributorConnectionIf 
{

	/**
	 * Close and remove the connection.
 	 *	Local subscribers and publishers still being open and
	 * associated with the connection will receive 
	 * <A HREF="DistributorConnectionClosedErrorEvent.html">DistributorConnectionClosedErrorEvent.html</A>
	 * through the event callback routine about the connection being closed. Remote
	 * subscribers and publishers being associated to the same UDP multicast
	 * address / port will receive an event telling that connection for the
	 * particular application/host has closed down i.e.
	 * <a HREF="DistributorRemoveRemoteConnectionEvent.html">DistributorRemoveRemoteConnectionEvent.html</a>
	 * event.
	 * 
	 */
	public void close();

	/**
	 * Returns the multicast (UDP) address being used when sending and receiving
	 * data.
	 * 
	 * @return String multicast (UDP) address
	 */
	public String getMcAddress();

	/**
	 * Returns the multicast (UDP) port being used when sending and receiving
	 * data.
	 * 
	 * @return int multicast (UDP) port
	 */
	public int getMcPort();

	/**
	 * Returns an unique identifier for the connection, application and host.
	 * The connection is volatile and will be renewed every time the connection
	 * is created.
	 * 
	 * @return long connection identifier.
	 */
	public long getConnectionId();

	/**
	 * Returns the connection is string which is the multicast (UDP) address &lt;&lt; 32 + multicast (UDP) port
	 * 
	 * @return connection id string
	 */
	public long getMcaConnectionId();


	/**
	 * Return the average fill rate on in percentage of the UDP packages sent.
	 */
	public double getPackageFillRate();

	/**
	 * Returns the average number of updates sent in an UDP package
	 */
	public double getUpdatesPerMessage();


}
