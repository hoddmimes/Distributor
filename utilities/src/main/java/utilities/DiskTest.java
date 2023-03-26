package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiskTest {
	SimpleDateFormat cTimeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

	int mMessagesToWrite = 4000;
	int mMessageSize = 2048;
	boolean mResultToFile = true;

	private void parseArguments(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].equalsIgnoreCase("-messages")) {
				mMessagesToWrite = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equalsIgnoreCase("-size")) {
				mMessageSize = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equalsIgnoreCase("-messages")) {
				mResultToFile = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
			i++;
		}
	}

	private void test() {
		RandomAccessFile tFile;
		PrintWriter tLogFile;
		ByteBuffer tBuffer;
		long tStartTime, tLogTime;

		try {
			tLogFile = null;
			tFile = new RandomAccessFile("disktest.bin", "rws");
			if (mResultToFile) {
				tLogFile = new PrintWriter(new FileOutputStream("DiskTest.log"));
			}
			tBuffer = ByteBuffer.allocate(mMessageSize);
			for (int i = 0; i < mMessageSize; i++) {
				tBuffer.put((byte) 'x');
			}
			tStartTime = System.currentTimeMillis();
			for (int i = 0; i < mMessagesToWrite; i++) {
				tLogTime = System.nanoTime();
				tFile.getChannel().write((ByteBuffer) tBuffer.flip());
				tFile.getFD().sync();
				if (mResultToFile) {
					long tTim = (System.nanoTime() - tLogTime) / 1000L;
					tLogFile.println(cTimeFormat.format(new Date())
							+ " record: " + (i + 1) + " microseconds: " + tTim);
				}
			}
			long tTotTime = System.currentTimeMillis() - tStartTime;
			long tMsgsSec = (mMessagesToWrite * 1000L) / tTotTime;
			long tTotBytes = (mMessagesToWrite * mMessageSize * 1000L);
			long tBytesSec = (tTotBytes / tTotTime);
			System.out.println("Total time: " + tTotTime
					+ " ms. messages/sec: " + String.valueOf(tMsgsSec)
					+ " bytes/sec: " + String.valueOf(tBytesSec));
			if (mResultToFile) {
				tLogFile.println("Total time: " + tTotTime
						+ " ms. messages/sec: " + String.valueOf(tMsgsSec)
						+ " bytes/sec: " + String.valueOf(tBytesSec));
				tLogFile.flush();
				tLogFile.close();
			}

			tFile.close();
			new File("disktest.bin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DiskTest t = new DiskTest();
		t.parseArguments(args);
		t.test();
	}

}
