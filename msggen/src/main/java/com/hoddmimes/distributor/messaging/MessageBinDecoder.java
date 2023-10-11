package com.hoddmimes.distributor.messaging;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MessageBinDecoder {
	private ByteBuffer mBuffer;

	
	public MessageBinDecoder( byte[] pBuffer) {
		mBuffer = ByteBuffer.wrap(pBuffer);
		mBuffer.position( pBuffer.length );
		mBuffer.flip();
	}
	
	public MessageBinDecoder( byte[] pBuffer, int pLength) {
		mBuffer = ByteBuffer.wrap(pBuffer);
		mBuffer.position( pLength );
		mBuffer.flip();
	}
	
	public MessageBinDecoder( ByteBuffer pBuffer) {
		mBuffer = pBuffer;
		mBuffer.flip();
	}

	public boolean readBoolean() {
		if (mBuffer.get() == (byte) 0) {
			return false;
		}
		return true;
	}

	public byte readByte() {
		return mBuffer.get();
	}

	public char readChar() {
		return (char) mBuffer.getShort();
	}

	public short readShort() {
		return mBuffer.getShort();
	}

	public int readInt() {
		return mBuffer.getInt();
	}

	public long readLong() {
		return mBuffer.getLong();
	}

	public float readFloat() {
		return mBuffer.getFloat();
	}

	public double readDouble() {
		return mBuffer.getDouble();
	}

	public byte[] readBytes() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = mBuffer.getInt();
		byte[] tByteArray = new byte[tSize];
		mBuffer.get(tByteArray);
		return tByteArray;
	}

	public String readString() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = mBuffer.getInt();
		if (tSize == 0) {
			return new String("");
		}

		byte[] tByteArray = new byte[tSize];
		mBuffer.get(tByteArray);
		return new String(tByteArray);
	}

	public Boolean readBooleanObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readBoolean();
		}
	}

	public Byte readByteObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readByte();
		}
	}

	public Character readCharacterObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readChar();
		}
	}

	public Short readShortObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readShort();
		}
	}

	public Integer readIntegerObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readInt();
		}
	}


	public Long readLongObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readLong();
		}
	}

	public Float readFloatObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readFloat();
		}
	}

	public Double readDoubleObject() {
		if (!readBoolean()) {
			return null;
		} else {
			return readDouble();
		}
	}


	
	
	public MessageInterface readMessage(Class<?> pMessageClass) {
		if (!readBoolean()) {
			return null;
		}

		int tSize = mBuffer.getInt();
		byte[] tByteArray = new byte[tSize];
		mBuffer.get(tByteArray);
		MessageBinDecoder tDecoder = new MessageBinDecoder(tByteArray);

		try {
			MessageInterface tMessage = (MessageInterface) pMessageClass.newInstance();
			tMessage.decode(tDecoder);
			return tMessage;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the total length of data available
	 * @return, total length of data available 
	 */
	public int getLength() {
		return mBuffer.limit();
	}
	
	/**
	 * Get length of remaining data to decode 
	 * @return, data available for decoding
	 */
	public int getRemaining() {
		return mBuffer.remaining();
	}
	
	public ByteBuffer getByteBuffer() {
		return mBuffer;
	}
	
	public void reset() {
		mBuffer.rewind();
	}
	
	
	/**
	 * ===============================================================
	 * Read Array Functions 
	 * ===============================================================
	 */
	
	public boolean[] readBooleanArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		boolean[] tArray = new boolean[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readBoolean();
		}
		return tArray;
	}
	
	public short[] readShortArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		short[] tArray = new short[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readShort();
		}
		return tArray;
	}
	
	public int[] readIntArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		int[] tArray = new int[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readInt();
		}
		return tArray;
	}
	
	public long[] readLongArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		long[] tArray = new long[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readLong();
		}
		return tArray;
	}
	
	public double[] readDoubleArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		double[] tArray = new double[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readDouble();
		}
		return tArray;
	}
	
	public String[] readStringArray() {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		String[] tArray = new String[ tSize ];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readString();
		}
		return tArray;
	}
	
	
	public List<?> readMessageArray(Class<?> pClass) {
		if (!readBoolean()) {
			return null;
		}
		int tSize = readInt(); // Read array size 
		List<MessageInterface> tList = new ArrayList<MessageInterface>();
		for( int i = 0; i < tSize; i++ ) {
			tList.add( readMessage(pClass));
		}
		return tList;
	}
	
	public byte[][] readBytesArray() {
		if (!readBoolean()) {
			return null;
		}
		
		int tSize = readInt(); // Read array size 
		byte[][] tArray = new byte[ tSize ][];
		for( int i = 0; i < tSize; i++ ) {
			tArray[i] = readBytes();
		}
		return tArray;
	}
	

	public static byte[] hexStringToBuffer(String pHexString) {
		if (pHexString == null) {
			return null;
		}

		byte[] tBuffer = new byte[pHexString.length() / 2];
		for (int i = 0; i < tBuffer.length; i++) {
			tBuffer[i] = (byte) Integer.parseInt(pHexString.substring(2 * i,
					2 * i + 2), 16);
		}
		return tBuffer;
	}

	public static int extractMessageId(byte[] pBuffer) {
		if (pBuffer == null) {
			throw new RuntimeException("Message buffer must not be null");
		}
		if (pBuffer.length < (Integer.SIZE / 8)) {
			throw new RuntimeException("Message buffer must be at least "
					+ (Integer.SIZE / 8) + " bytes");
		}
		return ((pBuffer[0] & 0xff) << 24) + ((pBuffer[1] & 0xff) << 16)
				+ ((pBuffer[2] & 0xff) << 8) + (pBuffer[3] & 0xff);
	}
}
