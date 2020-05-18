package com.hoddmimes.distributor.api;


import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;






class NetMsg {
	
	enum SequenceNumberActions {
		IGNORE, SYNCH, LOWER, HIGHER
	};

	final static short VERSION = 0x0100; // 1.0

	Segment mSegment;
	

	NetMsg(Segment pSegment) {
		mSegment = pSegment;
	}

	Segment getSegment() {
		return mSegment;
	}
	
	MessageBinEncoder getEncoder() {
		return mSegment.getEncoder();
	}
	
	MessageBinDecoder getDecoder() {
		return mSegment.getDecoder();
	}
	
	

	void setHeader(byte pMessageType, byte pSegmentFlags,
				   InetAddress pLocalAddress, int pSenderId, int pSenderStartTime, int pAppId)
	{
		mSegment.setHeader(VERSION, pMessageType, pSegmentFlags,
				pLocalAddress, pSenderId, pSenderStartTime, pAppId);
	}

	void setHeaderSegmentFlags(byte pSegmentFlags) {
		mSegment.setHeaderSegmentFlags( pSegmentFlags);
	}

	InetAddress getHeaderLocalSourceAddress() {
		return mSegment.getHeaderLocalAddress();
	}

	byte getHeaderSegmentFlags() {
		return mSegment.getHeaderSegmentFlags();
	}

	int getHeaderSenderStartTime() {
		return mSegment.getHeaderSenderStartTime();
	}

	int getHeaderAppId() { return mSegment.getHeaderAppId(); }

	

	public boolean isMsgFromBdxGwy() {
		if ((mSegment.getHeaderSegmentFlags() & Segment.FLAG_M_SEGMENT_BDXGWY) != 0) {
			return true;
		}
		return false;
	}

	public String toString() {
		return mSegment.toString();
	}


	void setSeqno( int  pSeqno ) {
		mSegment.setSeqno( pSeqno );
	}

	int getSeqno() {
		return mSegment.getSeqno();
	}

	void encode() {
		mSegment.encode();
	}
	
	void decode() {
		mSegment.decode();
	}

}
