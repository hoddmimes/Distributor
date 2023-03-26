package com.hoddmimes.distributor.tcpip;

public enum TcpIpConnectionTypes {

	Plain, 		 // Vanilla tcp/ip socket no extravaganza
	Compression; // ZLIB compression over a vanilla socket
}
