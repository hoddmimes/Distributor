package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.*;
import com.hoddmimes.distributor.generated.messages.*;
import com.hoddmimes.distributor.messaging.MessageBinDecoder;
import com.hoddmimes.distributor.messaging.MessageBinEncoder;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.messaging.MessageWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class DistributorManagementController implements DistributorEventCallbackIf, DistributorUpdateCallbackIf {
	static final Logger cLogger = LogManager.getLogger( DistributorManagementController.class.getSimpleName());
	static final String cConsoleSubjectControlTopic = "/DistCtlMsg001";

	DistributorConnectionIf mConnection; // Management connection
	Distributor mDistributor; // Parent distributor application
	DistributorApplicationConfiguration mApplicationConfiguration;

	DistributorPublisherIf mPublisher;
	DistributorSubscriberIf mSubscriber;


	String mLocalHostName;

	public DistributorManagementController(Distributor pDistributor, DistributorApplicationConfiguration pApplicationConfiguration) throws DistributorException {
		mDistributor = pDistributor;
		mApplicationConfiguration = pApplicationConfiguration;
		
		DistributorConnectionConfiguration tConfiguration;

		tConfiguration = new DistributorConnectionConfiguration(
				mApplicationConfiguration.getCMA(),
				mApplicationConfiguration.getCMAPort());
		tConfiguration.setMcaNetworkInterface(mApplicationConfiguration.getCMAInterface());
		
		tConfiguration.setCmaConnection(true);
		mConnection = pDistributor.createConnection(tConfiguration);
		mPublisher = pDistributor.createPublisher(mConnection, this);
		mSubscriber = pDistributor.createSubscriber(mConnection, null, this);
		mSubscriber.addSubscription(cConsoleSubjectControlTopic, null);

		mLocalHostName = "localhost";
		try {mLocalHostName = InetAddress.getLocalHost().getHostName();}
		catch( UnknownHostException e) { e.printStackTrace(); }
		
	
	}

	public void distributorEventCallback(DistributorEvent pDistributorEvent) {
		
	}

	void sendResponse(DistNetMsg pNetMsg) {
		MessageBinEncoder tEncoder = new MessageBinEncoder();
		try {
			pNetMsg.setIsRequestMessage(false);
			pNetMsg.setTimestamp(System.currentTimeMillis());
			pNetMsg.encode(tEncoder);
			mPublisher.publish(cConsoleSubjectControlTopic, tEncoder.getBytes());
		} catch (Exception e) {
			cLogger.error(e);
		}
	}

	String getLocalAddresses(List<DistributorConnection> pDistributorConnections ) {
		ArrayList<String> tLocalHostAddresses = new ArrayList<String>();
		String tLocalHostAddressString = null;
		for( int i = 0; i < pDistributorConnections.size(); i++) {
			DistributorConnection tConn = pDistributorConnections.get(i);
			tLocalHostAddressString = tConn.mConnectionSender.mLocalAddress.toString().substring(1);
			boolean tFound = false;
			for( int j = 0; j < tLocalHostAddresses.size(); j++) {	 
			  if (tLocalHostAddresses.get(j).equals(tLocalHostAddressString)) {
				  tFound = true;
				  break;
			  }
			}
			if (!tFound) {
				tLocalHostAddresses.add(tLocalHostAddressString);
			}
		}
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < tLocalHostAddresses.size(); i++) {
			sb.append( tLocalHostAddresses.get(i) + ", " );
		}
		String tStr = sb.toString();
		return tStr.substring(0, (tStr.length() - 2) );
	}
	
	void serveDistExploreDomainRqst(DistNetMsg pNetMsg) {
		
		
		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		
		
		DistExploreDomainRsp tResponse = new DistExploreDomainRsp();
		tResponse.setDistributor(new DistDomainDistributorEntry());
		tResponse.getDistributor().setApplicationId(mDistributor.getAppId());
		tResponse.getDistributor().setApplicationName(mApplicationConfiguration.getApplicationName());
		tResponse.getDistributor().setDistributorId(mDistributor.getDistributorId());
		tResponse.getDistributor().setHostname( mLocalHostName);
		tResponse.getDistributor().setStartTime(mDistributor.getStartTime());
		tResponse.getDistributor().setHostaddress(getLocalAddresses(tConnectedConnections));
		
		
		
		if (tConnectedConnections.size() == 0) {
				tResponse.getDistributor().setConnections( new DistDomainConnectionEntry[0]);
		} else {
			Vector<DistDomainConnectionEntry> tConnections = new Vector<DistDomainConnectionEntry>();
			for( int i = 0; i < tConnectedConnections.size(); i++) {
			   DistDomainConnectionEntry tEntry = new DistDomainConnectionEntry();
			   DistributorConnection tConnection = tConnectedConnections.get(i);
			   tEntry.setConnectionId(tConnection.mConnectionId);
			   tEntry.setMcaAddress(tConnection.mIpmg.mInetAddress.getHostAddress().toString());
			   tEntry.setMcaPort(tConnection.mIpmg.mPort);
			   tEntry.setSubscriptions(tConnection.mSubscriptionFilter.getActiveSubscriptions());
			   tEntry.setInRetransmissions(tConnection.mRetransmissionStatistics.mTotalIn);
			   tEntry.setOutRetransmissions(tConnection.mRetransmissionStatistics.mTotalOut);
			   tConnections.add(tEntry);
			}
			tResponse.getDistributor().setConnections(tConnections);
		}
		DistNetMsg tNetRsp = new DistNetMsg();
		tNetRsp.setMessage(new MessageWrapper(tResponse));
		tNetRsp.setRequestId(pNetMsg.getRequestId());
		sendResponse(tNetRsp);
	}

	void serveDistExploreDistributorRqst(DistNetMsg pNetMsg) {
		
		DistExploreDistributorRqst tRequest = (DistExploreDistributorRqst) pNetMsg.getMessage().getWrappedMessage();
		
		if (tRequest.getDistributorId() != mDistributor.getDistributorId()) {
			return; // not for us
		}
		
		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		
		DistExploreDistributorRsp tResponse = new DistExploreDistributorRsp();
		tResponse.setDistributor(new DistributorEntry());
		tResponse.getDistributor().setApplicationName(mApplicationConfiguration.getApplicationName());
		tResponse.getDistributor().setApplicationId( mDistributor.getAppId());
		tResponse.getDistributor().setConnections(tConnectedConnections.size());
		tResponse.getDistributor().setDistributorId(mDistributor.getDistributorId());
		tResponse.getDistributor().setHostaddress( getLocalAddresses(tConnectedConnections));
		tResponse.getDistributor().setHostname(mLocalHostName);
		tResponse.getDistributor().setMemFree(Runtime.getRuntime().freeMemory());
		tResponse.getDistributor().setMemMax(Runtime.getRuntime().maxMemory());
		tResponse.getDistributor().setMemUsed( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		tResponse.getDistributor().setStartTime(mDistributor.getStartTime());

		for( int i = 0; i < tConnectedConnections.size(); i++) {
			DistributorConnection tConnection = tConnectedConnections.get(i);
			tResponse.getDistributor().setInRetransmissions( tResponse.getDistributor().getInRetransmissions()
											+ tConnection.mRetransmissionStatistics.mTotalIn);
			tResponse.getDistributor().setOutRetransmissions( tResponse.getDistributor().getOutRetransmissions()
											+ tConnection.mRetransmissionStatistics.mTotalOut);
			tResponse.getDistributor().setSubscriptions( tConnection.mSubscriptionFilter.getActiveSubscriptions());
		}

		DistNetMsg tNetRsp = new DistNetMsg();
		tNetRsp.setMessage(new MessageWrapper(tResponse));
		tNetRsp.setRequestId(pNetMsg.getRequestId());
		sendResponse(tNetRsp);
	}

	void serveDistExploreConnectionRqst(DistNetMsg pNetMsg) {
		DistExploreConnectionRqst tRequest = (DistExploreConnectionRqst) pNetMsg.getMessage().getWrappedMessage();
		DistExploreConnectionRsp tResponse = null;
		DistributorConnection tConnection = null;
		boolean tFound = false;

		if (tRequest.getDistributorId() != mDistributor.getDistributorId()) {
			return; // not for us
		}

		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		
		for( int i = 0; i < tConnectedConnections.size(); i++) {
			tConnection = tConnectedConnections.get(i);
			if (tConnection.mConnectionId == tRequest.getConnectionId()) {
				tFound = true;
				break;
			}
		}

		if (!tFound) {
			return;
		}

		tResponse = new DistExploreConnectionRsp();
		tResponse.setConnection(new ConnectionEntry());
		tResponse.getConnection().setConnectionId(tConnection.getConnectionId());
		tResponse.getConnection().setMcaAddress(tConnection.mIpmg.mInetAddress.getHostAddress());
		tResponse.getConnection().setMcaPort(tConnection.mIpmg.mPort);

		tResponse.getConnection().setOutRetransmissions( tConnection.mRetransmissionStatistics.mTotalOut);
		tResponse.getConnection().setInRetransmissions(tConnection.mRetransmissionStatistics.mTotalIn);

		tResponse.getConnection().setDeliverUpdateQueue( ClientDeliveryController.getInstance().getQueueSize());

		tResponse.getConnection().setPublishers(tConnection.mPublishers.size());
		tResponse.getConnection().setSubscribers(tConnection.mSubscribers.size());

		tResponse.getConnection().setRcvTotalBytes(tConnection.mTrafficStatisticsTask.getTotalRcvBytes());
		tResponse.getConnection().setRcvTotalSegments(tConnection.mTrafficStatisticsTask.getTotalRcvSegments());
		tResponse.getConnection().setRcvTotalUpdates(tConnection.mTrafficStatisticsTask.getTotalRcvUpdates());

		tResponse.getConnection().setRcvBytes(tConnection.mTrafficStatisticsTask.getRcvBytesInfo());
		tResponse.getConnection().setRcvBytes1min(tConnection.mTrafficStatisticsTask.getRcvBytes1MinInfo());
		tResponse.getConnection().setRcvBytes5min(tConnection.mTrafficStatisticsTask.getRcvBytes5MinInfo());
		tResponse.getConnection().setRcvSegments(tConnection.mTrafficStatisticsTask.getRcvMsgsInfo());
		tResponse.getConnection().setRcvSegments1min(tConnection.mTrafficStatisticsTask.getRcvMsgs1MinInfo());
		tResponse.getConnection().setRcvSegments5min(tConnection.mTrafficStatisticsTask.getRcvMsgs5MinInfo());
		tResponse.getConnection().setRcvUpdates(tConnection.mTrafficStatisticsTask.getRcvUpdatesInfo());
		tResponse.getConnection().setRcvUpdates1min(tConnection.mTrafficStatisticsTask.getRcvUpdates1MinInfo());
		tResponse.getConnection().setRcvUpdates5min(tConnection.mTrafficStatisticsTask.getRcvUpdates5MinInfo());

		tResponse.getConnection().setXtaTotalBytes(tConnection.mTrafficStatisticsTask.getTotalXtaBytes());
		tResponse.getConnection().setXtaTotalSegments(tConnection.mTrafficStatisticsTask.getTotalXtaSegments());
		tResponse.getConnection().setXtaTotalUpdates(tConnection.mTrafficStatisticsTask.getTotalXtaUpdates());

		tResponse.getConnection().setXtaBytes(tConnection.mTrafficStatisticsTask.getXtaBytesInfo());
		tResponse.getConnection().setXtaBytes1min(tConnection.mTrafficStatisticsTask.getXtaBytes1MinInfo());
		tResponse.getConnection().setXtaBytes5min(tConnection.mTrafficStatisticsTask.getXtaBytes5MinInfo());
		tResponse.getConnection().setXtaSegments(tConnection.mTrafficStatisticsTask.getXtaMsgsInfo());
		tResponse.getConnection().setXtaSegments1min(tConnection.mTrafficStatisticsTask.getXtaMsgs1MinInfo());
		tResponse.getConnection().setXtaSegments5min(tConnection.mTrafficStatisticsTask.getXtaMsgs5MinInfo());
		tResponse.getConnection().setXtaUpdates(tConnection.mTrafficStatisticsTask.getXtaUpdatesInfo());
		tResponse.getConnection().setXtaUpdates1min(tConnection.mTrafficStatisticsTask.getXtaUpdates1MinInfo());
		tResponse.getConnection().setXtaUpdates5min(tConnection.mTrafficStatisticsTask.getXtaUpdates5MinInfo());

		tResponse.getConnection().setSubscriptions(tConnection.mSubscriptionFilter.getActiveSubscriptions());


		DistNetMsg tNetRsp = new DistNetMsg();
		tNetRsp.setMessage(new MessageWrapper(tResponse));
		tNetRsp.setRequestId(pNetMsg.getRequestId());
		sendResponse(tNetRsp);
	}

	void serveDistExploreRetransmissionsRqst(DistNetMsg pNetMsg) {
		DistExploreRetransmissionsRqst tRequest = (DistExploreRetransmissionsRqst) pNetMsg.getMessage().getWrappedMessage();
		DistributorConnection tConnection = null;
		boolean tFound = false;

	if (tRequest.getDistributorId() != mDistributor.getDistributorId()) {
			return; // not for us
		}

		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		
		for( int i = 0; i < tConnectedConnections.size(); i++) {
		  tConnection = tConnectedConnections.get(i);
		  if (tConnection.mConnectionId == tRequest.getConnectionId()) {
			  tFound = true;
			  break;
		  }
			
		}
		
		if (!tFound) {	return; }

		DistNetMsg tNetRsp = new DistNetMsg();

		DistExploreRetransmissonsRsp tResponse = tConnection.mRetransmissionStatistics.getRetransmissonsInfo();
		tResponse.setMcaAddress(tConnection.mIpmg.mInetAddress.getHostAddress());
		tResponse.setMcaPort(tConnection.mIpmg.mPort);

		tNetRsp.setMessage(new MessageWrapper(tResponse));
		tNetRsp.setRequestId(pNetMsg.getRequestId());

		sendResponse(tNetRsp);
	}


	void serveDistTriggerConfigurationRqst( DistNetMsg pNetMsg ) {
		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		for( DistributorConnection tConn : tConnectedConnections) {
			tConn.pushOutConfiguration();
		}
	}

	void serveDistExploreSubscriptionsRqst(DistNetMsg pNetMsg) {
		DistExploreSubscriptionsRqst tRequest = (DistExploreSubscriptionsRqst) pNetMsg
				.getMessage().getWrappedMessage();
		DistributorConnection tConnection = null;
		boolean tFound = false;

		if (tRequest.getDistributorId() != mDistributor.getDistributorId()) {
			return; // not for us
		}

		List<DistributorConnection> tConnectedConnections = DistributorConnectionController.getDistributorConnection();
		
		for( int i = 0; i < tConnectedConnections.size(); i++) {
		  tConnection = tConnectedConnections.get(i);
		  if (tConnection.mConnectionId == tRequest.getConnectionId()) {
			  tFound = true;
			  break;
		  }
			
		}
		
		if (!tFound) {	return; }

		DistExploreSubscriptionsRsp tResponse = new DistExploreSubscriptionsRsp();
		tResponse.setMcaAddress(tConnection.mIpmg.mInetAddress.getHostAddress());
		tResponse.setMcaPort(tConnection.mIpmg.mPort);
		tResponse.setSubscriptions(tConnection.mSubscriptionFilter
				.getActiveSubscriptionsStrings());

		DistNetMsg tNetRsp = new DistNetMsg();
		tNetRsp.setMessage(new MessageWrapper(tResponse));
		tNetRsp.setRequestId(pNetMsg.getRequestId());
		sendResponse(tNetRsp);
	}

	
	public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pAppId, int pDeliveryQueueLength) {
		MessageBinDecoder tDecoder = new MessageBinDecoder(pData);
		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.decode(tDecoder);

		MessageInterface tMessage = tNetMsg.getMessage().getWrappedMessage();
		if (tMessage == null) {
			return;
		}

		if (tMessage instanceof DistExploreDomainRqst) {
			serveDistExploreDomainRqst(tNetMsg);
		}

		if (tMessage instanceof DistTriggerCofigurationRqst) {
			serveDistTriggerConfigurationRqst(tNetMsg);
		}

		if (tMessage instanceof DistExploreDistributorRqst) {
			serveDistExploreDistributorRqst(tNetMsg);
		}

		if (tMessage instanceof DistExploreConnectionRqst) {
			serveDistExploreConnectionRqst(tNetMsg);
		}

		if (tMessage instanceof DistExploreRetransmissionsRqst) {
			serveDistExploreRetransmissionsRqst(tNetMsg);
		}

		if (tMessage instanceof DistExploreSubscriptionsRqst) {
			serveDistExploreSubscriptionsRqst(tNetMsg);
		}

	}

}
