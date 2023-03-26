package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


class NetMsgRetransmissionNAK extends NetMsg {

	private InetAddress				mMcAddress;
	private int  					mMcPort;
	private int 					mSenderId;
	private ArrayList<Integer> 	mNakSequenceNumbers;

	int mNakCount = 0;

	NetMsgRetransmissionNAK(Segment pSegment) {
		super(pSegment);
		mNakSequenceNumbers = new ArrayList<Integer>();
	}

	void encode() {
		super.encode();
		MessageBinEncoder tEncoder = super.getEncoder();

		tEncoder.add( InetAddressConverter.inetAddrToInt(mMcAddress ));
		tEncoder.add( mMcPort );
		tEncoder.add( mSenderId );

		tEncoder.add( mNakSequenceNumbers.size() );
		for( int i = 0; i < mNakSequenceNumbers.size(); i++) {
			tEncoder.add( mNakSequenceNumbers.get(i).intValue());
		}
	}

	void decode() {
		int tElements;
		
		super.decode();

		MessageBinDecoder tDecoder = mSegment.getDecoder();
		mMcAddress = InetAddressConverter.intToInetAddr(tDecoder.readInt());
		mMcPort = tDecoder.readInt();

		mSenderId = tDecoder.readInt();
		tElements = tDecoder.readInt();
		mNakSequenceNumbers.clear();
		for( int i = 0; i < tElements; i++) {
			mNakSequenceNumbers.add(tDecoder.readInt());
		}
	}

	void set(InetAddress pMcAddress, int pMcPort, int pSenderId) {
		setMcAddress(pMcAddress);
		setMcPort(pMcPort);
		setSenderId(pSenderId);
	}

	void setSenderId(int pSenderId) {
		mSenderId = pSenderId;
	}

	int getSenderId() {
		return mSenderId;
	}

	void setMcAddress(InetAddress pAddress) {
		mMcAddress = pAddress;
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

	void setNakSeqNo(List<Integer> pList) {
		mNakSequenceNumbers.addAll(pList);
	}

	List<Integer> getNakSeqNo() {
		return mNakSequenceNumbers;

	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( super.toString() );
		sb.append("\n    <");
		sb.append("MC addr: " + getMcAddress().toString());
		sb.append(" MC port: " + getMcPort());
		sb.append(" SndrId: " + Integer.toHexString( getSenderId()));
		sb.append(" NAK count: " + mNakSequenceNumbers.size());
		sb.append(" NAKSeqno:  ");
		for( int i = 0; i < mNakSequenceNumbers.size(); i++) {
		  sb.append( mNakSequenceNumbers.get(i) + ", " );
		}
		sb.setLength(sb.length() - 2);
		sb.append(">");
		return sb.toString();
	}

}
