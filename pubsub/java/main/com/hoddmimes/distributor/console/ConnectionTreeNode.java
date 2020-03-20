package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.generated.messages.DistDomainConnectionEntry;
import com.hoddmimes.distributor.generated.messages.DistExploreConnectionRqst;
import com.hoddmimes.distributor.generated.messages.DistNetMsg;
import com.hoddmimes.distributor.messaging.MessageWrapper;

import javax.swing.tree.DefaultMutableTreeNode;





public class ConnectionTreeNode extends DefaultMutableTreeNode implements
		DistributorTreeNodeIf {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DistDomainConnectionEntry mConnection;
	long mDistributionId;

	ConnectionTreeNode(long pDistributorId,
			DistDomainConnectionEntry pConnection) {
		mConnection = pConnection;
		mDistributionId = pDistributorId;
		add(new RetransmissionsTreeNode(mDistributionId, pConnection));
		add(new SubscriptionsTreeNode(mDistributionId, pConnection));
	}

	@Override
	public String toString() {
		return "Connection [ MG: " + mConnection.getMcaAddress() + " : "
				+ mConnection.getMcaPort() + " ("
				+ mConnection.getInRetransmissions() + ":"
				+ mConnection.getOutRetransmissions() + ")]";
	}

	public DistNetMsg getRequestMessage() {
		DistExploreConnectionRqst tRqst = new DistExploreConnectionRqst();
		tRqst.setDistributorId(mDistributionId);
		tRqst.setConnectionId(mConnection.getConnectionId());
		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.setMessage(new MessageWrapper(tRqst));
		return tNetMsg;
	}

}
