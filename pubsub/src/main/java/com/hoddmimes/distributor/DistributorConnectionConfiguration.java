package com.hoddmimes.distributor;

import java.net.InetAddress;

/**
 * A <i>DistributorConnectionConfiguration</i> instance wraps the parameters defining the
 * characteristics and behavior for a  Distributor Connection. The <i>DistributorConnectionConfiguration</i>
 * object is passed as parameter to the method creating a connection instance
 * i.e. Distributor.createConnection. The attributes in the
 * <i>DistributorConnectionConfiguration</i>object should be set before the connection is
 * created i.e. they are not dynamic.
 * 
 * @author POBE
 * 
 */

public class DistributorConnectionConfiguration {


	public static final String DEFAULT_MCA_ADDRESS = "224.42.42.1";
	public static final int DEFAULT_MCA_PORT = 4201;
	public static final String DEFAULT_MCA_NETWORK_INTERFACE = null;

	public static final long DEFAULT_FLOW_RATE_CHECK_INTERVAL = 100;
	

	public static final int DEFAULT_TTL = 128;
	public static final int DEFAULT_READ_THREADS = 1;
	public static final int DEFAULT_IP_BUFFER_SIZE = 128000;

	public static final int DEFAULT_SEGMENT_SIZE = 8192;
	public static final int DEFAULT_SMALL_SEGMENT_SIZE = 512;

	public static final long DEFUALT_CONFIGURATION_INTERVAL = 15000;
	public static final int DEFUALT_CONFIGURATION_MAX_LOST = 3;

	public static final long DEFUALT_HEARTBEAT_INTERVAL = 3000;
	public static final int DEFUALT_HEARTBEAT_MAX_LOST = 10;
	public static final long DEFUALT_FLOW_RECALCULATE_INTERVAL = 100;
	public static final int DEFAULT_MAX_BANDWIDTH = 0; // Kbit /sec
	public static final int DEFAULT_FAKE_XTA_ERROR_RATE = 0;
	public static final int DEFAULT_FAKE_RCV_ERROR_RATE = 0;

	public static final long DEFAULT_SEND_HOLDBACK = 0;
	public static final int DEFAULT_SEND_HOLDBACK_THRESHOLD = 200;
	public static final int DEFAULT_SENDER_ID_PORT_OFFSET = 61234;
	public static final int DEFAULT_SENDER_ID = 0;
	public static final int DEFAULT_RETRANS_SERVER_HOLDBACK = 20;
	public static final int DEFAULT_RETRANS_TIMEOUT = 800;
	public static final int DEFAULT_RETRANS_MAX_RETRIES = 10;
	public static final int DEFAULT_RETRANS_MAX_CACHE_SIZE = 10000000;
	public static final int DEFAULT_RETRANS_CACHE_LIFE_TIME = 60;
	public static final long DEFAULT_RETRANS_CACHE_CLEAN_INTERVAL = 2000;
	public static final long DEFAULT_NAGGING_WINDOW_INTERVAL = 4000;
	public static final long DEFAULT_NAGGING_CHECK_INTERVAL = 60000;
	public static final int DEFAULT_MAX_RECEIVER_QUEUE_LENGTH = 10000;
	public static final long DEFAULT_STATISTICS_INTERVAL = 0;

	/**
	 * Member variables
	 */


	protected InetAddress mMca; // Multicast group
	protected String mMcaNetworkInterface; // Multicast group interface address
	protected int mMcaPort; // Multicast group destination port
	
	protected long mStatisticInterval; // Statistics log interval
	protected String mStatisticFullFilename;

	protected int mTTL; // Multicast group TTL
	protected int mReadThreads; // Read threads
	protected int mIpBufferSize; // IP send/ receive buffer size
	protected int mSegmentSize; // MCA Segment size
	protected int mSmallSegmentSize; // Small MCA segment size

	protected int mFakeXtaErrorRate; // Simulated XTA error rate in promille
	protected int mFakeRcvErrorRate; // Simulated XTA error rate in promille

	protected long mConfigurationInterval; // Configuration Interval
	protected int mConfigurationMaxLost; // Max Lost Configuration messages
	protected long mHeartbeatInterval; // Heartbeat interval
	protected int mHeartbeatMaxLost; // Max lost Heartbeat

	protected int mSenderIdPortOffset; // Sender Id Port Offset
	protected int mSenderIdPort; // Multicast group sender id port
	protected long mSendHoldbackDelay; // Publisher holdback delay
	protected int mSendHoldbackThreshold; // Publisher holdback thresholdvalue
	protected int mMaxBandwidthKbit; // Max publisher bandwidth
	protected long mFlowRateRecalculateInterval;	 // How often the flow rate will be recalculated

	protected long mRetransmissionServerHoldback; // Retransmission holdback on publisher side
	protected long mRetransmissionTimeout; // Retransmission time (if not being servered)
	protected int  mRetransmissionRetries; // Max number of retransmission retries
	protected int  mRetransmissionMaxCacheSize; // Max size of of the the elements in the cache
	protected int  mRetransmissionCacheLifeTime; // Max time an elemnt will be keept in the retransmission cache
	protected long mRetransmissonCacheCleanInterval; // How frequently the retransmission cache in cleaned/purged

	/**
	 * If there has been an retransmission within the nagging window for nagging
	 * consecutive ticks the client is consider to be nagging an error will be
	 * signaled to upper layer.
	 */
	protected int mNaggingWindowInterval; // Nagging window
	protected int mNaggingCheckInterval; // Nagging check interval
	protected int mNaggingMaxRetransmission; // Max retransmission during the
	// check interval

	protected int mMaxReceiveQueueElements; // Maximum number of elements that

	// can be queued to the API consumer

	/**
	 * Constructor for creating a <i>DistributorConnectionConfiguration</i> instance
	 * 
	 * @param pMcaAddress UDP (class D) address used when sending / receiving data
	 * @param pMcaSourcePort UPD port on which data will be read and sent
	 */
	public DistributorConnectionConfiguration(String pMcaAddress, int pMcaSourcePort) {

		mMca = getInetAddress(pMcaAddress);
		mMcaNetworkInterface = DEFAULT_MCA_NETWORK_INTERFACE;
		mSenderIdPort = DEFAULT_SENDER_ID;
		mMcaPort = pMcaSourcePort;

		mFakeXtaErrorRate = DEFAULT_FAKE_XTA_ERROR_RATE;
		mFakeRcvErrorRate = DEFAULT_FAKE_RCV_ERROR_RATE;

		mTTL = DEFAULT_TTL;
		mReadThreads = DEFAULT_READ_THREADS;
		mIpBufferSize = DEFAULT_IP_BUFFER_SIZE;

		mStatisticFullFilename = null;

		mSegmentSize = DEFAULT_SEGMENT_SIZE;
		mSmallSegmentSize = DEFAULT_SMALL_SEGMENT_SIZE;

		mConfigurationInterval = DEFUALT_CONFIGURATION_INTERVAL;
		mConfigurationMaxLost = DEFUALT_CONFIGURATION_MAX_LOST;

		mSenderIdPortOffset = DEFAULT_SENDER_ID_PORT_OFFSET;
		mHeartbeatInterval = DEFUALT_HEARTBEAT_INTERVAL;
		mHeartbeatMaxLost = DEFUALT_HEARTBEAT_MAX_LOST;
		mMaxBandwidthKbit = DEFAULT_MAX_BANDWIDTH;
		mRetransmissionServerHoldback = DEFAULT_RETRANS_SERVER_HOLDBACK;

		mSendHoldbackDelay = DEFAULT_SEND_HOLDBACK;
		mSendHoldbackThreshold = DEFAULT_SEND_HOLDBACK_THRESHOLD;

		mRetransmissionTimeout = DEFAULT_RETRANS_TIMEOUT;
		mRetransmissionRetries = DEFAULT_RETRANS_MAX_RETRIES;
		mRetransmissionMaxCacheSize = DEFAULT_RETRANS_MAX_CACHE_SIZE;
		mRetransmissionCacheLifeTime = DEFAULT_RETRANS_CACHE_LIFE_TIME;
		mRetransmissonCacheCleanInterval = DEFAULT_RETRANS_CACHE_CLEAN_INTERVAL;
		mFlowRateRecalculateInterval = DEFUALT_FLOW_RECALCULATE_INTERVAL;

		mStatisticInterval = DEFAULT_STATISTICS_INTERVAL;
	}

	/**
	 * Get the interval with which the outbound flow rate should be calculated.
	 * @return flow rate recalculation interval in milliseconds.
	 */
	public long getFlowRateRecalculateInterval() {
		return mFlowRateRecalculateInterval;
	}
	
	/**
	 * sets the outbound flow recalculations interval in milliseconds. By default the value is set to 100 ms.
	 * @param pInterval interval in milliseconds 
	 */
	public void setFlowRateRecalculateInterval( long pInterval) {
		mFlowRateRecalculateInterval = pInterval;
	}
	


	/**
	 * Return the name of the statstic log file name to be used.
	 * 
	 * 
	 * @return String, file name
	 */
	public String getStatisticFilename() {
		return mStatisticFullFilename;
	}

	/**
	 * Sets the name of the file to which statistical information for the
	 * connection periodically will be written. By default the name is "null" the name is implicated generated based on the application name given 
	 * and a time stamp when the the file is created. 
	 * 
	 * @param pFilename statistical log file
	 */
	public void setStatisticFilename(String pFilename) {
		mStatisticFullFilename = pFilename;
	}

	/**
	 * Returns the UDP address being used when sending/receiving data.
	 * 
	 * @return InetAddress UDP address
	 */
	public InetAddress getMca() {
		return mMca;
	}

	/**
	 * Returns the network interface being used. A null value implicates that
	 * the the service is being enabled on the default interface.
	 * 
	 * @return String, network interface being used
	 */
	public String getMcaNetworkInterface() {
		return mMcaNetworkInterface;
	}

	/**
	 * Sets the interface that will be used when sending / receiving data. 
	 * By default INET_ANY is used. I.e. information may be received and sent on any interface.
	 * 
	 * @param pInterface interface symbolic name.
	 */
	public void setMcaNetworkInterface(String pInterface) {
		mMcaNetworkInterface = pInterface;
	}

	/**
	 * Returns the sender port id that uniquely identifies the connection within
	 * the host.
	 * 
	 * @return int, sender id port
	 */
	public int getSenderIdPort() {
		return mSenderIdPort;
	}

	/**
	 * Sets a connection sender port. Multiple applications can start the same
	 * connection i.e. use same UDP address / UDP port in order for receivers to
	 * make a distinction between two different application using the same
	 * connection on the same host a connection will use an additional
	 * "sender-id-port". Normally the distributor allocates this
	 * "sender-id-port" implicit when the connection (by binding a server
	 * tcp/ip). But for some reason it is possible to set this manually via this
	 * routine.
	 * 
	 * @param pValue <i>sender-id-port</i>
	 */
	public void setSenderIdPort(int pValue) {
		mSenderIdPort = pValue;
	}

	/**
	 * Returns the UDP port used
	 * 
	 * @return int, UDP port
	 */
	public int getMcaPort() {
		return mMcaPort;
	}



	private InetAddress getInetAddress(String pAddressString) {
		InetAddress tAddress = null;
		try {
			tAddress = InetAddress.getByName(pAddressString);
		} catch (Exception e) {
			e.printStackTrace();
			tAddress = null;
		}
		return tAddress;
	}

	/**
	 * Returns the TTL being used when sending data
	 * 
	 * @return int, time-to-live value
	 */
	public int getTTL() {
		return mTTL;
	}

	/**
	 * Sets the the time-to-live value used when sending data.
	 * 
	 * @param pValue time-to-live value
	 */
	public void setTTL(int pValue) {
		mTTL = pValue;
	}

	/**
	 * Returns the number of reader threads going to be started
	 * 
	 * @return int, read threads.
	 */
	public int getReadThreads() {
		return mReadThreads;
	}

	/**
	 * Sets the number of reader threads reading data. By the default one thread
	 * is started. Possibly having additional threads could result in better
	 * overlapp. Not convinced that multiple reader threads will make a
	 * difference.
	 * 
	 * @param pValue
	 *            number of reader threads to start.
	 */
	public void setReadThreads(int pValue) {
		mReadThreads = pValue;
	}

	/**
	 * Sets the IP receive (SO_RCVBUF) and send buffer (SO_SNDBUF) size
	 * 
	 * @param pBufferSize receive/send buffer size
	 */
	public void setIpBufferSize(int pBufferSize) {
		mIpBufferSize = pBufferSize;
	}

	/**
	 * Returns the IP receive (SO_RCVBUF) and send buffer (SO_SNDBUF) size
	 * 
	 * @return int, receive/send buffer size
	 */
	public int getIpBufferSize() {
		return mIpBufferSize;
	}

	/**
	 * Returns the heartbeat interval being used.
	 * 
	 * @return long, heartbeat interval in milliseconds
	 */
	public long getHearbeatInterval() {
		return mHeartbeatInterval;
	}

	/**
	 * Sets the heartbeat interval. In case no data is being sent on the
	 * connection a heartbeat message with the latest published sequence number
	 * will be sent inorder of receivers to discover if last message transmitted
	 * was lost.
	 * 
	 * @param pValue
	 *            heartbeat interval in milliseconds
	 */
	public void setHearbeatInterval(long pValue) {
		mHeartbeatInterval = pValue;
	}

	/**
	 * returns the maximum number of consequtive heartbeats that could be "lost"
	 * from a remote connection before the remote connection is considered to be
	 * "dead"
	 * 
	 * @return int, max lost heartbeats.
	 */
	public long getHearbeatMaxLost() {
		return mHeartbeatMaxLost;
	}

	/**
	 * Sets max allowed lost heartbeats.
	 * 
	 * @param pValue
	 *            max lost heartbeats
	 */
	public void setHearbeatMaxLost(int pValue) {
		mHeartbeatMaxLost = pValue;
	}

	/**
	 * Returns the maximum bandwidth (bytes/sec) that could be sent on the
	 * connection
	 * 
	 * @return int, max bandwidth bytes/sec (Mbit/sec)
	 */
	public int getMaxBandwidthKbit() {
		return mMaxBandwidthKbit;
	}
	

	/**
	 * Sets the maximum number of bytes that the connection can push out per
	 * second. By the default the bandwidth is set to zero implicating that the
	 * bandwidth allowed is infinite. The minimum bandwith will never be less than
	 * 250 Kbit/sec
	 * 
	 * @param pKbitPerSec max bandwidth (bytes/sec). Expressed as Kbit/sec
	 */
	public void setMaxBandwidth(int pKbitPerSec) {
		mMaxBandwidthKbit = pKbitPerSec;
	}

	/**
	 * Returns the server retransmision holdback value
	 * 
	 * @return long, server retransmission holdback in milliseconds
	 */
	public long getRetransmissionServerHoldback() {
		return mRetransmissionServerHoldback;
	}

	/**
	 * Sets the server retransmission holdback time (in milliseconds). When a
	 * retransmission request is received the missed message will not be re-sent
	 * immidetatly in case holdback value is &gt; 0. In case a message is missed
	 * it's not unlikely that more an one receiver is missing the message. By
	 * hold back the re-publishing of the missed message multiple retransmission
	 * requests can be "servered" if they arrive within the holdback interval,
	 * this since retransmissions are sent out as an UDP message.
	 * 
	 * @param pValue
	 */
	public void setRetransmissionServerHoldback(long pValue) {
		mRetransmissionServerHoldback = pValue;
	}

	/**
	 * Returns the retransmission timeout.
	 * 
	 * @return long, retransmission timeout (in milesonds).
	 */
	public long getRetransmissionTimeout() {
		return mRetransmissionTimeout;
	}

	/**
	 * Sets the retransmission timeout. Defines the timeout period before an
	 * unserved retransmission request will be re-requested.
	 * 
	 * @param pMilliseconds
	 *            retransmission timeout (in milliseconds)
	 */
	public void setRetransmissionTimeout(long pMilliseconds) {
		mRetransmissionTimeout = pMilliseconds;
	}

	/**
	 * Returns maximum number of retransmission retries.
	 * 
	 * @return int, max retries
	 */
	public int getRetransmissionRetries() {
		return mRetransmissionRetries;
	}

	/**
	 * Sets the maximum number of retransmission retries. A missed messages will
	 * be rerequest max 'n' times then associated subscribers will be notified
	 * about the data flow has been corrupted i.e
	 * DistributorTooManyRetransmissionRetriesException is signaled to the
	 * subscribers.
	 * 
	 * @param pValue
	 *            max number of retries.
	 */
	public void setRetransmissionRetries(int pValue) {
		mRetransmissionRetries = pValue;
	}

	/**
	 * Returns the maximum size of retransmission cache
	 * 
	 * @return int, maximum size in bytes
	 */
	public int getRetransmissionMaxCacheSize() {
		return mRetransmissionMaxCacheSize;
	}

	/**
	 * Sets the maximum size of the retransmission cache. When the upper limit
	 * is reached and additional messages are to be added to the cache old
	 * messages are removed so that the cahesize does not exceeds the defined
	 * threashold value (i.e. FIFO).
	 * 
	 * @param pValue
	 *            max retransmission cache size in bytes.
	 */
	public void setRetransmissionMaxCacheSize(int pValue) {
		mRetransmissionMaxCacheSize = pValue;
	}

	/**
	 * Returns the time an message will be kept in the retransmission cache
	 * before being removed from the cache
	 * 
	 * @return int, time in seconds.
	 */
	public int getRetransmissionCacheLifeTime() {
		return mRetransmissionCacheLifeTime;
	}

	/**
	 * Sets the time an message will be kept in the retransmission cache before
	 * being removed from the cache. A value of zero results in an infinite
	 * cache life time.
	 * <p>
	 * This mechanism works in conjuction with the cache-max-size. An element is
	 * removed either when it the life time expires or when there is a need for
	 * new messages i.e. FIFO.
	 * 
	 * @param pValue
	 *            time in seconds.
	 */
	public void setRetransmissionCacheLifeTime(int pValue) {
		mRetransmissionCacheLifeTime = pValue;
	}

	/**
	 * Sets how frequently the retransmission cache will be sweeped and purged.
	 * 
	 * @param pInterval
	 *            in milliseconds.
	 */
	public void setRetransmissionCacheCleanInterval(long pInterval) {
		mRetransmissonCacheCleanInterval = pInterval;
	}

	/**
	 * Returns how frequently the retransmission cache will be sweeped and
	 * purged.
	 * 
	 * @return long, cleaning interval
	 */
	public long getRetransmissionCacheCleanInterval() {
		return mRetransmissonCacheCleanInterval;
	}

	/**
	 * Returns the nagging interval check
	 * 
	 * @return int, interval in seconds
	 */
	public int getNaggingWindowInterval() {
		return mNaggingWindowInterval;
	}

	/**
	 * Sets the nagging check interval. Each connection will monitor themself to
	 * see if they misbehave i.e. if they generate too many retransmission
	 * request. The connection will every "interval" examine if a retransmission
	 * request has been sent since the previous check. If 'n' (tick-limit)
	 * consequtive intervals with retransmission requests has been detected the
	 * connection will consider it self to be nagging an generate an error to
	 * subscribers and publishers. The connection is the considered as corrupted
	 * and should be closed.
	 * 
	 * @param pValue
	 *            check interval in seconds
	 */
	public void setNaggingWindowInterval(int pValue) {
		mNaggingWindowInterval = pValue;
	}

	/**
	 * Returns nagging tick limit.
	 * 
	 * @return int, max consequetive ticks
	 */
	public int getNaggingCheckInterval() {
		return mNaggingCheckInterval;
	}

	/**
	 * Sets the max consequetive naggiging ticks intervals. Each connection will
	 * monitor themself to see if they misbehave i.e. if they generate too many
	 * retransmission request. The connection will every "windows interval"
	 * examine if a retransmission request has been sent since the previous
	 * check. If there has been retransmissions during every window for the
	 * whole check interval the connection will consider it self to be nagging
	 * an generate an error to subscribers and publishers. The connection is the
	 * considered as corrupted and should be closed.
	 * 
	 * @param pValue
	 *            max consequetive ticks
	 */
	public void setNaggingCheckInterval(int pValue) {
		mNaggingCheckInterval = pValue;
	}

	/**
	 * Sets the number of retransmissions that must have been issued during the
	 * nagging check interval in order to consider the connection to be
	 * "nagging".
	 * 
	 * @param pValue
	 *            retransmission count
	 */
	public void setNaggingMaxRetransmissions(int pValue) {
		mNaggingMaxRetransmission = pValue;
	}

	/**
	 * Returns the number of retransmissions that must have been issued during
	 * the nagging check interval in order to consider the connection to be
	 * "nagging".
	 * 
	 * @return int retransmission count
	 */
	public int getNaggingMaxRetransmissions() {
		return mNaggingMaxRetransmission;
	}

	/**
	 * Returns upper limit of elements in the delivery queue.
	 * 
	 * @return int, max undelivered updates to an application
	 */

	public int getMaxReceiveQueueElements() {
		return mMaxReceiveQueueElements;
	}

	/**
	 * Sets the max number of element that could exists in the delivery queue to
	 * the application before an error is signaled to the application and the
	 * connection becommes in an error state. By the deafult no limit is set,
	 * this implies that the queue can grow beyond any limit and the application
	 * runs out of memory. This will happen if updates are delivered to the
	 * application in a higher rate than the application can process them.
	 * 
	 * @param pValue
	 *            max elements in the delivery queue
	 */
	public void setMaxReceiveQueueElements(int pValue) {
		mMaxReceiveQueueElements = pValue;
	}

	/**
	 * Sets how frequently the connection should announce its presents
	 * 
	 * @param pValue
	 *            interval in milliseconds
	 */
	public void setConfigurationInterval(long pValue) {
		mConfigurationInterval = pValue;
	}

	/**
	 * Returns configuration interval
	 * 
	 * @return long, configuration interval in milliseconds.
	 */
	public long getConfigurationInterval() {
		return mConfigurationInterval;
	}

	/**
	 * Set max number allowed missed configuration messages from a remote
	 * connection before the remote connection is considered as "dead".
	 * 
	 * @param pValue
	 *            max lost configuration messages.
	 */
	public void setConfigurationMaxLost(int pValue) {
		mConfigurationMaxLost = pValue;
	}

	/**
	 * returns max lost allowed configuration messages for a remote connection.
	 * 
	 * @return int, max lost configuration messages.
	 */
	public int getConfigurationMaxLost() {
		return mConfigurationMaxLost;
	}

	/**
	 * Returns the interval for which statistical information will be logged to
	 * the statistical log file.
	 * 
	 * @return long, log interval in milliseconds
	 */
	public long getStatisticsLogInterval() {
		return mStatisticInterval;
	}

	/**
	 * Sets the logging interval with which statistical information will be
	 * written to the statistical log file. By the default the interval is zero
	 * implicating that no logging will take place.
	 * 
	 * @param pInterval
	 *            logging interval in milliseconds
	 */
	public void setStatisticsLogInterval(long pInterval) {
		mStatisticInterval = pInterval;
	}

	/**
	 * Sets how frequently transmission error should be faked. The purpose with
	 * this attribute is to be able to simulate transmission errors and test the
	 * recovery mechanism. The value given specifies the error rate in milli
	 * precentage. For example "10" will in average result in 10 dropped
	 * messages for every 1000 sent messages.
	 * 
	 * @param pValue
	 *            error rate in milli presentage
	 */
	public void setFakeXtaErrorRate(int pValue) {
		mFakeXtaErrorRate = pValue;
	}

	/**
	 * Returns the XTA simulated error rate
	 * 
	 * @return simulated error rate in milli precentage.
	 */
	public int getFakeXtaErrorRate() {
		return mFakeXtaErrorRate;
	}

	/**
	 * Sets how frequently receiver error should be faked. The purpose with this
	 * attribute is to be able to simulate transmission errors and test the
	 * recovery mechanism. The value given specifies the error rate in milli
	 * precentage. For example "10" will in average result in 10 dropped
	 * messages for every 1000 sent messages.
	 * 
	 * @param pValue
	 *            error rate in milli presentage
	 */
	public void setFakeRcvErrorRate(int pValue) {
		mFakeRcvErrorRate = pValue;
	}

	/**
	 * Returns the simulated receive error rate
	 * 
	 * @return simulated error rate in milli precentage.
	 */
	public int getFakeRcvErrorRate() {
		return mFakeRcvErrorRate;
	}

	/**
	 * Sets the port offset where the distributor will try to allocate sender
	 * ports. The port offset should be somewhere between 1024-65000. Normally
	 * you should use the default value 61234.
	 * 
	 * @param pValue
	 *            sender port offset
	 */
	public void setSenderIdPortOffset(int pValue) {
		mSenderIdPortOffset = pValue;
	}

	/**
	 * Returns the the sender id port offset
	 * 
	 * @return int, port offset
	 */
	public int getSenderIdPortOffset() {
		return mSenderIdPortOffset;
	}

	/**
	 * Sets the sender holdback time. The distributor is normally trying to
	 * publish an update as fast a possibly. This results in few updates and
	 * many physical I/O In case of relatively high update rates a holdback will
	 * increase more updates going into the packages and the overall number of
	 * I/O being reduced, resulting in an better throughput. The general
	 * approach is that a package is sent when being filled or holdback timer
	 * expires.
	 * 
	 * @param pHoldbackDelay
	 *            holdback time in milliseconds
	 */
	public void setSendHoldbackDelay(long pHoldbackDelay) {
		mSendHoldbackDelay = pHoldbackDelay;
	}

	/**
	 * Returns the sender holdback time.
	 * 
	 * @return long, sender holdback time in milliseconds.
	 */
	public long getSendHoldbackDelay() {
		return mSendHoldbackDelay;
	}

	/**
	 * Sets the holdback threashold value. The holdback value sets the rate when
	 * the holdback wil kick in. In case the update rate is lower than the
	 * holdback threashold value the distributor will publish update as soon as
	 * possible. If the rate is higher the holdback will be applied.
	 * 
	 * @param pHoldbackThreshold
	 *            holdback treashold back
	 */
	public void setSendHoldbackThreshold(int pHoldbackThreshold) {
		mSendHoldbackThreshold = pHoldbackThreshold;
	}

	/**
	 * Returns the holdback treashold value
	 * 
	 * @return int, holdback treshold value
	 */
	public int getSendHoldbackThreshold() {
		return mSendHoldbackThreshold;
	}

	/**
	 * Sets the packets size with what the distributor will publish messages.
	 * The segment size must be less than 64 Kb (limit of UDP messages).
	 * 
	 * @param pValue
	 *            distributor segment size.
	 */
	public void setSegmentSize(int pValue) {
		mSegmentSize = pValue;
	}

	/**
	 * Returns the distributor segment/package size.
	 * 
	 * @return int, segment size in bytes.
	 */
	public int getSegmentSize() {
		return mSegmentSize;
	}

	/**
	 * Sets the segment size used when sending distributor controll messages
	 * like heartbeats, configuration messages etc. By default the small segment
	 * size is set to 512 and there should be no reason to change this value.
	 * 
	 * @param pValue
	 *            small segment size in bytes.
	 */
	public void setSmallSegmentSize(int pValue) {
		mSmallSegmentSize = pValue;
	}

	/**
	 * Returns the size of small segments.
	 * 
	 * @return int, smal segment size in bytes.
	 */
	public int getSmallSegmentSize() {
		return mSmallSegmentSize;
	}

	@Override
	public String toString() {
		return "mMca:														" + mMca.toString() + "\n"
				+ "mMcaInterfaceAddress;                  			"
				+ mMcaNetworkInterface + "\n"
				+ "mSenderIdPort:                             			"
				+ mSenderIdPort + "\n"
				+ "mMcaPort:                     							" + mMcaPort + "\n"
				+ "mTTL:                                           			" + mTTL
				+ "\n" + "mReadThread:                               			"
				+ mReadThreads + "\n"
				+ "mIpBufferSize:                                  			"
				+ mIpBufferSize + "\n" + "mSegmentSize:											"
				+ mSegmentSize + "\n" + "mSmallSegmentSize:									"
				+ mSmallSegmentSize + "\n"
				+ "mConfigurationMaxLost                   			"
				+ mConfigurationMaxLost + "\n"
				+ "mConfigurationInterval                    			"
				+ mConfigurationInterval + "\n"
				+ "mHeartbeatInterval:                        			"
				+ mHeartbeatInterval + "\n"
				+ "mHeartbeatMaxLost:                       			"
				+ mHeartbeatMaxLost + "\n"
				+ "mSenderIdPortOffset:                     			"
				+ mSenderIdPortOffset + "\n"
				+ "mSendHoldbackDelay:                 			    "
				+ mSendHoldbackDelay + "\n"
				+ "mSendHoldbackThreshold:                 			"
				+ mSendHoldbackThreshold + "\n"
				+ "mMaxBandwidth:                            			"
				+ mMaxBandwidthKbit + "\n"
				+ "mRetransmissionServerHoldback:     			"
				+ mRetransmissionServerHoldback + "\n"
				+ "mRetransmissionTimeout:                			"
				+ mRetransmissionTimeout + "\n"
				+ "mRetransmissionRetries:                  			"
				+ mRetransmissionRetries + "\n"
				+ "mRetransmissionMaxCacheSize:       			"
				+ mRetransmissionMaxCacheSize + "\n"
				+ "mRetransmissionCacheLifeTime:      			"
				+ mRetransmissionCacheLifeTime + "\n"
				+ "mRetransmissonCacheCleanInterval: 			"
				+ mRetransmissonCacheCleanInterval + "\n"
				+ "mNaggingWindowInterval:                			"
				+ mNaggingWindowInterval + "\n"
				+ "mNaggingCheckInterval:                          	"
				+ mNaggingCheckInterval + "\n"
				+ "mNaggingMaxRetransmission:						"
				+ mNaggingMaxRetransmission + "\n"
				+ "mMaxReceiveQueueElements:           			"
				+ mMaxReceiveQueueElements + "\n";
	}
}
