
package com.hoddmimes.distributor.generated.messages;

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





public class NameValuePair implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 18);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mName;
	private String  mValue;
	private String  mCode;

public NameValuePair()
{
 
}


public  NameValuePair(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setName( String pName ) {
   mName  = pName ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getName() {
  return  mName ;
}





public void setValue( String pValue ) {
   mValue  = pValue ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getValue() {
  return  mValue ;
}





public void setCode( String pCode ) {
   mCode  = pCode ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getCode() {
  return  mCode ;
}

   

public String getMessageName() {
   return "NameValuePair";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.NameValuePair";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 18;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mName Type: String
	    */
	   
	pEncoder.add(  mName );
	    /**
	    * Encode Attribute: mValue Type: String
	    */
	   
	pEncoder.add(  mValue );
	    /**
	    * Encode Attribute: mCode Type: String
	    */
	   
	pEncoder.add(  mCode );
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

	   
	   /**
	    * Decoding Attribute: mName Type: String
	    */
	   
     mName = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mValue Type: String
	    */
	   
     mValue = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mCode Type: String
	    */
	   
     mCode = pDecoder.readString(); 
  }



public byte[] messageToBytes() {
    synchronized( this) { 
    
      if (mMessageBytesCached != null) {
	  
        return mMessageBytesCached ;
      } else {
    	 MessageBinEncoder tEncoder = new MessageBinEncoder();
    	 this.encode( tEncoder );
    	 mMessageBytesCached  =  tEncoder.getBytes();
		 return mMessageBytesCached;
	  }
    }
}




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
    	tSB.append("<Extending Message: " + "\"NameValuePair\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"NameValuePair\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mName: ");
       tSB.append( String.valueOf( mName ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mValue: ");
       tSB.append( String.valueOf( mValue ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mCode: ");
       tSB.append( String.valueOf( mCode ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "name"  + " : " + String.valueOf( mName )));
			treeNode.add( new TreeNode( "value"  + " : " + String.valueOf( mValue )));
			treeNode.add( new TreeNode( "code"  + " : " + String.valueOf( mCode )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("NameValuePair");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [NameValuePair]");
	}
	
			treeNode.add( new TreeNode( "name"  + " : " + String.valueOf( mName )));
			treeNode.add( new TreeNode( "value"  + " : " + String.valueOf( mValue )));
			treeNode.add( new TreeNode( "code"  + " : " + String.valueOf( mCode )));
	return treeNode;
	}
	
}

