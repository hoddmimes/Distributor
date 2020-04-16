package com.hoddmimes.distributor.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;

class RemoteConnectionController {
	Map<Segment, RemoteConnection> mRemoteConnections;
	DistributorConnection mConnection;

	RemoteConnectionController(DistributorConnection pConnection) {
		mRemoteConnections = new HashMap<Segment, RemoteConnection>();
		mConnection = pConnection;
	}

	void close() {
		Iterator<RemoteConnection> tItr;
		tItr = mRemoteConnections.values().iterator();
		while (tItr.hasNext()) {
			RemoteConnection tRmtConn = tItr.next();
			tRmtConn.mCheckConfigurationTask.cancel();
			tRmtConn.mCheckHeartbeatTask.cancel();
			tItr.remove();
		}

	}

	RemoteConnection getRemoteConnection( long pRemoteConnectionId ) {
		Iterator<RemoteConnection> tItr;
		tItr = mRemoteConnections.values().iterator();
		while (tItr.hasNext()) {
			RemoteConnection tRmtConn = tItr.next();
			if (tRmtConn.mRemoteConnectionId == pRemoteConnectionId) {
				return tRmtConn;
			}
		}
		return null;
	}
	
	
	RemoteConnection processConfigurationMessage(Segment pSegment) {

		RemoteConnection tRemoteConnection = null;

		synchronized (mRemoteConnections) {
			tRemoteConnection = mRemoteConnections.get(pSegment);

			if (tRemoteConnection == null) {

				tRemoteConnection = new RemoteConnection(pSegment, this, mConnection);
				mRemoteConnections.put(pSegment, tRemoteConnection);

				if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RMTDB_EVENTS)) {
					if (tRemoteConnection.mRemoteHostInetAddress.getAddress().toString().contains("38.0.167.192")) {
						System.out.println("debug");
					}
					mConnection.log("Remote Connection [CREATED] ("
							+ Integer.toHexString(pSegment.hashCode()) + ")\n"
							+ tRemoteConnection.toString());
				}
			}
			tRemoteConnection.mCfgIsActive = true;
		}
		return tRemoteConnection;
	}

	RemoteConnection getConnection(Segment pSegment) {
		RemoteConnection tConnection = null;

		synchronized (mRemoteConnections) {
			tConnection = mRemoteConnections.get(pSegment);
		}
		if (tConnection != null) {
			tConnection.mHbIsActive = true;
		}
		return tConnection;
	}

	void removeRemoteConnection(RemoteConnection pConnection) {
		synchronized (mRemoteConnections) {
			mRemoteConnections.values().remove(pConnection);
		}
	}

	void processHeartbeatMessage(RcvSegment pSegment) {
		RemoteConnection tConnection = null;

		synchronized (mRemoteConnections) {
			tConnection = mRemoteConnections.get(pSegment);
		}

		if (tConnection != null) {
			tConnection.processHeartbeatMsg(pSegment);
		}
	}

	void processUpdateSegment(RcvSegment pSegment) {
		RemoteConnection tRemoteConnection = null;

		tRemoteConnection = mRemoteConnections.get(pSegment);

		if (tRemoteConnection != null) {
			tRemoteConnection.mHbIsActive = true;
			tRemoteConnection.processUpdateSegment(pSegment);
		}
	}

	class SegmentBatch {
		List<RcvSegment> mList;

		SegmentBatch(RcvSegment pFirstRcvSegmentInBatch) {
			mList = new LinkedList<RcvSegment>();
			mList.add(pFirstRcvSegmentInBatch);
		}

		void addSegment(RcvSegment pSegment) {
			mList.add(pSegment);
		}
	}
}
