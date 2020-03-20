package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.generated.messages.DistDomainConnectionEntry;
import com.hoddmimes.distributor.generated.messages.DistExploreRetransmissionsRqst;
import com.hoddmimes.distributor.generated.messages.DistNetMsg;
import com.hoddmimes.distributor.messaging.MessageWrapper;

import javax.swing.tree.DefaultMutableTreeNode;




public class RetransmissionsTreeNode extends DefaultMutableTreeNode implements
		DistributorTreeNodeIf {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DistDomainConnectionEntry mConnection;
	long mDistributionId;

	RetransmissionsTreeNode(long pDistributorId,
			DistDomainConnectionEntry pConnection) {
		mConnection = pConnection;
		mDistributionId = pDistributorId;
	}

	@Override
	public String toString() {
		return "Retransmissions [In: " + mConnection.getInRetransmissions()
				+ " Out: " + mConnection.getOutRetransmissions() + "]";
	}

	public DistNetMsg getRequestMessage() {
		DistExploreRetransmissionsRqst tRqst = new DistExploreRetransmissionsRqst();
		tRqst.setConnectionId(mConnection.getConnectionId());
		tRqst.setDistributorId(mDistributionId);
		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.setMessage(new MessageWrapper(tRqst));
		return tNetMsg;
	}
}
