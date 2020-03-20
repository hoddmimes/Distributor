package com.hoddmimes.distributor.messaging;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	public TreeNode(String pNodeName) {
		super(pNodeName);
	}


	
	public static TreeNode createArray(String pAttributeName, List<? extends MessageInterface> pList) {
		if (pList == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pList.size() + "]");
		for (int i = 0; i < pList.size(); i++) {
			if (pList.get(i) == null) {
				tRoot.add(new TreeNode("[" + i + "] <null>"));
			} else {
				tRoot.add(pList.get(i).getNodeTree("[" + i + "]"));
			}
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, boolean[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}


	
	public static TreeNode createArray(String pAttributeName, byte[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, char[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, short[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, int[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, long[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, float[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, double[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length
				+ "]");
		for (int i = 0; i < pArray.length; i++) {
			tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
		}
		return tRoot;
	}

	public static TreeNode createArray(String pAttributeName, Object[] pArray) {
		if (pArray == null) {
			return new TreeNode(pAttributeName + " : <null>");
		}

		TreeNode tRoot = new TreeNode(pAttributeName + " [" + pArray.length + "]");
		for (int i = 0; i < pArray.length; i++) {
			if (pArray[i] == null) {
				tRoot.add(new TreeNode("[" + i + "] <null>"));
			} else {
				tRoot.add(new TreeNode("[" + i + "] " + String.valueOf(pArray[i])));
			}
		}
		return tRoot;
	}

}
