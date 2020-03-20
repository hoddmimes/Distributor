package com.hoddmimes.distributor.messaging;

import java.util.Arrays;

public class MessageWrapper implements MessageInterface {


	private MessageInterface mWrappedMessage;
	private byte[] mWrappedMessageBytes;

	public static final int MESSAGE_ID = -1;

	
	
	public MessageWrapper() 
	{
	}
	
	
	public MessageWrapper(MessageInterface pWrappedMessage) {
		mWrappedMessage = pWrappedMessage;
	}

	public MessageWrapper( byte[] pMessageBytes) {
		mWrappedMessageBytes = pMessageBytes;
		mWrappedMessage = null;
	}



	public String getFullMessageClassName() {
		return this.getClass().getName();
	}

	public String getMessageClassName() {
		return this.getClass().getSimpleName();
	}

	public String getWrappedMessageNameSimpleFormat() {
		if (this.mWrappedMessage == null) {
			return "<null>";
		}
		return mWrappedMessage.getClass().getSimpleName();
	}


	public MessageInterface getWrappedMessage() {
		if (mWrappedMessage != null) {
			return mWrappedMessage;
		}
		
		

		return mWrappedMessage;
	}



	@Override
	public void decode(MessageBinDecoder pDecoder) {
		pDecoder.readInt(); 									// Read Message Id of the WrappedMessage class
		String tFullMessageClassName = pDecoder.readString(); 	// Read Wrapped Message Id 
		mWrappedMessageBytes = pDecoder.readBytes();			// read wrapped message bytes
		
		MessageBinDecoder tDecoder = new MessageBinDecoder( mWrappedMessageBytes);
		try {
			Class<?> tClass = Class.forName(tFullMessageClassName);
			mWrappedMessage = (MessageInterface) tClass.newInstance();
			mWrappedMessage.decode(tDecoder);
		}
		catch( Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void encode(MessageBinEncoder pEncoder) {
		pEncoder.add(MESSAGE_ID);
		pEncoder.add(mWrappedMessage.getFullMessageName());			// Full class name of the wrapped message
		pEncoder.add(mWrappedMessage.messageToBytes());				// Get wrapped message bytes
	}

	@Override
	public String getFullMessageName() {
		return this.getClass().getName();
	}

	@Override
	public int getMessageId() {
		return MESSAGE_ID;
	}

	@Override
	public String getMessageName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte[] messageToBytes() {
		MessageBinEncoder tEncoder = new MessageBinEncoder();
		this.encode(tEncoder);
		return tEncoder.getBytes();
	}

	

	String blanks(int pCount) {
		if (pCount == 0) {
			return null;
		}
		String tBlanks = "                                                                                       ";
		return tBlanks.substring(0, pCount);
	}

	@Override
	public String toString() {
		return this.toString(0);
	}

	public String toString(int pCount) {
		return toString(pCount, false);
	}

	public String toString(int pCount, boolean pExtention) {
		StringBuilder tSB = new StringBuilder(512);
		if (pCount > 0) {
			tSB.append(blanks(pCount));
		}

		tSB.append("Message: " + "\"MessageWrapper\"  Id: " + MESSAGE_ID + "\n");

		tSB.append(blanks(pCount + 2) + "mClassName: ");
		tSB.append(String.valueOf( this.getWrappedMessage().getClass().getSimpleName()));
		tSB.append("\n");

		tSB.append(blanks(pCount + 2) + "mMessage: ");
		tSB.append(mWrappedMessage.toString(pCount + 4));

		tSB.append("\n");

		return tSB.toString();
	}

	@Override
	public TreeNode getNodeTree(String pMessageAttributeName) {
		TreeNode treeNode = null;
		if (pMessageAttributeName == null) {
			treeNode = new TreeNode("MessageWrapper");
		} else {
			treeNode = new TreeNode(pMessageAttributeName + " [MessageWrapper]");
		}

		treeNode.add(new TreeNode("className" + " : "+ String.valueOf(getWrappedMessage().getClass().getSimpleName())));
		if (mWrappedMessage == null) {
			treeNode.add(new TreeNode("WrappedMessage" + " : <null>"));
		} else {
			treeNode.add(this.getWrappedMessage().getNodeTree("WrappedMessage"));
		}
		return treeNode;
	}

}
