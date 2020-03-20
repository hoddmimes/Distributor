package com.hoddmimes.distributor.bdxgwy;


import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.DistributorConnectionIf;
import com.hoddmimes.distributor.DistributorPublisherIf;
import com.hoddmimes.distributor.DistributorSubscriberIf;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BdxGwyMulticastGroupEntry implements DistributorUpdateCallbackIf 
{
	private static final Logger cLogger = LogManager.getLogger(BdxGwyMulticastGroupEntry.class.getSimpleName());
	private DistributorPublisherIf mPublisher;
	private DistributorSubscriberIf mSubscriber;
	private BdxGatewayInterface mBdxGatewayInterface;
	private DistributorConnectionIf mDistConnetion;

	public BdxGwyMulticastGroupEntry(BdxGwyMulticastGroupParameterEntry pParameters, BdxGatewayInterface pBdxGatewayInterface) {
		mBdxGatewayInterface = pBdxGatewayInterface;

		try {
			DistributorConnectionConfiguration tConfiguration = new DistributorConnectionConfiguration(pParameters.getMulticastAddress(), pParameters.getUdpPort());
			mDistConnetion = pBdxGatewayInterface.getDistributor().createConnection(tConfiguration);
			mPublisher = pBdxGatewayInterface.getDistributor().createPublisher(mDistConnetion, null);
			mSubscriber = pBdxGatewayInterface.getDistributor().createSubscriber(mDistConnetion, null, this);
			mSubscriber.addSubscription("/...", null);
		} catch (Exception e) {
			cLogger.fatal( "Could not open multicast group: " + pParameters.getMulticastAddress() + " port: " + pParameters.getUdpPort());
			cLogger.fatal(e);
			System.exit(0);
		}
	}

	public long getMulticastGroupId() {
		return this.mDistConnetion.getMcaConnectionId();
	}

	@Override
	public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pDeliveryQueueLength) {
		mBdxGatewayInterface.localDistributorUpdate(this.mDistConnetion.getMcaConnectionId(), pSubjectName, pData);
	}

	public void publish(String pSubjectname, byte[] pMessageBytes) {
		try {
			this.mPublisher.publish(pSubjectname, pMessageBytes);
		} catch (Exception e) {
			cLogger.fatal(e);
		}
	}
}
