/*
 *
 * Copyright (c) 2008 Hoddmimes Solutions AB, Stockholm,
 * Sweden. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Hoddmimes Solutions AB, Stockholm, Sweden. You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Hoddmimes.
 *
 * Hoddmimes makes no representations or warranties about the suitability
 * of the software, either expressed or implied, including, but not limited
 * to, the implied warranties of merchantibility, fitness for a particular
 * purpose, or non-infringement. Hoddmimes shall not be liable for any
 * damages suffered by licensee as a result of using, modifying, or
 * distributing this software or its derivatives.
 */
package com.hoddmimes.distributor.messaging;

import java.security.MessageDigest;

public class MessageAux {

	public static String format(byte[][] pArray ) {
		if (pArray == null) {
			return "null";
		}
		StringBuffer tSB = new StringBuffer(512);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
		   tSB.append( format(pArray[i])); 
		   tSB.append("\n");
		}
		tSB.append("]");
		return tSB.toString();
	}
		
	
	public static String format(String[] pArray) {
		if (pArray == null) {
			return "null";
		}
		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(pArray[i] + ", ");
			} else {
				tSB.append(pArray[i]);
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String format(int[] pArray) {
		if (pArray == null) {
			return "null";
		}

		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(String.valueOf(pArray[i] + ", "));
			} else {
				tSB.append(String.valueOf(pArray[i]));
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String format(byte[] pArray) {
		if (pArray == null) {
			return "null";
		}

		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(String.valueOf(pArray[i] + ", "));
			} else {
				tSB.append(String.valueOf(pArray[i]));
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String format(short[] pArray) {
		if (pArray == null) {
			return "null";
		}

		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(String.valueOf(pArray[i] + ", "));
			} else {
				tSB.append(String.valueOf(pArray[i]));
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String format(long[] pArray) {
		if (pArray == null) {
			return "null";
		}

		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(String.valueOf(pArray[i] + ", "));
			} else {
				tSB.append(String.valueOf(pArray[i]));
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String format(boolean[] pArray) {
		if (pArray == null) {
			return "null";
		}

		StringBuffer tSB = new StringBuffer(256);
		tSB.append("[");
		for (int i = 0; i < pArray.length; i++) {
			if (i < (pArray.length - 1)) {
				tSB.append(String.valueOf(pArray[i] + ", "));
			} else {
				tSB.append(String.valueOf(pArray[i]));
			}
		}
		tSB.append("]");
		return tSB.toString();
	}

	public static String hashPassword(String pPassword) {
		if (pPassword == null) {
			return "";
		}
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			byte[] tBytes = m.digest(pPassword.getBytes());
			StringBuffer tSB = new StringBuffer(tBytes.length * 2);
			for (int i = 0; i < tBytes.length; i++) {
				tSB.append(Integer.toHexString(0x0100 + (tBytes[i] & 0x00FF))
						.substring(1));
			}
			return tSB.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
