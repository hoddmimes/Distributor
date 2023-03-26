
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





public class DistBdxGwySubscrInterestItem implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 23);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mMulticastGroupId;
	private String  mHandler;
	private String  mSubject;

public DistBdxGwySubscrInterestItem()
{
 
}


public  DistBdxGwySubscrInterestItem(byte[]  pMessageByteArray ) {
  
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





public void setHandler( String pHandler ) {
   mHandler  = pHandler ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getHandler() {
  return  mHandler ;
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

   

public String getMessageName() {
   return "DistBdxGwySubscrInterestItem";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterestItem";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 23;
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
	    * Encode Attribute: mHandler Type: String
	    */
	   
	pEncoder.add(  mHandler );
	    /**
	    * Encode Attribute: mSubject Type: String
	    */
	   
	pEncoder.add(  mSubject );
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
	    * Decoding Attribute: mHandler Type: String
	    */
	   
     mHandler = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mSubject Type: String
	    */
	   
     mSubject = pDecoder.readString(); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwySubscrInterestItem\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwySubscrInterestItem\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mMulticastGroupId: ");
       tSB.append( String.valueOf( mMulticastGroupId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mHandler: ");
       tSB.append( String.valueOf( mHandler ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mSubject: ");
       tSB.append( String.valueOf( mSubject ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "multicastGroupId"  + " : " + String.valueOf( mMulticastGroupId )));
			treeNode.add( new TreeNode( "handler"  + " : " + String.valueOf( mHandler )));
			treeNode.add( new TreeNode( "subject"  + " : " + String.valueOf( mSubject )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwySubscrInterestItem");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwySubscrInterestItem]");
	}
	
			treeNode.add( new TreeNode( "multicastGroupId"  + " : " + String.valueOf( mMulticastGroupId )));
			treeNode.add( new TreeNode( "handler"  + " : " + String.valueOf( mHandler )));
			treeNode.add( new TreeNode( "subject"  + " : " + String.valueOf( mSubject )));
	return treeNode;
	}
	
}

