package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.DistributorApplicationConfiguration;
import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorNewRemoteConnectionEvent;
import com.hoddmimes.distributor.auxillaries.InetAddressConverter;

import java.util.*;

class RemoteConnectionController {
	Map<Long, RemoteConnection> mRemoteConnections;
	DistributorConnection mConnection;

	RemoteConnectionController(DistributorConnection pConnection) {
		mRemoteConnections = new HashMap<>();
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

	void triggerRemoteConfigurationNotifications(DistributorEventCallbackIf pCallback ) {
		for( RemoteConnection tRemoteConnection : mRemoteConnections.values()) {
			DistributorNewRemoteConnectionEvent tEvent = new DistributorNewRemoteConnectionEvent(
					InetAddressConverter.inetAddrToInt(tRemoteConnection.mRemoteHostInetAddress),
					InetAddressConverter.inetAddrToInt( tRemoteConnection.mMca.mInetAddress ),
					tRemoteConnection.mMca.mPort,
					tRemoteConnection.mRemoteApplicationName,
					tRemoteConnection.mRemoteAppId,
					tRemoteConnection.mRemoteSenderId,
					tRemoteConnection.mRemoteStartTime);

			ClientDeliveryController.getInstance().queueEvent(mConnection.mConnectionId, tEvent, pCallback);

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

		NetMsgConfiguration tMsg = new NetMsgConfiguration( pSegment );
		tMsg.decode();


		RemoteConnection tRemoteConnection = null;
		synchronized (mRemoteConnections) {

			tRemoteConnection = mRemoteConnections.get(pSegment.getSourceId());

			if (tRemoteConnection == null) {
				tRemoteConnection = new RemoteConnection(tMsg, this, mConnection);
				mRemoteConnections.put(pSegment.getSourceId(), tRemoteConnection);

				if (mConnection.isLogFlagSet(DistributorApplicationConfiguration.LOG_RMTDB_EVENTS)) {
					mConnection.log("Remote Connection [CREATED] \n" + tRemoteConnection );
				}
				// Notify clients
				DistributorNewRemoteConnectionEvent tEvent = new DistributorNewRemoteConnectionEvent(
						InetAddressConverter.inetAddrToInt(tRemoteConnection.mRemoteHostInetAddress),
						InetAddressConverter.inetAddrToInt( tRemoteConnection.mMca.mInetAddress ),
						tRemoteConnection.mMca.mPort,
						tRemoteConnection.mRemoteApplicationName,
						tRemoteConnection.mRemoteAppId,
						tRemoteConnection.mRemoteSenderId,
						tRemoteConnection.mRemoteStartTime);

				ClientDeliveryController.getInstance().queueEvent(mConnection.mConnectionId, tEvent);
			}
			tRemoteConnection.mCfgIsActive = true;
		}
		return tRemoteConnection;
	}

	RemoteConnection getConnection(Segment pSegment) {
		RemoteConnection tConnection = null;

		synchronized (mRemoteConnections) {
			tConnection = mRemoteConnections.get(pSegment.getSourceId());
		}
		if (tConnection != null) {
			tConnection.mIsActive = true;
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
			tConnection = mRemoteConnections.get(pSegment.getSourceId());
		}

		if (tConnection != null) {
			tConnection.processHeartbeatMsg(pSegment);
		}
	}

	void processUpdateSegment(RcvSegment pSegment) {
		RemoteConnection tRemoteConnection = null;

		tRemoteConnection = mRemoteConnections.get(pSegment.getSourceId());

		if (tRemoteConnection != null) {
			tRemoteConnection.mIsActive = true;
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
