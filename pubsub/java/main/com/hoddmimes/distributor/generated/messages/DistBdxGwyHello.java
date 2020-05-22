
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





public class DistBdxGwyHello implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 22);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mBdxGwyName;

public DistBdxGwyHello()
{
 
}


public  DistBdxGwyHello(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setBdxGwyName( String pBdxGwyName ) {
   mBdxGwyName  = pBdxGwyName ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getBdxGwyName() {
  return  mBdxGwyName ;
}

   

public String getMessageName() {
   return "DistBdxGwyHello";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwyHello";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 22;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mBdxGwyName Type: String
	    */
	   
	pEncoder.add(  mBdxGwyName );
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
	    * Decoding Attribute: mBdxGwyName Type: String
	    */
	   
     mBdxGwyName = pDecoder.readString(); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwyHello\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwyHello\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mBdxGwyName: ");
       tSB.append( String.valueOf( mBdxGwyName ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "bdxGwyName"  + " : " + String.valueOf( mBdxGwyName )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwyHello");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwyHello]");
	}
	
			treeNode.add( new TreeNode( "bdxGwyName"  + " : " + String.valueOf( mBdxGwyName )));
	return treeNode;
	}
	
}

