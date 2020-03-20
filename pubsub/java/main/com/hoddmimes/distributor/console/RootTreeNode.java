package com.hoddmimes.distributor.console;

import com.hoddmimes.distributor.generated.messages.DistNetMsg;

import javax.swing.tree.DefaultMutableTreeNode;





@SuppressWarnings("serial")
public class RootTreeNode extends DefaultMutableTreeNode implements
		DistributorTreeNodeIf {

	public RootTreeNode() {
		super("Distributors");
		// TODO Auto-generated constructor stub
	}

	public RootTreeNode(Object arg0, boolean arg1) {
		super("Distributors", arg1);
		// TODO Auto-generated constructor stub
	}

	void addDistributor(DistributorTreeNode pDistributor) {
		super.add(pDistributor);
	}

	public DistNetMsg getRequestMessage() {
		return null;
	}

}
