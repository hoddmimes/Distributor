
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





public class DistBdxGwySubscrInterest implements MessageInterface   
{
    public static final int MESSAGE_ID = ((1 << 16) + 24);
	public static final int ADD_INTEREST = 1; 
   public static final int REMOVE_INTEREST = 0; 
   
   protected volatile byte[]  mMessageBytesCached=null;
   
  
   
	private int  mAction;
	private List<DistBdxGwySubscrInterestItem> mInterests;

public DistBdxGwySubscrInterest()
{
 
}


public  DistBdxGwySubscrInterest(byte[]  pMessageByteArray ) {
  
  MessageBinDecoder tDecoder = new MessageBinDecoder( pMessageByteArray );
  this.decode( tDecoder );
}






public void setAction( int pAction ) {
   mAction  = pAction ;
   synchronized( this) { 
   	mMessageBytesCached = null;
   }
}

 
public  int getAction() {
  return  mAction ;
}


public void addInterestsToArray( DistBdxGwySubscrInterestItem pObject ) {
    if (pObject == null) {
      return;
    }
    
    if ( mInterests == null) {
       mInterests = new ArrayList<DistBdxGwySubscrInterestItem>();
    }
    mInterests.add( pObject );
    
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void setInterests( List<DistBdxGwySubscrInterestItem> pInterests ) {
      if (pInterests == null) {
        mInterests  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pInterests.size();
      
      if ( mInterests == null) 
	  mInterests = new ArrayList<DistBdxGwySubscrInterestItem>( tSize + 1 );
	  

	mInterests .addAll( pInterests );

	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

public void setInterests( DistBdxGwySubscrInterestItem[] pInterests ) {
      if (pInterests == null) {
        mInterests  = null;
        synchronized( this) { 
        	mMessageBytesCached = null;
        }
        return;
      }
      
      int tSize =  pInterests.length;
      
      if ( mInterests == null) 
	  mInterests = new ArrayList<DistBdxGwySubscrInterestItem>( tSize + 1 );
	  
	for( int i = 0; i < tSize; i++ ) {
	    mInterests .add( pInterests[i]);
	}
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}

	
public void addInterests( List<DistBdxGwySubscrInterestItem> pInterests ) {

      if ( mInterests == null) 
	     mInterests = new ArrayList<DistBdxGwySubscrInterestItem>();
	  
	mInterests .addAll(  pInterests );
	synchronized( this) { 
       	mMessageBytesCached = null;
    }
}


public  List<DistBdxGwySubscrInterestItem> getInterests() {
	
	if (mInterests == null)
	  return null;
	
	List<DistBdxGwySubscrInterestItem> tList = new ArrayList<DistBdxGwySubscrInterestItem>( mInterests.size() );
	tList.addAll(  mInterests );
	return tList;
}


   

public String getMessageName() {
   return "DistBdxGwySubscrInterest";
}

public String getFullMessageName() {
    return "com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterest";
}
 
 public int getMessageId() {
   	return  (1 << 16) + 24;
  }
   


  public void encode( MessageBinEncoder pEncoder) {
    encode( pEncoder, false );
  }

  public void encode( MessageBinEncoder pEncoder, boolean pIsExtensionInvoked ) {
  		if (!pIsExtensionInvoked) {
                  pEncoder.add( getMessageId());
                }
  		
	    /**
	    * Encode Attribute: mAction Type: int
	    */
	   
	pEncoder.add(  mAction );
	    /**
	    * Encode Attribute: mInterests Type: DistBdxGwySubscrInterestItem
	    */
	   
     pEncoder.addMessageArray( mInterests );
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
	    * Decoding Attribute: mAction Type: int
	    */
	   
     mAction = pDecoder.readInt(); 
	   /**
	    * Decoding Attribute: mInterests Type: DistBdxGwySubscrInterestItem
	    */
	   
    mInterests = (List<DistBdxGwySubscrInterestItem>) pDecoder.readMessageArray(  DistBdxGwySubscrInterestItem.class ); 
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
    	tSB.append("<Extending Message: " + "\"DistBdxGwySubscrInterest\"  Id: " + Integer.toHexString(getMessageId()) + ">\n");
    } else {
    		tSB.append("Message: " + "\"DistBdxGwySubscrInterest\"  Id: " +  Integer.toHexString(getMessageId())  + "\n");
    }
     		
     	
       tSB.append( blanks( pCount + 2 ) + "mAction: ");
       tSB.append( String.valueOf( mAction ));
       tSB.append("\n"); 
     tSB.append( blanks( pCount + 2 ) + "mInterests[]: ");
     if ( mInterests == null) {
       tSB.append("<null>");
     } else {
     	    tSB.append("\n");
          int tSize = mInterests.size();
          for( int i = 0; i < tSize; i++ ) {
             DistBdxGwySubscrInterestItem tMsg = (DistBdxGwySubscrInterestItem) mInterests.get( i );
            tSB.append(  tMsg.toString( pCount + 4 ) );
          }
     } 
     tSB.append("\n");

	return tSB.toString();
  }


	public void treeAddMessageAsSuperClass( TreeNode treeNode ) {

	
			treeNode.add( new TreeNode( "action"  + " : " + String.valueOf( mAction )));
				treeNode.add( TreeNode.createArray( "interests", mInterests ));
	}




	public TreeNode getNodeTree(String pMessageAttributeName ) {
	TreeNode treeNode = null;
	if (pMessageAttributeName  == null) {
	treeNode = new TreeNode("DistBdxGwySubscrInterest");
	} else {
	treeNode =  new TreeNode( pMessageAttributeName  + " [DistBdxGwySubscrInterest]");
	}
	
			treeNode.add( new TreeNode( "action"  + " : " + String.valueOf( mAction )));
					treeNode.add( TreeNode.createArray( "interests", mInterests ));
	return treeNode;
	}
	
}

