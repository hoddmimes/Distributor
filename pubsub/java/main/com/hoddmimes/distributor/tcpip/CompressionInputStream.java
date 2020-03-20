package com.hoddmimes.distributor.tcpip;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;

public class CompressionInputStream extends ZInputStream {

	public CompressionInputStream(InputStream pIn, int pLevel) throws IOException {
		super(pIn, pLevel);
		this.setFlushMode(JZlib.Z_SYNC_FLUSH);
	}

	public CompressionInputStream(InputStream pIn) throws IOException{
		super(pIn);
		this.setFlushMode(JZlib.Z_SYNC_FLUSH);
	}

}
