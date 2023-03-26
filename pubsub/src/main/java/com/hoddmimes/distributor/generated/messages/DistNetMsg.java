
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





public class DistNetMsg implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 1);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mRequestId;
	private long  mTimestamp;
	private boolean  mIsRequestMessage;
	private MessageWrapper mMessage;

public DistNetMsg()
{
 
}


public  DistNetMsg(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setRequestId( long pRequestId ) {
   mRequestId  = pRequestId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getRequestId() {
  return  mRequestId ;
}





public void setTimestamp( long pTimestamp ) {
   mTimestamp  = pTimestamp ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getTimestamp() {
  return  mTimestamp ;
}





public void setIsRequestMessage( boolean pIsRequestMessage ) {
   mIsRequestMessage  = pIsRequestMessage ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  boolean getIsRequestMessage() {
  return  mIsRequestMessage ;
}


public  MessageWrapper  getMessage() {
  return mMessage;
}

public  void setMessage(MessageWrapper  pMessage) {
  mMessage = pMessage;
}
	

   

public String getMessageName() {
   return "DistNetMsg";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistNetMsg";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 1;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mRequestId Type: long
	    */
	   
	pEncoder.add(  mRequestId );
	    /**
	    * Encode Attribute: mTimestamp Type: long
	    */
	   
	pEncoder.add(  mTimestamp );
	    /**
	    * Encode Attribute: mIsRequestMessage Type: boolean
	    */
	   
	pEncoder.add(  mIsRequestMessage );
	    /**
	    * Encode Attribute: mMessage Type: MessageWrapper
	    */
	     
    pEncoder.add( mMessage );
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
	    * Decoding Attribute: mRequestId Type: long
	    */
	   
     mRequestId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mTimestamp Type: long
	    */
	   
     mTimestamp = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mIsRequestMessage Type: boolean
	    */
	   
     mIsRequestMessage = pDecoder.readBoolean(); 
	   /**
	    * Decoding Attribute: mMessage Type: MessageWrapper
	    */
	   
     mMessage = (MessageWrapper) pDecoder.readMessage( MessageWrapper.class );  
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
    	tSB.append("<Extending Message: " + "\"DistNetMsg\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistNetMsg\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mRequestId: ");
       tSB.append( String.valueOf( mRequestId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mTimestamp: ");
       tSB.append( String.valueOf( mTimestamp ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mIsRequestMessage: ");
       tSB.append( String.valueOf( mIsRequestMessage ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mMessage: ");
     if ( mMessage == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mMessage.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "requestId"  + " : " + String.valueOf( mRequestId )));
			treeNode.add( new TreeNode( "timestamp"  + " : " + String.valueOf( mTimestamp )));
			treeNode.add( new TreeNode( "isRequestMessage"  + " : " + String.valueOf( mIsRequestMessage )));
			if (mMessage == null) {
			treeNode.add( new TreeNode( "message"  + " : <null>"));
			} else {
			treeNode.add( mMessage.getNodeTree("message"));
			}
		
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistNetMsg");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistNetMsg]");
	}
	
			treeNode.add( new TreeNode( "requestId"  + " : " + String.valueOf( mRequestId )));
			treeNode.add( new TreeNode( "timestamp"  + " : " + String.valueOf( mTimestamp )));
			treeNode.add( new TreeNode( "isRequestMessage"  + " : " + String.valueOf( mIsRequestMessage )));
			if (mMessage == null) {
			treeNode.add( new TreeNode( "message"  + " : <null>"));
			} else {
			treeNode.add( mMessage.getNodeTree("message"));
			}
		
	return treeNode;
	}
	
}

