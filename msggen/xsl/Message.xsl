<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java" xmlns:lxslt="http://xml.apache.org/xslt" 
     xmlns:extensions="com.hoddmimes.transform.MessageCompiler$Extensions" xmlns:redirect="com.hoddmimes.transform.MessageCompiler$Redirect"
     xmlns:o="urn:schemas-microsoft-com:office:office" 
     extension-element-prefixes="extensions redirect" xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="xalan java">
	
	<xsl:output method="text"/>
	<xsl:param name="messageBase"/>
	<xsl:param name="inputXml"/>
	<xsl:param name="inputXsl"/>
	<xsl:param name="inputXmlPath"/>
	<xsl:param name="outPath"/>
	<xsl:param name="package"/>

	
	<!--  ============================================== -->
	<!--  				Define JAVA data types 			 -->
	<!--  ============================================== -->	
	
	
	<xsl:variable name="TypeTableDefinitions">
		<DataTypes>
		   <Type name="boolean" type="boolean" readMethod="readBoolean" readArrayMethod="readBooleanArray" />
		   <Type name="byte" type="byte"  readMethod="readByte"/>
		   <Type name="short" type="short"  readMethod="readShort"  readArrayMethod="readShortArray" />
		   <Type name="int" type="int" readMethod="readInt"  readArrayMethod="readIntArray" />
		   <Type name="long" type="long" readMethod="readLong"  readArrayMethod="readLongArray" />
		   <Type name="double" type="double" readMethod="readDouble"  readArrayMethod="readDoubleArray" />
		   <Type name="String" type="String" readMethod="readString"  readArrayMethod="readStringArray" />
		   <Type name="byte[]" type="byte[]" readMethod="readBytes"  readArrayMethod="readBytesArray" />
		</DataTypes>
	</xsl:variable>
      <xsl:variable name="typeTable" select="xalan:nodeset($TypeTableDefinitions)/DataTypes"/>

	<xsl:variable name="services" select="document('../definitions/mao-services.xml')"/>

	

<xsl:variable name="messageIdentities" select="java:java.util.Hashtable.new()"/>



<!--     ============================================== -->
<!--     					Main Entry Point						   -->
<!--     ============================================== -->

<xsl:template match="/Messages">
   <xsl:for-each select="Message">
   		<xsl:variable name="msgPos" select="position()"/>
   	       <xsl:apply-templates mode="generateMessage" select=".">
   	       	<xsl:with-param name="msgPos" select="$msgPos"/>
   	       </xsl:apply-templates>
    </xsl:for-each>  
    <!--xsl:apply-templates mode="generateMessageByteOrderInterface" select="."/ -->
    <xsl:apply-templates mode="generateMessageFactory" select="."/>
    <xsl:apply-templates mode="generateConstantGroups" select="."/>
</xsl:template>



<!--     ==================================================== -->
<!--     			      Generate Message Factor			  -->
<!--     ==================================================== -->
<xsl:template mode="generateMessageFactory" match="Messages">

<xsl:variable name="moduleName" select="substring-before(extensions:extractFilename ($inputXml), '.')"/>
 <redirect:write select="concat( $outPath, concat($moduleName,'Factory.java'))">
package <xsl:value-of select="$package"/>;

import com.hoddmimes.distributor.messaging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public class <xsl:value-of select="$moduleName"/>Factory implements MessageFactoryInterface
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
            default:
              return null;
		}	
	}
}

</redirect:write>
</xsl:template>


<!--     ==================================================== -->
<!--     			      Generate ConstantGroups			  -->
<!--     ==================================================== -->

<xsl:template mode="generateConstantGroups" match="Messages">
<xsl:if test="ConstantGroups">
<xsl:for-each select="ConstantGroups/Group">


<redirect:write select="concat($outPath,'Constant',@name,'.java')">
package <xsl:value-of select="$package"/>;



public enum Constant<xsl:value-of select="@name"/>
{
	<xsl:for-each select="Constant">
		 <xsl:variable name="pos" select="position()"/><xsl:value-of select="../@name"/>_<xsl:value-of select="@id"/>(<xsl:value-of select="$pos"/>),</xsl:for-each> <xsl:value-of select="@name"/>_NotSet(-1);
	
	int mValue;

	Constant<xsl:value-of select="@name"/>( int pValue ) {
		mValue = pValue;
	}
	
	public static Constant<xsl:value-of select="@name"/> intToConstant( int pValue ) {

		switch( pValue ) {
<xsl:for-each select="Constant">
	 <xsl:variable name="pos" select="position()"/>
		  case <xsl:value-of select="$pos"/>:
			  return Constant<xsl:value-of select="../@name"/>.<xsl:value-of select="../@name"/>_<xsl:value-of select="@id"/>;
</xsl:for-each>			  
		  default: 
			  return Constant<xsl:value-of select="@name"/>.<xsl:value-of select="@name"/>_NotSet;	
		}
	}
	
	public int constantToInt() {
		return mValue;
	}
	
}

</redirect:write>
</xsl:for-each>	
</xsl:if>
</xsl:template>


<!--     ============================================== -->
<!--     			      Import templates							   -->
<!--     ============================================== -->

<xsl:template mode="addImports" match="Imports">
	// Add XML defined imports
	<xsl:for-each select="Import">
import <xsl:value-of select="@path"/>;</xsl:for-each>
</xsl:template>


<!--     ============================================== -->
<!--     			      Generate Message Class				   -->
<!--     ============================================== -->
<xsl:template mode="generateMessage" match="Message">
   <xsl:param name="msgPos"/>
   

  
  

<redirect:write select="concat($outPath,@name,'.java')">
package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;


import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.messaging.MessageAux;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.messaging.MessageWrapper;
import com.hoddmimes.distributor.messaging.TreeNode;


<xsl:apply-templates mode="addImports" select="../Imports"/>
<xsl:apply-templates mode="addImports" select="./Imports"/>


public class <xsl:value-of select="@name"/> <xsl:if test="@extends"> extends <xsl:value-of select="@extends"/> </xsl:if> <xsl:if test="not(@extends)"> implements MessageInterface  </xsl:if> 
{
    public static final int MESSAGE_ID = ((<xsl:value-of select="$messageBase"/> &lt;&lt; 16) + <xsl:value-of select="$msgPos"/>);
	<xsl:apply-templates mode="declareMessageConstants" select="."/>
	<xsl:apply-templates mode="declareAttributes" select="."/>
	<xsl:apply-templates mode="declareConstructors" select="."/>
	<xsl:apply-templates mode="declareGettersSetters" select="."/>
	<xsl:apply-templates mode="declareMessageIfMethods" select=".">
		<xsl:with-param name="msgPos" select="$msgPos"/>
	</xsl:apply-templates>
	<xsl:apply-templates mode="declareFormater" select="."/>
	<xsl:apply-templates mode="declareTree" select="."/>
	<xsl:apply-templates mode="applyCode" select="."/>
}

</redirect:write>
</xsl:template>



<!--     ============================================== -->
<!--     			Declare Constants           					   -->
<!--     ============================================== -->

<xsl:template mode="declareMessageConstants" match="Message">
   <xsl:for-each select="Constants/Constant">
   <xsl:if test="@type='String'">public static final <xsl:value-of select="@type"/>  <xsl:text> </xsl:text> <xsl:value-of select="@name"/> = "<xsl:value-of select="@value"/>"; 
   </xsl:if>
   <xsl:if test="not(@type='String')">public static final <xsl:value-of select="@type"/> <xsl:text> </xsl:text> <xsl:value-of select="@name"/> = <xsl:value-of select="@value"/>; 
   </xsl:if>
   </xsl:for-each>
</xsl:template>




<!--     ============================================== -->
<!--     			Declare Message Attributes					   -->
<!--     ============================================== -->

<xsl:template mode="declareAttributes" match="Message">
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   <xsl:for-each select="Attribute">
   	<xsl:if test="@constantGroup">
   	<xsl:if test="not(@array)">
   	private Constant<xsl:value-of select="@constantGroup"/> m<xsl:value-of select="extensions:upperFirst (@name)"/>;
   	</xsl:if>
   	<xsl:if test="@array">
   	private List&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; m<xsl:value-of select="extensions:upperFirst (@name)"/>;
   	</xsl:if>
   	</xsl:if>
   	<xsl:if test="not(@constantGroup)">
      <xsl:variable name="dataType" select="@type"/>
      <xsl:if test="$typeTable/Type[@name=$dataType]">
	private <xsl:value-of select="$typeTable/Type[@name=$dataType]/@type"/><xsl:if test="@array">[]</xsl:if>  m<xsl:value-of select="extensions:upperFirst (@name)"/>;</xsl:if>
	<xsl:if test="not($typeTable/Type[@name=$dataType])">
	private <xsl:if test="@array">List&lt;<xsl:value-of select="$dataType"/>&gt;</xsl:if><xsl:if test="not(@array)"><xsl:value-of select="@type"/></xsl:if> m<xsl:value-of select="extensions:upperFirst (@name)"/>;</xsl:if></xsl:if>
   </xsl:for-each>   
</xsl:template>


<!--     ============================================== -->
<!--     			Declare Message Constructors				   -->
<!--     ============================================== -->

<xsl:template mode="declareConstructors" match="Message">

public <xsl:value-of select="@name"/>()
{
 <xsl:if test="@extends">super();</xsl:if>
 <xsl:for-each select="Attribute">
 <xsl:if test="@constantGroup and not(@array)">
  m<xsl:value-of select="extensions:upperFirst (@name)"/> = Constant<xsl:value-of select="@constantGroup"/>.<xsl:value-of select="@constantGroup"/>_NotSet;
 </xsl:if>
 </xsl:for-each>
}


public  <xsl:value-of select="@name"/>(byte[]  pMessageByteArray ) {
  <xsl:for-each select="Attribute">
 <xsl:if test="@constantGroup and not(@array)">
  m<xsl:value-of select="extensions:upperFirst (@name)"/> = Constant<xsl:value-of select="@constantGroup"/>.<xsl:value-of select="@constantGroup"/>_NotSet;
 </xsl:if>
 </xsl:for-each>
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}

</xsl:template>






<!--     ============================================== -->
<!--     			Apply code written in XSL very uggly             	   -->
<!--     ============================================== -->

<xsl:template mode="applyCode" match="Message">
	<xsl:value-of select="code"/>
</xsl:template>


<!--     ============================================== -->
<!--     			Declare DeclareGettersSetters                  	   -->
<!--     ============================================== -->


<xsl:template mode="declareGettersSetters" match="Message">

<xsl:for-each select="Attribute"> 
    <xsl:if test="not(@noGetterSetter='true')">
       
    <xsl:if test="@constantGroup">
        <xsl:apply-templates mode="declareConstantGetterSetter" select="."/>
    </xsl:if>
    <xsl:if test="not(@constantGroup)">

    <xsl:variable name="dataType" select="@type"/>
       <xsl:if test="$typeTable/Type[@name=$dataType]">
           <xsl:apply-templates mode="declareNativeGetterSetter" select="."/>
       </xsl:if>
       <xsl:if test="not($typeTable/Type[@name=$dataType])">
             <xsl:apply-templates mode="declareMessageGetterSetter" select="."/>
       </xsl:if>
        </xsl:if>
      </xsl:if>
</xsl:for-each>
</xsl:template>

<xsl:template mode="declareConstantGetterSetter" match="Attribute">
<xsl:if test="not(@array)">
public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( Constant<xsl:value-of select="@constantGroup"/> p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {
    m<xsl:value-of select="extensions:upperFirst (@name)"/>  = p<xsl:value-of select="extensions:upperFirst (@name)"/> ;
	synchronized( this) { 
   	  mMessageBytesCached = null;
    }
} 


public  Constant<xsl:value-of select="@constantGroup"/>  get<xsl:value-of select="extensions:upperFirst (@name)"/>() {
  return  m<xsl:value-of select="extensions:upperFirst (@name)"/> ;
}
</xsl:if>

<xsl:if test="@array">
public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( List&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; pList ) {
      if (pList == null) {
        m<xsl:value-of select="extensions:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pList.size();
      
      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
	   m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt;( tSize + 1 );
	  }

	  m<xsl:value-of select="extensions:upperFirst (@name)"/> .addAll( pList );

	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( Constant<xsl:value-of select="@constantGroup"/>[] pArray ) {
      if (pArray == null) {
        m<xsl:value-of select="extensions:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pArray.length;
      
      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt;( tSize + 1 );
	  
	for( int i = 0; i &lt; tSize; i++ ) {
	    m<xsl:value-of select="extensions:upperFirst (@name)"/> .add( pArray[i]);
	}
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

	
public void add<xsl:value-of select="extensions:upperFirst (@name)"/>( List&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; pList ) {

      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) 
	     m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt;();
	  
	m<xsl:value-of select="extensions:upperFirst (@name)"/> .addAll(  pList );
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}


public  List&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; get<xsl:value-of select="extensions:upperFirst (@name)"/>() {
	
	if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null)
	  return null;
	
	List&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; tList = new ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt;( m<xsl:value-of select="extensions:upperFirst (@name)"/>.size() );
	tList.addAll(  m<xsl:value-of select="extensions:upperFirst (@name)"/> );
	return tList;
}
</xsl:if>

</xsl:template>




<xsl:template mode="declareNativeGetterSetter" match="Attribute">

<xsl:variable name="dataType" select="@type"/>
<xsl:variable name="type" select="$typeTable/Type[@name=$dataType]/@type"/>

<xsl:if test="@array">
<xsl:apply-templates mode="declareNativeAdd" select="."/>
</xsl:if>

<xsl:if test="@array and  (dataType='byte[]')">
	public void set<xsl:value-of select="extensions:upperFirst (@name)"/>(List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="extensions:upperFirst (@name)"/>) {
		if (p<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
			p<xsl:value-of select="extensions:upperFirst (@name)"/> = null;
			synchronized (this) {
				m<xsl:value-of select="extensions:upperFirst (@name)"/> = null;
			}
			return;
		}
		int tSize = p<xsl:value-of select="extensions:upperFirst (@name)"/>.size();
		m<xsl:value-of select="extensions:upperFirst (@name)"/> = new byte[tSize][]; 
		for( int i = 0; i &lt; tSize; i++) {
			m<xsl:value-of select="extensions:upperFirst (@name)"/>[i] = p<xsl:value-of select="extensions:upperFirst (@name)"/>.get(i);
		}
		synchronized (this) {
			mMessageBytesCached = null;
		}
	}
	

public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {
   m<xsl:value-of select="extensions:upperFirst (@name)"/>  = new <xsl:value-of select="$dataType"/>[p<xsl:value-of select="extensions:upperFirst (@name)"/>.size()][];
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}
</xsl:if>




public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( <xsl:value-of select="$type"/><xsl:if test="@array">[]</xsl:if> p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {
   m<xsl:value-of select="extensions:upperFirst (@name)"/>  = p<xsl:value-of select="extensions:upperFirst (@name)"/> ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  <xsl:value-of select="$type"/><xsl:if test="@array">[]</xsl:if> get<xsl:value-of select="extensions:upperFirst (@name)"/>() {
  return  m<xsl:value-of select="extensions:upperFirst (@name)"/> ;
}
</xsl:template>

<xsl:template mode="declareNativeAdd" match="Attribute">
<xsl:variable name="dataType" select="@type"/>
<xsl:variable name="type" select="$typeTable/Type[@name=$dataType]/@type"/>

<xsl:if test="($dataType='byte[]') or ($dataType='String')">
public void add<xsl:value-of select="extensions:upperFirst (@name)"/>ToArray( <xsl:value-of select="$dataType"/> pValue) 
{
   if (pValue == null) {
      return;
   }
   
    if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
<xsl:if test="$dataType='byte[]'">
      m<xsl:value-of select="extensions:upperFirst (@name)"/> = new byte[1][]; </xsl:if>
<xsl:if test="$dataType='String'">
      m<xsl:value-of select="extensions:upperFirst (@name)"/> = new String[1]; </xsl:if>
      m<xsl:value-of select="extensions:upperFirst (@name)"/>[0] = pValue;
	} else {
		int tSize =  m<xsl:value-of select="extensions:upperFirst (@name)"/>.length + 1;
<xsl:if test="$dataType='byte[]'">
         byte[][] tArray = new byte[tSize + 1][]; </xsl:if>
<xsl:if test="$dataType='String'">
         String[] tArray = new String[tSize + 1]; </xsl:if>		
         for( int i = 0; i &lt; tSize - 1; i++ ) {
         	tArray[i] =  m<xsl:value-of select="extensions:upperFirst (@name)"/>[i];
         }
         tArray[ tSize - 1] = pValue;
         m<xsl:value-of select="extensions:upperFirst (@name)"/> = tArray;	
	}
    synchronized (this) {
      mMessageBytesCached = null;
    }
}
</xsl:if>

<xsl:if test="not($dataType='byte[]') and not($dataType='String')">
public void add<xsl:value-of select="extensions:upperFirst (@name)"/>ToArray( <xsl:value-of select="$dataType"/> pValue) 
{

    if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
      <xsl:value-of select="$dataType"/>[] m<xsl:value-of select="extensions:upperFirst (@name)"/> = new <xsl:value-of select="$dataType"/>[1];
      m<xsl:value-of select="extensions:upperFirst (@name)"/>[0] = pValue;
    } else {
         int tSize =  m<xsl:value-of select="extensions:upperFirst (@name)"/>.length + 1;
    	 <xsl:value-of select="$dataType"/>[] tArray = new <xsl:value-of select="$dataType"/>[tSize];	
         for( int i = 0; i &lt; tSize - 1; i++ ) {
         	tArray[i] =  m<xsl:value-of select="extensions:upperFirst (@name)"/>[i];
         }
         tArray[ tSize - 1] = pValue;
         m<xsl:value-of select="extensions:upperFirst (@name)"/> = tArray;	
		 synchronized (this) {
			mMessageBytesCached = null;
		}
	}
}
</xsl:if>	
</xsl:template>


<xsl:template mode="declareMessageGetterSetter" match="Attribute">
<xsl:variable name="dataType" select="@type"/>

<xsl:if test="@array">

public void add<xsl:value-of select="extensions:upperFirst (@name)"/>ToArray( <xsl:value-of select="$dataType"/> pObject ) {
    if (pObject == null) {
      return;
    }
    
    if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
       m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;();
    }
    m<xsl:value-of select="extensions:upperFirst (@name)"/>.add( pObject );
    
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {
      if (p<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
        m<xsl:value-of select="extensions:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  p<xsl:value-of select="extensions:upperFirst (@name)"/>.size();
      
      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( tSize + 1 );
	  

	m<xsl:value-of select="extensions:upperFirst (@name)"/> .addAll( p<xsl:value-of select="extensions:upperFirst (@name)"/> );

	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void set<xsl:value-of select="extensions:upperFirst (@name)"/>( <xsl:value-of select="$dataType"/>[] p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {
      if (p<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
        m<xsl:value-of select="extensions:upperFirst (@name)"/>  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  p<xsl:value-of select="extensions:upperFirst (@name)"/>.length;
      
      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) 
	  m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( tSize + 1 );
	  
	for( int i = 0; i &lt; tSize; i++ ) {
	    m<xsl:value-of select="extensions:upperFirst (@name)"/> .add( p<xsl:value-of select="extensions:upperFirst (@name)"/>[i]);
	}
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

	
public void add<xsl:value-of select="extensions:upperFirst (@name)"/>( List&lt;<xsl:value-of select="$dataType"/>&gt; p<xsl:value-of select="extensions:upperFirst (@name)"/> ) {

      if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) 
	     m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;();
	  
	m<xsl:value-of select="extensions:upperFirst (@name)"/> .addAll(  p<xsl:value-of select="extensions:upperFirst (@name)"/> );
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}


public  List&lt;<xsl:value-of select="$dataType"/>&gt; get<xsl:value-of select="extensions:upperFirst (@name)"/>() {
	
	if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null)
	  return null;
	
	List&lt;<xsl:value-of select="$dataType"/>&gt; tList = new ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;( m<xsl:value-of select="extensions:upperFirst (@name)"/>.size() );
	tList.addAll(  m<xsl:value-of select="extensions:upperFirst (@name)"/> );
	return tList;
}

</xsl:if>

<xsl:if test="not(@array)">

public  <xsl:value-of select="$dataType"/>  get<xsl:value-of select="extensions:upperFirst (@name)"/>() {
  return m<xsl:value-of select="extensions:upperFirst (@name)"/>;
}

public  void set<xsl:value-of select="extensions:upperFirst (@name)"/>(<xsl:value-of select="$dataType"/>  p<xsl:value-of select="extensions:upperFirst (@name)"/>) {
  m<xsl:value-of select="extensions:upperFirst (@name)"/> = p<xsl:value-of select="extensions:upperFirst (@name)"/>;
}
	
</xsl:if>



</xsl:template>



<!--     ============================================== -->
<!--     			Declare MessageIf methods           -->
<!--     ============================================== -->

<xsl:template mode="declareMessageIfMethods" match="Message">
	<xsl:param name="msgPos"/>
	<xsl:apply-templates mode="declareMsgIdMethods" select=".">
		<xsl:with-param name="msgPos" select="$msgPos"/>
	</xsl:apply-templates>
	<xsl:apply-templates mode="declareMsgCodecMethods" select="."/>


public byte[] messageToBytes() {
    synchronized( this) { 
    <xsl:if test="@extends">
    if ((mMessageBytesCached != null)  &amp;&amp; (super.mMessageBytesCached != null)) {
    </xsl:if>
    <xsl:if test="not(@extends)">
      if (mMessageBytesCached != null) {
	  </xsl:if>
        return mMessageBytesCached ;
      } else {
    	 MessageBinEncoder tEncoder = new MessageBinEncoder();
    	 this.encode( tEncoder );
    	 mMessageBytesCached  =  tEncoder.getBytes();
		 return mMessageBytesCached;
	  }
    }
}


</xsl:template>


<!--     ==================================================== -->
<!--     			Declare MessageIf ID methodsmethods                       	  -->
<!--     ==================================================== -->
<xsl:template mode="declareMsgIdMethods" match="Message">
   <xsl:param name="msgPos"/>

   
   <!-- Get Message Id -->
   <xsl:if test="not($msgPos)">
   	<xsl:message>**** Error: message <xsl:value-of select="@name"/> has not a messageId attribute defined!</xsl:message>
  </xsl:if>
   
  
   <!-- Check that the message id is unique -->
   <xsl:variable name="dup" select="java:get($messageIdentities,string($msgPos))"/> 
    <xsl:if test="$dup">
   	<xsl:message> **** Error: duplicate message id: <xsl:value-of select="$msgPos"/> Message: <xsl:value-of select="@name"/> </xsl:message>
   	Error: Error: duplicate message id: <xsl:value-of select="$msgPos"/> Message: <xsl:value-of select="@name"/> 
   </xsl:if>  
    <xsl:if test="not($dup)">
      	 <xsl:variable name="dmy" select="java:put($messageIdentities, string($msgPos), string($msgPos))"/> 
    </xsl:if>
   

public String getMessageName() {
   return "<xsl:value-of select="@name"/>";
}

public String getFullMessageName() {
    return "<xsl:value-of select="$package"/>.<xsl:value-of select="@name"/>";
}
 
 public int getMessageId() {
   	return  (<xsl:value-of select="$messageBase"/> &lt;&lt; 16) + <xsl:value-of select="$msgPos"/>;
  }
   
</xsl:template>


<!--     ==================================================== -->
<!--     			Declare MessageIf encode/decode methods               	  -->
<!--     ==================================================== -->


<xsl:template mode="declareMsgCodecMethods" match="Message">

  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		<xsl:if test="@extends">super.encode( pEncoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	    <xsl:if test="@constantGroup">
	    <xsl:apply-templates mode="generate-encode-constantgroup" select="."/>
	    </xsl:if>
	  

	   <xsl:if test="not(@constantGroup)">
	    /**
	    * Encode Attribute: m<xsl:value-of select="extensions:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="encoderArrayBin" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="encoderSingleBin" select="."/>
	  </xsl:if>
	  </xsl:if>
	</xsl:for-each>
  }


  public void decode( MessageBinDecoder pDecoder) {
     decode( pDecoder, false );
  }
  
  @SuppressWarnings("unchecked")
  public void decode( MessageBinDecoder pDecoder, boolean pIsExtensionInvoked ) {
      String tStr = null;
      int tSize = 0;
      
     if (!pIsExtensionInvoked) {    
      pDecoder.readInt();	// Read Message Id 
     }

	   <xsl:if test="@extends">super.decode( pDecoder, true );</xsl:if>

	   <xsl:for-each select="Attribute">
	    <xsl:if test="@constantGroup">
	  	  <xsl:apply-templates mode="decoderConstantGroup" select="."/>
	    </xsl:if>
	  
	    <xsl:if test="not(@constantGroup)">
	   /**
	    * Decoding Attribute: m<xsl:value-of select="extensions:upperFirst (@name)"/> Type: <xsl:value-of select="@type"/>
	    */
	   <xsl:if test="@array">
	      <xsl:apply-templates mode="decoderArrayBin" select="."/>
	   </xsl:if>
	  <xsl:if test="not(@array)">
	      <xsl:apply-templates mode="decoderSingleBin" select="."/>
	  </xsl:if>
	  </xsl:if>
	</xsl:for-each>
  }
</xsl:template>

<xsl:template mode="decoderConstantGroup" match="Attribute">
  <xsl:if test="not(@array)">
     /**
      * Decode Attribute: m<xsl:value-of select="extensions:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
      */
	  int tConstantId = pDecoder.readInt();
      m<xsl:value-of select="extensions:upperFirst (@name)"/> = Constant<xsl:value-of select="@constantGroup"/>.intToConstant( tConstantId );
	    </xsl:if>
	   <xsl:if test="@array">
	   if (!pDecoder.readBoolean()) {
	     m<xsl:value-of select="extensions:upperFirst (@name)"/> = null;
	   } else {
	    ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt; tList = new ArrayList&lt;Constant<xsl:value-of select="@constantGroup"/>&gt;();
	   	tSize = pDecoder.readInt(); 
		for( int i = 0; i &lt; tSize; i++ ) {
		  tList.add( Constant<xsl:value-of select="@constantGroup"/>.intToConstant(pDecoder.readInt()));
		}
		m<xsl:value-of select="extensions:upperFirst (@name)"/> = tList;
	   }
	   </xsl:if>   
</xsl:template>



<xsl:template mode="generate-encode-constantgroup" match="Attribute">
   <xsl:if test="not(@array)">
	  	 /**
	       * Encode Attribute: m<xsl:value-of select="extensions:upperFirst (@name)"/> Constant Group: <xsl:value-of select="@constantGroup"/>
	       */
	  	   pEncoder.add(  m<xsl:value-of select="extensions:upperFirst (@name)"/>.constantToInt() );
	  	</xsl:if>
	  	<xsl:if test="@array">
		  /**
	       * Encode Attribute: m<xsl:value-of select="extensions:upperFirst (@name)"/> Constant Array Group: <xsl:value-of select="@constantGroup"/>
	       */  	
	      if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
	           pEncoder.add( false );
	      } else {
	          int tSize = m<xsl:value-of select="extensions:upperFirst (@name)"/>.size();
	          int[] tConstValues = new int[ tSize ];
	          for( int i = 0; i &lt; tSize; i++ ) {
	            tConstValues[i] = m<xsl:value-of select="extensions:upperFirst (@name)"/>.get(i).constantToInt();
	          }
	          pEncoder.add( tConstValues );
	      }
	  	</xsl:if>
</xsl:template>
<!-- ++++++++++++++++++++++++++++++++++++++++++
                           Decode methods 
 ++++++++++++++++++++++++++++++++++++++++++++ -->
 
 <xsl:template mode="decoderSingleBin" match="Attribute">
 <xsl:variable name="dataType" select="@type"/>

  <xsl:if test="$typeTable/Type[@name=$dataType]">
     m<xsl:value-of select="extensions:upperFirst (@name)"/> = pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readMethod"/>(); </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
     m<xsl:value-of select="extensions:upperFirst (@name)"/> = (<xsl:value-of select="$dataType"/>) pDecoder.readMessage( <xsl:value-of select="$dataType"/>.class );  </xsl:if>
 </xsl:template>
 
<xsl:template mode="decoderArrayBin" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
     m<xsl:value-of select="extensions:upperFirst (@name)"/> =  pDecoder.<xsl:value-of select="$typeTable/Type[@name=$dataType]/@readArrayMethod"/>();  </xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
    m<xsl:value-of select="extensions:upperFirst (@name)"/> = (List&lt;<xsl:value-of select="$dataType"/>&gt;) pDecoder.readMessageArray(  <xsl:value-of select="$dataType"/>.class ); </xsl:if>
</xsl:template>




<!-- +++++++++++++++++++++++++++++++++++++++++++++
                           Enoder methods 
++++++++++++++++++++++++++++++++++++++++++++++++ -->
<xsl:template mode="encoderArrayBin" match="Attribute">
  <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
     pEncoder.add( m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
  <xsl:if test="not($typeTable/Type[@name=$dataType])">
     pEncoder.addMessageArray( m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
</xsl:template>












<xsl:template mode="encoderSingleBin" match="Attribute">
    <xsl:variable name="dataType" select="@type"/>
  <xsl:if test="$typeTable/Type[@name=$dataType]">
	pEncoder.add(  m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
	<xsl:if test="not($typeTable/Type[@name=$dataType])">  
    pEncoder.add( m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
</xsl:template>

<xsl:template mode="formatNative" match="Attribute">
 
   <xsl:if test="@array">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="extensions:upperFirst (@name)"/>[]: ");
     tSB.append( MessageAux.format( m<xsl:value-of select="extensions:upperFirst (@name)"/> ));
     tSB.append("\n");</xsl:if>
   <xsl:if test="not(@array)">
     <xsl:if test="@type='byte[]'">
        tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="extensions:upperFirst (@name)"/>: ");
        tSB.append( MessageAux.format( m<xsl:value-of select="extensions:upperFirst (@name)"/>));
        tSB.append("\n");</xsl:if>
      <xsl:if test="not(@type='byte[]')">
       tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="extensions:upperFirst (@name)"/>: ");
       tSB.append( String.valueOf( m<xsl:value-of select="extensions:upperFirst (@name)"/> ));
       tSB.append("\n"); </xsl:if></xsl:if>
</xsl:template>

<xsl:template mode="formatMessage" match="Attribute">
   <xsl:if test="@array">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select='extensions:upperFirst (@name)'/>[]: ");
     if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
       tSB.append("&lt;null&gt;");
     } else {
     	    tSB.append("\n");
          int tSize = m<xsl:value-of select="extensions:upperFirst (@name)"/>.size();
          for( int i = 0; i &lt; tSize; i++ ) {
             <xsl:value-of select="@type"/> tMsg = (<xsl:value-of select="@type"/>) m<xsl:value-of select="extensions:upperFirst (@name)"/>.get( i );
            tSB.append(  tMsg.toString( pCount + 4 ) );
          }
     } </xsl:if>
     <xsl:if test="not(@array)">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select="extensions:upperFirst (@name)"/>: ");
     if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
       tSB.append("&lt;null&gt;");
     } else {
          tSB.append( m<xsl:value-of select="extensions:upperFirst (@name)"/>.toString( pCount + 4 ) );
     } </xsl:if>
     tSB.append("\n");
</xsl:template>

<xsl:template mode="formatConstant" match="Attribute">
  <xsl:if test="not(@array)">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select='extensions:upperFirst (@name)'/>: ");
     tSB.append(  m<xsl:value-of select="extensions:upperFirst (@name)"/>.toString() );
      
     tSB.append("\n");
  </xsl:if>
  <xsl:if test="@array">
     tSB.append( blanks( pCount + 2 ) + "m<xsl:value-of select='extensions:upperFirst (@name)'/>[]: ");
     if ( m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
       tSB.append("&lt;null&gt;");
     } else { 
          int tSize = m<xsl:value-of select="extensions:upperFirst (@name)"/>.size();
          for( int i = 0; i &lt; tSize; i++ ) {
            tSB.append( m<xsl:value-of select="extensions:upperFirst (@name)"/>.get( i ).toString() + " ");
          }
     } 
     tSB.append("\n");
     </xsl:if>  
</xsl:template>

<xsl:template mode="declareFormater" match="Message">

String blanks(int pCount) {
	if (pCount == 0) {
	  return null;
	}
	String tBlanks = "                                                                                       ";
	return tBlanks.substring( 0,pCount ); 
}


public String toString() {
    return this.toString(0);
 }

public String toString( int pCount ) {
	return toString( pCount, false );
}

public String toString( int pCount, boolean pExtention ) {
    StringBuilder tSB = new StringBuilder (512);
    if (pCount > 0) {
      tSB.append( blanks( pCount ));
    }
    
    if (pExtention)  {
    	tSB.append("&lt;Extending Message: " + "\"<xsl:value-of select="@name"/>\"  Id: " + Integer.toHexString(getMessageId()) + "&gt;\n");
    } else {
    		tSB.append("Message: " + "\"<xsl:value-of select="@name"/>\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	<xsl:if test="@extends">tSB.append( super.toString( pCount + 3, true ));</xsl:if>

      <xsl:for-each select="Attribute">
      		<xsl:if test="@constantGroup">
      			<xsl:apply-templates mode="formatConstant" select="."/>
      		</xsl:if>
      	<xsl:if test="not(@constantGroup)">
        <xsl:variable name="dataType" select="@type"/>
        <xsl:if test="$typeTable/Type[@name=$dataType]">
          <xsl:apply-templates mode="formatNative" select="."/>
        </xsl:if>
        <xsl:if test="not($typeTable/Type[@name=$dataType])">
           <xsl:apply-templates mode="formatMessage" select="."/>
        </xsl:if>
        </xsl:if>
	</xsl:for-each>
	return tSB.toString();
  }
</xsl:template>






<!-- ============================================================================ -->
<!--							Declare Clone Method										-->
<!-- ============================================================================ -->

<xsl:template mode="declareCloneMethod" match="Message">

@SuppressWarnings("unchecked")
public <xsl:value-of select="@name"/> clone() {
	<xsl:value-of select="@name"/> tMessage = new <xsl:value-of select="@name"/>();
	<xsl:if test="@extends">
		((<xsl:value-of select="@extends"/>) tMessage).clone();
	</xsl:if>
	<xsl:for-each select="Attribute">
	      <xsl:variable name="dataType" select="@type"/>
     	     <xsl:if test="@constantGroup">
     	     <xsl:if test="not(@array)">
     	     if (this.m<xsl:value-of select="extensions:upperFirst (@name)"/> != null) { 
     	    	 tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/> = Constant<xsl:value-of select="@constantGroup"/>.intToConstant( this.m<xsl:value-of select="extensions:upperFirst (@name)"/>.constantToInt() );
     	    }</xsl:if>
     	    <xsl:if test="@array">
     	    if (this.m<xsl:value-of select="extensions:upperFirst (@name)"/> != null) 
     	    {
     	    	 tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/> = new ArrayList&lt; Constant<xsl:value-of select="@constantGroup"/>&gt;();
     	    	 tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/>.addAll( m<xsl:value-of select="extensions:upperFirst (@name)"/>);
     	    }
     	    </xsl:if>
			</xsl:if>	
     	    <xsl:if test="not(@constantGroup)">
     	     <xsl:if test="$typeTable/Type[@name=$dataType]">
     	     tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/> = MessageAux.copyAttribute( this.m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
	     <xsl:if test="not($typeTable/Type[@name=$dataType]) and not(@array)">
     	     tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/> = (<xsl:value-of select="$dataType"/>) MessageAux.copyAttribute( this.m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>
	    <xsl:if test="not($typeTable/Type[@name=$dataType]) and @array">
     	     tMessage.m<xsl:value-of select="extensions:upperFirst (@name)"/> = (ArrayList&lt;<xsl:value-of select="$dataType"/>&gt;) MessageAux.copyAttribute( this.m<xsl:value-of select="extensions:upperFirst (@name)"/> );</xsl:if>	    
	    </xsl:if>		
	</xsl:for-each>
	return tMessage;
}  
</xsl:template>






	<!-- ============================================================================ -->
	<!--							Declare Tree Node Method							  -->
	<!-- ============================================================================ -->
	<xsl:template mode="declareTree" match="Message">

	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	<xsl:if test="@extends">
		<xsl:variable name="extendedMsgName" select="@extends"/>
		<xsl:variable name="extendedMsg" select="../Message[@name=$extendedMsgName]"/>
		<xsl:if test="$extendedMsg and $extendedMsg/@extends">
			super.treeAddMessageAsSuperClass( treeNode );</xsl:if>
	</xsl:if>

	<xsl:for-each select="Attribute">
		<xsl:if test="@array">
			<xsl:if test="not(@constantGroup)">
				treeNode.add( TreeNode.createArray( "<xsl:value-of select="@name"/>", m<xsl:value-of select="extensions:upperFirst (@name)"/> ));</xsl:if>
			<xsl:if test="@constantGroup">
				treeNode.add( TreeNode.createArray( "<xsl:value-of select="@name"/>", m<xsl:value-of select="extensions:upperFirst (@name)"/>.toArray() ));</xsl:if>
		</xsl:if>
		<xsl:if test="not(@array)">
			<xsl:apply-templates mode="treeNodeAddAttribute" select="."/></xsl:if>
	</xsl:for-each>
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("<xsl:value-of select="@name"/>");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [<xsl:value-of select="@name"/>]");
	}
	<xsl:if test="@extends">super.treeAddMessageAsSuperClass( treeNode );</xsl:if>

	<xsl:for-each select="Attribute">
		<xsl:if test="@array">
			<xsl:if test="@array">
				<xsl:if test="not(@constantGroup)">
					treeNode.add( TreeNode.createArray( "<xsl:value-of select="@name"/>", m<xsl:value-of select="extensions:upperFirst (@name)"/> ));</xsl:if>
				<xsl:if test="@constantGroup">
					treeNode.add( TreeNode.createArray( "<xsl:value-of select="@name"/>", m<xsl:value-of select="extensions:upperFirst (@name)"/>.toArray() ));</xsl:if>
			</xsl:if>
		</xsl:if>
		<xsl:if test="not(@array)">
			<xsl:apply-templates mode="treeNodeAddAttribute" select="."/>
		</xsl:if>
	</xsl:for-each>
	return treeNode;
	}
	</xsl:template>


	<xsl:template mode="treeNodeAddAttribute" match="Attribute">
	<xsl:if test="@constantGroup">
		if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
		treeNode.add( new TreeNode( "<xsl:value-of select="@name"/>"  + " : &lt;null&gt;" ));
		} else {
		treeNode.add( new TreeNode( "<xsl:value-of select="@name"/>"  + " : " +  m<xsl:value-of select="extensions:upperFirst (@name)"/>.toString() ));
		}
	</xsl:if>
	<xsl:if test="not(@constantGroup)">
		<xsl:variable name="dataType" select="@type"/>
		<xsl:if test="$typeTable/Type[@name=$dataType]">
			<xsl:apply-templates mode="treeAddNativeAttribute" select="."/>
		</xsl:if>
		<xsl:if test="not($typeTable/Type[@name=$dataType])">
			if (m<xsl:value-of select="extensions:upperFirst (@name)"/> == null) {
			treeNode.add( new TreeNode( "<xsl:value-of select="@name"/>"  + " : &lt;null&gt;"));
			} else {
			treeNode.add( m<xsl:value-of select="extensions:upperFirst (@name)"/>.getNodeTree("<xsl:value-of select="@name"/>"));
			}
		</xsl:if>
	</xsl:if>
</xsl:template>

	<xsl:template mode="treeAddNativeAttribute" match="Attribute">
		<xsl:if test="@type='byte[]'">
			treeNode.add( TreeNode.createArray(  "<xsl:value-of select="@name"/>", m<xsl:value-of select="extensions:upperFirst (@name)"/> ));</xsl:if>
		<xsl:if test="not(@type='byte[]')">
			treeNode.add( new TreeNode( "<xsl:value-of select="@name"/>"  + " : " + String.valueOf( m<xsl:value-of select="extensions:upperFirst (@name)"/> )));</xsl:if>
	</xsl:template>

</xsl:stylesheet>
