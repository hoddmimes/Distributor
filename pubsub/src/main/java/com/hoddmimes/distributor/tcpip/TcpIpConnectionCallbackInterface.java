package com.hoddmimes.distributor.tcpip;

import java.io.IOException;

public interface TcpIpConnectionCallbackInterface {
	public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer);

	public void tcpipClientError(TcpIpConnection pConnection, IOException e);

}
