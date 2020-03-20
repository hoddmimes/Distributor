package com.hoddmimes.distributor.tcpip;

public interface TcpIpCountingInterface {
	public long getBytesWritten();

	public long getBytesRead();

	public double getOutputCompressionRate();

	public double getInputCompressionRate();

}
