package com.hoddmimes.distributor.messaging;

public class Hex {
	static public byte[] toByteArray(String pString) {
		byte[] tByteArray = new byte[pString.length() / 2];
		for (int i = 0; i < pString.length(); i += 2) {
			int j = Integer.parseInt(pString.substring(i, i + 2), 16);
			tByteArray[i / 2] = (byte) (j & 0x000000ff);
		}
		return tByteArray;
	}

	static public String toHex(byte b) {
		Integer I = new Integer(((b) << 24) >>> 24);
		int i = I.intValue();

		if (i < (byte) 16) {
			return "0" + Integer.toString(i, 16);
		} else {
			return Integer.toString(i, 16);
		}
	}

	static public byte toByte(String pString) {
		int i = Integer.parseInt(pString, 16);
		return (byte) i;
	}

	static public String toHexString(byte[] pArray) {
		if (pArray == null) {
			return null;
		}
		StringBuilder tSB = new StringBuilder();
		for (int i = 0; i < pArray.length; i++) {
			tSB.append(toHex(pArray[i]));
		}
		return tSB.toString();
	}

	public static void main(String[] args) {
		byte[] tArray = new byte[256];
		for (int i = 0; i < tArray.length; i++) {
			tArray[i] = (byte) i;
		}
		String tString = toHexString(tArray);
		tArray = toByteArray(tString);

	}

}
