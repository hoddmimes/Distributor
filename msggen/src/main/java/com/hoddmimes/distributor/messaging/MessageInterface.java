/*
 *
 * Copyright (c) 2008 Hoddmimes Solutions AB, Stockholm,
 * Sweden. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Hoddmimes Solutions AB, Stockholm, Sweden. You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Hoddmimes.
 *
 * Hoddmimes makes no representations or warranties about the suitability
 * of the software, either expressed or implied, including, but not limited
 * to, the implied warranties of merchantibility, fitness for a particular
 * purpose, or non-infringement. Hoddmimes shall not be liable for any
 * damages suffered by licensee as a result of using, modifying, or
 * distributing this software or its derivatives.
 */
package com.hoddmimes.distributor.messaging;

public interface MessageInterface {

	public void encode(MessageBinEncoder pEncoder);
	public void decode(MessageBinDecoder pEnCoder);
	public abstract int getMessageId();
	public abstract String toString(int pCount);
	public TreeNode getNodeTree(String pMessageAttributeName );

	default String getMessageName() {
		return this.getClass().getSimpleName();
	}

	default byte[] messageToBytes() {
		MessageBinEncoder tEncoder = new MessageBinEncoder();
		this.encode( tEncoder );
		return tEncoder.getBytes();
	}

	default String getFullMessageName() {
		return this.getClass().getName();
	}
}
