package com.hoddmimes.distributor.tcpip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class TcpIpClient {
	

	public static TcpIpConnection connect(String pHost, int pPort,
			TcpIpConnectionCallbackInterface pCallback) throws IOException {
		return TcpIpClient.connect(TcpIpConnectionTypes.Plain, pHost, pPort, pCallback);
	}

	public static TcpIpConnection connect(TcpIpConnectionTypes pConnectionType,
			String pHost, int pPort, TcpIpConnectionCallbackInterface pCallback) throws IOException {
		Socket tSocket;


		SocketAddress tEndpointAddress = new InetSocketAddress(pHost, pPort);
		tSocket = new Socket();
		tSocket.connect(tEndpointAddress, 10000); // 10 seconds timeout
		
		TcpIpConnection tTcpIpConnection = new TcpIpConnection(tSocket,
				TcpIpConnection.ConnectionDirection.Out, pConnectionType, pCallback);
		tTcpIpConnection.start();
		return tTcpIpConnection;
	}
}
