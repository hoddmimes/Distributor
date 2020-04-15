package com.hoddmimes.distributor.api;

public class XtaUpdate 
{
	 final byte[]  mData;
	 final int 	   mLength;
	 final boolean mCommesFromBdxGwy;
	 final String  mSubjectName;
	
	XtaUpdate( String pSubjectname, byte[] pData, int pLength, boolean pCommesFromBdxGwy )
	{
		mSubjectName = pSubjectname;
		mData = pData;
		mLength = pLength;
		mCommesFromBdxGwy = pCommesFromBdxGwy;
	}
	
    // Subject Layout
	// 1 byte, subject name present
	// 4 bytes subject name, length field
	// 'n' bytes, subject name payload
	// 1 byte, update data present
	// 4 bytes, update data length
	// 'n' bytes, updata data payload
	int getSize() {
		int tSize = mSubjectName.length() + (1+4+1+4) + mLength;
		return tSize;
	}
	
	int getDataLength() {
		return mLength;
	}
}
