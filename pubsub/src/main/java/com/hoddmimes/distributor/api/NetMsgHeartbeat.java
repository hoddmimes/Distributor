package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;



class NetMsgHeartbeat extends NetMsg {
	private InetAddress	 mMcAddress;
	private int	 		 mMcPort;
	private int	 		 mSenderId;
	private long 		 mSenderStartTime;
	private int  		 mSeqno;

	NetMsgHeartbeat(Segment pSegment) {
		super(pSegment);
	}
	
	void encode() {
		super.encode();

		MessageBinEncoder tEncoder = super.getEncoder();
		tEncoder.add( InetAddressConverter.inetAddrToInt( mMcAddress));
		tEncoder.add( mMcPort );
		tEncoder.add( mSenderId );
		tEncoder.add( mSenderStartTime );
		tEncoder.add( mSeqno );
	}

	void decode() {
		super.decode();

		MessageBinDecoder tDecoder = super.getDecoder();
		mMcAddress = InetAddressConverter.intToInetAddr( tDecoder.readInt());
		mMcPort = tDecoder.readInt();
		mSenderId = tDecoder.readInt();
		mSenderStartTime = tDecoder.readLong();
		mSeqno = tDecoder.readInt();
	}

	void set(InetAddress pMcAddress, int pMcPort, int pSenderId, long pSenderStartTime, int pSeqNo) {
		setMcAddress(pMcAddress);
		setMcPort(pMcPort);
		setSenderId(pSenderId);
		setSenderStartTime(pSenderStartTime);
		setSeqNo(pSeqNo);
	}

	void setSenderStartTime( long pStartTime ) {
		mSenderStartTime = pStartTime;
	}

	long getSenderStartTime() {
		return mSenderStartTime;
	}


	void setSeqNo(int pSeqNo) {
		mSeqno = pSeqNo;
	}

	int getSeqNo() {
		return mSeqno;
	}

	void setMcAddress(InetAddress pMcAddress) {
		mMcAddress = pMcAddress;
	}

	InetAddress getMcAddress() {
	  return mMcAddress;
	}

	void setMcPort(int pPort) {
		mMcPort = pPort;
	}

	int getMcPort() {
		return mMcPort;
	}

	void setSenderId(int pSenderId) {
		mSenderId = pSenderId;
	}

	int getSenderId() {
		return mSenderId;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( super.toString() );
		sb.append("\n    <");
		sb.append("MC addr: " + getMcAddress().toString());
		sb.append(" MC dst-port: " + getMcPort());
		sb.append(" SndrId: " + Integer.toHexString(getSenderId()));
		sb.append(" Seqno: " + getSeqno());
		sb.append(">");
		return sb.toString();
	}

}
