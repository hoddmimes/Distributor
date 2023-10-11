package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;



class NetMsgUpdate extends NetMsg {
	final static AtomicLong cSeqnoStamp = new AtomicLong(1);

	volatile int  mUpdateCount = 0; 			// Number of updates in segment
	volatile long mFlushSequenceNumber;
	volatile long mCreateTime;					// Time when the NetMessage was create


	NetMsgUpdate(Segment pSegment) {
		super(pSegment);
		mFlushSequenceNumber = cSeqnoStamp.getAndIncrement();
		mCreateTime = System.currentTimeMillis();
	}
	
	String mLargeSubjectName = null;
	int    mLargeMessageSize = 0;
	
	@Override 
	void setHeader( byte pMessageType, 
					byte pSegmentFlags,
					InetAddress  pLocalAddress,
					int  pSenderId, 
					int  pSenderStartTime,
					int	 pAppId )
	{
		super.setHeader( pMessageType,
						 pSegmentFlags,
						 pLocalAddress,
						 pSenderId,
						 pSenderStartTime,
						 pAppId);
		
		// now encode header
		super.encode();
		MessageBinEncoder tEncoder = super.getEncoder();
		tEncoder.add( mUpdateCount );
		tEncoder.add( (int) 0 ); // place holder for the sequence number 
	}
	
	static public int getMinUpdateHeaderSize()
	{
		return (Segment.SEGMENT_HEADER_SIZE + 8); // Segment-header-size + (sequence number + update count )
	}
	
	
	void encode() 
	{
		// Poke in the update count header in the header 
		MessageBinEncoder tEncoder = super.getEncoder();
		ByteBuffer bb = tEncoder.getByteBuffer();
		bb.putInt(Segment.SEGMENT_HEADER_SIZE, getSeqno());
		bb.putInt(Segment.SEGMENT_HEADER_SIZE+4, mUpdateCount);
	}

	void decode() {
		super.decode();
		MessageBinDecoder tDecoder = super.getDecoder();
		int tSequenceNumber = tDecoder.readInt(); // read sequence number 
		super.setSeqno(tSequenceNumber);
		mUpdateCount = tDecoder.readInt();
	}
	

	void setSequenceNumber(int pSeqNo) {
	  mSegment.setSeqno(pSeqNo);
	}

	int getSequenceNumber() {
		return mSegment.getSeqno();
	}



	boolean addUpdate(XtaUpdate pXtaUpdate) {
		MessageBinEncoder tEncoder = super.getEncoder();
		if (pXtaUpdate.getSize() <= tEncoder.getRemaining()) {
			tEncoder.add( pXtaUpdate.mSubjectName, true );
			tEncoder.add( pXtaUpdate.mData, pXtaUpdate.getDataLength());
			mUpdateCount++;
			return true;
		} else {
			return false;
		}
	}

	RcvUpdate[] getUpdates(long pDistributorConnectionId) {
		RcvUpdate[] tRcvUpdates = null;
		String tSubjectName = null;
		byte[] tData = null;
		super.decode();
		MessageBinDecoder tDecoder = super.getDecoder();
		
		int tSeqno = tDecoder.readInt(); // read sequence number
		mUpdateCount = tDecoder.readInt();
		tRcvUpdates = new RcvUpdate[mUpdateCount];
		
		for( int i = 0; i < mUpdateCount; i++) {
		  tSubjectName = tDecoder.readString();
		  tData = tDecoder.readBytes();
		  tRcvUpdates[i] = new RcvUpdate(pDistributorConnectionId, tSubjectName, tData, super.getHeaderAppId() );
		}
		return tRcvUpdates;
	}

	void addLargeUpdateHeader(String pSubjectName, int pAppId, int pDataSize) {
		MessageBinEncoder tEncoder = super.getEncoder();
		tEncoder.add( pSubjectName );
		tEncoder.add( true );
		tEncoder.add( pDataSize );

		mUpdateCount = 1;
	}
	
	int addLargeData(byte[] pUpdateData, int pOffset) {
		MessageBinEncoder tEncoder = super.getEncoder();
		int tDataLeft = pUpdateData.length - pOffset;
		int tSize = Math.min(tEncoder.getRemaining(), tDataLeft);
		
		ByteBuffer bb = tEncoder.getByteBuffer();
		bb.put(pUpdateData, pOffset, tSize);
		return tSize;
	}
	
	void readLargeDataHeader() {
		MessageBinDecoder tDecoder = super.getDecoder();
		mLargeSubjectName = tDecoder.readString();
		tDecoder.readBoolean();
		mLargeMessageSize = tDecoder.readInt();
	}

	String getLargeSubjectName() {
		return mLargeSubjectName;
	}
	
	int getLargeDataSize() {
		return mLargeMessageSize;
	}



	int getLargeData(byte[] pOutBuffer, int pOffset) {
		ByteBuffer bb = super.getDecoder().getByteBuffer();
		byte[] tInBuffer = bb.array();
		
		int tSizeToRead = bb.remaining();
		System.arraycopy(tInBuffer, bb.position(), pOutBuffer, pOffset, tSizeToRead);
		bb.position( bb.limit() );
		return tSizeToRead;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append( super.toString());
		sb.append("\n    <");
		sb.append("updseqno: " + getSequenceNumber());
		sb.append("  updcnt: " +  mUpdateCount);
		sb.append(">");
		return sb.toString();
	}

}
