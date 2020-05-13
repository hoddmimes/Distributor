package com.hoddmimes.distributor.messaging;

import java.nio.ByteBuffer;
import java.util.List;

public class MessageBinEncoder {
	private final static int DEFAULT_BUFFER_SIZE = 1024;
	private ByteBuffer mBuffer;



	public MessageBinEncoder() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public MessageBinEncoder( int pBufferSize) {
		mBuffer = ByteBuffer.allocate(pBufferSize);
	}
	
	private void ensureCapacity(int pSize) {
		if ((mBuffer.remaining() - pSize) < 0) {
			ByteBuffer tBuffer = null;
			int tAllocSize = 0;

			if (mBuffer.capacity() >= 262144) {
				tAllocSize = Math.max(262144, pSize);
			} else {
				tAllocSize = Math.max((2 * mBuffer.capacity()), pSize);
			}
			tBuffer = ByteBuffer.allocate(mBuffer.capacity() + tAllocSize);
			tBuffer.order( mBuffer.order());
			mBuffer.flip();
			tBuffer.put(mBuffer);
			mBuffer = null;
			mBuffer = tBuffer;
		}
	}

	/**
	 * Get length of encoded data
	 * @return, total length of data encoded 
	 */
	public int getLength() {
		return mBuffer.position();
	}
	
	public int getSize() {
		return mBuffer.position();
	}
	
	public void reset() {
		mBuffer.rewind();
	}
	
	
	/**
	 * Get length of remaining space left in the buffer 
	 * @return, get space left for encoding
	 */
	public int getRemaining() {
		return mBuffer.remaining();
	}
	
	public ByteBuffer getByteBuffer() {
		return mBuffer;
	}
	
	
	
	public void add(boolean pValue) {
		if (pValue) {
			add((byte) 1);
		} else {
			add((byte) 0);
		}
	}
	

	public void add(byte pValue) {
		ensureCapacity((Byte.SIZE / 8));
		mBuffer.put(pValue);
	}

	public void add(char pValue) {
		ensureCapacity((Short.SIZE / 8));
		mBuffer.putShort((short) pValue);
	}

	public void add(short pValue) {
		ensureCapacity((Short.SIZE / 8));
		mBuffer.putShort(pValue);
	}

	public void add(int pValue) {
		ensureCapacity((Integer.SIZE / 8));
		mBuffer.putInt(pValue);
	}

	public void add(long pValue) {
		ensureCapacity((Long.SIZE / 8));
		mBuffer.putLong(pValue);
	}

	public void add(float pValue) {
		ensureCapacity((Float.SIZE / 8));
		mBuffer.putFloat(pValue);
	}

	public void add(double pValue) {
		ensureCapacity((Double.SIZE / 8));
		mBuffer.putDouble(pValue);
	}

	public void add(byte[] pBuffer) {
		add( pBuffer, pBuffer.length);
	}

	public void add(byte[] pBuffer, int pLength) {
		if (pBuffer == null) {
			add(false);
		} else {
			ensureCapacity(pLength + 8);
			add(true);
			add(pLength);
			mBuffer.put(pBuffer, 0, pLength );
		}
	}

	public void add( MessageInterface pMessage) {
		if (pMessage == null) {
			add(false);
		} else {
			// Object present is implicit via the byte array
			MessageBinEncoder tEncoder = new MessageBinEncoder();
			pMessage.encode(tEncoder);
			add(tEncoder.getBytes());
		}
	}

	private byte[] fastStringToBytes( String pString ) {
		final int tSize = pString.length();
		final byte[] tBuffer = new byte[tSize];
		for( int i = 0; i < tSize; i++) {
			tBuffer[i] = (byte) (pString.charAt(i) & 0xff);
		}
		return tBuffer;
	}

	public void add(String pString) {
		add( pString, false);
	}

	public void add(String pString, boolean pFastEncode) {
		if (pString == null) {
			add(false);
		} else {
			byte[] tBytes = (pFastEncode) ? fastStringToBytes(pString) : pString.getBytes();
			ensureCapacity(tBytes.length + 5);
			add(true);
			add(tBytes.length);
			if (tBytes.length > 0) {
				mBuffer.put(tBytes);
			}
		}
	}

	public void add(Boolean pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.booleanValue());
		}
	}

	public void add(Byte pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.byteValue());
		}
	}

	public void add(Character pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.charValue());
		}
	}

	public void add(Short pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.shortValue());
		}
	}

	public void add(Integer pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.intValue());
		}
	}

	public void add(Long pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.longValue());
		}
	}

	public void add(Float pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.floatValue());
		}
	}

	public void add(Double pValue) {
		if (pValue == null) {
			add(false);
		} else {
			add(true);
			add(pValue.doubleValue());
		}
	}
	
	/**
	 * =======================================================================
	 * Add Array Functions
	 * =======================================================================
	 */
	
	public void add( boolean[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( short[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( int[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( long[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( double[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( byte[][] pArray ) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	public void add( String[] pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	

	
	public void addMessageArray( List<?> pArray) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.size() );
			for( int i = 0; i < pArray.size(); i++) {
			  add( (MessageInterface) pArray.get(i) );
			}
		}
	}
	
	public void addMessageArray( MessageInterface[] pArray ) {
		if (pArray == null) {
			add(false);
		} else {
			add(true);
			add( pArray.length );
			for( int i = 0; i < pArray.length; i++) {
			  add( pArray[i] );
			}
		}
	}
	
	
	
	
	

	public byte[] getBytes() {
		byte[] tDst = new byte[mBuffer.position()];
		byte[] tSrc = mBuffer.array();
		System.arraycopy(tSrc, 0, tDst, 0, mBuffer.position());
		return tDst;
	}

	public static String hexifyByteArray(byte[] pBuffer) {

		if (pBuffer == null) {
			return null;
		}

		StringBuffer tSB = new StringBuffer(pBuffer.length * 2);
		for (int i = 0; i < pBuffer.length; i++) {
			tSB.append(Integer.toHexString(0x0100 + (pBuffer[i] & 0x00FF))
					.substring(1));
		}
		return tSB.toString();
	}

}
