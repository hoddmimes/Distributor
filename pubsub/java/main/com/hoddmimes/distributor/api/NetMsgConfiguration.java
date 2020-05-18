package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;

import java.net.InetAddress;




class NetMsgConfiguration extends NetMsg 
{
	private InetAddress	mMcAddress;
	private int	mMcPort;
	private int	mHbInterval;
	private int	mCfgInterval;
	private String mApplName;
	private int mSenderId;
	private InetAddress mHostAddress;
	private long mSenderStartTime;



	NetMsgConfiguration(Segment pSegment) {
		super(pSegment);
	}

	void set(InetAddress pMcAddress, int pMcPort, int pSenderId,
			long pStartTime, int pHeartbeatInterval, int pConfigurationInterval,
			InetAddress pHostAddress, String pApplicationName) {
		setMcAddress(pMcAddress);
		setMcPort(pMcPort);
		setHeartbeatInterval(pHeartbeatInterval);
		setSenderStartTime(pStartTime);
		setSenderId(pSenderId);
		setConfigurationInterval(pConfigurationInterval);
		setHostAddress( pHostAddress );
		setApplicationName( pApplicationName );
	}

	void encode() {
		super.encode();
		MessageBinEncoder tEncoder = super.getEncoder();
		tEncoder.add( InetAddressConverter.inetAddrToInt( mMcAddress));
		tEncoder.add( mMcPort );
		tEncoder.add( mSenderId );
		tEncoder.add( mSenderStartTime );
		tEncoder.add( mHbInterval);
		tEncoder.add( mCfgInterval );
		tEncoder.add( InetAddressConverter.inetAddrToInt( mHostAddress ));
		tEncoder.add( mApplName );

	}
	
	void decode() {
		super.decode();
		MessageBinDecoder tDecoder = super.getDecoder();
		mMcAddress = InetAddressConverter.intToInetAddr( tDecoder.readInt());
		mMcPort = tDecoder.readInt();
		mSenderId = tDecoder.readInt();
		mSenderStartTime = tDecoder.readLong();
		mHbInterval = tDecoder.readInt();
		mCfgInterval = tDecoder.readInt();
		mHostAddress = InetAddressConverter.intToInetAddr(tDecoder.readInt());
		mApplName = tDecoder.readString();

	}
	
	
	void setHostAddress( InetAddress pHostAddress ) {
		mHostAddress = pHostAddress;
	}


	void setApplicationName( String pApplicationName ) {
		mApplName = pApplicationName;
	}
	
	void setSenderId(int pSenderId) {
		mSenderId = pSenderId;
	}

	int getSenderId() {
	  return mSenderId;
	}

	InetAddress getHostAddress() {
		return mHostAddress;
	}

	String getApplicationName() {
		return mApplName;
	}



	void setMcAddress(InetAddress pMcAddress) {
		mMcAddress = pMcAddress;
	}

	InetAddress getMcAddress() {
		return mMcAddress;
	}

	void setSenderStartTime(long pStartTime) {
		mSenderStartTime = pStartTime;
	}

	long getSenderStartTime() {
		return mSenderStartTime;
	}

	void setMcPort(int pPort) {
		mMcPort = pPort;
	}

	int getMcPort() {
		return mMcPort;
	}

	void setHeartbeatInterval(int pInterval) {
		mHbInterval = pInterval;
	}

	int getHeartbeatInterval() {
		return mHbInterval;
	}

	void setConfigurationInterval(int pInterval) {
		mCfgInterval = pInterval;
	}

	int getConfigurationInterval() {
		return mCfgInterval;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( super.toString());
		sb.append("\n    <");
		sb.append("MC Addr: " + getMcAddress().toString());
		sb.append(" MC port: " + getMcPort());
		sb.append(" HB intval: " + getHeartbeatInterval());
		sb.append(" CFG intval: " + getConfigurationInterval());
		sb.append(" StartTime: " + getSenderStartTime());
		sb.append(" SndrId: " + Integer.toHexString(getSenderId()));
		sb.append(" Host: " + getHostAddress().toString() );
		sb.append(" Appl: " + getApplicationName());
		sb.append(">");
		return sb.toString();
	}

}
