package com.hoddmimes.distributor.tcpip;



import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.DeflaterOutputStream;


import java.io.IOException;
import java.io.OutputStream;



public class CompressionOutputStream extends DeflaterOutputStream {

	public CompressionOutputStream(OutputStream pOut, int pLevel) throws IOException {
		super( pOut,  new Deflater(pLevel));
		this.setSyncFlush( true );
	}

	public CompressionOutputStream(OutputStream pOut) throws IOException {
		super( pOut );
		this.setSyncFlush( true );
	}


}
