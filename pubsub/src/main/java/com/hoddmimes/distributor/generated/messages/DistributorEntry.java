
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





public class DistributorEntry implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 6);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mHostname;
	private String  mHostaddress;
	private String  mApplicationName;
	private int  mApplicationId;
	private String  mStartTime;
	private int  mConnections;
	private long  mDistributorId;
	private long  mMemMax;
	private long  mMemUsed;
	private long  mMemFree;
	private int  mInRetransmissions;
	private int  mOutRetransmissions;
	private int  mSubscriptions;

public DistributorEntry()
{
 
}


public  DistributorEntry(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setHostname( String pHostname ) {
   mHostname  = pHostname ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getHostname() {
  return  mHostname ;
}





public void setHostaddress( String pHostaddress ) {
   mHostaddress  = pHostaddress ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getHostaddress() {
  return  mHostaddress ;
}





public void setApplicationName( String pApplicationName ) {
   mApplicationName  = pApplicationName ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getApplicationName() {
  return  mApplicationName ;
}





public void setApplicationId( int pApplicationId ) {
   mApplicationId  = pApplicationId ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getApplicationId() {
  return  mApplicationId ;
}





public void setStartTime( String pStartTime ) {
   mStartTime  = pStartTime ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String getStartTime() {
  return  mStartTime ;
}





public void setConnections( int pConnections ) {
   mConnections  = pConnections ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getConnections() {
  return  mConnections ;
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





public void setMemMax( long pMemMax ) {
   mMemMax  = pMemMax ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getMemMax() {
  return  mMemMax ;
}





public void setMemUsed( long pMemUsed ) {
   mMemUsed  = pMemUsed ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getMemUsed() {
  return  mMemUsed ;
}





public void setMemFree( long pMemFree ) {
   mMemFree  = pMemFree ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  long getMemFree() {
  return  mMemFree ;
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





public void setSubscriptions( int pSubscriptions ) {
   mSubscriptions  = pSubscriptions ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getSubscriptions() {
  return  mSubscriptions ;
}

   

public String getMessageName() {
   return "DistributorEntry";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistributorEntry";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 6;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mHostname Type: String
	    */
	   
	pEncoder.add(  mHostname );
	    /**
	    * Encode Attribute: mHostaddress Type: String
	    */
	   
	pEncoder.add(  mHostaddress );
	    /**
	    * Encode Attribute: mApplicationName Type: String
	    */
	   
	pEncoder.add(  mApplicationName );
	    /**
	    * Encode Attribute: mApplicationId Type: int
	    */
	   
	pEncoder.add(  mApplicationId );
	    /**
	    * Encode Attribute: mStartTime Type: String
	    */
	   
	pEncoder.add(  mStartTime );
	    /**
	    * Encode Attribute: mConnections Type: int
	    */
	   
	pEncoder.add(  mConnections );
	    /**
	    * Encode Attribute: mDistributorId Type: long
	    */
	   
	pEncoder.add(  mDistributorId );
	    /**
	    * Encode Attribute: mMemMax Type: long
	    */
	   
	pEncoder.add(  mMemMax );
	    /**
	    * Encode Attribute: mMemUsed Type: long
	    */
	   
	pEncoder.add(  mMemUsed );
	    /**
	    * Encode Attribute: mMemFree Type: long
	    */
	   
	pEncoder.add(  mMemFree );
	    /**
	    * Encode Attribute: mInRetransmissions Type: int
	    */
	   
	pEncoder.add(  mInRetransmissions );
	    /**
	    * Encode Attribute: mOutRetransmissions Type: int
	    */
	   
	pEncoder.add(  mOutRetransmissions );
	    /**
	    * Encode Attribute: mSubscriptions Type: int
	    */
	   
	pEncoder.add(  mSubscriptions );
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
	    * Decoding Attribute: mHostname Type: String
	    */
	   
     mHostname = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mHostaddress Type: String
	    */
	   
     mHostaddress = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mApplicationName Type: String
	    */
	   
     mApplicationName = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mApplicationId Type: int
	    */
	   
     mApplicationId = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mStartTime Type: String
	    */
	   
     mStartTime = pDecoder.readString(); 
	   /**
	    * Decoding Attribute: mConnections Type: int
	    */
	   
     mConnections = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mDistributorId Type: long
	    */
	   
     mDistributorId = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mMemMax Type: long
	    */
	   
     mMemMax = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mMemUsed Type: long
	    */
	   
     mMemUsed = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mMemFree Type: long
	    */
	   
     mMemFree = pDecoder.readLong(); 
	   /**
	    * Decoding Attribute: mInRetransmissions Type: int
	    */
	   
     mInRetransmissions = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mOutRetransmissions Type: int
	    */
	   
     mOutRetransmissions = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mSubscriptions Type: int
	    */
	   
     mSubscriptions = pDecoder.readInt(); 
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
    	tSB.append("<Extending Message: " + "\"DistributorEntry\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistributorEntry\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mHostname: ");
       tSB.append( String.valueOf( mHostname ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mHostaddress: ");
       tSB.append( String.valueOf( mHostaddress ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mApplicationName: ");
       tSB.append( String.valueOf( mApplicationName ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mApplicationId: ");
       tSB.append( String.valueOf( mApplicationId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mStartTime: ");
       tSB.append( String.valueOf( mStartTime ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mConnections: ");
       tSB.append( String.valueOf( mConnections ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mDistributorId: ");
       tSB.append( String.valueOf( mDistributorId ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMemMax: ");
       tSB.append( String.valueOf( mMemMax ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMemUsed: ");
       tSB.append( String.valueOf( mMemUsed ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMemFree: ");
       tSB.append( String.valueOf( mMemFree ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mInRetransmissions: ");
       tSB.append( String.valueOf( mInRetransmissions ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mOutRetransmissions: ");
       tSB.append( String.valueOf( mOutRetransmissions ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mSubscriptions: ");
       tSB.append( String.valueOf( mSubscriptions ));
       tSB.append("\n"); 
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "hostname"  + " : " + String.valueOf( mHostname )));
			treeNode.add( new TreeNode( "hostaddress"  + " : " + String.valueOf( mHostaddress )));
			treeNode.add( new TreeNode( "applicationName"  + " : " + String.valueOf( mApplicationName )));
			treeNode.add( new TreeNode( "applicationId"  + " : " + String.valueOf( mApplicationId )));
			treeNode.add( new TreeNode( "startTime"  + " : " + String.valueOf( mStartTime )));
			treeNode.add( new TreeNode( "connections"  + " : " + String.valueOf( mConnections )));
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
			treeNode.add( new TreeNode( "memMax"  + " : " + String.valueOf( mMemMax )));
			treeNode.add( new TreeNode( "memUsed"  + " : " + String.valueOf( mMemUsed )));
			treeNode.add( new TreeNode( "memFree"  + " : " + String.valueOf( mMemFree )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistributorEntry");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistributorEntry]");
	}
	
			treeNode.add( new TreeNode( "hostname"  + " : " + String.valueOf( mHostname )));
			treeNode.add( new TreeNode( "hostaddress"  + " : " + String.valueOf( mHostaddress )));
			treeNode.add( new TreeNode( "applicationName"  + " : " + String.valueOf( mApplicationName )));
			treeNode.add( new TreeNode( "applicationId"  + " : " + String.valueOf( mApplicationId )));
			treeNode.add( new TreeNode( "startTime"  + " : " + String.valueOf( mStartTime )));
			treeNode.add( new TreeNode( "connections"  + " : " + String.valueOf( mConnections )));
			treeNode.add( new TreeNode( "distributorId"  + " : " + String.valueOf( mDistributorId )));
			treeNode.add( new TreeNode( "memMax"  + " : " + String.valueOf( mMemMax )));
			treeNode.add( new TreeNode( "memUsed"  + " : " + String.valueOf( mMemUsed )));
			treeNode.add( new TreeNode( "memFree"  + " : " + String.valueOf( mMemFree )));
			treeNode.add( new TreeNode( "inRetransmissions"  + " : " + String.valueOf( mInRetransmissions )));
			treeNode.add( new TreeNode( "outRetransmissions"  + " : " + String.valueOf( mOutRetransmissions )));
			treeNode.add( new TreeNode( "subscriptions"  + " : " + String.valueOf( mSubscriptions )));
	return treeNode;
	}
	
}

