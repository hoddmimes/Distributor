package com.hoddmimes.distributor.tcpip;


import com.jcraft.jzlib.InflaterInputStream;


import java.io.IOException;
import java.io.InputStream;



public class CompressionInputStream extends InflaterInputStream {

	public CompressionInputStream( InputStream pIn ) throws IOException {
		super( pIn );
	}


}
