
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





public class QueueSizeItem implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 10);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mSize;
	private int  mPeakSize;
	private String  mPeakTime;

public QueueSizeItem()
{
 
}


public  QueueSizeItem(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setSize( long pSize ) {
   mSize  = pSize ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getSize() {
  return  mSize ;
}





public void setPeakSize( int pPeakSize ) {
   mPeakSize  = pPeakSize ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getPeakSize() {
  return  mPeakSize ;
}





public void setPeakTime( String pPeakTime ) {
   mPeakTime  = pPeakTime ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getPeakTime() {
  return  mPeakTime ;
}

   

public String getMessageName() {
   return "QueueSizeItem";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.QueueSizeItem";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 10;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mSize Type: long
	    */
	   
	pEncoder.add(  mSize );
	    /**
	    * Encode Attribute: mPeakSize Type: int
	    */
	   
	pEncoder.add(  mPeakSize );
	    /**
	    * Encode Attribute: mPeakTime Type: String
	    */
	   
	pEncoder.add(  mPeakTime );
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
	    * Decoding Attribute: mSize Type: long
	    */
	   
     mSize = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mPeakSize Type: int
	    */
	   
     mPeakSize = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mPeakTime Type: String
	    */
	   
     mPeakTime = pDecoder.readString(); 
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
    	tSB.append("<Extending Message: " + "\"QueueSizeItem\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"QueueSizeItem\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mSize: ");
       tSB.append( String.valueOf( mSize ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mPeakSize: ");
       tSB.append( String.valueOf( mPeakSize ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mPeakTime: ");
       tSB.append( String.valueOf( mPeakTime ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "size"  + " : " + String.valueOf( mSize )));
			treeNode.add( new TreeNode( "peakSize"  + " : " + String.valueOf( mPeakSize )));
			treeNode.add( new TreeNode( "peakTime"  + " : " + String.valueOf( mPeakTime )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("QueueSizeItem");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [QueueSizeItem]");
	}
	
			treeNode.add( new TreeNode( "size"  + " : " + String.valueOf( mSize )));
			treeNode.add( new TreeNode( "peakSize"  + " : " + String.valueOf( mPeakSize )));
			treeNode.add( new TreeNode( "peakTime"  + " : " + String.valueOf( mPeakTime )));
	return treeNode;
	}
	
}

