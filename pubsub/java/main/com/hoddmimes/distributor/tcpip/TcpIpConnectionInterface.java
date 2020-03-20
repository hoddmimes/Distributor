package com.hoddmimes.distributor.tcpip;

import java.io.IOException;

public interface TcpIpConnectionInterface extends TcpIpCountingInterface {
	public void setUserCntx(Object pObject);

	public Object getUserCntx();

	void close();

	public void send(byte[] pBuffer, boolean pFlush) throws IOException;

	public void send(byte[] pBuffer) throws IOException;

}
