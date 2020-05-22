
package com.hoddmimes.distributor.generated.messages;

import com.hoddmimes.distributor.messaging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public class DistributorMessagesFactory implements MessageFactoryInterface
{

	public MessageInterface createMessage( byte[] pBuffer ) 
	{
	  	ByteBuffer tByteBuffer = ByteBuffer.wrap(pBuffer);
		int tMessageId = tByteBuffer.getInt();
	
		switch( tMessageId ) 
		{
			case -1: // WrappedMessage
            {
               MessageWrapper tWrappedMessage = new MessageWrapper();
               MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
               tWrappedMessage.decode( pDecoder );
               return tWrappedMessage;
            }

            case ((1 << 16) + 1): // DistNetMsg 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistNetMsg tMessage = new DistNetMsg();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 2): // DistDomainConnectionEntry 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistDomainConnectionEntry tMessage = new DistDomainConnectionEntry();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 3): // DistDomainDistributorEntry 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistDomainDistributorEntry tMessage = new DistDomainDistributorEntry();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 4): // DistExploreDomainRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreDomainRqst tMessage = new DistExploreDomainRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 5): // DistExploreDomainRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreDomainRsp tMessage = new DistExploreDomainRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 6): // DistributorEntry 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistributorEntry tMessage = new DistributorEntry();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 7): // DistExploreDistributorRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreDistributorRqst tMessage = new DistExploreDistributorRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 8): // DistExploreDistributorRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreDistributorRsp tMessage = new DistExploreDistributorRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 9): // DataRateItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DataRateItem tMessage = new DataRateItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 10): // QueueSizeItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	QueueSizeItem tMessage = new QueueSizeItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 11): // ConnectionEntry 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	ConnectionEntry tMessage = new ConnectionEntry();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 12): // DistExploreConnectionRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreConnectionRqst tMessage = new DistExploreConnectionRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 13): // DistExploreConnectionRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreConnectionRsp tMessage = new DistExploreConnectionRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 14): // DistExploreSubscriptionsRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreSubscriptionsRqst tMessage = new DistExploreSubscriptionsRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 15): // DistExploreSubscriptionsRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreSubscriptionsRsp tMessage = new DistExploreSubscriptionsRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 16): // DistExploreRetransmissionsRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreRetransmissionsRqst tMessage = new DistExploreRetransmissionsRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 17): // DistExploreRetransmissonsRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistExploreRetransmissonsRsp tMessage = new DistExploreRetransmissonsRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 18): // NameValuePair 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	NameValuePair tMessage = new NameValuePair();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 19): // DistTriggerCofigurationRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistTriggerCofigurationRqst tMessage = new DistTriggerCofigurationRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 20): // ConfigurationXmlLoadRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	ConfigurationXmlLoadRqst tMessage = new ConfigurationXmlLoadRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 21): // ConfigurationXmlLoadRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	ConfigurationXmlLoadRsp tMessage = new ConfigurationXmlLoadRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 22): // DistBdxGwyHello 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwyHello tMessage = new DistBdxGwyHello();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 23): // DistBdxGwySubscrInterestItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwySubscrInterestItem tMessage = new DistBdxGwySubscrInterestItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 24): // DistBdxGwySubscrInterest 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwySubscrInterest tMessage = new DistBdxGwySubscrInterest();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 25): // DistBdxGwyPingRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwyPingRqst tMessage = new DistBdxGwyPingRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 26): // DistBdxGwyPingRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwyPingRsp tMessage = new DistBdxGwyPingRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 27): // DistBdxGwyUpdateItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwyUpdateItem tMessage = new DistBdxGwyUpdateItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 28): // DistBdxGwyUpdate 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DistBdxGwyUpdate tMessage = new DistBdxGwyUpdate();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            default:
              return null;
		}	
	}
}

