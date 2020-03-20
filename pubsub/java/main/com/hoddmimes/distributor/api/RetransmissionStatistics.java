package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.generated.messages.DistExploreRetransmissonsRsp;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;





class RetransmissionStatistics {
	Map<Long, NodeEntry> mOutStatistics;
	Map<Long, NodeEntry> mInStatistics;

	int mTotalIn = 0;
	int mTotalOut = 0;
	int mTotalSeen = 0;

	RetransmissionStatistics() {
		mOutStatistics = Collections
				.synchronizedMap(new HashMap<Long, NodeEntry>());
		mInStatistics = Collections
				.synchronizedMap(new HashMap<Long, NodeEntry>());
	}

	DistExploreRetransmissonsRsp getRetransmissonsInfo() {
		DistExploreRetransmissonsRsp tRsp = new DistExploreRetransmissonsRsp();
		tRsp.setTotalInRqst(mTotalIn);
		tRsp.setTotalOutRqst(mTotalOut);
		tRsp.setTotalSeenRqst(mTotalSeen);

		NodeEntry[] tInNodes = mInStatistics.values().toArray(new NodeEntry[0]);
		if (tInNodes != null) {
			sortInNodes(tInNodes);
			String[] tInNodeArray = new String[tInNodes.length];
			for (int i = 0; i < tInNodes.length; i++) {
				tInNodeArray[i] = tInNodes[i].toInString();
			}
			tRsp.setInHosts(tInNodeArray);

		} else {
			tRsp.setInHosts(null);
		}

		NodeEntry[] tOutNodes = mOutStatistics.values().toArray(
				new NodeEntry[0]);
		if (tOutNodes != null) {
			sortOutNodes(tOutNodes);
			String[] tOutNodeArray = new String[tInNodes.length];
			for (int i = 0; i < tOutNodes.length; i++) {
				tOutNodeArray[i] = tOutNodes[i].toOutString();
			}
			tRsp.setOutHosts(tOutNodeArray);
		} else {
			tRsp.setOutHosts(null);
		}

		return tRsp;
	}

	private void sortInNodes(NodeEntry[] pInNodes) {
		if (pInNodes == null) {
			return;
		}
		for (int j = 0; j < pInNodes.length; j++) {
			for (int i = 1; i < pInNodes.length; i++) {
				if (pInNodes[i].mToThisNodeCount > pInNodes[i - 1].mToThisNodeCount) {
					NodeEntry tTmp = pInNodes[i - 1];
					pInNodes[i - 1] = pInNodes[i];
					pInNodes[i] = tTmp;
				}
			}
		}
	}

	private void sortOutNodes(NodeEntry[] pOutNodes) {
		if (pOutNodes == null) {
			return;
		}
		for (int j = 0; j < pOutNodes.length; j++) {
			for (int i = 1; i < pOutNodes.length; i++) {
				if (pOutNodes[i].mOutCount > pOutNodes[i - 1].mOutCount) {
					NodeEntry tTmp = pOutNodes[i - 1];
					pOutNodes[i - 1] = pOutNodes[i];
					pOutNodes[i] = tTmp;
				}
			}
		}
	}

	private int InetAddressToInt(InetAddress pAddress) {
		int x0 = pAddress.getAddress()[0];
		int x1 = pAddress.getAddress()[1];
		int x2 = pAddress.getAddress()[2];
		int x3 = pAddress.getAddress()[3];
		return (x0 << 24) + (x1 << 16) + (x2 << 8) + x3;
	}

	private Long getKey(InetAddress pMcaAddress, int pMcaPort,
			InetAddress pAddress) {
		long tValue = ((InetAddressToInt(pMcaAddress) & 0x00ffffff) << 40)
				+ ((InetAddressToInt(pAddress) & 0x00ffffff) << 16)
				+ (pMcaPort & 0xffff);
		return new Long(tValue);
	}

	void updateInStatistics(InetAddress pMcaAddress, int pMcaPort,
			InetAddress pAddress, boolean pToThisApplication) {
		Long tKey = getKey(pMcaAddress, pMcaPort, pAddress);
		NodeEntry tEntry = mInStatistics.get(tKey);
		if (tEntry == null) {
			tEntry = new NodeEntry(pMcaAddress, pMcaPort, pAddress);
			mInStatistics.put(tKey, tEntry);
		}
		mTotalSeen++;
		tEntry.mTotalInCount++;
		if (pToThisApplication) {
			mTotalIn++;
			tEntry.mToThisNodeCount++;
		} else {
			tEntry.mToRemoteNodeCount++;
		}
	}

	void updateOutStatistics(InetAddress pMcaAddress, int pMcaPort,
			InetAddress pAddress) {
		Long tKey = getKey(pMcaAddress, pMcaPort, pAddress);
		NodeEntry tEntry = mOutStatistics.get(tKey);
		if (tEntry == null) {
			tEntry = new NodeEntry(pMcaAddress, pMcaPort, pAddress);
			mOutStatistics.put(tKey, tEntry);
		}
		tEntry.mOutCount++;
		mTotalIn++;
	}

	class NodeEntry {
		InetAddress mMcaAddress; // MCA address
		int mMcaPort; // MCA port
		InetAddress mAddress; // Source or Destination Address
		int mOutCount;
		int mTotalInCount;
		int mToThisNodeCount;
		int mToRemoteNodeCount;

		NodeEntry(InetAddress pMcaAddress, int pMcaPort, InetAddress pAddress) {
			mMcaAddress = pMcaAddress;
			mMcaPort = pMcaPort;
			mAddress = pAddress;
			mToThisNodeCount = 0;
			mToRemoteNodeCount = 0;
			mTotalInCount = 0;
			mOutCount = 0;
		}

		public String toOutString() {
			return "Host: " + mAddress.getHostAddress()
					+ " number of outgoing retransmissions "
					+ String.valueOf(mOutCount);
		}

		public String toInString() {
			return "Host: " + mAddress.getHostAddress()
					+ " seen retransmissions " + String.valueOf(mTotalInCount)
					+ " for this hosts " + String.valueOf(mToThisNodeCount);
		}
	}

}
