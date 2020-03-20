
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





public class DistBdxGwyUpdate implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 27);
	
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private List<DistBdxGwyUpdateItem> mUpdates;

public DistBdxGwyUpdate()
{
 
}


public  DistBdxGwyUpdate(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}



public void addUpdatesToArray( DistBdxGwyUpdateItem pObject ) {
    if (pObject == null) {
      return;
    }
    
    if ( mUpdates == null) {
       mUpdates = new ArrayList<DistBdxGwyUpdateItem>();
    }
    mUpdates.add( pObject );
    
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void setUpdates( List<DistBdxGwyUpdateItem> pUpdates ) {
      if (pUpdates == null) {
        mUpdates  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pUpdates.size();
      
      if ( mUpdates == null) 
	  mUpdates = new ArrayList<DistBdxGwyUpdateItem>( tSize + 1 );
	  

	mUpdates .addAll( pUpdates );

	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void setUpdates( DistBdxGwyUpdateItem[] pUpdates ) {
      if (pUpdates == null) {
        mUpdates  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pUpdates.length;
      
      if ( mUpdates == null) 
	  mUpdates = new ArrayList<DistBdxGwyUpdateItem>( tSize + 1 );
	  
	for( int i = 0; i < tSize; i++ ) {
	    mUpdates .add( pUpdates[i]);
	}
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

	
public void addUpdates( List<DistBdxGwyUpdateItem> pUpdates ) {

      if ( mUpdates == null) 
	     mUpdates = new ArrayList<DistBdxGwyUpdateItem>();
	  
	mUpdates .addAll(  pUpdates );
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}


public  List<DistBdxGwyUpdateItem> getUpdates() {
	
	if (mUpdates == null)
	  return null;
	
	List<DistBdxGwyUpdateItem> tList = new ArrayList<DistBdxGwyUpdateItem>( mUpdates.size() );
	tList.addAll(  mUpdates );
	return tList;
}


   

public String getMessageName() {
   return "DistBdxGwyUpdate";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwyUpdate";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 27;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mUpdates Type: DistBdxGwyUpdateItem
	    */
	   
     pEncoder.addMessageArray( mUpdates );
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
	    * Decoding Attribute: mUpdates Type: DistBdxGwyUpdateItem
	    */
	   
    mUpdates = (List<DistBdxGwyUpdateItem>) pDecoder.readMessageArray(  DistBdxGwyUpdateItem.class ); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwyUpdate\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwyUpdate\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
     tSB.append( blanks( pCount + 2 ) + "mUpdates[]: ");
     if ( mUpdates == null) {
       tSB.append("<null>");
     } else {
     	    tSB.append("\n");
          int tSize = mUpdates.size();
          for( int i = 0; i < tSize; i++ ) {
             DistBdxGwyUpdateItem tMsg = (DistBdxGwyUpdateItem) mUpdates.get( i );
            tSB.append(  tMsg.toString( pCount + 4 ) );
          }
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
				treeNode.add( TreeNode.createArray( "updates", mUpdates ));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwyUpdate");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwyUpdate]");
	}
	
					treeNode.add( TreeNode.createArray( "updates", mUpdates ));
	return treeNode;
	}
	
}

