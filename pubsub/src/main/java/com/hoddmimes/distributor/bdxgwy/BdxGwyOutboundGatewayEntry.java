package com.hoddmimes.distributor.bdxgwy;


import com.hoddmimes.distributor.generated.messages.DistBdxGwyHello;
import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterest;
import com.hoddmimes.distributor.generated.messages.DistBdxGwyUpdateItem;
import com.hoddmimes.distributor.messaging.MessageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handling the interaction with a remote bdx gateway from which we would like to 
 * subscribe/retreive information from. The message interaction is the following
 * 
 * DistBdxGwyHello (out), sent to identify ourself. Sent when connection is established
 * DistBdxGwySubscrInterest (out), sent initially to declare local interest. Also sent every time the local interest is updated.
 * DistBdxGwyPingRqst (out), sent periodically to keep the session alive 
 * DistBdxGwyUpdateItem (in), updates from remote broadcast gateway
 * DistBdxGwyPingRsp (in), responses from remote broadcast gateway for a previous sent DistBdxGwyPingRqst
 * @author bertilsson
 *
 */



public class BdxGwyOutboundGatewayEntry extends Thread implements BdxGwyOutboundTcpIpConnection.CallbackInterface
{
	private final static Logger cLogger = LogManager.getLogger( BdxGwyOutboundGatewayEntry.class.getSimpleName());
	private BdxGatewayParameterEntry mParameters;
	private BdxGatewayInterface mBdxGatewayInterface;
	private BdxGwyOutboundTcpIpConnection mConnection;
	/**
	 * Create ans start a BDX gateway instance. Declare the gateway as a server and connect up to remote 
	 * BDX gateways.
	 * 
	 * @param pParameters, list with addresses and ports to remote gateways where to act as a client
	 * @param pBdxGatewayInterface, 
	 */
	public BdxGwyOutboundGatewayEntry(BdxGatewayParameterEntry pParameters ,BdxGatewayInterface pBdxGatewayInterface) {
		mParameters = pParameters;
		mBdxGatewayInterface = pBdxGatewayInterface;
		mConnection = new BdxGwyOutboundTcpIpConnection( pParameters.getHostAddress(), pParameters.getHostPort(), pBdxGatewayInterface.getBroadcastGatewayName(), this);
	}

	/**
	 * Notify remote gateway about the local subscription interest 
	 * @param tInterest, BdxGwy interest message
	 */
	public void localClientSubscriptionInterest(DistBdxGwySubscrInterest tInterest) {
		mConnection.send(tInterest);
	}




	@Override
	public void connectionEstablished(BdxGwyOutboundTcpIpConnection pConnection) 
	{
		DistBdxGwyHello tHelloMessage = new DistBdxGwyHello();
		tHelloMessage.setBdxGwyName(mBdxGatewayInterface.getBroadcastGatewayName());
		mConnection.send(tHelloMessage);
		mConnection.send( mBdxGatewayInterface.getLocalActiveSubscriptions());
	}

	@Override
	public void connectionMessageRead(MessageInterface pMessage)
	{
		if (pMessage instanceof DistBdxGwyUpdateItem) {
			DistBdxGwyUpdateItem tUpd = (DistBdxGwyUpdateItem) pMessage;
			this.mBdxGatewayInterface.remoteBdxGatewayUpdate(tUpd.getMulticastGroupId(), tUpd.getSubject(), tUpd.getMessage());
			return;
		}		
	}

	@Override
	public void connectionTerminated(BdxGwyOutboundTcpIpConnection pConnection, Exception pException) {
		cLogger.warn("outbound connection to broadcast gateway: "
				+ mParameters.getHostAddress() + " port: "	+ mParameters.getHostPort() + " terminated, restablishing connection...");
		
	}


}
	
