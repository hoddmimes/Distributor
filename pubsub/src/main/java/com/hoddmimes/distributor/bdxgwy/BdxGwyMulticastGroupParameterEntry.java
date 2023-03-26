package com.hoddmimes.distributor.bdxgwy;

public class BdxGwyMulticastGroupParameterEntry {
	private String mMulticastAddress;
	private int mUdpPort;

	public BdxGwyMulticastGroupParameterEntry(String pMulticastAddress, int pUdpPort) {
		mUdpPort = pUdpPort;
		mMulticastAddress = pMulticastAddress;
	}

	int getUdpPort() {
		return this.mUdpPort;
	}

	String getMulticastAddress() {
		return mMulticastAddress;
	}

}