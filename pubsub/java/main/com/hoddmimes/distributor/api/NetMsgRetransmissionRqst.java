package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;



class NetMsgRetransmissionRqst extends NetMsg {
	private InetAddress	mRequesstorAddress;
	private int		mSenderId;
	private int 	mSenderStartTime;
	private int		mLowSeqno;
	private int 	mHighSeqno;
	private String	mApplName;
	private String  mHostName;

	NetMsgRetransmissionRqst(Segment pSegment) {
		super(pSegment);
		mApplName = "";
		mHostName = "";
	}

	void set(InetAddress pLocalAddress, int pLowSeqno, int pHighSeqno,
			String pLocalHostName, String pApplicationName, int pSenderId,
			int pSenderTime) {
		setRequestorAddress(pLocalAddress);
		setLowSeqNo(pLowSeqno);
		setSenderStartTime(pSenderTime);
		setSenderId(pSenderId);
		setHighSeqNo(pHighSeqno);
		setHostName(pLocalHostName);
		setApplicationName(pApplicationName);

	}

	void encode() {
		super.encode();

		MessageBinEncoder tEncoder = super.getEncoder();
		tEncoder.add( inetAddressToInt(mRequesstorAddress));
		tEncoder.add( mSenderId );
		tEncoder.add( mSenderStartTime );
		tEncoder.add( mLowSeqno );
		tEncoder.add( mHighSeqno );
		tEncoder.add( mHostName );
		tEncoder.add( mApplName );
	}

	void decode() {
		super.decode();

		MessageBinDecoder tDecoder = super.getDecoder();
		mRequesstorAddress = intToInetAddress(tDecoder.readInt());
		mSenderId = tDecoder.readInt();
		mSenderStartTime = tDecoder.readInt();
		mLowSeqno = tDecoder.readInt();
		mHighSeqno = tDecoder.readInt();

		mHostName = tDecoder.readString();
		mApplName = tDecoder.readString();
	}
	
	void setApplicationName( String pApplicationName ) {
		mApplName = pApplicationName;
	}
	
	String getApplicationName() {
		return mApplName;
	}
	
	void setHostName( String pHostName ) {
		mHostName = pHostName;
	}
	
	String getHostName() {
		return mHostName;
	}
	
	
	void setSenderStartTime(int pTime) {
		mSenderStartTime = pTime;
	}

	int getSenderStartTime() {
		return mSenderStartTime;
	}

	void setSenderId(int pSenderId) {
		mSenderId = pSenderId;
	}

	int getSenderId() {
		return mSenderId;
	}


	void setRequestorAddress(InetAddress pAddress) {
		mRequesstorAddress = pAddress;
	}

	InetAddress getRequestorAddress() {
		return mRequesstorAddress;
	}

	void setLowSeqNo(int pLowSeqNo) {
		mLowSeqno = pLowSeqNo;
	}

	int getLowSeqNo() {
		return mLowSeqno;
	}

	void setHighSeqNo(int pHighSeqNo) {
		mHighSeqno = pHighSeqNo;
	}

	int getHighSeqNo() {
		return mHighSeqno;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(super.toString());
		sb.append("\n    <");
		sb.append("Rqstr addr: " + getRequestorAddress().toString());
		sb.append(" Rqstr appl name: " + getApplicationName());
		sb.append(" SndrId: " + Integer.toHexString(getSenderId()));
		sb.append(" StartTime: " + getSenderStartTime());
		sb.append(" Seqno Lo: " + getLowSeqNo());
		sb.append(" Seqno Hi: " + getHighSeqNo());
		sb.append(">");
		return sb.toString();
	}
}
