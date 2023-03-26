package com.hoddmimes.distributor.bdxgwy;

public class BdxGatewayParameterEntry {
	private String mHostAddress;
	private int mHostPort;
	private String mBdxGatewayName;

	public BdxGatewayParameterEntry(String pBdxGatewayName, String pHostAddress, int pHostPort) {
		mHostAddress = pHostAddress;
		mHostPort = pHostPort;
		mBdxGatewayName = pBdxGatewayName;
	}

	public String getHostAddress() {
		return mHostAddress;
	}

	public int getHostPort() {
		return mHostPort;
	}

	public String getBdxGatewayName() {
		return mBdxGatewayName;
	}

}
