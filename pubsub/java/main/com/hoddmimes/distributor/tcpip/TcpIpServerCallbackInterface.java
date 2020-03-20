package com.hoddmimes.distributor.tcpip;

public interface TcpIpServerCallbackInterface extends
		TcpIpConnectionCallbackInterface {
	public void tcpipInboundConnection(TcpIpConnection pConnection);
}
