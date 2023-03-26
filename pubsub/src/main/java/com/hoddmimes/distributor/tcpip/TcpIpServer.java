package com.hoddmimes.distributor.tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpIpServer extends Thread {

	private int mAcceptPort;
	private TcpIpServerCallbackInterface mCallback;
	private ServerSocket mServerSocket;
	private TcpIpConnectionTypes mConnectionType;
	private volatile boolean mCloseServer;
	private volatile Socket tServerSocket; 



	public TcpIpServer(int pAcceptPort, TcpIpServerCallbackInterface pCallback)
			throws IOException {
		this(TcpIpConnectionTypes.Plain, pAcceptPort, pCallback);
	}

	public TcpIpServer(TcpIpConnectionTypes pConnectionType, int pAcceptPort,
			TcpIpServerCallbackInterface pCallback) throws IOException {
		mConnectionType = pConnectionType;
		mAcceptPort = pAcceptPort;
		mCallback = pCallback;
		mCloseServer = false;
		
		mServerSocket = new ServerSocket(mAcceptPort);
		

		// Start accept thread
		start();
	}
	
	public synchronized void close() 
	{
		if (mCloseServer) {
			return;
		}
		mCloseServer = true;
		try {
			mServerSocket.close();
		}
		catch( IOException e ) {};
	}
	

	@Override
	public void run() {
		setName("TcpIp Server [" + mConnectionType.toString() + "] on port "
				+ mAcceptPort);
		while (!mCloseServer) {
			try {
				tServerSocket = mServerSocket.accept();
				TcpIpConnection tConnection = new TcpIpConnection(tServerSocket,
						TcpIpConnection.ConnectionDirection.In,
						mConnectionType, mCallback);
				mCallback.tcpipInboundConnection(tConnection);
				tConnection.start();
				
			} catch (IOException e) {
				if (mCloseServer) {
					return;
				}
				e.printStackTrace();
			}
		}
	}
}
