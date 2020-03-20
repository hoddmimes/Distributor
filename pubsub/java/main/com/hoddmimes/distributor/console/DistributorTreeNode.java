package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.generated.messages.DistDomainDistributorEntry;
import com.hoddmimes.distributor.generated.messages.DistExploreDistributorRqst;
import com.hoddmimes.distributor.generated.messages.DistExploreDomainRsp;
import com.hoddmimes.distributor.generated.messages.DistNetMsg;
import com.hoddmimes.distributor.messaging.MessageWrapper;

import javax.swing.tree.DefaultMutableTreeNode;




public class DistributorTreeNode extends DefaultMutableTreeNode implements
		DistributorTreeNodeIf {
	private static final long serialVersionUID = 1L;
	DistDomainDistributorEntry mDistributorInfo;

	DistributorTreeNode(DistExploreDomainRsp pDistExploreRsp) {
		mDistributorInfo = pDistExploreRsp.getDistributor();
		for (int i = 0; i < pDistExploreRsp.getDistributor().getConnections()
				.size(); i++) {
			add(new ConnectionTreeNode(pDistExploreRsp.getDistributor()
					.getDistributorId(), pDistExploreRsp.getDistributor()
					.getConnections().get(i)));
		}
	}

	@Override
	public String toString() {
		return "Distributor [ " + mDistributorInfo.getHostaddress() + " : "
				+ mDistributorInfo.getHostname() + " : "
				+ mDistributorInfo.getApplicationName() + " ("
				+ mDistributorInfo.getInRetransmissions() + ":"
				+ mDistributorInfo.getOutRetransmissions() + ")]";
	}

	public DistNetMsg getRequestMessage() {
		DistExploreDistributorRqst tRqst = new DistExploreDistributorRqst();
		tRqst.setDistributorId(mDistributorInfo.getDistributorId());
		DistNetMsg tNetMsg = new DistNetMsg();
		tNetMsg.setMessage(new MessageWrapper(tRqst));
		return tNetMsg;
	}

}
