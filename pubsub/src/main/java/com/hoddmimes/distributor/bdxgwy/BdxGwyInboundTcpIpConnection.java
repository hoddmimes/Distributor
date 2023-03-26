package com.hoddmimes.distributor.bdxgwy;

import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public  class BdxGwyInboundTcpIpConnection extends Thread
{
	private static final Logger cLogger = LogManager.getLogger( BdxGwyInboundTcpIpConnection.class.getSimpleName());
	
   private TcpIpConnection mTcpIpConnection;
   private LinkedBlockingQueue<MessageInterface> mOutQueue;
   private volatile boolean mTimeToDie;
   
   
   
   BdxGwyInboundTcpIpConnection( TcpIpConnection pConnection ) {
	   mTcpIpConnection = pConnection;
	   mOutQueue = new LinkedBlockingQueue<MessageInterface>();
	   mTimeToDie = false;

	   // Start sender thread
	   start();
   }
  
   
   public void send( MessageInterface pMessage ) {
	   if (!mTimeToDie) {
		   if (pMessage != null) {
			   mOutQueue.add(pMessage);
		   }
	   }
   }
   
   public void close(Exception pException) 
   {
	  mTimeToDie = true;
	  this.interrupt();
	  mTcpIpConnection.close();
	  String tReason = (pException != null) ? pException.getMessage() : "<null>";
		cLogger.warn(	"connection to bdx gateway: "
				+ mTcpIpConnection.getRemoteHost() + " port: "	+ mTcpIpConnection.getRemotePort() + " is being terminated\n"
				+ " reason: " + tReason);
   }
   
	@Override
	public void run() {
		MessageInterface tUpdItem;
		ArrayList<MessageInterface> tList = new ArrayList<MessageInterface>(50);

		setName("BdxGwyInbound (sender):" + mTcpIpConnection.getRemoteHost() + ":" +  mTcpIpConnection.getRemotePort() );
		while (!mTimeToDie) {
			tUpdItem = null;
			try { tUpdItem = mOutQueue.take(); } 
			catch (InterruptedException e) {}
	
			if (mTimeToDie) {
			  mOutQueue.clear();
			  return;
			}
			
			
			if (tUpdItem != null) {
				try {
					mTcpIpConnection.write(tUpdItem.messageToBytes(), true);
					while (mOutQueue.size() > 0) {
						tList.clear();
						mOutQueue.drainTo(tList, 50);
						for (int i = 0; i < tList.size(); i++) {
							mTcpIpConnection.write(tList.get(i).messageToBytes(), false);
						}
						mTcpIpConnection.flush();
					}
					mTcpIpConnection.flush();
				} 
				catch (Exception e) 
				{
					cLogger.warn("send thread failed to send message to bdx gateway: "
									+ mTcpIpConnection.getRemoteHost() + " port: "	+ mTcpIpConnection.getRemotePort());
					return;
				}
			}
		}
		mOutQueue.clear();
	}   
}
