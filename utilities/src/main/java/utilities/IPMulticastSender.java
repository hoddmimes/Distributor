package utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;



public class IPMulticastSender
{
	static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");

	
	int mPort = 12121;
	int mTTL = 127;
	int mIpBufferSize = 256000;
	String mAddressString = "224.10.10.1";
	int mMessageSize = 61000;
	
	
	MulticastSocket 	mSocket;
	InetSocketAddress 	mDestinationSocketAddress;
	
	
	
	void parseArguments( String[] args ) 
	{
		int i = 0;
		while( i < args.length) {
			if (args[i].compareToIgnoreCase("-ip_buffer_size") == 0) {
				mIpBufferSize = Integer.parseInt(args[i+1]);
				i++;
			}
			if (args[i].compareToIgnoreCase("-message_size") == 0) {
				mMessageSize = Integer.parseInt(args[i+1]);
				i++;
			}
			if (args[i].compareToIgnoreCase("-udp_address") == 0) {
				mAddressString = args[i+1];
				i++;
			}
			
			if (args[i].compareToIgnoreCase("-udp_port") == 0) {
				mPort = Integer.parseInt(args[i+1]);
				i++;
			}
			i++;
		}
	}
	
	void connect() {
		try {
			mSocket = new MulticastSocket(null);
			mSocket.setReuseAddress(true);
			mSocket.bind(new InetSocketAddress(mPort));
			if (!mSocket.getReuseAddress()) {
				throw new RuntimeException("Hmmm, reuse of address not supported, socket: " + this.toString());
			}
		} catch (IOException e2) {
			throw new RuntimeException("Could not create MulticastSocket IOException: " + e2.getMessage());
		}
		try {
			mSocket.setReceiveBufferSize(mIpBufferSize);
			mSocket.setSendBufferSize(mIpBufferSize);
		} catch (SocketException e3) {
			throw new RuntimeException("Could not set buffer size  SocketException: " + e3.getMessage());
		}

	

		try {
			mSocket.setTimeToLive(mTTL);
		} catch (IOException e5) {
			throw new RuntimeException( "Could not set the TTL  IOException: " + e5.getMessage());
		}
		try {
			mSocket.setLoopbackMode(false);
		} catch (SocketException e6) {
			throw new RuntimeException("Could not set loopback mode,  SocketException: " + e6.getMessage());
		}

		InetAddress tInetAddress;
		try {tInetAddress = InetAddress.getByName(mAddressString);}
		catch (Exception e7) {
			throw new RuntimeException("failed to translate MCA, reason: " + e7.getMessage());
		}
			
		mDestinationSocketAddress = new InetSocketAddress(tInetAddress, mPort);
		
		try {  mSocket.joinGroup(tInetAddress); } 
		catch (IOException e8) {
			throw new RuntimeException( "Could not join multicast group \"" + tInetAddress.toString() + "\" IOException: " + e8.getMessage());
		}
	}
	
	static void longToBuffer( long pValue, byte[] pBuffer, int pOffset ) 
	{
		for( int i = 7; i >= 0; i--) {
			pBuffer[ (i+pOffset) ] = (byte)((pValue >> (7-i)) & 0xFF);
		}
	}
	
	static long bufferToLong( byte[] pBuffer, int pOffset ) {
		long v = 0;
		for( int i = 7; i >= 0; i--) {
			v += ((long) pBuffer[(i+pOffset)]) << (7 - i);
		}
		return v;
	}
	
	void send() {
		Statistics tStat = new Statistics();
		int tSeqNo = 0;
		DatagramPacket tPacket;
		byte[] tBuffer = new byte[ mMessageSize ];
		long tStartTime, tSendTime;

		try {
			while( true ) 
			{
				longToBuffer( tSeqNo++, tBuffer, 0 );

				tPacket = new DatagramPacket(tBuffer, mMessageSize, mDestinationSocketAddress);
				tStartTime = System.nanoTime();
				mSocket.send(tPacket);
				tSendTime = (System.nanoTime() - tStartTime) / 1000L;
				if (mMessageSize != tPacket.getLength()) {
					throw new IOException("Failed to send, send data("
							+ mMessageSize + " != buffer length("
							+ tPacket.getLength() + ") MCA: "
							+ mDestinationSocketAddress.toString());
				}
				tStat.updateStatistics( tPacket.getLength(), tSendTime );
			}
		}
		catch( Exception e ) {
			throw new RuntimeException("Failed to multicast, reason: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) 
	{
		IPMulticastSender tSender = new IPMulticastSender();
		tSender.parseArguments( args );
		tSender.connect();
		tSender.send();
	}
	
	
	class Statistics {
		long mBytesSent;
		long mStartTime;
		long mLastTimeStamp;
		long mMsgsSent;
		long mTotSendTime;
		
		
		Statistics() {
		  mStartTime = mLastTimeStamp = System.currentTimeMillis();
		}
		
		void presentStatistics() {
			long tNow = System.currentTimeMillis();
			long tTimeDiff = tNow - mStartTime;
			long tBitRate =  (8 * mBytesSent ) / tTimeDiff;
			long tMessageRate = (mMsgsSent * 1000) / tTimeDiff;
			long tAvgSendTime = mTotSendTime / mMsgsSent;
			System.out.println( cSDF.format(tNow) + " message rate: " + tMessageRate + " bit rate:" + tBitRate  +
					" kbit/sec   Avg Xta Time: " + tAvgSendTime + " usec");
		}
		
		void updateStatistics( int pMessageSize, long pSendTime ) {
			mMsgsSent++;
			mTotSendTime += pSendTime;
			mBytesSent += pMessageSize;
			if ((mMsgsSent % 5000 ) == 0) {
				presentStatistics();
			}
		}
		
		
		
		
		
		
		
		
	}

}
