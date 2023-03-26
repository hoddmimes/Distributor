
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





public class DataRateItem implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 9);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private long  mTotal;
	private int  mCurrValue;
	private int  mPeakValue;
	private String  mPeakTime;

public DataRateItem()
{
 
}


public  DataRateItem(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setTotal( long pTotal ) {
   mTotal  = pTotal ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getTotal() {
  return  mTotal ;
}





public void setCurrValue( int pCurrValue ) {
   mCurrValue  = pCurrValue ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getCurrValue() {
  return  mCurrValue ;
}





public void setPeakValue( int pPeakValue ) {
   mPeakValue  = pPeakValue ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getPeakValue() {
  return  mPeakValue ;
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
   return "DataRateItem";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DataRateItem";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 9;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mTotal Type: long
	    */
	   
	pEncoder.add(  mTotal );
	    /**
	    * Encode Attribute: mCurrValue Type: int
	    */
	   
	pEncoder.add(  mCurrValue );
	    /**
	    * Encode Attribute: mPeakValue Type: int
	    */
	   
	pEncoder.add(  mPeakValue );
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
	    * Decoding Attribute: mTotal Type: long
	    */
	   
     mTotal = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mCurrValue Type: int
	    */
	   
     mCurrValue = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mPeakValue Type: int
	    */
	   
     mPeakValue = pDecoder.readInt(); 
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
    	tSB.append("<Extending Message: " + "\"DataRateItem\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DataRateItem\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mTotal: ");
       tSB.append( String.valueOf( mTotal ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mCurrValue: ");
       tSB.append( String.valueOf( mCurrValue ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mPeakValue: ");
       tSB.append( String.valueOf( mPeakValue ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mPeakTime: ");
       tSB.append( String.valueOf( mPeakTime ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "total"  + " : " + String.valueOf( mTotal )));
			treeNode.add( new TreeNode( "currValue"  + " : " + String.valueOf( mCurrValue )));
			treeNode.add( new TreeNode( "peakValue"  + " : " + String.valueOf( mPeakValue )));
			treeNode.add( new TreeNode( "peakTime"  + " : " + String.valueOf( mPeakTime )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DataRateItem");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DataRateItem]");
	}
	
			treeNode.add( new TreeNode( "total"  + " : " + String.valueOf( mTotal )));
			treeNode.add( new TreeNode( "currValue"  + " : " + String.valueOf( mCurrValue )));
			treeNode.add( new TreeNode( "peakValue"  + " : " + String.valueOf( mPeakValue )));
			treeNode.add( new TreeNode( "peakTime"  + " : " + String.valueOf( mPeakTime )));
	return treeNode;
	}
	
}

