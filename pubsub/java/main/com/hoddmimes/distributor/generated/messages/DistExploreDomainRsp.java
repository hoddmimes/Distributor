
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





public class DistExploreDomainRsp implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 5);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private DistDomainDistributorEntry mDistributor;

public DistExploreDomainRsp()
{
 
}


public  DistExploreDomainRsp(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}



public  DistDomainDistributorEntry  getDistributor() {
  return mDistributor;
}

public  void setDistributor(DistDomainDistributorEntry  pDistributor) {
  mDistributor = pDistributor;
}
	

   

public String getMessageName() {
   return "DistExploreDomainRsp";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreDomainRsp";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 5;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mDistributor Type: DistDomainDistributorEntry
	    */
	     
    pEncoder.add( mDistributor );
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
	    * Decoding Attribute: mDistributor Type: DistDomainDistributorEntry
	    */
	   
     mDistributor = (DistDomainDistributorEntry) pDecoder.readMessage( DistDomainDistributorEntry.class );  
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
    	tSB.append("<Extending Message: " + "\"DistExploreDomainRsp\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreDomainRsp\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
     tSB.append( blanks( pCount + 2 ) + "mDistributor: ");
     if ( mDistributor == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mDistributor.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			if (mDistributor == null) {
			treeNode.add( new TreeNode( "distributor"  + " : <null>"));
			} else {
			treeNode.add( mDistributor.getNodeTree("distributor"));
			}
		
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreDomainRsp");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreDomainRsp]");
	}
	
			if (mDistributor == null) {
			treeNode.add( new TreeNode( "distributor"  + " : <null>"));
			} else {
			treeNode.add( mDistributor.getNodeTree("distributor"));
			}
		
	return treeNode;
	}
	
}

