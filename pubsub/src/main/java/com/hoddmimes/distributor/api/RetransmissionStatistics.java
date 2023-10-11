package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.auxillaries.InetAddressConverter;
import com.hoddmimes.distributor.generated.messages.MgmtConnectionRetransmissionInfo;

import java.net.InetAddress;
import java.util.*;


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


	private Long getKey(InetAddress pMcaAddress, int pMcaPort, InetAddress pAddress) {
		long tValue = ((InetAddressConverter.inetAddrToInt(pMcaAddress) & 0x00ffffff) << 40)
				+ ((InetAddressConverter.inetAddrToInt(pAddress) & 0x00ffffff) << 16)
				+ (pMcaPort & 0xffff);
		return tValue;
	}

	void updateInStatistics(InetAddress pMcaAddress, int pMcaPort, InetAddress pAddress, boolean pToThisApplication) {
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

	void updateOutStatistics(InetAddress pMcaAddress, int pMcaPort, InetAddress pAddress) {
		Long tKey = getKey(pMcaAddress, pMcaPort, pAddress);
		NodeEntry tEntry = mOutStatistics.get(tKey);
		if (tEntry == null) {
			tEntry = new NodeEntry(pMcaAddress, pMcaPort, pAddress);
			mOutStatistics.put(tKey, tEntry);
		}
		tEntry.mOutCount++;
		mTotalOut++;
	}

	public MgmtConnectionRetransmissionInfo geRetransmissionStatistics() {
		MgmtConnectionRetransmissionInfo tInfo = new MgmtConnectionRetransmissionInfo();
		tInfo.setTotalInRqst(this.mTotalIn);
		tInfo.setTotalOutRqst(this.mTotalOut);
		tInfo.setTotalSeenRqst(this.mTotalSeen);


		List<NodeEntry> tOutNodes = new ArrayList<>(this.mOutStatistics.values());
		tOutNodes.sort( new NodeEntryComparator(false));
		for( NodeEntry ne: tOutNodes) {
			tInfo.addOutHostsToArray( ne.toOutString());
		}

		List<NodeEntry> tInNodes = new ArrayList<>(this.mInStatistics.values());
		tInNodes.sort( new NodeEntryComparator(true));
		for( NodeEntry ne: tInNodes) {
			tInfo.addInHostsToArray( ne.toInString());
		}
		return tInfo;

	}

	class NodeEntry {
		InetAddress mMcaAddress; // MCA address
		int mMcaPort; // MCA port
		InetAddress mAddress; // Source or Destination Address
		int mOutCount;
		int mTotalInCount;
		int mToThisNodeCount;
		int mToRemoteNodeCount;

		NodeEntry(InetAddress pMcaAddress, int pMcaPort, InetAddress pAddress)  {
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
					+ " number of outgoing retransmissions served: "
					+ String.valueOf(this.mOutCount);
		}

		public String toInString() {
			return "Host: " + mAddress.getHostAddress()
					+ " retransmissions seen: " + String.valueOf(this.mTotalInCount)
					+ " retransmissions for this distributor: " + String.valueOf(this.mToThisNodeCount);
		}
	}

	static class NodeEntryComparator implements Comparator<NodeEntry>
	{
		private boolean mSortInNodes;

		NodeEntryComparator( boolean pSortInNodes) {
			mSortInNodes = pSortInNodes;
		}
		@Override
		public int compare(NodeEntry N1, NodeEntry N2) {
			if (mSortInNodes) {
				return (N2.mToRemoteNodeCount - N1.mToRemoteNodeCount);
			} else {
				return (N2.mOutCount - N1.mOutCount);
			}
		}
	}
}
