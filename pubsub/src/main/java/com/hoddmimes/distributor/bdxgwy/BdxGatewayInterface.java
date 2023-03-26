package com.hoddmimes.distributor.bdxgwy;

import com.hoddmimes.distributor.Distributor;
import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterest;


public interface BdxGatewayInterface {
	/**
	 * Get reference to the distributor object used in the broadcast gateway
	 * @return handle to the local distributor object 
	 */
	public Distributor getDistributor();
	
	/**
	 * Returns the "name" id used by the broadcast gateway instance 
	 * @return
	 */
	public String getBroadcastGatewayName();
	
	/**
	 * Return all active subscriptions for a distributor clients being connected to 
	 * this broadcast gateway instance.
	 * @return, DistBdxGwySubscrInterest ore-built message with all active subscriptions  
	 */
	public DistBdxGwySubscrInterest getLocalActiveSubscriptions();

	/**
	 * Invoked when the broadcast gateway has received an local update on any of the 
	 * multicast groups that the gateway has connected to.
	 * 
	 * @param pMulticastGroupId, the id identifying the multicast group on which thhe update was received
	 * @param pSubjectName, the subject being updated
	 * @param pMessageBytes, the message/object in its serialized form
	 */
	public void localDistributorUpdate(long pMulticastGroupId, String pSubjectName, byte[] pMessageBytes);

	/**
	 * Invoked when receiving an update that the this gateway has subscribed to, from a remote broadcast gateway.
	 * @param pMulticastGroupId
	 * @param pSubjectName
	 * @param pMessageBytes
	 */
	public void remoteBdxGatewayUpdate(long pMulticastGroupId, String pSubjectName, byte[] pMessageBytes);
	

	/**
	 * Check what a remote bdx gateway connection is to be accepted or not.
	 * Only defined gateways are accepted.
	 * 
	 * @param pRemoteBdxGwyName, bdx gateway "name" of the gateway connecting
	 * @param pInboundIpAddress, ip address of the inbound remote bdx gateway
	 * @return true if the gateway is allowed to connect otherwise false.
	 */
	public boolean validInboundClient( String pRemoteBdxGwyName, String pInboundIpAddress);

	/**
	 * Return the Distribution Gateway application configuration when acting as a local distributor
	 * @return the app configuration when acting local publisher.
	 */
	public DistributorApplicationConfiguration getApplicationConfiguration();
}
