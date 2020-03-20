
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





public class DistBdxGwyUpdateItem implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 26);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mMulticastGroupId;
	private String  mSubject;
	private byte[]  mMessage;

public DistBdxGwyUpdateItem()
{
 
}


public  DistBdxGwyUpdateItem(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setMulticastGroupId( long pMulticastGroupId ) {
   mMulticastGroupId  = pMulticastGroupId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getMulticastGroupId() {
  return  mMulticastGroupId ;
}





public void setSubject( String pSubject ) {
   mSubject  = pSubject ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getSubject() {
  return  mSubject ;
}





public void setMessage( byte[] pMessage ) {
   mMessage  = pMessage ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  byte[] getMessage() {
  return  mMessage ;
}

   

public String getMessageName() {
   return "DistBdxGwyUpdateItem";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwyUpdateItem";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 26;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mMulticastGroupId Type: long
	    */
	   
	pEncoder.add(  mMulticastGroupId );
	    /**
	    * Encode Attribute: mSubject Type: String
	    */
	   
	pEncoder.add(  mSubject );
	    /**
	    * Encode Attribute: mMessage Type: byte[]
	    */
	   
	pEncoder.add(  mMessage );
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
	    * Decoding Attribute: mMulticastGroupId Type: long
	    */
	   
     mMulticastGroupId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mSubject Type: String
	    */
	   
     mSubject = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mMessage Type: byte[]
	    */
	   
     mMessage = pDecoder.readBytes(); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwyUpdateItem\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwyUpdateItem\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mMulticastGroupId: ");
       tSB.append( String.valueOf( mMulticastGroupId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mSubject: ");
       tSB.append( String.valueOf( mSubject ));
       tSB.append("\n"); 
        tSB.append( blanks( pCount + 2 ) + "mMessage: ");
        tSB.append( MessageAux.format( mMessage));
        tSB.append("\n");
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "multicastGroupId"  + " : " + String.valueOf( mMulticastGroupId )));
			treeNode.add( new TreeNode( "subject"  + " : " + String.valueOf( mSubject )));
			treeNode.add( TreeNode.createArray(  "message", mMessage ));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwyUpdateItem");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwyUpdateItem]");
	}
	
			treeNode.add( new TreeNode( "multicastGroupId"  + " : " + String.valueOf( mMulticastGroupId )));
			treeNode.add( new TreeNode( "subject"  + " : " + String.valueOf( mSubject )));
			treeNode.add( TreeNode.createArray(  "message", mMessage ));
	return treeNode;
	}
	
}

