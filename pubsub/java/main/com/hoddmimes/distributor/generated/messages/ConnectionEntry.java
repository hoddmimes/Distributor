
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





public class ConnectionEntry implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 11);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mMcaAddress;
	private int  mMcaPort;
	private long  mConnectionId;
	private int  mPublishers;
	private int  mSubscribers;
	private int  mSubscriptions;
	private int  mInRetransmissions;
	private int  mOutRetransmissions;
	private QueueSizeItem mDeliverUpdateQueue;
	private long  mXtaTotalBytes;
	private long  mXtaTotalSegments;
	private long  mXtaTotalUpdates;
	private DataRateItem mXtaBytes;
	private DataRateItem mXtaSegments;
	private DataRateItem mXtaUpdates;
	private DataRateItem mXtaBytes1min;
	private DataRateItem mXtaSegments1min;
	private DataRateItem mXtaUpdates1min;
	private DataRateItem mXtaBytes5min;
	private DataRateItem mXtaSegments5min;
	private DataRateItem mXtaUpdates5min;
	private long  mRcvTotalBytes;
	private long  mRcvTotalSegments;
	private long  mRcvTotalUpdates;
	private DataRateItem mRcvBytes;
	private DataRateItem mRcvSegments;
	private DataRateItem mRcvUpdates;
	private DataRateItem mRcvBytes1min;
	private DataRateItem mRcvSegments1min;
	private DataRateItem mRcvUpdates1min;
	private DataRateItem mRcvBytes5min;
	private DataRateItem mRcvSegments5min;
	private DataRateItem mRcvUpdates5min;

public ConnectionEntry()
{
 
}


public  ConnectionEntry(byte[]  pMessageByteArray ) {
  
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





public void setConnectionId( long pConnectionId ) {
   mConnectionId  = pConnectionId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getConnectionId() {
  return  mConnectionId ;
}





public void setPublishers( int pPublishers ) {
   mPublishers  = pPublishers ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getPublishers() {
  return  mPublishers ;
}





public void setSubscribers( int pSubscribers ) {
   mSubscribers  = pSubscribers ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getSubscribers() {
  return  mSubscribers ;
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


public  QueueSizeItem  getDeliverUpdateQueue() {
  return mDeliverUpdateQueue;
}

public  void setDeliverUpdateQueue(QueueSizeItem  pDeliverUpdateQueue) {
  mDeliverUpdateQueue = pDeliverUpdateQueue;
}
	





public void setXtaTotalBytes( long pXtaTotalBytes ) {
   mXtaTotalBytes  = pXtaTotalBytes ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getXtaTotalBytes() {
  return  mXtaTotalBytes ;
}





public void setXtaTotalSegments( long pXtaTotalSegments ) {
   mXtaTotalSegments  = pXtaTotalSegments ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getXtaTotalSegments() {
  return  mXtaTotalSegments ;
}





public void setXtaTotalUpdates( long pXtaTotalUpdates ) {
   mXtaTotalUpdates  = pXtaTotalUpdates ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getXtaTotalUpdates() {
  return  mXtaTotalUpdates ;
}


public  DataRateItem  getXtaBytes() {
  return mXtaBytes;
}

public  void setXtaBytes(DataRateItem  pXtaBytes) {
  mXtaBytes = pXtaBytes;
}
	


public  DataRateItem  getXtaSegments() {
  return mXtaSegments;
}

public  void setXtaSegments(DataRateItem  pXtaSegments) {
  mXtaSegments = pXtaSegments;
}
	


public  DataRateItem  getXtaUpdates() {
  return mXtaUpdates;
}

public  void setXtaUpdates(DataRateItem  pXtaUpdates) {
  mXtaUpdates = pXtaUpdates;
}
	


public  DataRateItem  getXtaBytes1min() {
  return mXtaBytes1min;
}

public  void setXtaBytes1min(DataRateItem  pXtaBytes1min) {
  mXtaBytes1min = pXtaBytes1min;
}
	


public  DataRateItem  getXtaSegments1min() {
  return mXtaSegments1min;
}

public  void setXtaSegments1min(DataRateItem  pXtaSegments1min) {
  mXtaSegments1min = pXtaSegments1min;
}
	


public  DataRateItem  getXtaUpdates1min() {
  return mXtaUpdates1min;
}

public  void setXtaUpdates1min(DataRateItem  pXtaUpdates1min) {
  mXtaUpdates1min = pXtaUpdates1min;
}
	


public  DataRateItem  getXtaBytes5min() {
  return mXtaBytes5min;
}

public  void setXtaBytes5min(DataRateItem  pXtaBytes5min) {
  mXtaBytes5min = pXtaBytes5min;
}
	


public  DataRateItem  getXtaSegments5min() {
  return mXtaSegments5min;
}

public  void setXtaSegments5min(DataRateItem  pXtaSegments5min) {
  mXtaSegments5min = pXtaSegments5min;
}
	


public  DataRateItem  getXtaUpdates5min() {
  return mXtaUpdates5min;
}

public  void setXtaUpdates5min(DataRateItem  pXtaUpdates5min) {
  mXtaUpdates5min = pXtaUpdates5min;
}
	





public void setRcvTotalBytes( long pRcvTotalBytes ) {
   mRcvTotalBytes  = pRcvTotalBytes ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getRcvTotalBytes() {
  return  mRcvTotalBytes ;
}





public void setRcvTotalSegments( long pRcvTotalSegments ) {
   mRcvTotalSegments  = pRcvTotalSegments ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getRcvTotalSegments() {
  return  mRcvTotalSegments ;
}





public void setRcvTotalUpdates( long pRcvTotalUpdates ) {
   mRcvTotalUpdates  = pRcvTotalUpdates ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getRcvTotalUpdates() {
  return  mRcvTotalUpdates ;
}


public  DataRateItem  getRcvBytes() {
  return mRcvBytes;
}

public  void setRcvBytes(DataRateItem  pRcvBytes) {
  mRcvBytes = pRcvBytes;
}
	


public  DataRateItem  getRcvSegments() {
  return mRcvSegments;
}

public  void setRcvSegments(DataRateItem  pRcvSegments) {
  mRcvSegments = pRcvSegments;
}
	


public  DataRateItem  getRcvUpdates() {
  return mRcvUpdates;
}

public  void setRcvUpdates(DataRateItem  pRcvUpdates) {
  mRcvUpdates = pRcvUpdates;
}
	


public  DataRateItem  getRcvBytes1min() {
  return mRcvBytes1min;
}

public  void setRcvBytes1min(DataRateItem  pRcvBytes1min) {
  mRcvBytes1min = pRcvBytes1min;
}
	


public  DataRateItem  getRcvSegments1min() {
  return mRcvSegments1min;
}

public  void setRcvSegments1min(DataRateItem  pRcvSegments1min) {
  mRcvSegments1min = pRcvSegments1min;
}
	


public  DataRateItem  getRcvUpdates1min() {
  return mRcvUpdates1min;
}

public  void setRcvUpdates1min(DataRateItem  pRcvUpdates1min) {
  mRcvUpdates1min = pRcvUpdates1min;
}
	


public  DataRateItem  getRcvBytes5min() {
  return mRcvBytes5min;
}

public  void setRcvBytes5min(DataRateItem  pRcvBytes5min) {
  mRcvBytes5min = pRcvBytes5min;
}
	


public  DataRateItem  getRcvSegments5min() {
  return mRcvSegments5min;
}

public  void setRcvSegments5min(DataRateItem  pRcvSegments5min) {
  mRcvSegments5min = pRcvSegments5min;
}
	


public  DataRateItem  getRcvUpdates5min() {
  return mRcvUpdates5min;
}

public  void setRcvUpdates5min(DataRateItem  pRcvUpdates5min) {
  mRcvUpdates5min = pRcvUpdates5min;
}
	

   

public String getMessageName() {
   return "ConnectionEntry";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.ConnectionEntry";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 11;
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
	    * Encode Attribute: mConnectionId Type: long
	    */
	   
	pEncoder.add(  mConnectionId );
	    /**
	    * Encode Attribute: mPublishers Type: int
	    */
	   
	pEncoder.add(  mPublishers );
	    /**
	    * Encode Attribute: mSubscribers Type: int
	    */
	   
	pEncoder.add(  mSubscribers );
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
	    /**
	    * Encode Attribute: mDeliverUpdateQueue Type: QueueSizeItem
	    */
	     
    pEncoder.add( mDeliverUpdateQueue );
	    /**
	    * Encode Attribute: mXtaTotalBytes Type: long
	    */
	   
	pEncoder.add(  mXtaTotalBytes );
	    /**
	    * Encode Attribute: mXtaTotalSegments Type: long
	    */
	   
	pEncoder.add(  mXtaTotalSegments );
	    /**
	    * Encode Attribute: mXtaTotalUpdates Type: long
	    */
	   
	pEncoder.add(  mXtaTotalUpdates );
	    /**
	    * Encode Attribute: mXtaBytes Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaBytes );
	    /**
	    * Encode Attribute: mXtaSegments Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaSegments );
	    /**
	    * Encode Attribute: mXtaUpdates Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaUpdates );
	    /**
	    * Encode Attribute: mXtaBytes1min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaBytes1min );
	    /**
	    * Encode Attribute: mXtaSegments1min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaSegments1min );
	    /**
	    * Encode Attribute: mXtaUpdates1min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaUpdates1min );
	    /**
	    * Encode Attribute: mXtaBytes5min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaBytes5min );
	    /**
	    * Encode Attribute: mXtaSegments5min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaSegments5min );
	    /**
	    * Encode Attribute: mXtaUpdates5min Type: DataRateItem
	    */
	     
    pEncoder.add( mXtaUpdates5min );
	    /**
	    * Encode Attribute: mRcvTotalBytes Type: long
	    */
	   
	pEncoder.add(  mRcvTotalBytes );
	    /**
	    * Encode Attribute: mRcvTotalSegments Type: long
	    */
	   
	pEncoder.add(  mRcvTotalSegments );
	    /**
	    * Encode Attribute: mRcvTotalUpdates Type: long
	    */
	   
	pEncoder.add(  mRcvTotalUpdates );
	    /**
	    * Encode Attribute: mRcvBytes Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvBytes );
	    /**
	    * Encode Attribute: mRcvSegments Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvSegments );
	    /**
	    * Encode Attribute: mRcvUpdates Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvUpdates );
	    /**
	    * Encode Attribute: mRcvBytes1min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvBytes1min );
	    /**
	    * Encode Attribute: mRcvSegments1min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvSegments1min );
	    /**
	    * Encode Attribute: mRcvUpdates1min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvUpdates1min );
	    /**
	    * Encode Attribute: mRcvBytes5min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvBytes5min );
	    /**
	    * Encode Attribute: mRcvSegments5min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvSegments5min );
	    /**
	    * Encode Attribute: mRcvUpdates5min Type: DataRateItem
	    */
	     
    pEncoder.add( mRcvUpdates5min );
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
	    * Decoding Attribute: mConnectionId Type: long
	    */
	   
     mConnectionId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mPublishers Type: int
	    */
	   
     mPublishers = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mSubscribers Type: int
	    */
	   
     mSubscribers = pDecoder.readInt(); 
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
	   /**
	    * Decoding Attribute: mDeliverUpdateQueue Type: QueueSizeItem
	    */
	   
     mDeliverUpdateQueue = (QueueSizeItem) pDecoder.readMessage( QueueSizeItem.class );  
	   /**
	    * Decoding Attribute: mXtaTotalBytes Type: long
	    */
	   
     mXtaTotalBytes = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mXtaTotalSegments Type: long
	    */
	   
     mXtaTotalSegments = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mXtaTotalUpdates Type: long
	    */
	   
     mXtaTotalUpdates = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mXtaBytes Type: DataRateItem
	    */
	   
     mXtaBytes = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaSegments Type: DataRateItem
	    */
	   
     mXtaSegments = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaUpdates Type: DataRateItem
	    */
	   
     mXtaUpdates = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaBytes1min Type: DataRateItem
	    */
	   
     mXtaBytes1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaSegments1min Type: DataRateItem
	    */
	   
     mXtaSegments1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaUpdates1min Type: DataRateItem
	    */
	   
     mXtaUpdates1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaBytes5min Type: DataRateItem
	    */
	   
     mXtaBytes5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaSegments5min Type: DataRateItem
	    */
	   
     mXtaSegments5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mXtaUpdates5min Type: DataRateItem
	    */
	   
     mXtaUpdates5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvTotalBytes Type: long
	    */
	   
     mRcvTotalBytes = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mRcvTotalSegments Type: long
	    */
	   
     mRcvTotalSegments = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mRcvTotalUpdates Type: long
	    */
	   
     mRcvTotalUpdates = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mRcvBytes Type: DataRateItem
	    */
	   
     mRcvBytes = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvSegments Type: DataRateItem
	    */
	   
     mRcvSegments = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvUpdates Type: DataRateItem
	    */
	   
     mRcvUpdates = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvBytes1min Type: DataRateItem
	    */
	   
     mRcvBytes1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvSegments1min Type: DataRateItem
	    */
	   
     mRcvSegments1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvUpdates1min Type: DataRateItem
	    */
	   
     mRcvUpdates1min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvBytes5min Type: DataRateItem
	    */
	   
     mRcvBytes5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvSegments5min Type: DataRateItem
	    */
	   
     mRcvSegments5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
	   /**
	    * Decoding Attribute: mRcvUpdates5min Type: DataRateItem
	    */
	   
     mRcvUpdates5min = (DataRateItem) pDecoder.readMessage( DataRateItem.class );  
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
    	tSB.append("<Extending Message: " + "\"ConnectionEntry\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"ConnectionEntry\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mMcaAddress: ");
       tSB.append( String.valueOf( mMcaAddress ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMcaPort: ");
       tSB.append( String.valueOf( mMcaPort ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mConnectionId: ");
       tSB.append( String.valueOf( mConnectionId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mPublishers: ");
       tSB.append( String.valueOf( mPublishers ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mSubscribers: ");
       tSB.append( String.valueOf( mSubscribers ));
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
     tSB.append( blanks( pCount + 2 ) + "mDeliverUpdateQueue: ");
     if ( mDeliverUpdateQueue == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mDeliverUpdateQueue.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

       tSB.append( blanks( pCount + 2 ) + "mXtaTotalBytes: ");
       tSB.append( String.valueOf( mXtaTotalBytes ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mXtaTotalSegments: ");
       tSB.append( String.valueOf( mXtaTotalSegments ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mXtaTotalUpdates: ");
       tSB.append( String.valueOf( mXtaTotalUpdates ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mXtaBytes: ");
     if ( mXtaBytes == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaBytes.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaSegments: ");
     if ( mXtaSegments == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaSegments.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaUpdates: ");
     if ( mXtaUpdates == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaUpdates.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaBytes1min: ");
     if ( mXtaBytes1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaBytes1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaSegments1min: ");
     if ( mXtaSegments1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaSegments1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaUpdates1min: ");
     if ( mXtaUpdates1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaUpdates1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaBytes5min: ");
     if ( mXtaBytes5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaBytes5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaSegments5min: ");
     if ( mXtaSegments5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaSegments5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mXtaUpdates5min: ");
     if ( mXtaUpdates5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mXtaUpdates5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

       tSB.append( blanks( pCount + 2 ) + "mRcvTotalBytes: ");
       tSB.append( String.valueOf( mRcvTotalBytes ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mRcvTotalSegments: ");
       tSB.append( String.valueOf( mRcvTotalSegments ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mRcvTotalUpdates: ");
       tSB.append( String.valueOf( mRcvTotalUpdates ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mRcvBytes: ");
     if ( mRcvBytes == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvBytes.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvSegments: ");
     if ( mRcvSegments == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvSegments.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvUpdates: ");
     if ( mRcvUpdates == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvUpdates.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvBytes1min: ");
     if ( mRcvBytes1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvBytes1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvSegments1min: ");
     if ( mRcvSegments1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvSegments1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvUpdates1min: ");
     if ( mRcvUpdates1min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvUpdates1min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvBytes5min: ");
     if ( mRcvBytes5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvBytes5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvSegments5min: ");
     if ( mRcvSegments5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvSegments5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

     tSB.append( blanks( pCount + 2 ) + "mRcvUpdates5min: ");
     if ( mRcvUpdates5min == null) {
       tSB.append("<null>");
     } else {
          tSB.append( mRcvUpdates5min.toString( pCount + 4 ) );
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
			treeNode.add( new TreeNode( "publishers"  + " : " + String.valueOf( mPublishers )));
			treeNode.add( new TreeNode( "subscribers"  + " : " + String.valueOf( mSubscribers )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
			if (mDeliverUpdateQueue == null) {
			treeNode.add( new TreeNode( "deliverUpdateQueue"  + " : <null>"));
			} else {
			treeNode.add( mDeliverUpdateQueue.getNodeTree("deliverUpdateQueue"));
			}
		
			treeNode.add( new TreeNode( "xtaTotalBytes"  + " : " + String.valueOf( mXtaTotalBytes )));
			treeNode.add( new TreeNode( "xtaTotalSegments"  + " : " + String.valueOf( mXtaTotalSegments )));
			treeNode.add( new TreeNode( "xtaTotalUpdates"  + " : " + String.valueOf( mXtaTotalUpdates )));
			if (mXtaBytes == null) {
			treeNode.add( new TreeNode( "xtaBytes"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes.getNodeTree("xtaBytes"));
			}
		
			if (mXtaSegments == null) {
			treeNode.add( new TreeNode( "xtaSegments"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments.getNodeTree("xtaSegments"));
			}
		
			if (mXtaUpdates == null) {
			treeNode.add( new TreeNode( "xtaUpdates"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates.getNodeTree("xtaUpdates"));
			}
		
			if (mXtaBytes1min == null) {
			treeNode.add( new TreeNode( "xtaBytes1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes1min.getNodeTree("xtaBytes1min"));
			}
		
			if (mXtaSegments1min == null) {
			treeNode.add( new TreeNode( "xtaSegments1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments1min.getNodeTree("xtaSegments1min"));
			}
		
			if (mXtaUpdates1min == null) {
			treeNode.add( new TreeNode( "xtaUpdates1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates1min.getNodeTree("xtaUpdates1min"));
			}
		
			if (mXtaBytes5min == null) {
			treeNode.add( new TreeNode( "xtaBytes5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes5min.getNodeTree("xtaBytes5min"));
			}
		
			if (mXtaSegments5min == null) {
			treeNode.add( new TreeNode( "xtaSegments5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments5min.getNodeTree("xtaSegments5min"));
			}
		
			if (mXtaUpdates5min == null) {
			treeNode.add( new TreeNode( "xtaUpdates5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates5min.getNodeTree("xtaUpdates5min"));
			}
		
			treeNode.add( new TreeNode( "rcvTotalBytes"  + " : " + String.valueOf( mRcvTotalBytes )));
			treeNode.add( new TreeNode( "rcvTotalSegments"  + " : " + String.valueOf( mRcvTotalSegments )));
			treeNode.add( new TreeNode( "rcvTotalUpdates"  + " : " + String.valueOf( mRcvTotalUpdates )));
			if (mRcvBytes == null) {
			treeNode.add( new TreeNode( "rcvBytes"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes.getNodeTree("rcvBytes"));
			}
		
			if (mRcvSegments == null) {
			treeNode.add( new TreeNode( "rcvSegments"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments.getNodeTree("rcvSegments"));
			}
		
			if (mRcvUpdates == null) {
			treeNode.add( new TreeNode( "rcvUpdates"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates.getNodeTree("rcvUpdates"));
			}
		
			if (mRcvBytes1min == null) {
			treeNode.add( new TreeNode( "rcvBytes1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes1min.getNodeTree("rcvBytes1min"));
			}
		
			if (mRcvSegments1min == null) {
			treeNode.add( new TreeNode( "rcvSegments1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments1min.getNodeTree("rcvSegments1min"));
			}
		
			if (mRcvUpdates1min == null) {
			treeNode.add( new TreeNode( "rcvUpdates1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates1min.getNodeTree("rcvUpdates1min"));
			}
		
			if (mRcvBytes5min == null) {
			treeNode.add( new TreeNode( "rcvBytes5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes5min.getNodeTree("rcvBytes5min"));
			}
		
			if (mRcvSegments5min == null) {
			treeNode.add( new TreeNode( "rcvSegments5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments5min.getNodeTree("rcvSegments5min"));
			}
		
			if (mRcvUpdates5min == null) {
			treeNode.add( new TreeNode( "rcvUpdates5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates5min.getNodeTree("rcvUpdates5min"));
			}
		
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("ConnectionEntry");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [ConnectionEntry]");
	}
	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "connectionId"  + " : " + String.valueOf( mConnectionId )));
			treeNode.add( new TreeNode( "publishers"  + " : " + String.valueOf( mPublishers )));
			treeNode.add( new TreeNode( "subscribers"  + " : " + String.valueOf( mSubscribers )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
			if (mDeliverUpdateQueue == null) {
			treeNode.add( new TreeNode( "deliverUpdateQueue"  + " : <null>"));
			} else {
			treeNode.add( mDeliverUpdateQueue.getNodeTree("deliverUpdateQueue"));
			}
		
			treeNode.add( new TreeNode( "xtaTotalBytes"  + " : " + String.valueOf( mXtaTotalBytes )));
			treeNode.add( new TreeNode( "xtaTotalSegments"  + " : " + String.valueOf( mXtaTotalSegments )));
			treeNode.add( new TreeNode( "xtaTotalUpdates"  + " : " + String.valueOf( mXtaTotalUpdates )));
			if (mXtaBytes == null) {
			treeNode.add( new TreeNode( "xtaBytes"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes.getNodeTree("xtaBytes"));
			}
		
			if (mXtaSegments == null) {
			treeNode.add( new TreeNode( "xtaSegments"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments.getNodeTree("xtaSegments"));
			}
		
			if (mXtaUpdates == null) {
			treeNode.add( new TreeNode( "xtaUpdates"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates.getNodeTree("xtaUpdates"));
			}
		
			if (mXtaBytes1min == null) {
			treeNode.add( new TreeNode( "xtaBytes1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes1min.getNodeTree("xtaBytes1min"));
			}
		
			if (mXtaSegments1min == null) {
			treeNode.add( new TreeNode( "xtaSegments1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments1min.getNodeTree("xtaSegments1min"));
			}
		
			if (mXtaUpdates1min == null) {
			treeNode.add( new TreeNode( "xtaUpdates1min"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates1min.getNodeTree("xtaUpdates1min"));
			}
		
			if (mXtaBytes5min == null) {
			treeNode.add( new TreeNode( "xtaBytes5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaBytes5min.getNodeTree("xtaBytes5min"));
			}
		
			if (mXtaSegments5min == null) {
			treeNode.add( new TreeNode( "xtaSegments5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaSegments5min.getNodeTree("xtaSegments5min"));
			}
		
			if (mXtaUpdates5min == null) {
			treeNode.add( new TreeNode( "xtaUpdates5min"  + " : <null>"));
			} else {
			treeNode.add( mXtaUpdates5min.getNodeTree("xtaUpdates5min"));
			}
		
			treeNode.add( new TreeNode( "rcvTotalBytes"  + " : " + String.valueOf( mRcvTotalBytes )));
			treeNode.add( new TreeNode( "rcvTotalSegments"  + " : " + String.valueOf( mRcvTotalSegments )));
			treeNode.add( new TreeNode( "rcvTotalUpdates"  + " : " + String.valueOf( mRcvTotalUpdates )));
			if (mRcvBytes == null) {
			treeNode.add( new TreeNode( "rcvBytes"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes.getNodeTree("rcvBytes"));
			}
		
			if (mRcvSegments == null) {
			treeNode.add( new TreeNode( "rcvSegments"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments.getNodeTree("rcvSegments"));
			}
		
			if (mRcvUpdates == null) {
			treeNode.add( new TreeNode( "rcvUpdates"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates.getNodeTree("rcvUpdates"));
			}
		
			if (mRcvBytes1min == null) {
			treeNode.add( new TreeNode( "rcvBytes1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes1min.getNodeTree("rcvBytes1min"));
			}
		
			if (mRcvSegments1min == null) {
			treeNode.add( new TreeNode( "rcvSegments1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments1min.getNodeTree("rcvSegments1min"));
			}
		
			if (mRcvUpdates1min == null) {
			treeNode.add( new TreeNode( "rcvUpdates1min"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates1min.getNodeTree("rcvUpdates1min"));
			}
		
			if (mRcvBytes5min == null) {
			treeNode.add( new TreeNode( "rcvBytes5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvBytes5min.getNodeTree("rcvBytes5min"));
			}
		
			if (mRcvSegments5min == null) {
			treeNode.add( new TreeNode( "rcvSegments5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvSegments5min.getNodeTree("rcvSegments5min"));
			}
		
			if (mRcvUpdates5min == null) {
			treeNode.add( new TreeNode( "rcvUpdates5min"  + " : <null>"));
			} else {
			treeNode.add( mRcvUpdates5min.getNodeTree("rcvUpdates5min"));
			}
		
	return treeNode;
	}
	
}

