package com.hoddmimes.distributor.api;

import com.hoddmimes.distributor.generated.messages.QueueSizeItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


class SenderQueue<E> {
	static final SimpleDateFormat cSDF = new SimpleDateFormat("HH:mm:ss.SSS");
	private int mPeakLength;
	private String mPeakTime;
	private LinkedBlockingQueue<E> mQueue;

	SenderQueue() {
		mPeakLength = 0;
		mPeakTime = "00:00:00.000";
		mQueue = new LinkedBlockingQueue<E>();
	}

	SenderQueue(int pLimit) {
		mPeakLength = 0;
		mPeakTime = "00:00:00.000";
		mQueue = new LinkedBlockingQueue<E>(pLimit);
	}

	void add(E pItem) {
		try {
			mQueue.put(pItem);
		} catch (InterruptedException e) {
		}
		if (mQueue.size() > mPeakLength) {
			mPeakLength = mQueue.size();
			mPeakTime = cSDF.format(new Date());
		}
	}

	E poll() {
		return mQueue.poll();
	}

	E poll(long pTimeout) {
		E tItem = null;
		try {
			tItem = mQueue.poll(pTimeout, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
		}
		return tItem;
	}

	E take() {
		E tItem = null;
		try {
			tItem = mQueue.take();
		} catch (InterruptedException e) {
		}
		return tItem;
	}

	void clear() {
		mQueue.clear();
		synchronized (mQueue) {
			mQueue.notify();
		}
	}

	boolean isEmpty() {
		return mQueue.isEmpty();
	}

	int getQueueLength() {
		return mQueue.size();
	}

	QueueSizeItem getSizeInfo() {
		QueueSizeItem tQI = new QueueSizeItem();
		tQI.setSize(mQueue.size());
		tQI.setPeakSize(mPeakLength);
		tQI.setPeakTime(mPeakTime);
		return tQI;
	}

}
