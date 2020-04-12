package com.hoddmimes.distributor.tcpip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteCountingOutputStream extends FilterOutputStream {

	private long mBytesWritten;

	public ByteCountingOutputStream(OutputStream pOutputStream) {
		super(pOutputStream);
		mBytesWritten = 0;
	}

	@Override
	public void write(byte[] pBytes) throws IOException {
		out.write(pBytes);
		mBytesWritten += pBytes.length;
	}

	@Override
	public void write(byte[] pBytes, int pOffset, int pLength)
			throws IOException {
		out.write(pBytes, pOffset, pLength);
		mBytesWritten += pLength;
	}

	@Override
	public void write(int pByte) throws IOException {
		out.write(pByte);
		mBytesWritten++;
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	public long getBytesWritten() {
		return mBytesWritten;
	}

}
