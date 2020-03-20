
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





public class DistExploreDistributorRqst implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 7);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mDistributorId;

public DistExploreDistributorRqst()
{
 
}


public  DistExploreDistributorRqst(byte[]  pMessageByteArray ) {
  
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

   

public String getMessageName() {
   return "DistExploreDistributorRqst";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreDistributorRqst";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 7;
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
    	tSB.append("<Extending Message: " + "\"DistExploreDistributorRqst\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreDistributorRqst\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mDistributorId: ");
       tSB.append( String.valueOf( mDistributorId ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreDistributorRqst");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreDistributorRqst]");
	}
	
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
	return treeNode;
	}
	
}

