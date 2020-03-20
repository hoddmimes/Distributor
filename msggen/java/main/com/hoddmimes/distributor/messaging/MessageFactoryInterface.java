package com.hoddmimes.distributor.messaging;

public interface MessageFactoryInterface {

	
	public  MessageInterface createMessage( byte[] pBuffer );
	
	
}
