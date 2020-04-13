package com.hoddmimes.distributor.bdxgwy;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.generated.messages.*;
import com.hoddmimes.distributor.messaging.MessageFactoryInterface;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;



/**
 * This class handles the interaction from inbound broadcast gateways.
 * The message interaction is the following 
 * 
 * DistBdxGwyHello (in), received as first message to identify remote bdx gateway.
 * DistBdxGwySubscrInterest (in), received initially to declare local interest. Also sent every time the remote interest is updated.
 * DistBdxGwyPingRqst (in), sent periodically to keep the session alive 
 * DistBdxGwyUpdateItem (out), updates received on the local LAN sent to remote bdx gateway
 * DistBdxGwyPingRsp (out), responses to remote broadcast gateway for a previous received DistBdxGwyPingRqst
 *
 * @author bertilsson
 *
 */


public class BdxGwyInboundGatewayEntry 
{
	private static final Logger cLogger = LogManager.getLogger( BdxGwyInboundGatewayEntry.class.getSimpleName());
	private BdxGatewayInterface mBdxGwyInterface;
	private BdxGwyInboundTcpIpConnection mConnection;
	private BdxGwySubscriptionFilter mSubscriptionFilter;
	private MessageFactoryInterface mMessageFactory;
	
	
	BdxGwyInboundGatewayEntry(TcpIpConnection pTcpIpConnection, BdxGatewayInterface pBdxGwyInterface) {
		mSubscriptionFilter = new BdxGwySubscriptionFilter();
		mConnection = new BdxGwyInboundTcpIpConnection( pTcpIpConnection );
		mMessageFactory = new DistributorMessagesFactory();
		mBdxGwyInterface = pBdxGwyInterface;
	}
	
	public void updateToRemoteBroadcastGateway(String pSubject, byte[] pMessageBytes, long pMulticastGroupId) {
		if (mSubscriptionFilter.matchAny(pSubject)) 
		{
			DistBdxGwyUpdateItem tUpdate = new DistBdxGwyUpdateItem();
			tUpdate.setMessage(pMessageBytes);
			tUpdate.setMulticastGroupId(pMulticastGroupId);
			tUpdate.setSubject(pSubject);
			mConnection.send(tUpdate);
		} 
	}
	
	public void close(Exception pException) 
	{
		mConnection.close(pException);
	}
	
	
	private void processDistBdxGwyHello( DistBdxGwyHello pMessage, String pIpAddress )
	{
		if (!mBdxGwyInterface.validInboundClient(pMessage.getBdxGwyName(), pIpAddress)) {
			mConnection.close(new Exception("connections from broadcast gateway \"" + pMessage.getBdxGwyName() + "\" are not allowed" ));
		}
	}
	
	private void processDistBdxGwySubscrInterest(DistBdxGwySubscrInterest pMessage)
	{
		List<DistBdxGwySubscrInterestItem> tIterests = pMessage.getInterests();
		if (tIterests != null) {
			for (int i = 0; i < tIterests.size(); i++) {
				DistBdxGwySubscrInterestItem tIterest = tIterests.get(i);
				if (pMessage.getAction() == DistBdxGwySubscrInterest.ADD_INTEREST) {
					if (mBdxGwyInterface.getApplicationConfiguration().isLogFlagEnabled(DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS)) {
						cLogger.info( mConnection.getName() + " added subscription " + tIterest.getSubject() );
					}
					mSubscriptionFilter.addSubscription(tIterest);
				} else {
					if (mBdxGwyInterface.getApplicationConfiguration().isLogFlagEnabled(DistributorApplicationConfiguration.LOG_SUBSCRIPTION_EVENTS)) {
						cLogger.info( mConnection.getName() + " remove subscription " + tIterest.getSubject() );
					}
					mSubscriptionFilter.removeSubscription(tIterest);
				}
			}
		}
	}
	
	private void processDistBdxGwyPingRqst( DistBdxGwyPingRqst pRequest ) {
		DistBdxGwyPingRsp tResponse = new DistBdxGwyPingRsp();
		tResponse.setBdxGwyName(mBdxGwyInterface.getBroadcastGatewayName());
		tResponse.setRqstId(pRequest.getRqstId());
		tResponse.setStartTime(pRequest.getStartTime());
		mConnection.send(tResponse);
	}
	

	public void connectionDataRead(byte[] pData, String pRemoteHost)
	{
		MessageInterface tMessage = mMessageFactory.createMessage(pData);

		if (tMessage != null) {
			if (tMessage instanceof DistBdxGwyHello) {
				processDistBdxGwyHello( (DistBdxGwyHello) tMessage, pRemoteHost );
			}

			if (tMessage instanceof DistBdxGwySubscrInterest) {
				processDistBdxGwySubscrInterest((DistBdxGwySubscrInterest) tMessage); 
			}

			if (tMessage instanceof DistBdxGwyPingRqst) {
				processDistBdxGwyPingRqst((DistBdxGwyPingRqst) tMessage);  
			}	  
		}

	}
	

}
