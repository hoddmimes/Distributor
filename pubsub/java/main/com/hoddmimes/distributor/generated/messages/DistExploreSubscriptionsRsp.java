
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





public class DistExploreSubscriptionsRsp implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 15);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mMcaAddress;
	private int  mMcaPort;
	private String[]  mSubscriptions;

public DistExploreSubscriptionsRsp()
{
 
}


public  DistExploreSubscriptionsRsp(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
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

public void addSubscriptionsToArray( String pValue) 
{
   if (pValue == null) {
      return;
   }
   
    if (mSubscriptions == null) {

      String[] mSubscriptions = new String[1]; 
      mSubscriptions[0] = pValue;
	} else {
		int tSize =  mSubscriptions.length + 1;

         String[] tArray = new String[tSize + 1]; 		
         for( int i = 0; i < tSize - 1; i++ ) {
         	tArray[i] =  mSubscriptions[i];
         }
         tArray[ tSize - 1] = pValue;
         mSubscriptions = tArray;	
	}
    synchronized (this) {
      mMessageBytesCached = null;
    }
}





public void setSubscriptions( String[] pSubscriptions ) {
   mSubscriptions  = pSubscriptions ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String[] getSubscriptions() {
  return  mSubscriptions ;
}

   

public String getMessageName() {
   return "DistExploreSubscriptionsRsp";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreSubscriptionsRsp";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 15;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mMcaAddress Type: String
	    */
	   
	pEncoder.add(  mMcaAddress );
	    /**
	    * Encode Attribute: mMcaPort Type: int
	    */
	   
	pEncoder.add(  mMcaPort );
	    /**
	    * Encode Attribute: mSubscriptions Type: String
	    */
	   
     pEncoder.add( mSubscriptions );
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
	    * Decoding Attribute: mMcaAddress Type: String
	    */
	   
     mMcaAddress = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mMcaPort Type: int
	    */
	   
     mMcaPort = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mSubscriptions Type: String
	    */
	   
     mSubscriptions =  pDecoder.readStringArray();  
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
    	tSB.append("<Extending Message: " + "\"DistExploreSubscriptionsRsp\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreSubscriptionsRsp\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mMcaAddress: ");
       tSB.append( String.valueOf( mMcaAddress ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMcaPort: ");
       tSB.append( String.valueOf( mMcaPort ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mSubscriptions[]: ");
     tSB.append( MessageAux.format( mSubscriptions ));
     tSB.append("\n");
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
				treeNode.add( TreeNode.createArray( "subscriptions", mSubscriptions ));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreSubscriptionsRsp");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreSubscriptionsRsp]");
	}
	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
					treeNode.add( TreeNode.createArray( "subscriptions", mSubscriptions ));
	return treeNode;
	}
	
}

