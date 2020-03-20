
package generated;

import com.hoddmimes.distributor.messaging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public class TestMessageFactory implements MessageFactoryInterface
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

            case ((1000 << 16) + 1): // TestMessage 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	TestMessage tMessage = new TestMessage();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }

            default:
              return null;
		}	
	}
}

