package com.hoddmimes.distributor.tcpip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**

 * Binary header 
 * +------------------------------------------------+-----------+
 * | Four byte magical header (0x50,0x4f,0x42,0x45) | 4 bytes   |
 * +------------------------------------------------+-----------+ 
 * | Length field, integer network byte order       | 4 bytes   |
 * +------------------------------------------------+-----------+ 
 * | Data pay load                                  | n bytes   | 
 * +------------------------------------------------+-----------+
 * 
 */

public class TcpIpConnection extends Thread implements TcpIpCountingInterface {
	static enum ConnectionDirection {In, Out};

	private static final int cMagicBinarySign = 0x504f4245;

	private static SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private static AtomicInteger cThreadIndex = new AtomicInteger(0);

	private TcpIpConnectionCallbackInterface mCallback;
	private Socket mSocket;
	private volatile boolean mClosedCalled;
	private int mIndex;
	private Date mConnectionTime;
	private Object mUserCntx;
	private TcpIpConnectionTypes mConnectionType;
	private ConnectionDirection mConnectionDirection;
	private volatile DataInputStream mIn;
	private volatile DataOutputStream mOut;


	private volatile ByteCountingOutputStream mUnCompStatOutputStream;
	private volatile ByteCountingOutputStream mCompStatOutputStream;
	private volatile ByteCountingInputStream mUnCompStatInputStream;
	private volatile ByteCountingInputStream mCompStatInputStream;

	public TcpIpConnection( Socket pSocket, ConnectionDirection pConnectionDirection, TcpIpConnectionTypes pConnectionType, 
							TcpIpConnectionCallbackInterface pCallback) {

		
		mSocket = pSocket;
		mConnectionDirection = pConnectionDirection;
		mCallback = pCallback;
		mClosedCalled = false;
		mConnectionType = pConnectionType;

		mConnectionTime = new Date();
		mIndex = cThreadIndex.incrementAndGet();
		try {
			mSocket.setTcpNoDelay(true);
			mSocket.setKeepAlive(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			/**
			 * Time to figure out how the input / output stream stack should
			 * look
			 */
			InputStream tSocketInputStream = mSocket.getInputStream();
			OutputStream tSocketOutputStream = mSocket.getOutputStream();

			if (mConnectionType == TcpIpConnectionTypes.Compression)  {
				mCompStatInputStream = new ByteCountingInputStream( new BufferedInputStream(tSocketInputStream));
				mCompStatOutputStream = new ByteCountingOutputStream( new BufferedOutputStream(tSocketOutputStream));

				mUnCompStatOutputStream = new ByteCountingOutputStream( new CompressionOutputStream(mCompStatOutputStream));
				mUnCompStatInputStream = new ByteCountingInputStream( new CompressionInputStream(mCompStatInputStream));

				mIn = new DataInputStream(mUnCompStatInputStream);
				mOut = new DataOutputStream(mUnCompStatOutputStream);

			} else {
				mUnCompStatOutputStream = new ByteCountingOutputStream( new BufferedOutputStream(tSocketOutputStream));
				mUnCompStatInputStream = new ByteCountingInputStream( new BufferedInputStream(tSocketInputStream));

				mIn = new DataInputStream(mUnCompStatInputStream);
				mOut = new DataOutputStream(mUnCompStatOutputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUserCntx(Object pObject) {
		mUserCntx = pObject;
	}

	public Object getUserCntx() {
		return mUserCntx;
	}

	public void close() {
		
		synchronized (this) {
			if (mClosedCalled) {
				return;
			}
			mClosedCalled = true;
			try {
				mIn.close();
				mOut.close();
				mSocket.close();
			} catch (IOException e) {}
		}
	}

	public void flush() throws IOException {
		mOut.flush();
	}

	public TcpIpConnectionCallbackInterface getCallbackInterface() {
		return this.mCallback;
	}

	public String getRemoteHost() {
		return mSocket.getRemoteSocketAddress().toString();
	}

	public int getRemotePort() {
		return mSocket.getPort();
	}

	public void write(byte[] pBuffer) throws IOException {
			sendBinary(pBuffer, true);
	}

	public void write(byte[] pBuffer, boolean pFlushFlag) throws IOException {
	   sendBinary(pBuffer, pFlushFlag);
	}

	

	private void sendBinary(byte[] pBuffer, boolean pFlush) throws IOException {
		synchronized (this) {
			mOut.writeInt(cMagicBinarySign);
			mOut.writeInt(pBuffer.length);
			mOut.write(pBuffer);
			if (pFlush) {
				mOut.flush();
			}
		}
	}

	@Override
	public String toString() {
		return this.getConnectionInfo();
	}

	public String getConnectionInfo() {
		return "[host: " + mSocket.getRemoteSocketAddress().toString()
				+ " direction: " + this.mConnectionDirection.toString()
				+ " port: " + mSocket.getPort() + " conn id: " + mIndex
				+ " conn time: " + cSDF.format(mConnectionTime) + "]";
	}

	@Override
	public void run() {
		byte[] tBuffer;
		int tSize;

		this.setName("TCPIP thread " + mIndex);

		while (!mClosedCalled) {
			try {
				int tMagicalSign = mIn.readInt();
				if (tMagicalSign != cMagicBinarySign) 
				{
					if (!mClosedCalled) {
						mCallback.tcpipClientError(this,
								new IOException("Read message with invalid magic sign (" + Integer.toHexString( tMagicalSign ) + ")"));
					}
					return;
				} 
					
				tSize = mIn.readInt();
				tBuffer = new byte[tSize];
				mIn.readFully(tBuffer);
				mCallback.tcpipMessageRead(this, tBuffer);
			} catch (IOException e) {
				if (!mClosedCalled) {
					mCallback.tcpipClientError(this, e);
			    }
				return;
			}
		}
	}

	public boolean isClosed() {
		return mClosedCalled;
	}
	
	@Override
	public long getBytesRead() {
		return this.mUnCompStatInputStream.getBytesRead();
	}

	@Override
	public long getBytesWritten() {
		return this.mUnCompStatOutputStream.getBytesWritten();
	}

	@Override
	public double getInputCompressionRate() {
		if (mConnectionType == TcpIpConnectionTypes.Compression) {

			double tRatio = (((double) mUnCompStatInputStream.getBytesRead() - (double) mCompStatInputStream
					.getBytesRead()) / mUnCompStatInputStream.getBytesRead());
			return tRatio;
		}
		return 0D;
	}

	@Override
	public double getOutputCompressionRate() {
		if (mConnectionType == TcpIpConnectionTypes.Compression) {

			double tRatio = (((double) mUnCompStatOutputStream.getBytesWritten() - (double) mCompStatOutputStream .getBytesWritten()) / 
					mUnCompStatOutputStream.getBytesWritten());
			return tRatio;
		}

		return 0D;
	}
}
