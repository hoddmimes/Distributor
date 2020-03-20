
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





public class DistExploreRetransmissonsRsp implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 17);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private String  mMcaAddress;
	private int  mMcaPort;
	private int  mTotalInRqst;
	private int  mTotalOutRqst;
	private int  mTotalSeenRqst;
	private String[]  mInHosts;
	private String[]  mOutHosts;

public DistExploreRetransmissonsRsp()
{
 
}


public  DistExploreRetransmissonsRsp(byte[]  pMessageByteArray ) {
  
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





public void setTotalInRqst( int pTotalInRqst ) {
   mTotalInRqst  = pTotalInRqst ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getTotalInRqst() {
  return  mTotalInRqst ;
}





public void setTotalOutRqst( int pTotalOutRqst ) {
   mTotalOutRqst  = pTotalOutRqst ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getTotalOutRqst() {
  return  mTotalOutRqst ;
}





public void setTotalSeenRqst( int pTotalSeenRqst ) {
   mTotalSeenRqst  = pTotalSeenRqst ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getTotalSeenRqst() {
  return  mTotalSeenRqst ;
}

public void addInHostsToArray( String pValue) 
{
   if (pValue == null) {
      return;
   }
   
    if (mInHosts == null) {

      String[] mInHosts = new String[1]; 
      mInHosts[0] = pValue;
	} else {
		int tSize =  mInHosts.length + 1;

         String[] tArray = new String[tSize + 1]; 		
         for( int i = 0; i < tSize - 1; i++ ) {
         	tArray[i] =  mInHosts[i];
         }
         tArray[ tSize - 1] = pValue;
         mInHosts = tArray;	
	}
    synchronized (this) {
      mMessageBytesCached = null;
    }
}





public void setInHosts( String[] pInHosts ) {
   mInHosts  = pInHosts ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String[] getInHosts() {
  return  mInHosts ;
}

public void addOutHostsToArray( String pValue) 
{
   if (pValue == null) {
      return;
   }
   
    if (mOutHosts == null) {

      String[] mOutHosts = new String[1]; 
      mOutHosts[0] = pValue;
	} else {
		int tSize =  mOutHosts.length + 1;

         String[] tArray = new String[tSize + 1]; 		
         for( int i = 0; i < tSize - 1; i++ ) {
         	tArray[i] =  mOutHosts[i];
         }
         tArray[ tSize - 1] = pValue;
         mOutHosts = tArray;	
	}
    synchronized (this) {
      mMessageBytesCached = null;
    }
}





public void setOutHosts( String[] pOutHosts ) {
   mOutHosts  = pOutHosts ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  String[] getOutHosts() {
  return  mOutHosts ;
}

   

public String getMessageName() {
   return "DistExploreRetransmissonsRsp";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistExploreRetransmissonsRsp";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 17;
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
	    * Encode Attribute: mTotalInRqst Type: int
	    */
	   
	pEncoder.add(  mTotalInRqst );
	    /**
	    * Encode Attribute: mTotalOutRqst Type: int
	    */
	   
	pEncoder.add(  mTotalOutRqst );
	    /**
	    * Encode Attribute: mTotalSeenRqst Type: int
	    */
	   
	pEncoder.add(  mTotalSeenRqst );
	    /**
	    * Encode Attribute: mInHosts Type: String
	    */
	   
     pEncoder.add( mInHosts );
	    /**
	    * Encode Attribute: mOutHosts Type: String
	    */
	   
     pEncoder.add( mOutHosts );
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
	    * Decoding Attribute: mTotalInRqst Type: int
	    */
	   
     mTotalInRqst = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mTotalOutRqst Type: int
	    */
	   
     mTotalOutRqst = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mTotalSeenRqst Type: int
	    */
	   
     mTotalSeenRqst = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mInHosts Type: String
	    */
	   
     mInHosts =  pDecoder.readStringArray();  
	   /**
	    * Decoding Attribute: mOutHosts Type: String
	    */
	   
     mOutHosts =  pDecoder.readStringArray();  
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
    	tSB.append("<Extending Message: " + "\"DistExploreRetransmissonsRsp\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistExploreRetransmissonsRsp\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mMcaAddress: ");
       tSB.append( String.valueOf( mMcaAddress ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mMcaPort: ");
       tSB.append( String.valueOf( mMcaPort ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mTotalInRqst: ");
       tSB.append( String.valueOf( mTotalInRqst ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mTotalOutRqst: ");
       tSB.append( String.valueOf( mTotalOutRqst ));
       tSB.append("\n"); 
       tSB.append( blanks( pCount + 2 ) + "mTotalSeenRqst: ");
       tSB.append( String.valueOf( mTotalSeenRqst ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mInHosts[]: ");
     tSB.append( MessageAux.format( mInHosts ));
     tSB.append("\n");
     tSB.append( blanks( pCount + 2 ) + "mOutHosts[]: ");
     tSB.append( MessageAux.format( mOutHosts ));
     tSB.append("\n");
	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "totalInRqst"  + " : " + String.valueOf( mTotalInRqst )));
			treeNode.add( new TreeNode( "totalOutRqst"  + " : " + String.valueOf( mTotalOutRqst )));
			treeNode.add( new TreeNode( "totalSeenRqst"  + " : " + String.valueOf( mTotalSeenRqst )));
				treeNode.add( TreeNode.createArray( "inHosts", mInHosts ));
				treeNode.add( TreeNode.createArray( "outHosts", mOutHosts ));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistExploreRetransmissonsRsp");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistExploreRetransmissonsRsp]");
	}
	
			treeNode.add( new TreeNode( "mcaAddress"  + " : " + String.valueOf( mMcaAddress )));
			treeNode.add( new TreeNode( "mcaPort"  + " : " + String.valueOf( mMcaPort )));
			treeNode.add( new TreeNode( "totalInRqst"  + " : " + String.valueOf( mTotalInRqst )));
			treeNode.add( new TreeNode( "totalOutRqst"  + " : " + String.valueOf( mTotalOutRqst )));
			treeNode.add( new TreeNode( "totalSeenRqst"  + " : " + String.valueOf( mTotalSeenRqst )));
					treeNode.add( TreeNode.createArray( "inHosts", mInHosts ));
					treeNode.add( TreeNode.createArray( "outHosts", mOutHosts ));
	return treeNode;
	}
	
}

