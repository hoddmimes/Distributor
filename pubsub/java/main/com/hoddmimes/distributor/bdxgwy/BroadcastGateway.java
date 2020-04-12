package com.hoddmimes.distributor.bdxgwy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.hoddmimes.distributor.Distributor;
import com.hoddmimes.distributor.DistributorApplicationConfiguration;

import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterest;
import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterestItem;
import com.hoddmimes.distributor.generated.messages.DistributorMessagesFactory;
import com.hoddmimes.distributor.messaging.MessageFactoryInterface;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionTypes;
import com.hoddmimes.distributor.tcpip.TcpIpServer;
import com.hoddmimes.distributor.tcpip.TcpIpServerCallbackInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BroadcastGateway extends Thread implements BdxGatewayInterface {

	private static Pattern IP_Connection_Pattern = Pattern.compile("^/(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
	private static final Logger cLogger = LogManager.getLogger( BdxGwySubscriptionFilter.class.getSimpleName());
	Map<Long, BdxGwyMulticastGroupEntry> mMulticastGroups; 			   // local multicast group entries
	ArrayList<BdxGwyOutboundGatewayEntry> mBdxGwyOutboundConnections;  // outbound broadcast gateway entries
	ArrayList<BdxGwyInboundGatewayEntry> mBdxGwyInboundConnections;  // inbound broadcast gateway entries
	ArrayList<BdxGwyAuthEntry> mBdxGwyAllowedInboundGateways;		// a list of names and optiobal ip-addresses of the gateways that are allowed to connect
	TcpIpServer mTcpIpDistributorClientServer;					    // TCP/IP server handling distributor client connections
	TcpIpServer mTcpIpBdxGwyServer;									// TCP/IP server handling connections from remote bdx gateways 
	Distributor mDistributor;										// Local distributor handle
	String mBroadcastGatewayName;									// Broadcast gateway name of this instance
	
	Map<TcpIpConnection, ArrayList<DistBdxGwySubscrInterestItem>> mLocalLanSubscriptions; // All active distributor client subscription being
																						  // connected to this broadcast gateway 

	/**
	 * Constructor for creating and starting a broadcast gateway instance
	 * 
	 * @param pApplConfig, the Distributor App configuration to be used when the gateway acts as a local publisher of remote updates.
	 * @param pDistributorClientAcceptPort, the TCP/IP port on which local distributor application will connect up and advertise
	 * 							 the subjects that they subscribe for.
	 *
	 * @param pBdxGwyAcceptPort, the TCP/IP port on which the the broadcast gateway will accept inbound connections
	 * 							 on from other broadcast gateways acting as clients
	 *
	 * @param pBdxGwyAllowedInboundGateways, a list of remote gateways and optional ip-addresses that are allowed to connect to this gateway
	 * 										 if being null any gateway is allowed to connect
	 * @param pBdxGwyOutboundEntries, a list of broadcast gateways to which we will try to connect
	 * @param pMcgEntries, the multicast groups that the gateway will listing to and publish data on.
	 *
	 */
	public BroadcastGateway(  DistributorApplicationConfiguration pApplConfig,
			                  int pDistributorClientAcceptPort,
							  int pBdxGwyAcceptPort,
							  List<BdxGwyAuthEntry> pBdxGwyAllowedInboundGateways,
							  List<BdxGatewayParameterEntry> pBdxGwyOutboundEntries,
							  List<BdxGwyMulticastGroupParameterEntry> pMcgEntries) {

		if (pBdxGwyAllowedInboundGateways != null) {
			mBdxGwyAllowedInboundGateways = new ArrayList<>();
			mBdxGwyAllowedInboundGateways.addAll(pBdxGwyAllowedInboundGateways);
		}
		mMulticastGroups = new HashMap<Long, BdxGwyMulticastGroupEntry>(); // Local  multicast groups
		mBdxGwyOutboundConnections = new ArrayList<BdxGwyOutboundGatewayEntry>(); // List with connections to remote bdx gateways
		mBdxGwyInboundConnections = new ArrayList<BdxGwyInboundGatewayEntry>();
		mLocalLanSubscriptions = new HashMap<TcpIpConnection, ArrayList<DistBdxGwySubscrInterestItem>>(); // Local subscription map.
		mBroadcastGatewayName = pApplConfig.getApplicationName();																																					 // Contains what local dist appl subscribes to
		/**
		 * Connect all to local multicast groups being defined
		 */

		try {
			pApplConfig.setIsBroadcastGateway(true);
			mDistributor = new Distributor(pApplConfig);

			for (int i = 0; i < pMcgEntries.size(); i++) {
				BdxGwyMulticastGroupEntry tEntry = new BdxGwyMulticastGroupEntry( pMcgEntries.get(i), this);
				mMulticastGroups.put(tEntry.getMulticastGroupId(), tEntry);
			}
		} catch (Exception e) {
			cLogger.fatal("BroadcastGateway", e);
			System.exit(0);
		}

		/**
		 * Define and start connections to all remote broadcast gateways.
		 * However do not connect to ourself.
		 */

		for (int i = 0; i < pBdxGwyOutboundEntries.size(); i++) {
			if (!mBroadcastGatewayName.equals(pBdxGwyOutboundEntries.get(i).getBdxGatewayName())) { // do not connect to our self
				BdxGwyOutboundGatewayEntry tEntry = new BdxGwyOutboundGatewayEntry(pBdxGwyOutboundEntries.get(i), this);
				mBdxGwyOutboundConnections.add(tEntry);
			}
		}

		/**
		 * Declare TcpIp server allowing local distributor clients to connect to
		 * this broadcast gateway and advice what subject names there are interested in.
		 * This subject names are forward to remote gateways to capture remote updates.
		 */
		try {
			mTcpIpDistributorClientServer = new TcpIpServer(pDistributorClientAcceptPort, new LocalDistributorClientController());
		} catch (IOException e) {
			cLogger.fatal( "Failed to declare distributor client server on port: " + pDistributorClientAcceptPort);
			cLogger.fatal(e);
			System.exit(0);
		}

		/**
		 * Declare TcpIp server allowing remote gateways to connect and subscribe to 
		 * updates being published on the local LAN.
		 */
		try {
			mTcpIpBdxGwyServer = new TcpIpServer( TcpIpConnectionTypes.Compression,
												  pBdxGwyAcceptPort, 
												  new RemoteBroadcastGatewayController());
		} catch (IOException e) {
			cLogger.fatal( "Failed to declare broadcast gateway server on port: " + pBdxGwyAcceptPort);
			cLogger.fatal( e);
			System.exit(0);
		}

	}

	@Override
	public String getBroadcastGatewayName() {
		return this.mBroadcastGatewayName;
	}

	@Override
	public Distributor getDistributor() {
		return this.mDistributor;
	}

	@Override
	synchronized public DistBdxGwySubscrInterest getLocalActiveSubscriptions()
	{
		if (this.mLocalLanSubscriptions.size() == 0) {
			return null;
		}
		
		DistBdxGwySubscrInterest tBdx = new DistBdxGwySubscrInterest();
		ArrayList<DistBdxGwySubscrInterestItem> tSubscrList = new ArrayList<DistBdxGwySubscrInterestItem>();
		Iterator<ArrayList<DistBdxGwySubscrInterestItem>> tConnList = this.mLocalLanSubscriptions.values().iterator();
		while(tConnList.hasNext()) {
			ArrayList<DistBdxGwySubscrInterestItem> tList = tConnList.next();
			if ((tList != null)  && (tList.size() > 0)) {
				tSubscrList.addAll(tList);
			}
		}
		
		if (tSubscrList.size() == 0) {
			return null;
		}
		
		tBdx.setAction(DistBdxGwySubscrInterest.ADD_INTEREST);
		tBdx.setInterests(tSubscrList);
		return tBdx;
	}
		
		
		
	/**
	 * This method is invoked when a local update has received. The update is propagated connected 
	 * to remote broadcast gateways. Only updates matching the remote filter are forward (see updateToRemoteBroadcastGateway).
	 * @param pMulticastGroupId, the local MCG id on which the update was received
	 * @param pSubjectName, the canonical subject name 
	 * @param pMessageBytes, update message payload i.e. serialized.
	 */
	@Override
	public void localDistributorUpdate(long pMulticastGroupId, String pSubjectName, byte[] pMessageBytes) {
		synchronized( mBdxGwyInboundConnections ) {
			for (int i = 0; i < mBdxGwyInboundConnections.size(); i++) {
				mBdxGwyInboundConnections.get(i).updateToRemoteBroadcastGateway(pSubjectName, pMessageBytes, pMulticastGroupId);
			}
		}
	}

	/**
	 * This method is invoked when an update from a remote broadcast gateway has been received
	 * The update is republished on the corresponding multicast group i.e. same multicast address and port id.
	 */
	@Override
	public void remoteBdxGatewayUpdate(long pMulticastGroupId, String pSubjectName, byte[] pMessageBytes) {
		BdxGwyMulticastGroupEntry tEntry = this.mMulticastGroups.get(pMulticastGroupId);
		if (tEntry != null) {
			tEntry.publish(pSubjectName, pMessageBytes);
		} 
	}
	
	@Override
	public boolean validInboundClient(String pRemoteBdxGwyName, String pInboundIpAddress) {
		if (mBdxGwyAllowedInboundGateways == null) {
			return true;
		}

		/**
		 * Validate inbound connnection from
		 */
		Matcher m = IP_Connection_Pattern.matcher( pInboundIpAddress );
		if (!m.matches()) {
			cLogger.warn("unable to extract IP address from connect info (" + pInboundIpAddress + ")");
			return false;
		}

		String tIpAddr = m.group(1);

		for( int i = 0; i < mBdxGwyAllowedInboundGateways.size(); i++) {
			if (mBdxGwyAllowedInboundGateways.get(i).equal(pRemoteBdxGwyName, tIpAddr)) {
				cLogger.info("Successfull authorization of bdxgwy: " + pRemoteBdxGwyName + " ip-address: " + tIpAddr );
				return true;
			}
		}

		cLogger.warn("Unauthorized access from bdxgwy: " + pRemoteBdxGwyName + " ip-address: " + tIpAddr );
		return false;
	}

	/**
	 * ===========================================================
	 * 
	 * Class for handling the Tcp/Ip connectivity with remote broadcast gateways
	 * 
	 *=============================================================
	 */

	class RemoteBroadcastGatewayController implements TcpIpServerCallbackInterface {

		@Override
		public void tcpipInboundConnection(TcpIpConnection pConnection) {
			synchronized( mBdxGwyInboundConnections ) {
				BdxGwyInboundGatewayEntry tEntry = new BdxGwyInboundGatewayEntry( pConnection, BroadcastGateway.this );
				mBdxGwyInboundConnections.add(tEntry);
				pConnection.setUserCntx(tEntry);
			}
			cLogger.info( "Inbound connection from remote broadcast gateway\n     " + pConnection.getConnectionInfo());
		}

		@Override
		public void tcpipClientError(TcpIpConnection pConnection, IOException pException) {
			BdxGwyInboundGatewayEntry tEntry = (BdxGwyInboundGatewayEntry) pConnection.getUserCntx();
			synchronized( mBdxGwyInboundConnections ) {
				tEntry.close(pException);
				mBdxGwyInboundConnections.remove(tEntry);
			}
		}

		@Override
		public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) {
			BdxGwyInboundGatewayEntry tEntry = (BdxGwyInboundGatewayEntry) pConnection.getUserCntx();
			tEntry.connectionDataRead(pBuffer, pConnection.getRemoteHost());
		}
	}

	/**
	 * ===========================================================
	 * 
	 * Class for handling the Tcp/Ip connectivity with local distributor clients
	 * When a subscriber on the local LAN adds an subscription the gateway is receiving 
	 * a DistBdxGwySubscrInterest message. The cache over local LAN subscriptions are updated 
	 * and remote gateways are notified about the subscription interest 
	 * 
	 *=============================================================
	 */

	class LocalDistributorClientController implements TcpIpServerCallbackInterface {
		
		MessageFactoryInterface mMessageFactory = new DistributorMessagesFactory();
		
		@Override
		public void tcpipInboundConnection(TcpIpConnection pConnection) {
			cLogger.info( "inbound connection from local distributor client \n       " + pConnection.toString());
		}

		@Override
		public void tcpipClientError(TcpIpConnection pConnection, IOException pE) {
			cLogger.info( "local distributor client disconnected, reason: " + pE.getMessage() + "\n       " + pConnection.toString());
			cleanupLocalSubscription(pConnection);
			pConnection.close();
		}

		private synchronized void cleanupLocalSubscription(TcpIpConnection pConnection) {
			ArrayList<DistBdxGwySubscrInterestItem> tCltSubscrList = BroadcastGateway.this.mLocalLanSubscriptions.remove(pConnection);
			if (tCltSubscrList != null) {
				DistBdxGwySubscrInterest tMsg = new DistBdxGwySubscrInterest();
				tMsg.setAction(DistBdxGwySubscrInterest.REMOVE_INTEREST);
				tMsg.setInterests(tCltSubscrList);

				/**
				 * Notify all remote clients about that interests for this local distributor application
				 * should be removed. The notification is sent to all remote broadcast gateways.
				 */
				for (int i = 0; i < mBdxGwyOutboundConnections.size(); i++) {
					mBdxGwyOutboundConnections.get(i).localClientSubscriptionInterest(tMsg);
				}
			}
		}

		private synchronized void updateLocalSubscription( TcpIpConnection pConnection, DistBdxGwySubscrInterest tMessage ) {
			ArrayList<DistBdxGwySubscrInterestItem> tCltSubscrList = BroadcastGateway.this.mLocalLanSubscriptions.get(pConnection);
			if (tCltSubscrList == null) {
				tCltSubscrList = new ArrayList<DistBdxGwySubscrInterestItem>();
				BroadcastGateway.this.mLocalLanSubscriptions.put(pConnection, tCltSubscrList);
			}

			if (tMessage.getAction() == DistBdxGwySubscrInterest.ADD_INTEREST) {
				List<DistBdxGwySubscrInterestItem> tList = tMessage.getInterests();
				if (tList != null) {
					for (int i = 0; i < tList.size(); i++) {
						tCltSubscrList.add(tList.get(i));
					}
				}
			}

			if (tMessage.getAction() == DistBdxGwySubscrInterest.REMOVE_INTEREST) {
				List<DistBdxGwySubscrInterestItem> tList = tMessage .getInterests();
				if (tList != null) {
					for (int i = 0; i < tList.size(); i++) {
						tCltSubscrList.remove(tList.get(i));
					}
				}
			}
		}

		/**
		 * Invoked when a local distributor client subscribes to a subject. The local gateway is notified and 
		 * will propagate the subscription interest to remote gateways for capture remote updates on the subject.
		 * @param pConnection, local tcp/ip connection to the local distributor client
		 * @param tMessage, DistBdxGwySubscrInterest the subscription interest message
		 */
		private void processDistBdxGwySubscrInterest( TcpIpConnection pConnection, DistBdxGwySubscrInterest tMessage) {
			updateLocalSubscription(pConnection, tMessage);
			for (int i = 0; i < mBdxGwyOutboundConnections.size(); i++) {
				mBdxGwyOutboundConnections.get(i).localClientSubscriptionInterest( tMessage);
			}
		}

		/**
		 * Invoked when receiving a message from a local distributor client (subscriber)
		 * Each time a local distributor client subscribes to a subject the broadcast 
		 * gateway is notified with a DistBdxGwySubscrInterest message.
		 */
		@Override
		public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) {
			MessageInterface tMessage = mMessageFactory.createMessage(pBuffer);
			if (tMessage.getMessageId() == DistBdxGwySubscrInterest.MESSAGE_ID) {
				processDistBdxGwySubscrInterest(pConnection, (DistBdxGwySubscrInterest) tMessage);
			}
		}
	}




}
