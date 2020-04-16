package com.hoddmimes.distributor.api;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;


import com.hoddmimes.distributor.DistributorException;

public class Ipmg
{
	InetSocketAddress mDestinationSocketAddress;
	MulticastSocket mSocket;
	InetAddress mInetAddress;
	int mPort;
	NetworkInterface mNetworkInterface;

	public Ipmg(InetAddress pAddress, String pNetworkInterface, int pPort,int pBufferSize, int pTTL) throws DistributorException {
		mInetAddress = pAddress;
		mPort = pPort;

		if (pNetworkInterface != null) {
			try {
				mNetworkInterface = NetworkInterface.getByName(pNetworkInterface);
				if (mNetworkInterface == null) {
					throw new DistributorException( "Could not locate network interface \""+ pNetworkInterface + "\"");
				}
			} catch (SocketException e1) {
				throw new DistributorException("Could not locate network interface \""+ pNetworkInterface + "\" SocketException: " + e1.getMessage());
			}
		} else {
			mNetworkInterface = null;
		}

		try {
			mSocket = new MulticastSocket(null);
			mSocket.setReuseAddress(true);
			mSocket.bind(new InetSocketAddress(mPort));
			if (!mSocket.getReuseAddress()) {
				throw new DistributorException("Hmmm, reuse of address not supported, socket: " + this.toString());
			}
		} catch (IOException e2) {
			throw new DistributorException("Could not create MulticastSocket IOException: " + e2.getMessage());
		}
		try {
			mSocket.setReceiveBufferSize(pBufferSize);
			mSocket.setSendBufferSize(pBufferSize);
		} catch (SocketException e3) {
			throw new DistributorException("Could not set buffer size  SocketException: " + e3.getMessage());
		}

		try {
			if (mNetworkInterface != null) {
				mSocket.setNetworkInterface(mNetworkInterface);
			}
		} catch (SocketException e4) {
			throw new DistributorException( "Could not set the network interface \"" + pNetworkInterface + "\"  SocketException: " + e4.getMessage());
		}

		try {
			mSocket.setTimeToLive(pTTL);
		} catch (IOException e5) {
			throw new DistributorException( "Could not set the TTL  IOException: " + e5.getMessage());
		}
		try {
			mSocket.setLoopbackMode(false);
		} catch (SocketException e6) {
			throw new DistributorException("Could not set loopback mode,  SocketException: " + e6.getMessage());
		}

		mDestinationSocketAddress = new InetSocketAddress(mInetAddress, mPort);
		try {
			if (mNetworkInterface != null) {
				mSocket.joinGroup(mDestinationSocketAddress, mNetworkInterface);
			} else {
				mSocket.joinGroup(mInetAddress);
			}
		} catch (IOException e7) {
			throw new DistributorException( "Could not join multicast group \"" + pAddress.toString() + "\" IOException: " + e7.getMessage());
		}

	}

	public void close() {
		try {
			mSocket.leaveGroup(mInetAddress);
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int send(ByteBuffer pBuffer) throws Exception {
		long tSendTime = 0, tSendStartTime = 0;
		DatagramPacket tPacket = new DatagramPacket(pBuffer.array(), pBuffer.position(), mDestinationSocketAddress);
		tSendStartTime = System.nanoTime();
		mSocket.send(tPacket);
		tSendTime = ((System.nanoTime() - tSendStartTime) / 1000L);
		if (pBuffer.position() != tPacket.getLength()) {
			throw new IOException("Failed to send, send data("
					+ tPacket.getLength() + " != buffer length("
					+ pBuffer.position() + ") MCA: "
					+ mDestinationSocketAddress.toString());
		}
		tPacket = null;
		return (int) tSendTime;
	}

	public SocketAddress receive(ByteBuffer pBuffer) throws IOException {
		pBuffer.clear();
		DatagramPacket tPacket = new DatagramPacket(pBuffer.array(), pBuffer.capacity());
		mSocket.receive(tPacket);

		pBuffer.position(tPacket.getLength());
		return tPacket.getSocketAddress();
	}

	public long getMcaConnectionId() {
		byte[] tBuffer = mInetAddress.getAddress();
		long tHigh = ((tBuffer[0] & 0xff) << 24) + ((tBuffer[1] & 0xff) << 16)
				+ ((tBuffer[2] & 0xff) << 8) + (tBuffer[3] & 0xff);
		long tLow = mPort;

		return ((tHigh << 24) + tLow);
	}

	@Override
	public String toString() {
		return mInetAddress.getHostAddress() + ":" + mPort;
	}

}
