
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





public class DistBdxGwyPingRsp implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 25);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private int  mRqstId;
	private long  mStartTime;
	private String  mBdxGwyName;

public DistBdxGwyPingRsp()
{
 
}


public  DistBdxGwyPingRsp(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setRqstId( int pRqstId ) {
   mRqstId  = pRqstId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getRqstId() {
  return  mRqstId ;
}





public void setStartTime( long pStartTime ) {
   mStartTime  = pStartTime ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getStartTime() {
  return  mStartTime ;
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
   return "DistBdxGwyPingRsp";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwyPingRsp";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 25;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mRqstId Type: int
	    */
	   
	pEncoder.add(  mRqstId );
	    /**
	    * Encode Attribute: mStartTime Type: long
	    */
	   
	pEncoder.add(  mStartTime );
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
	    * Decoding Attribute: mRqstId Type: int
	    */
	   
     mRqstId = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mStartTime Type: long
	    */
	   
     mStartTime = pDecoder.readLong(); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwyPingRsp\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwyPingRsp\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mRqstId: ");
       tSB.append( String.valueOf( mRqstId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mStartTime: ");
       tSB.append( String.valueOf( mStartTime ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mBdxGwyName: ");
       tSB.append( String.valueOf( mBdxGwyName ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "rqstId"  + " : " + String.valueOf( mRqstId )));
			treeNode.add( new TreeNode( "startTime"  + " : " + String.valueOf( mStartTime )));
			treeNode.add( new TreeNode( "bdxGwyName"  + " : " + String.valueOf( mBdxGwyName )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwyPingRsp");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwyPingRsp]");
	}
	
			treeNode.add( new TreeNode( "rqstId"  + " : " + String.valueOf( mRqstId )));
			treeNode.add( new TreeNode( "startTime"  + " : " + String.valueOf( mStartTime )));
			treeNode.add( new TreeNode( "bdxGwyName"  + " : " + String.valueOf( mBdxGwyName )));
	return treeNode;
	}
	
}

