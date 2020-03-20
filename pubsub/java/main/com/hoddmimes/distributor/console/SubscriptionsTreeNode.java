package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.generated.messages.DistDomainConnectionEntry;
import com.hoddmimes.distributor.generated.messages.DistExploreSubscriptionsRqst;
import com.hoddmimes.distributor.generated.messages.DistNetMsg;
import com.hoddmimes.distributor.messaging.MessageWrapper;

import javax.swing.tree.DefaultMutableTreeNode;




public class SubscriptionsTreeNode extends DefaultMutableTreeNode implements
		DistributorTreeNodeIf {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DistDomainConnectionEntry mConnection;
	long mDistributionId;

	SubscriptionsTreeNode(long pDistributorId,
			DistDomainConnectionEntry pConnection) {
		mConnection = pConnection;
		mDistributionId = pDistributorId;
	}

	@Override
	public String toString() {
		return "Subscriptions [ active subscriptions: "
				+ mConnection.getSubscriptions() + " ]";
	}

	public DistNetMsg getRequestMessage() {
		DistExploreSubscriptionsRqst tRqst = new DistExploreSubscriptionsRqst();
		tRqst.setConnectionId(mConnection.getConnectionId());
		tRqst.setDistributorId(mDistributionId);
		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.setMessage(new MessageWrapper(tRqst));
		return tNetMsg;
	}
}
