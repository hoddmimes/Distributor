package com.hoddmimes.distributor.api;

public class XtaUpdate 
{
	 final byte[]  mData;
	 final String  mSubjectName;
	
	XtaUpdate( String pSubjectname, byte[] pData )
	{
		mSubjectName = pSubjectname;
		mData = pData;
	}
	
    // Subject Layout
	// 1 byte, subject name present
	// 4 bytes subject name, length field
	// 'n' bytes, subject name payload
	// 1 byte, update data present
	// 4 bytes, update data length
	// 'n' bytes, update data payload
	int getSize() {
		int tSize = mSubjectName.length() + (1+4+1+4) + mData.length;
		return tSize;
	}
	
	int getDataLength() {
		return mData.length;
	}
}
