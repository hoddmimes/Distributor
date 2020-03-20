package com.hoddmimes.distributor.api;

import java.net.InetAddress;
import java.nio.ByteBuffer;

class RcvSegment extends Segment 
{
	private InetAddress mFromAddress;
	private int mFromPort;
	
	RcvSegment(ByteBuffer pByteBuffer) {
		super(pByteBuffer);
	}

	boolean isStartSegment() {
		if ((getHeaderSegmentFlags() & FLAG_M_SEGMENT_START) != 0) {
			return true;
		} else {
			return false;
		}
	}

	boolean isEndSegment() {
		if ((getHeaderSegmentFlags() & FLAG_M_SEGMENT_END) != 0) {
			return true;
		} else {
			return false;
		}
	}

	void setFromAddress( InetAddress pFromAddress ) {
		mFromAddress = pFromAddress;
	}
	
	InetAddress getFromAddress() {
		return mFromAddress;
	}
	
	void setFromPort( int pFromPort ) {
		mFromPort = pFromPort;
	}
	
	
	int getFromPort() {
		return mFromPort;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object pObj) {
		return super.equals(pObj);
	}



}
