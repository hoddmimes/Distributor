package com.hoddmimes.distributor.tcpip;

import java.io.IOException;
import java.io.OutputStream;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

public class CompressionOutputStream extends ZOutputStream {

	public CompressionOutputStream(OutputStream pOutputStream) throws IOException {
		this(pOutputStream, JZlib.Z_DEFAULT_COMPRESSION);
	}

	public CompressionOutputStream(OutputStream pOutputStream, int pCompressionLevel) throws IOException {
		super(pOutputStream, pCompressionLevel);
		this.setFlushMode(JZlib.Z_SYNC_FLUSH);
	}

}
