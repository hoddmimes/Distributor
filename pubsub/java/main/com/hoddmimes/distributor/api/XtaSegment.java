package com.hoddmimes.distributor.api;

class XtaSegment extends Segment {
	XtaSegment(int pBufferSize) {
		super(pBufferSize);
	}
	
	public int getSize() {
		return this.getLength();
	}

}
