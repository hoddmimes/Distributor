package com.hoddmimes.distributor.bdxgwy;

import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterest;
import com.hoddmimes.distributor.generated.messages.DistBdxGwySubscrInterestItem;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpClient;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionCallbackInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;




/**
 * This class is used within the  distributor when communicating with a "local"
 * broadcast gateway. Each application will inform the broadcast gateway when
 * the application setup a new subscription or when the application removes a
 * subscription.
 * 
 * In case an application loss it connection to a broadcast gateway it will try
 * to reestablish the connection to the gateway When a connection is
 * reestablished all active subscriptions are uploaded the broadcast gateway
 * 
 * @author Bertilsson
 * 
 */
public class BdxGwyDistributorClient implements TcpIpConnectionCallbackInterface {
	private static final Logger cLogger = LogManager.getLogger( BdxGwyDistributorClient.class.getSimpleName());
	private String mHostAddress;
	private int mHostPort;
	private volatile TcpIpConnection mConnection; // If null no connection to bdxgwy
	private AtomicReference<ConnectionThread> mConnectionThread;
	private LinkedList<SubjectItem> mSubjectItems;

	public BdxGwyDistributorClient(String pHostAddress, int pHostPort) {
		mHostAddress = pHostAddress;
		mHostPort = pHostPort;
		mSubjectItems = new LinkedList<SubjectItem>();

		try {
			mConnectionThread = new AtomicReference<ConnectionThread>(null);
			mConnection = TcpIpClient.connect(TcpIpConnectionTypes.Plain, pHostAddress,pHostPort, this);
			cLogger.info("BdxGwyClientConnector successfully connected to  host: " + pHostAddress + " port " + pHostPort );
		} catch (IOException e) {
			cLogger.warn("BdxGwyClientConnector, failed to connect to broadcast gateway " + "host: " + pHostAddress + " port: " + pHostPort);
			mConnection = null; // No connection yet
			mConnectionThread.set(new ConnectionThread());
			mConnectionThread.get().start();
		}
	}

	private DistBdxGwySubscrInterest createInterestMessage(SubjectItem pSubjectItem, int pAction) {
		DistBdxGwySubscrInterest tMsg = new DistBdxGwySubscrInterest();
		DistBdxGwySubscrInterestItem[] tInterests = new DistBdxGwySubscrInterestItem[1];
		tInterests[0] = pSubjectItem.getInterest();
		tMsg.setAction(pAction);
		tMsg.setInterests(tInterests);
		return tMsg;
	}

	/**
	 * This method is invoked when a distributor client (subscriber) adds an subscription. An DistBdxGwySubscrInterest message
	 * is created and send to the local broadcast gateway.
	 * @param pSubject, subject name
	 * @param pHandle, local subscription handle used within the distributor client
	 * @param pMulticastGroupId, multicast group id on which the subscription is setup
	 */
	public void addSubject(String pSubject, Object pHandle,	long pMulticastGroupId) {
		SubjectItem tSubjectItem = new SubjectItem(pSubject, pHandle.toString(), pMulticastGroupId);
		mSubjectItems.add(tSubjectItem);
		// Notify broadcast gateway
		DistBdxGwySubscrInterest tMsg = createInterestMessage(tSubjectItem, DistBdxGwySubscrInterest.ADD_INTEREST);
		sendMessageToBroadcastGateway(tMsg);
	}

	private void resendSubjectNames() {

		if (mSubjectItems.size() == 0) {
			return;
		}

		int i = 0;
		DistBdxGwySubscrInterestItem[] tInterests = new DistBdxGwySubscrInterestItem[mSubjectItems.size()];
		ListIterator<SubjectItem> tItr = mSubjectItems.listIterator();
		while (tItr.hasNext()) {
			tInterests[i++] = tItr.next().getInterest();
		}

		DistBdxGwySubscrInterest tMsg = new DistBdxGwySubscrInterest();
		tMsg.setAction(DistBdxGwySubscrInterest.ADD_INTEREST);
		tMsg.setInterests(tInterests);

		sendMessageToBroadcastGateway(tMsg);
	}

	public void removeSubject(Object pHandle, long pMulticastGroupId) {
		SubjectItem tSubItm = null;

		// Locate subject to be removed
		ListIterator<SubjectItem> tItr = mSubjectItems.listIterator();
		while (tItr.hasNext()) {
			tSubItm = tItr.next();
			if (pHandle.toString().equals(tSubItm.mHandle) && (pMulticastGroupId == tSubItm.mMulticastGroupId)) {
				tItr.remove();
				break;
			} else {
				tSubItm = null;
			}
		}

		// Notify broadcast gateway
		if (tSubItm != null) {
			DistBdxGwySubscrInterest tMsg = createInterestMessage(tSubItm, DistBdxGwySubscrInterest.REMOVE_INTEREST);
			sendMessageToBroadcastGateway(tMsg);
		}
	}

	private void sendMessageToBroadcastGateway(MessageInterface pMessage) {
		synchronized (this) {
			if (mConnection != null) {
				try {
					mConnection.write(pMessage.messageToBytes());
				} catch (IOException e) {
					cLogger.warn( "BdxGwyClientConnector, failed to send message to broadcast gateway "
									                            + "host: " + mHostAddress + " port: " + mHostPort + "\n reason: " + e.getMessage());
				}
			}
		}
	}

	@Override
	public void tcpipClientError(TcpIpConnection pConnection, IOException pE) {
		cLogger.warn( "BdxGwyClientConnector, lost connection to broadcast gateway "
						+ "host: " + mHostAddress + " port: " + mHostPort + "\n reason: " + pE.getMessage());
		pConnection.close();
		synchronized (mConnectionThread) {
			if (mConnectionThread.get() == null) { // Is zero out by the the ClientConnection after successfull connection
				cLogger.info( "BdxGwyClientConnector, trying to re-connect to broadcast gateway "
						+ "host: " + mHostAddress + " port: " + mHostPort );
				synchronized (this) {
					mConnection = null; // Connection lost make sure that it will not be used
				}
				mConnectionThread.set(new ConnectionThread());
				mConnectionThread.get().start();
			}
		}
	}

	@Override
	public void tcpipMessageRead(TcpIpConnection pConnection,  byte[] pBuffer) {
		// We should never receive anything from the broadcast gateway this way
		// all updates are published over IP multicast

	}

	class SubjectItem {
		long mMulticastGroupId;
		String mSubject;
		String mHandle;

		SubjectItem(String pSubject, String pHandle, long pMulticastGroupId) {
			mMulticastGroupId = pMulticastGroupId;
			mSubject = pSubject;
			mHandle = pHandle;
		}

		DistBdxGwySubscrInterestItem getInterest() {
			DistBdxGwySubscrInterestItem tInterest = new DistBdxGwySubscrInterestItem();
			tInterest.setHandler(mHandle);
			tInterest.setMulticastGroupId(mMulticastGroupId);
			tInterest.setSubject(mSubject);
			return tInterest;
		}
	}

	class ConnectionThread extends Thread {
		long mStartTimeMs = System.currentTimeMillis();
		long mLogMsgTimeMs = System.currentTimeMillis();

		public String formatTime(long secs) {
			return String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
		}

		public void logConnectionFailure( Exception pException ) {
			long tSecSinceStart = ((System.currentTimeMillis() - mStartTimeMs) / 1000L);
			long tSecSinceLastLog = ((System.currentTimeMillis() - mLogMsgTimeMs) / 1000L);
			boolean tLog = false;

			if (tSecSinceStart <= 300L) // 5 min
			{
				if (tSecSinceLastLog >= 60) { tLog = true; }
			}
			else if (tSecSinceStart <= 900L) // 15 min
			{
				if (tSecSinceLastLog >= 120) { tLog = true; }
			}
			 else if (tSecSinceStart <= 900L) // 15 min
			{
				if (tSecSinceLastLog >= 180) { tLog = true; }
			} else {
				if (tSecSinceLastLog >= 300) { tLog = true; }
			}

			if (tLog) {
				cLogger.info("BdxGwyClientConnector, failed to re-connect (since: " + formatTime( tSecSinceStart ) + ") to " + "host: " + mHostAddress + " port: " + mHostPort +
						"\n reason: " + pException.getMessage());

				mLogMsgTimeMs = System.currentTimeMillis();
			}
		}

		@Override
		public void run() {


			while( true ) {
				try {
					TcpIpConnection tConn = TcpIpClient.connect(TcpIpConnectionTypes.Plain, mHostAddress, mHostPort, BdxGwyDistributorClient.this);
					synchronized (mConnectionThread) {
						mConnectionThread.set(null);
						synchronized (BdxGwyDistributorClient.this) {
							mConnection = tConn;
							resendSubjectNames();
						}
						cLogger.info("BdxGwyClientConnector, successfully connected to broadcast gateway " + "host: " + mHostAddress + " port: " + mHostPort);
						return;
					}
				} catch (IOException e) {
					logConnectionFailure( e );
				}
				try { Thread.sleep(10000); }
				catch (InterruptedException e) {
				}
			}
		}
	}
}
