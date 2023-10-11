package com.hoddmimes.distributor.api;

public class RcvUpdate 
{
	private byte[]	mUpdateData;
	private boolean	mCommesFromBdxGwy;
	private String mSubjectName;
	private int mAppId;
	private long   mDistributorConnectionId;
	
	RcvUpdate( long pDistributorConnectionId, String pSubjectName, byte[] pUpdateData, int pAppId)
	{
		mUpdateData = pUpdateData;
		mSubjectName = pSubjectName;
		mAppId = pAppId;
		mDistributorConnectionId = pDistributorConnectionId;
	}
	
	
	long getDistributorConnectionId() {
		return mDistributorConnectionId;
	}

	int getSize() {
		// 8 == bool string + 2 string len + string data + bool data + 4 data length + data
	   return mSubjectName.length() + 8 + mUpdateData.length;
	}

	byte[] getData() {
		return mUpdateData;
	}

	String getSubjectName() {
		return mSubjectName;
	}

	int getAppId() { return mAppId; }
	
}
