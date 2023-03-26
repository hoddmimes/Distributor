
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





public class DistDomainConnectionEntry implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 2);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mConnectionId;
	private String  mMcaAddress;
	private int  mMcaPort;
	private int  mSubscriptions;
	private int  mInRetransmissions;
	private int  mOutRetransmissions;

public DistDomainConnectionEntry()
{
 
}


public  DistDomainConnectionEntry(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
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





public void setMcaAddress( String pMcaAddress ) {
   mMcaAddress  = pMcaAddress ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getMcaAddress() {
  return  mMcaAddress ;
}





public void setMcaPort( int pMcaPort ) {
   mMcaPort  = pMcaPort ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getMcaPort() {
  return  mMcaPort ;
}





public void setSubscriptions( int pSubscriptions ) {
   mSubscriptions  = pSubscriptions ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getSubscriptions() {
  return  mSubscriptions ;
}





public void setInRetransmissions( int pInRetransmissions ) {
   mInRetransmissions  = pInRetransmissions ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getInRetransmissions() {
  return  mInRetransmissions ;
}





public void setOutRetransmissions( int pOutRetransmissions ) {
   mOutRetransmissions  = pOutRetransmissions ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getOutRetransmissions() {
  return  mOutRetransmissions ;
}

   

public String getMessageName() {
   return "DistDomainConnectionEntry";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistDomainConnectionEntry";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 2;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mConnectionId Type: long
	    */
	   
	pEncoder.add(  mConnectionId );
	    /**
	    * Encode Attribute: mMcaAddress Type: String
	    */
	   
	pEncoder.add(  mMcaAddress );
	    /**
	    * Encode Attribute: mMcaPort Type: int
	    */
	   
	pEncoder.add(  mMcaPort );
	    /**
	    * Encode Attribute: mSubscriptions Type: int
	    */
	   
	pEncoder.add(  mSubscriptions );
	    /**
	    * Encode Attribute: mInRetransmissions Type: int
	    */
	   
	pEncoder.add(  mInRetransmissions );
	    /**
	    * Encode Attribute: mOutRetransmissions Type: int
	    */
	   
	pEncoder.add(  mOutRetransmissions );
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
	    * Decoding Attribute: mConnectionId Type: long
	    */
	   
     mConnectionId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mMcaAddress Type: String
	    */
	   
     mMcaAddress = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mMcaPort Type: int
	    */
	   
     mMcaPort = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mSubscriptions Type: int
	    */
	   
     mSubscriptions = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mInRetransmissions Type: int
	    */
	   
     mInRetransmissions = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mOutRetransmissions Type: int
	    */
	   
     mOutRetransmissions = pDecoder.readInt(); 
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
    	tSB.append("<Extending Message: " + "\"DistDomainConnectionEntry\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistDomainConnectionEntry\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mConnectionId: ");
       tSB.append( String.valueOf( mConnectionId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMcaAddress: ");
       tSB.append( String.valueOf( mMcaAddress ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMcaPort: ");
       tSB.append( String.valueOf( mMcaPort ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mSubscriptions: ");
       tSB.append( String.valueOf( mSubscriptions ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mInRetransmissions: ");
       tSB.append( String.valueOf( mInRetransmissions ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mOutRetransmissions: ");
       tSB.append( String.valueOf( mOutRetransmissions ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistDomainConnectionEntry");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistDomainConnectionEntry]");
	}
	
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
	return treeNode;
	}
	
}

