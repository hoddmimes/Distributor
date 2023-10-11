package com.hoddmimes.distributor;

import java.net.*;
import java.util.Enumeration;

/**
 * An <I>DistributorApplicationConfiguration</I> object specifies global
 * settings for a Distributor. The parameter context is passed as parameter to
 * the Distributor constructor when being created. Parameters should be set
 * before the instance is passed to the creation of an "Distributor" instance.
 * 
 * @author POBE
 */
public class DistributorApplicationConfiguration {

	public static int LOG_ERROR_EVENTS = 1;
	public static int LOG_CONNECTION_EVENTS = 2;
	public static int LOG_RMTDB_EVENTS = 4;
	public static int LOG_RETRANSMISSION_EVENTS = 8;
	public static int LOG_SUBSCRIPTION_EVENTS = 16;
	public static int LOG_STATISTIC_EVENTS = 32;
	public static int LOG_SEGMENTS_EVENTS = 64;
	public static int LOG_DATA_PROTOCOL_RCV = 128;
	public static int LOG_DATA_PROTOCOL_XTA = 256;
	public static int LOG_RETRANSMISSION_CACHE = 512;




	public static final boolean DEFAULT_MANAGEMENT_CONTROL_ENABLED = true;
	public static final boolean DEFAULT_MANAGEMENT_HTTP_ENABLED = true;

	public static final String DEFAULT_MANAGEMENT_ADDRESS = "224.42.42.100";
	public static final int DEFAULT_MANAGEMENT_PORT = 4242;
	public static final int DEFAULT_MANAGEMENT_HTTP_PORT = 8888;
	public static final String DEFAULT_MANAGEMENT_INTERFACE = null;


	public static final String DEFAULT_ETH_DEVICE = "eth0";

	public static final int DEFAULT_TIMER_THREADS = 2;

	private String mApplicationName;
	private String mMgmtAddress;
	private String mMgmtInterface;
	private int mMgmtPort;
	private int mMgmtHttpPort;
	private boolean mMgmtHttpEnabled;



	private int mTimerThreads;
	private int mLogFlags;
	private boolean mLogToConsole;
	private boolean mLogToFile;
	
	private String mEthDevice;
	private String mLocalHostAddress;

	private boolean mMgmtControlEnabled; // Start Distributor management port

	// (CMA)

	/**
	 * Constructor for creating an application configuration instance. A global parameters and setting 
	 * used by the Distributor and its components are wrapped in a <i>DistributorApplicationConfiguration</i> object.
	 * Parameters are static in the sense that they must be set before the object is passed to the Distributor 
	 * constructor. Changing parameters after creating the Distributor object will not have any effects, with one exception;<br>
	 * <i>SetLogFlags</i> could be changed dynamically.
	 * <br><br>
	 * Default logging enabled is:
	 * <ul>
	 * <li>LOG_ERROR_EVENTS logging of application events signaled to subscribers and publishers
	 * <li>LOG_CONNECTION_EVENTS create and delete of distributor connections
	 * <li>LOG_RETRANSMISSION_EVENTS tracing of retransmission and recovery events
	 * <li>LOG_RMTDB_EVENTS creation and deletion of remote distributor connections
	 * </ul>
	 * <br>
	 * @param pApplicationName name of the application. Used for identification and tracing purpose, must not be null.
	 * @throws DistributorException exception thrown if failed to create an instance
	 *         
	 */
	public DistributorApplicationConfiguration(String pApplicationName) {
		if (pApplicationName == null) {
			throw new RuntimeException("Application name must not be null!");
		}
		mApplicationName = pApplicationName;
		setLogFlags(( LOG_ERROR_EVENTS + LOG_CONNECTION_EVENTS + 
					  LOG_RETRANSMISSION_EVENTS + LOG_RMTDB_EVENTS));

		mTimerThreads = DEFAULT_TIMER_THREADS;
		mEthDevice = DEFAULT_ETH_DEVICE;

		mLogToConsole = true;
		mLogToFile = true;
		mLocalHostAddress = null;

		mMgmtControlEnabled = DEFAULT_MANAGEMENT_CONTROL_ENABLED;
		mMgmtHttpEnabled = DEFAULT_MANAGEMENT_HTTP_ENABLED;
		mMgmtAddress = DEFAULT_MANAGEMENT_ADDRESS;
		mMgmtPort = DEFAULT_MANAGEMENT_PORT;
		mMgmtInterface = DEFAULT_MANAGEMENT_INTERFACE;
		mMgmtHttpPort = DEFAULT_MANAGEMENT_HTTP_PORT;
	}

	/**
	 * Check whatever a specific log flag is enabled.
	 * @param pLogFlag log flag to be checked
	 * @return true is log flag is enabled, otherwise false
	 */
	public boolean isLogFlagEnabled( int pLogFlag ) {
		if ((mLogFlags & pLogFlag) != 0) {
			return true;
		}
		return false;
	}


	/**
	 * By default a specific distributor log file is created in the working directory. The 
	 * distributor will write its event to the log file. It is possible to disable creation and logging 
	 * to the log file by passing false to the method.
	 * @param pLogToFileFlag enable / disable creation and logging to file.
	 */
	public void setLogToFile( boolean pLogToFileFlag) {
		mLogToFile = pLogToFileFlag;
	}
	
	/**
	 * Check whatever a specific distributor log file is to be created and used.
	 * @return true if logging is enabled, otherwise false.
	 */
	public boolean isLogToFileEnabled() {
		return mLogToFile;
	}
	
	/**
	 * By default the distributor will log its output to stdout and stderr. It is possible 
	 * to turn of the console logging to stdout and stderr by passing false to the method
	 * 
	 * @param pLogToConsoleFlag turn console logging on / off
	 */
	public void setLogToConsole( boolean pLogToConsoleFlag) {
		mLogToConsole = pLogToConsoleFlag;
	}
	
	/**
	 * Check whatever logging to stdout and stderr should take place or not.
	 * @return true if logging is enabled, otherwise false
	 */
	public boolean isLogToConsoleEnabled() {
		return mLogToConsole;
	}

	/**
	 * Check whatever the Management controller should start
	 * a HTTP service or not
	 * @return
	 */
	public boolean isMgmtHttpEnabled() {
		return mMgmtHttpEnabled;
	}

	/**
	 * Enable or disable the start of HTTP service as part of the Mgmt controller
	 * @param pValue
	 */
	public void mMgmtHttpEnabled( boolean pValue ) {
		mMgmtHttpEnabled = pValue;
	}

	/**
	 * Sets the log mask filtering what distributor events being logged to the
	 * log file The log mask values are
	 * <ul>
	<li>LOG_ERROR_EVENTS, trace when application error events are passed to the publisher / subscriber event callback routines. 
	<li>LOG_CONNECTION_EVENTS, trace the creation and deletion of distributor connection instances
	<li>LOG_RMTDB_EVENTS, trace the creation and deletion of <i>remote</i> distributor connection
	<li>LOG_RETRANSMISSION_EVENTS, trace retransmission requests and recovery events
	<li>LOG_SUBSCRIPTION_EVENTS, trace when subscribers add and remove subscriptions
	<li>LOG_STATISTIC_EVENTS, periodically out statistics information to the log file in case statistic tracing is enabled.
	<li>LOG_SEGMENTS_EVENTS, trace when sending and receiving packages (can be extensive).
	<li>LOG_DATA_PROTOCOL_RCV, trace distributor protocol messages received.
	<li>int LOG_DATA_PROTOCOL_XTA, trace distributor protocol messages sent.
	<li>int LOG_RETRANSMISSION_CACHE, periodically trace the size and time span of the messages in the retransmission cache. 
	 *</ul>
	 * 
	 * @param pLogFlags  to be enabled. Values are "OR" together  
	 */
	public void setLogFlags(int pLogFlags) {
		mLogFlags = pLogFlags;
	}

	/**
	 * Returns the log flags enabled
	 * 
	 * @return int, log flag mask value
	 */
	public int getLogFlags() {
		return mLogFlags;
	}
	
	/**
	 * The distributor will identify itself with its local IP address with interacting with other distributors. 
	 * The address used will be retrieved from the ethernet device specified . By default "eth0" is used. On windows that will be the first 
	 * ethernet device having a IP V4 address set.
	 * 
	 * @param pEthDevice to retrieve the IP address from
	 */
	public void setEthDevice( String pEthDevice ) {
		mEthDevice = pEthDevice;
	}
	
	/**
	 * Returns the ethernet device to retrieve the IP address from
	 * @return ethernet device string, like "eth0"
	 */
	public String getEthDevice() {
		return mEthDevice;
	}

	/**
	 * It is possible to set the local IP address used for identification. When specifying a IP address the distributor 
	 * will address supplied instead of trying to retrieve it from the ethernet device specified.
	 *   
	 * @param pLocalHostAddress address string to be used "xxx.xxx.xxx.xxx"
	 */
	public void setLocalHostAddress( String pLocalHostAddress ) {
		mLocalHostAddress = pLocalHostAddress;
	}
	
	/**
	 * Returns the IP addressed used for identification when iteracting with other distributors.
	 * @return address string to be used "xxx.xxx.xxx.xxx"
	 */
	public String getLocalHostAddress() {
		return mLocalHostAddress;
	}
	
	
	/**
	 * Returns whatever the Distributor management control multicast group is to be started or not.
	 * When being started counters and values can be viewed locally or remotely via the Distributor 
	 * Viewer application. 
	 * 
	 * @return boolean, flag indicating whatever the Distributor management port
	 *         is to be started or not.
	 */
	public boolean isMgmtControlEnabled() {
		return mMgmtControlEnabled;
	}

	/**
	 * Enable/disable start of Distributor management control transport (for this application)
	 * By default the transport is disabled.
	 * 
	 * @param pEnableControl true if enabling management control transport
	 */
	public void setMgmtControlEnabled(boolean pEnableControl) {
		mMgmtControlEnabled = pEnableControl;
	}

	/**
	 * Setting the network interface on which the management control transport
	 * is to be enabled. By default, the transport is enabled on all interfaces.
	 * 
	 * @param pManagementInterfaceAddress, configuration multicast address interface address
	 */
	public void setMgmtInterface(String pManagementInterfaceAddress) {
		mMgmtInterface = pManagementInterfaceAddress;
	}

	/**
	 * Return the network interface address on which the Distributor management
	 * control transport is enabled.
	 * 
	 * @return String network interface address.
	 */
	public String getMgmtInterface() {
		return mMgmtInterface;
	}

	/**
	 * Specifies the multicast UDP address being used for management control
	 * 
	 * @param pMulticastGroupAddress  UDP multicast address
	 */
	public void setMgmtAddress(String pMulticastGroupAddress) {
		mMgmtAddress = pMulticastGroupAddress;
	}

	/**
	 * Return the UDP multicast address being used for management control
	 * 
	 * @return String, UDP multicast address
	 */
	public String getMgmtAddress() {
		return mMgmtAddress;
	}

	/**
	 * Sets the UDP multicast port being used for management control
	 * 
	 * @param pMulticastGroupPort UDP port
	 */
	public void setMgmtPort(int pMulticastGroupPort) {
		mMgmtHttpPort = pMulticastGroupPort;
	}

	/**
	 * Returns the UDP multicast port used for management control
	 * 
	 * @return int, UDP port
	 */
	public int getMgmtPort() {
		return mMgmtPort;
	}


	/**
	 * Sets the HTTP port to use when declaring the Distributor HTTP service
	 *
	 * @param pHttpPort Http port
	 */
	public void setMgmtHttpPort(int pHttpPort) {
		mMgmtHttpPort = pHttpPort;
	}

	/**
	 * Returns the HTTP port used for management http service
	 *
	 * @return int, HTTP port
	 */
	public int getMgmtHttpPort() {
		return mMgmtHttpPort;
	}




	/**
	 * Returns the number of timer threads that should be started. The
	 * recommended value is 2. In case of <i> <b>many</i> </b> connections being created it
	 * could be applicable to started 1 or 2 more timer threads.
	 * 
	 * @return int, number of timer threads that will be started
	 */
	public int getTimerThreads() {
		return mTimerThreads;
	}

	/**
	 * Sets the number of timer threads that will be started. The recommended
	 * value is 2. In case of <i><b>many</b></i> connections being created it could be
	 * applicable to started 1 or 2 more timer threads. By default, two threads are started.
	 * This should be sufficient.
	 * 
	 * @param pValue, number of timer threads to activate
	 */
	public void setTimerThreads(int pValue) {
		mTimerThreads = pValue;
	}

	
	/**
	 * Returns the <i>application name</i> set used to identification and tracing purpose. 
	 * @return - application name string
	 */
	public String getApplicationName() {
		return this.mApplicationName;
	}



	InetAddress getLocalInetAddress() throws DistributorException
	{
		// Check if local host address is configured, if so just use it as being defined.
		InetAddress tLocalHostAddress = null;
		try {

			if ((this.mLocalHostAddress != null) && (this.mLocalHostAddress.length() > 0)) {
				tLocalHostAddress = InetAddress.getByName(this.mLocalHostAddress );
				return tLocalHostAddress;
			}
		}
		catch (UnknownHostException e) {
			throw new DistributorException( "Invalid localhost address ("+ this.mLocalHostAddress  + ") UnknownHostException: " + e.getMessage());
		}


		// Get IP address from device specified
		String tEthDevice = this.mEthDevice;
		if ((tEthDevice == null) || (tEthDevice.length() == 0)) {
			throw new DistributorException( "Parameter EthDevice must be defined for the connection" );
		}

		try {
			Enumeration<NetworkInterface> tEnumInterface = NetworkInterface.getNetworkInterfaces();
			while( tEnumInterface.hasMoreElements()) {
				NetworkInterface tInterface = tEnumInterface.nextElement();
				if (tInterface.getName().equals( tEthDevice )) {
					Enumeration<InetAddress> tEnumInetAddr = tInterface.getInetAddresses();
					while(tEnumInetAddr.hasMoreElements()) {
						InetAddress tAdr = tEnumInetAddr.nextElement();
						if (tAdr instanceof Inet4Address) {
							tLocalHostAddress = tAdr;
							return tLocalHostAddress;
						}
					}
				}
			}
		}
		catch( SocketException e) {
			throw new DistributorException( "Failed to get local ip host address for ethernet device ("+ tEthDevice + ") UnknownHostException: " + e.getMessage());
		}

		throw new DistributorException( "Failed to get local ip host address for ethernet device ("+ tEthDevice + ") ");

	}

}
