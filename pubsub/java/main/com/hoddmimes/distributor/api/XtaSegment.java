package com.hoddmimes.distributor.api;

class XtaSegment extends Segment {
	private int mAllocatedBufferSize;

	XtaSegment(int pBufferSize) {
		super(pBufferSize);
		mAllocatedBufferSize = pBufferSize;
	}
	
	public int getSize() {
		return this.getLength();
	}

	public int getBufferAllocationSize() {
		return mAllocatedBufferSize;
	}

}
