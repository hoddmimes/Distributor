package com.hoddmimes.distributor.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.hoddmimes.distributor.Distributor;
import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.DistributorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistributorConnectionController {
	private static final Logger cLogger = LogManager.getLogger( DistributorConnectionController.class.getSimpleName());
	private static ReentrantLock cMutexAccess = new ReentrantLock();
	private static ReentrantLock cMutexRemove = new ReentrantLock();
	private static LinkedList<DistributorConnection> cConnections = new LinkedList<DistributorConnection>();
	
	
	static private DistributorConnection getDistributorConnection( long pDistributorConnectionId ) {
		DistributorConnection tConnection = null;
		
		Iterator<DistributorConnection> tItr = cConnections.iterator();
		while( tItr.hasNext() ) {
			tConnection = tItr.next();
			if (tConnection.mConnectionId == pDistributorConnectionId) {
			  return tConnection;
			}
		}
		return null;
	}
	
	static List<DistributorConnection> getDistributorConnection() {
		ArrayList<DistributorConnection> tConnection = new ArrayList<DistributorConnection>();
		if (cConnections.isEmpty()) {
			return tConnection;
		}
		
		try {
			cMutexAccess.lock();
			tConnection.addAll(cConnections);
		} finally {
			cMutexAccess.unlock();
		}
		return tConnection;

	}
	
	public static DistributorConnection createConnection( Distributor pDistributor, DistributorConnectionConfiguration pConfiguration, 
												   DistributorApplicationConfiguration pApplicationConfiguration) throws DistributorException {
		DistributorConnection tConnection = null;
		
		cMutexRemove.lock();
		cMutexAccess.lock();
		try {
			Iterator<DistributorConnection> tItr = cConnections.iterator();
			while( tItr.hasNext() ) {
				tConnection = tItr.next();
				if (tConnection.mIpmg.mInetAddress.equals(pConfiguration.getMca()) && (tConnection.mIpmg.mPort == pConfiguration.getMcaPort())) {
					DistributorException tException = new DistributorException(
									"Connection for multicast group: "
									+ pConfiguration.getMca().toString() + " port: "
									+ pConfiguration.getMcaPort()
									+ " as already been created");
					cLogger.fatal(tException);
					throw tException;
				}
			}
		
			tConnection = new DistributorConnection(pDistributor, pConfiguration, pApplicationConfiguration);
			cConnections.add(tConnection);
		} 
		finally
		{
			cMutexAccess.unlock();
			cMutexRemove.unlock();
		}

		return tConnection;
	}
	
	public static DistributorConnection getAndLockDistributor( long pDistributorConnectionId ) 
	{
		DistributorConnection tConnection = null;
		
		cMutexAccess.lock();
		try  {
			tConnection = getDistributorConnection(pDistributorConnectionId);
			if (tConnection != null) {
				tConnection.lock();
			}
		}
		finally
		{
			cMutexAccess.unlock();
		}
		return tConnection;
	}
	
	public static void unlockDistributor( DistributorConnection pConnection ) {
		pConnection.unlock();
	}
	
	static void removeConnection( long pDistributorConnectionId ) 
	{
		DistributorConnection tConnection = null;
		
		try {
			cMutexRemove.lock();
			cMutexAccess.lock();

			try { tConnection = getDistributorConnection(pDistributorConnectionId); }
			finally { cMutexAccess.unlock(); }
		
			cConnections.remove(tConnection);
		}
		finally {
			cMutexRemove.lock();
		}
	}
	
	
	static boolean queueAsyncEvent( long pDistributorConnectionId,  AsyncEvent pAsyncEvent ) {
		DistributorConnection tConnection = null;

		
		tConnection = getDistributorConnection(pDistributorConnectionId);
		if (tConnection == null) {
		  return false;
		}
		
		tConnection.queueAsyncEvent( pAsyncEvent );
		return true;
	}
		
		
		

}
