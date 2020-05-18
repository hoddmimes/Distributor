package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.auxillaries.NumberConvert;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Comparator;

abstract class Segment implements Comparable<Segment>, Comparator<Segment> 
{

	final static int SEGMENT_HEADER_SIZE = 20;

	/**
	 * +---------------------------------+----------- + 
	 * | Protocol Version 				 |	   BYTE 2 | 
	 * +---------------------------------+----------- + 
	 * | Message Type                    |     BYTE 1 | 
	 * +---------------------------------+----------- + 
	 * | Message Flags                   |     BYTE 1 | 
	 * +---------------------------------+----------- + 
	 * | Local Host Address              |     Byte 4 | 
	 * +---------------------------------+----------- +
	 * | Sender Id                       |     Byte 4 | 
	 * +---------------------------------+----------- + 
	 * | Sender Start Time               |     Byte 4 |
	 * +---------------------------------+----------- +
	 * | Application Id 				 | 	   Byte 4 |
	 * +---------------------------------+------------+
	 */

	final static byte MSG_TYPE_UPDATE = 1;
	final static byte MSG_TYPE_RETRANSMISSION = 2;
	final static byte MSG_TYPE_HEARTBEAT = 3;
	final static byte MSG_TYPE_CONFIGURATION = 4;
	final static byte MSG_TYPE_RETRANSMISSION_RQST = 5;
	final static byte MSG_TYPE_RETRANSMISSION_NAK = 6;

	final static byte FLAG_M_SEGMENT_START = 1;
	final static byte FLAG_M_SEGMENT_MORE = 2;
	final static byte FLAG_M_SEGMENT_END = 4;
	final static byte FLAG_M_SEGMENT_BDXGWY = 8; // Sender of this segment is a bdxgwy


	/**
	 * Header Data Attributes
	 */
	private short 		mHdrVersion;
	private byte  		mHdrMsgType;
	private byte  		mHdrSegmentFlags;
	private InetAddress	mHdrLocalHostAddr;
	private int	  		mHdrSenderId;
	private int	  		mHdrSenderStartTime;
	private int			mHdrAppId;
	
	private MessageBinDecoder mDecoder;
	private MessageBinEncoder mEncoder;
	
	volatile int	mHashCodeValue;		// Segment hash code, use data in the NetHeader to compile the hash code
	volatile int	mSeqno;				// Sequence number only applicable for NetUpdate messages.
	

	/**
	 * Constructor for a segment used for sending data 
	 * @param pBufferSize, (max) size of the segment to send
	 */
	Segment(int pBufferSize) 
	{
		mEncoder = new MessageBinEncoder(pBufferSize);
		mDecoder = null;
		mHashCodeValue = 0;
		mSeqno = 0;
	}
	
	/**
	 * Constructor for a segment used for received data
	 * @param pByteBuffer, byte buffer containing read data
	 */
	Segment( ByteBuffer pByteBuffer)
	{
		mEncoder = null;
		mDecoder = new MessageBinDecoder(pByteBuffer);
		mHashCodeValue = 0;
		mSeqno = 0;
	}

	int getLength() {
		if (mDecoder != null) {
			return mDecoder.getLength(); // Get the length of data encoded 
		}
		return mEncoder.getLength(); // Get total length of data that could be decoded 
	}

    int getFreeSpaceLeft() {
    	return mEncoder.getRemaining();
    }
    
    int getAvailableDataLeft() {
    	return mDecoder.getRemaining();
    }
	
	
	void setHeader(short pVersion, byte pMessageType, byte pSegmentFlags,
			InetAddress pLocalAddress, int pSenderId, int pSenderStartTime, int pAppId) {
		setHeaderVersion(pVersion);
		setHeaderMessageType(pMessageType);
		setHeaderSegmentFlags(pSegmentFlags);
		setHeaderLocalAddress(pLocalAddress);
		setHeaderSenderId(pSenderId);
		setHeaderSenderStartTime(pSenderStartTime);
		setHeaderAppId(pAppId);
	}
	
	void encode() {
		mEncoder.reset();
		mEncoder.add(mHdrVersion);
		mEncoder.add(mHdrMsgType);
		mEncoder.add(mHdrSegmentFlags);
		mEncoder.add(InetAddressConverter.inetAddrToInt(mHdrLocalHostAddr));
		mEncoder.add(mHdrSenderId);
		mEncoder.add(mHdrSenderStartTime);
		mEncoder.add(mHdrAppId);
	}

	void decode() {
		mDecoder.reset();
		mHdrVersion = mDecoder.readShort();
		mHdrMsgType = mDecoder.readByte();
		mHdrSegmentFlags = mDecoder.readByte();
		mHdrLocalHostAddr = InetAddressConverter.intToInetAddr(mDecoder.readInt());
		mHdrSenderId = mDecoder.readInt();
		mHdrSenderStartTime = mDecoder.readInt();
		mHdrAppId = mDecoder.readInt();
	}
	
	
	
	
	void setHeaderAppId( int pAppId ) { mHdrAppId = pAppId; }
	int  getHeaderAppId() { return mHdrAppId;}

	void setHeaderVersion(short pVersion) {
	    mHdrVersion = pVersion;
	}

	short getHeaderVersion() {
		return mHdrVersion;
	}

	void setHeaderMessageType(byte pMsgType) {
		mHdrMsgType = pMsgType;
		if (mEncoder != null) {
			mEncoder.getByteBuffer().put(2,pMsgType);
		}
	}

	byte getHeaderMessageType() {
		return mHdrMsgType;
	}

	void setHeaderSegmentFlags(byte pSegmentFlags) {
		mHdrSegmentFlags = pSegmentFlags;
		if (mEncoder != null) {
			mEncoder.getByteBuffer().put(3, pSegmentFlags);
		}
	}

	byte getHeaderSegmentFlags() {
		return mHdrSegmentFlags;
	}

	void setHeaderLocalAddress(InetAddress pLocalAddress) {
		mHdrLocalHostAddr = pLocalAddress;
	}

	InetAddress getHeaderLocalAddress() {
		return mHdrLocalHostAddr;
	}

	void setHeaderSenderId(int pSenderId) {
		mHdrSenderId = pSenderId;
	}

	int getHeaderSenderId() {
		return mHdrSenderId;
	}

	void setHeaderSenderStartTime(int pSenderStartTime) {
		mHdrSenderStartTime = pSenderStartTime;
	}

	int getHeaderSenderStartTime() {
		return mHdrSenderStartTime;
	}

	//========================================================================================================

	void setSeqno( int pSeqno ) {
		mSeqno = pSeqno;
		if (mEncoder != null) {
			mEncoder.getByteBuffer().putInt(SEGMENT_HEADER_SIZE, pSeqno);
		}	  
	}

	
	boolean isUpdateMessage() {
		if ((getHeaderMessageType() == MSG_TYPE_UPDATE) || (getHeaderMessageType() == MSG_TYPE_RETRANSMISSION)) {
			return true;
		} else {
			return false;
		}
	}

	int getSeqno() 
	{
		return mSeqno;
	}

	@Override
	public int hashCode() {
		if (mHashCodeValue == 0) {
			int tAddr = ((mHdrLocalHostAddr.getAddress()[3]) << 24);
			int tSndrId = ((mHdrSenderId & 0xff) << 16);
			int tTime = (mHdrSenderStartTime & 0xffff);
			mHashCodeValue = tAddr + tSndrId + tTime;
		}
		return mHashCodeValue;
	}

	
	
	@Override
	public boolean equals(Object pObj) {
		Segment tObj = (Segment) pObj;

		if (pObj == this) {
			return true;
		}

		if ((mHdrLocalHostAddr.equals(tObj.mHdrLocalHostAddr)) &&
			(mHdrSenderId == tObj.mHdrSenderId) && 
			(mHdrSenderStartTime == tObj.mHdrSenderStartTime)) {
			return true;
		}
		return false;
	}
	
	
	@Override
	public int compareTo(Segment o) {
		int tSts = mHdrLocalHostAddr.toString().compareTo(o.mHdrLocalHostAddr.toString());
		if (tSts != 0) {
			return tSts;
		}
		if (mHdrSenderId < o.mHdrSenderId) {
			return -1;
		} else if (mHdrSenderId > o.mHdrSenderId) {
			return 1; 
		}
		
		if (mHdrSenderStartTime < o.mHdrSenderStartTime) {
			return -1;
		} else if (mHdrSenderStartTime > o.mHdrSenderStartTime) {
			return 1; 
		}
		
		return 0;
	}

	@Override
	public int compare(Segment o1, Segment o2) {
		return o1.compareTo(o2);
	}
	
	
	
	
	String getFlagsString() {
		StringBuilder sb = new StringBuilder();

		if (mHdrSegmentFlags == 0) {
			return "NONE";
		}

		if ((mHdrSegmentFlags & FLAG_M_SEGMENT_START) != 0) {
			sb.append("START+");
		}
		if ((mHdrSegmentFlags & FLAG_M_SEGMENT_MORE) != 0) {
			sb.append("MORE+");
		}
		if ((mHdrSegmentFlags & FLAG_M_SEGMENT_END) != 0) {
			sb.append("END+");
		}
		if ((mHdrSegmentFlags & FLAG_M_SEGMENT_BDXGWY) != 0) {
			sb.append("BDXGWY+");
		}
		return sb.substring(0,sb.length() - 1);
	}

	String getMessageTypeString() {

		if (mHdrMsgType == MSG_TYPE_UPDATE) {
			return "UPDATE";
		}
		else if (mHdrMsgType == MSG_TYPE_RETRANSMISSION) {
			return "RETRANSMISSION";
		}
		else if (mHdrMsgType == MSG_TYPE_HEARTBEAT) {
			return "HEARTBEAT";
		}
		else if (mHdrMsgType == MSG_TYPE_CONFIGURATION) {
			return "CONFIGURATION";
		}
		else if (mHdrMsgType == MSG_TYPE_RETRANSMISSION_RQST) {
			return "RETRANSMISSION_RQST";
		}
		else if (mHdrMsgType == MSG_TYPE_RETRANSMISSION_NAK) {
			return "TYPE_RETRANSMISSION_NAK";
		}

		return "UNKNOWN (" + mHdrMsgType + ")";
	}

   
   

	
   static public String netAddressAsString( int pIpAddress ) {
		byte[] tArr = NumberConvert.int2Bytes( pIpAddress );
		try {
			return Inet4Address.getByAddress(tArr).toString();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[ Type: ");
		sb.append( getMessageTypeString() );
		sb.append(" SndrId: ");
		sb.append( Integer.toHexString(mHdrSenderId));
		sb.append(" Len: ");
		sb.append( getLength() );
		sb.append(" Flgs: ");
		sb.append(getFlagsString());
		sb.append(" LclHst: ");
		sb.append(  mHdrLocalHostAddr.toString() );
		sb.append(" StartTime: ");
		sb.append( mHdrSenderStartTime );
		sb.append(" Vrs: ");
		sb.append(Integer.toHexString(mHdrVersion));
		sb.append(" AppId: ");
		sb.append(Integer.toHexString(mHdrAppId));

		if ((mHdrMsgType == MSG_TYPE_UPDATE) || (mHdrMsgType == MSG_TYPE_RETRANSMISSION)) {
	      int pValue;
		  if (mDecoder != null) {
			sb.append(" Seqno: ");
			pValue = mDecoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE);
			sb.append(pValue);
			sb.append(" Updcnt: ");
			pValue = mDecoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE + 4);
			sb.append(pValue);
		  } else {
			sb.append(" Seqno: ");
			pValue = mEncoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE);
			sb.append(pValue);
			sb.append(" Updcnt: ");
			pValue = mEncoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE + 4);
			sb.append(pValue);
		  }
		}

		sb.append("]");
		return sb.toString();
	}
	
	MessageBinDecoder getDecoder() {
		return mDecoder;
	}
	
	MessageBinEncoder getEncoder() {
		return mEncoder;
	}
	
	/**
	 * The following two routines are back door methods for accessing
	 * MsgUpdate attributes is a convenient and fast way. Ugly? Absolutely!!!
	 * For offset within the buffer see NetUpdate message
	 *
	 * In a segment the segment header is present first in the buffer.
	 * The for update messages an int32_t SEQNO followed by a UPDATE-COUNT follows.
	 */
	int getUpdateSeqno()
	{
		if (mDecoder != null) {
			return mDecoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE);
		}
		return mEncoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE);
	}



	int getUpdateUpdateCount()
	{
		if (mDecoder != null) {
			return mDecoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE + 4);
		}
		return mEncoder.getByteBuffer().getInt(SEGMENT_HEADER_SIZE + 4);
	}
	
	
}
