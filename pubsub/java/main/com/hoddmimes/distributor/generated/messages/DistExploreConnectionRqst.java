
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





public class DistExploreConnectionRqst implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 12);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mDistributorId;
	private long  mConnectionId;

public DistExploreConnectionRqst()
{
 
}


public  DistExploreConnectionRqst(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setDistributorId( long pDistributorId ) {
   mDistributorId  = pDistributorId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getDistributorId() {
  return  mDistributorId ;
}





public void setConnectionId( long pConnectionId ) {
   mConnectionId  = pConnectionId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getConnectionId() {
  return  mConnectionId ;
}

   

public String getMessageName() {
   return "DistExploreConnectionRqst";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreConnectionRqst";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 12;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mDistributorId Type: long
	    */
	   
	pEncoder.add(  mDistributorId );
	    /**
	    * Encode Attribute: mConnectionId Type: long
	    */
	   
	pEncoder.add(  mConnectionId );
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
	    * Decoding Attribute: mDistributorId Type: long
	    */
	   
     mDistributorId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mConnectionId Type: long
	    */
	   
     mConnectionId = pDecoder.readLong(); 
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
    	tSB.append("<Extending Message: " + "\"DistExploreConnectionRqst\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreConnectionRqst\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mDistributorId: ");
       tSB.append( String.valueOf( mDistributorId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mConnectionId: ");
       tSB.append( String.valueOf( mConnectionId ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreConnectionRqst");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreConnectionRqst]");
	}
	
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
	return treeNode;
	}
	
}

