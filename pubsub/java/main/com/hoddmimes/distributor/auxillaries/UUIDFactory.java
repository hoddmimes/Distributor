package com.hoddmimes.distributor.auxillaries;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The TXID is a 64 bit value. It's not guaranteed to be unique but is guarantee
 * to be unique over the next year. The 64 bits are used in the following way
 * 
 * 32 bits, number of milliseconds since start of month 8 bits, host nibble 8
 * bits, port offset 16 bits sequence number
 * 
 * 
 */

public class UUIDFactory {
	private static final SimpleDateFormat cStartOfMonthFormat = new SimpleDateFormat("yyyy-MM");
	private static final int cPortStartOffset = 60123;

	private static AtomicReference<UUIDFactory> cInstance = new AtomicReference<UUIDFactory>(null);
	private byte mHostNibble;
	private byte mPortNibble;
	private long mTimeStamp;

	private AtomicInteger mAtomicSeqno;
	private long mLastTimeStamp;
	private long mStartOfMonth;

	private UUIDFactory() {
	}

	public static long getId() {
		if (cInstance.get() == null) {
			synchronized (UUIDFactory.class) {
				UUIDFactory tTXID = new UUIDFactory();
				tTXID.initialize();
				cInstance.set(tTXID);
			}
		}
		return cInstance.get().getNextTransactionId();
	}

	private long getNextTransactionId() {
		if (mAtomicSeqno.get() >= 0x7ffff) {
			synchronized (UUIDFactory.class) {
				this.mTimeStamp = getTimeStamp();
				mAtomicSeqno.set(0);
			}
		}

		long txid = ((mTimeStamp & 0xffffffL) << 32);
		txid += ((long) mHostNibble << 24);
		txid += ((long) mPortNibble << 17);
		txid += mAtomicSeqno.incrementAndGet();
		return txid;
	}

	private long getStartOfMonthTime() {
		try {
			String tTimeStr = cStartOfMonthFormat.format(new Date());
			Date tDate = cStartOfMonthFormat.parse(tTimeStr);
			return tDate.getTime();

		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}

	}

	private void initialize() {
		// Get start of day
		mStartOfMonth = getStartOfMonthTime();
		// Get time stamp
		mTimeStamp = getTimeStamp();
		// Get host nibble
		try {
			mHostNibble = InetAddress.getLocalHost().getAddress()[3];
		} catch (IOException e) {
			mHostNibble = 1;
		}
		// Set sequence number
		mAtomicSeqno = new AtomicInteger(0);

		// Port nibble
		int i = 0;
		boolean tFound = false;
		while ((!tFound) && (i < 127)) {
			try {
				new ServerSocket(cPortStartOffset + i);
				mPortNibble = (byte) i;
				tFound = true;
			} catch (IOException e) {
				i++;
			}
		}
		if (!tFound) {
			IOException e = new IOException("Could not allocate port nibble");
			e.printStackTrace();
			mPortNibble = 127;
		}

	}

	private long getTimeStamp() {
		long tTimeStamp;
		do {
			tTimeStamp = (System.currentTimeMillis() - mStartOfMonth);
			if (tTimeStamp >= 0x9FA52400) {
				mStartOfMonth = getStartOfMonthTime();
				tTimeStamp = (System.currentTimeMillis() - mStartOfMonth);
			}
			if (tTimeStamp == mLastTimeStamp) {
				System.out.println("Fast allocation!!");
			}
		} while (tTimeStamp == mLastTimeStamp);

		mLastTimeStamp = tTimeStamp;
		return tTimeStamp;
	}

	public static void main(String[] args) {
		try {
			String tTimStr = cStartOfMonthFormat.format(new Date());
			cStartOfMonthFormat.parse(tTimStr);
			long t1 = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				long l = UUIDFactory.getId();

				if ((i % 10000) == 0) {
					System.out.println("Loop: " + i + " txid: "
							+ Long.toHexString(l));
				}
			}
			System.out.println("Total time: "
					+ (System.currentTimeMillis() - t1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
