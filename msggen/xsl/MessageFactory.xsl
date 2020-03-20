<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java" xmlns:lxslt="http://xml.apache.org/xslt" 
     xmlns:extensions="MessageCompiler" xmlns:redirect="MessageCompiler$Redirect" 
     xmlns:o="urn:schemas-microsoft-com:office:office" 
     extension-element-prefixes="extensions redirect" xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="xalan java">
	
	<xsl:output method="text"/>
	<xsl:param name="outPath"/>
	<xsl:param name="package"/>





	
<xsl:template match="/MessagesRoot">
<redirect:write select="concat( $outPath,'AllMessageFactory.java')">
package <xsl:value-of select="$package"/>;

import com.hoddmimes.boreas.core.messaging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public class AllMessageFactory implements MessageFactoryInterface
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
<xsl:for-each select="Messages">
<xsl:variable name="messageBase" select="@messageBase"/>
<xsl:for-each select="Message">
		<xsl:variable name="msgPos" select="position()"/>
            case ((<xsl:value-of select="$messageBase"/> &lt;&lt; 16) + <xsl:value-of select="$msgPos"/>): // <xsl:value-of select="@name"/> 
            {
            	MessageBinDecoder pDecoder = new MessageBinDecoder( pBuffer );
            	<xsl:value-of select="@name"/> tMessage = new <xsl:value-of select="@name"/>();
            	tMessage.decode( pDecoder );
            	return tMessage;
            }
</xsl:for-each>
</xsl:for-each>
            default:
              return null;
		}	
	}
}

</redirect:write>
</xsl:template>


</xsl:stylesheet>
