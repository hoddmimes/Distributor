package com.hoddmimes.distributor.tcpip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteCountingInputStream extends FilterInputStream {

	private long mBytesRead;

	public ByteCountingInputStream(InputStream pInputStream) {
		super(pInputStream);
		mBytesRead = 0;
	}

	@Override
	public int read() throws IOException {
		int tByte = in.read();
		if (tByte >= 0) {
			mBytesRead++;
		}
		return tByte;
	}

	@Override
	public int read(byte[] pBytes) throws IOException {
		int tReadSize = in.read(pBytes);
		if (tReadSize > 0) {
			mBytesRead += tReadSize;
		}
		return tReadSize;
	}

	@Override
	public int read(byte[] pBytes, int pOffset, int pLength) throws IOException {
		int tReadSize = in.read(pBytes, pOffset, pLength);
		if (tReadSize > 0) {
			mBytesRead += tReadSize;
		}
		return tReadSize;
	}

	public long getBytesRead() {
		return mBytesRead;
	}

}
