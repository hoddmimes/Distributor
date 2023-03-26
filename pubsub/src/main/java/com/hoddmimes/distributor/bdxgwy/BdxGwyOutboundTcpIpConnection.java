package com.hoddmimes.distributor.bdxgwy;

import com.hoddmimes.distributor.generated.messages.DistBdxGwyPingRqst;
import com.hoddmimes.distributor.generated.messages.DistributorMessagesFactory;
import com.hoddmimes.distributor.messaging.MessageFactoryInterface;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpClient;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionCallbackInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;



public  class BdxGwyOutboundTcpIpConnection extends Thread implements TcpIpConnectionCallbackInterface
{
	private static final Logger cLogger = LogManager.getLogger( BdxGwyOutboundTcpIpConnection.class.getSimpleName());
	
   private volatile AtomicReference<TcpIpConnection> mTcpIpConnectionRef;
   private String mRemoteHost;
   private int    mRemotePort;
   private volatile ConnectionThread mConnectionThread;
   private LinkedBlockingQueue<MessageInterface> mOutQueue;
   private CallbackInterface mCallback;
   private MessageFactoryInterface mMessageFactory;
   private long mStartTime;
   private String mBdxGwyName;
   
   public interface CallbackInterface
   {
	   public void connectionEstablished( BdxGwyOutboundTcpIpConnection pConnection );
	   public void connectionMessageRead( MessageInterface pMessage );
	   public void connectionTerminated( BdxGwyOutboundTcpIpConnection pConnection, Exception pException );
   }
   
   
   BdxGwyOutboundTcpIpConnection( String pRemoteHost, int pRemotePort, String pBdxGwyName, CallbackInterface pCallback  ) {
	   mRemoteHost = pRemoteHost;
	   mRemotePort = pRemotePort;
	   mTcpIpConnectionRef = new  AtomicReference<TcpIpConnection>(null);
	   mOutQueue = new LinkedBlockingQueue<MessageInterface>();
	   mCallback = pCallback;
	   mBdxGwyName = pBdxGwyName;
	   mMessageFactory = new DistributorMessagesFactory();
	   // Start sender thread
	   start();
	   
	   // Start Connection thread 
	   startConnectionThread();
   }
  
   public void send( MessageInterface pMessage ) {
	   if (pMessage != null) {
		   mOutQueue.add(pMessage);
	   }
   }
   
   private void startConnectionThread() {
		synchronized (this) {
			TcpIpConnection tConnection = mTcpIpConnectionRef.get();
			if (tConnection != null) {
				tConnection.close();
			}
			mTcpIpConnectionRef.set(null);
			if (mConnectionThread == null) {
				mConnectionThread = new ConnectionThread();
				mConnectionThread.start();
			}
		}
	}
   
   
	@Override
	public void run() {
		MessageInterface tUpdItem;
		TcpIpConnection tConnection;
		ArrayList<MessageInterface> tList = new ArrayList<MessageInterface>(50);

		setName("BdxGwySender:" + mRemoteHost + ":" + mRemotePort );
		while (true) {
			tUpdItem = null;
			try {
				tUpdItem = mOutQueue.poll(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
			tConnection = mTcpIpConnectionRef.get();
			
			if (tUpdItem != null) {
				try {
					if (tConnection != null) {
						tConnection.write(tUpdItem.messageToBytes(), true);
					}
					while (mOutQueue.size() > 0) {
						tList.clear();
						mOutQueue.drainTo(tList, 50);
						for (int i = 0; i < tList.size(); i++) {
						 if (tConnection != null) {
							tConnection.write(tList.get(i).messageToBytes(), false);
						 }
						}
						if (tConnection != null) {
							tConnection.flush();
						}
					}
					if (tConnection != null) {
						tConnection.flush();
					}
				} catch (Exception e) {
					mCallback.connectionTerminated(this, e);
					startConnectionThread();
				}
			} else if (tConnection != null) {
				DistBdxGwyPingRqst tMsg = new DistBdxGwyPingRqst();
				tMsg.setBdxGwyName(mBdxGwyName);
				tMsg.setStartTime(mStartTime);
				try { 	tConnection.write(tMsg.messageToBytes(), true); }
				catch (Exception e) {
						mCallback.connectionTerminated(this, e);
						startConnectionThread(); 
				}
			}
		}
	}
   
   class ConnectionThread extends Thread 
	{
		
		@SuppressWarnings("static-access")
		private void waitAWhile( int pConnectionAttempts ) {
			try {
				if (pConnectionAttempts < 20) {
					Thread.currentThread().sleep(1000L); 
				} else if (pConnectionAttempts < 200) {
					Thread.currentThread().sleep(5000L); 
				} else if (pConnectionAttempts < 600) {
					Thread.currentThread().sleep(15000L); 
				} else {
					Thread.currentThread().sleep(30000L); 
				}
			}
			catch( InterruptedException e) {
				// by design
			}
		}

		@Override
		public void run() {
			int 		mConnectionAttempts = 0;
			
			while (true) {
				try {
					waitAWhile( mConnectionAttempts );

					TcpIpConnection tConnection = TcpIpClient.connect( TcpIpConnectionTypes.Compression, mRemoteHost, mRemotePort, BdxGwyOutboundTcpIpConnection.this );
					synchronized (BdxGwyOutboundTcpIpConnection.this) {
						mConnectionThread = null;
						mTcpIpConnectionRef.set(tConnection);
						mStartTime = System.currentTimeMillis();
					}
					mCallback.connectionEstablished(BdxGwyOutboundTcpIpConnection.this);
					return;
				} catch (IOException e) {
					if ((mConnectionAttempts > 600) || ((mConnectionAttempts % 10) == 0)) {
							cLogger.warn(" Failed to establish connection remote gateway: "
											+ mRemoteHost + " port: " + mRemotePort);
					}
				}
				mConnectionAttempts++;
			}
		}
	}


@Override
public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) 
{
	MessageInterface tMessage = mMessageFactory.createMessage(pBuffer);
	if (tMessage != null) {
		mCallback.connectionMessageRead(tMessage);
	} else {
		cLogger.info("BDXGWY outbound connection host: " + mRemoteHost + " port: " + mRemotePort +
				                     " failed to decode received message " );
	}
}


@Override
public void tcpipClientError(TcpIpConnection pConnection, IOException e) {
	mCallback.connectionTerminated(BdxGwyOutboundTcpIpConnection.this, e);
}
   
   
}
