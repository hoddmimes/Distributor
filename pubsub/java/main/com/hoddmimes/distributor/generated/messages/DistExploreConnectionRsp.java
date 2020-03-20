
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





public class DistExploreConnectionRsp implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 13);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private ConnectionEntry mConnection;

public DistExploreConnectionRsp()
{
 
}


public  DistExploreConnectionRsp(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}



public  ConnectionEntry  getConnection() {
  return mConnection;
}

public  void setConnection(ConnectionEntry  pConnection) {
  mConnection = pConnection;
}
	

   

public String getMessageName() {
   return "DistExploreConnectionRsp";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreConnectionRsp";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 13;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mConnection Type: ConnectionEntry
	    */
	     
    pEncoder.add( mConnection );
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
	    * Decoding Attribute: mConnection Type: ConnectionEntry
	    */
	   
     mConnection = (ConnectionEntry) pDecoder.readMessage( ConnectionEntry.class );  
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
    	tSB.append("<Extending Message: " + "\"DistExploreConnectionRsp\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreConnectionRsp\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
     tSB.append( blanks( pCount + 2 ) + "mConnection: ");
     if ( mConnection == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mConnection.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			if (mConnection == null) {
			treeNode.add( new TreeNode( "connection"  + " : <null>"));
			} else {
			treeNode.add( mConnection.getNodeTree("connection"));
			}
		
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreConnectionRsp");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreConnectionRsp]");
	}
	
			if (mConnection == null) {
			treeNode.add( new TreeNode( "connection"  + " : <null>"));
			} else {
			treeNode.add( mConnection.getNodeTree("connection"));
			}
		
	return treeNode;
	}
	
}

