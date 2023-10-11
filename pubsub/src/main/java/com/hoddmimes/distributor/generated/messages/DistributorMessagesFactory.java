
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

            case ((1 << 16) + 1): // MgmtDistributorBdx 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtDistributorBdx tMessage = new MgmtDistributorBdx();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 2): // MgmtDistributorRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtDistributorRqst tMessage = new MgmtDistributorRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 3): // MgmtDistributorRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtDistributorRsp tMessage = new MgmtDistributorRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 4): // MgmtConnection 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnection tMessage = new MgmtConnection();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 5): // MgmtDistributorView 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtDistributorView tMessage = new MgmtDistributorView();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 6): // MgmtConnectionRqst 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnectionRqst tMessage = new MgmtConnectionRqst();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 7): // MgmtConnectionRsp 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnectionRsp tMessage = new MgmtConnectionRsp();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 8): // DataRateItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	DataRateItem tMessage = new DataRateItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 9): // QueueSizeItem 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	QueueSizeItem tMessage = new QueueSizeItem();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 10): // MgmtConnectionTrafficInfo 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnectionTrafficInfo tMessage = new MgmtConnectionTrafficInfo();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 11): // MgmtConnectionRetransmissionInfo 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnectionRetransmissionInfo tMessage = new MgmtConnectionRetransmissionInfo();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            case ((1 << 16) + 12): // MgmtConnectionView 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	MgmtConnectionView tMessage = new MgmtConnectionView();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            default:
              return null;
		}	
	}
}

