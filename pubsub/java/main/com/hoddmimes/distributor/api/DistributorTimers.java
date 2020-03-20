package com.hoddmimes.distributor.api;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributorTimers {
	static private DistributorTimers cInstance = null;

	int 					mIndex = 0;
	private int 			mThreads;
	private Timer[] 		mTimerThreads;

	private AtomicInteger 		mSecondTicks;
	SecondTickTask 				mSecondTickTask;

	static DistributorTimers getInstance() {
		return cInstance;
	}

	public synchronized static void createTimers(int pTimerThreads) {
		if (cInstance == null) {
			cInstance = new DistributorTimers(pTimerThreads);
		}
	}

	static int getSecondTicks() {
		return cInstance.mSecondTicks.get();
	}

	private DistributorTimers(int pTimerThreads) {
		mSecondTicks = new AtomicInteger(1);
		mThreads = pTimerThreads;
		mTimerThreads = new Timer[pTimerThreads];
		try {
			for (int i = 0; i < pTimerThreads; i++) {
				mTimerThreads[i] = new Timer(new String(
						"Distributor_Timer_Daemon_" + (i + 1)), true);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (pTimerThreads > 1) {
			mIndex = 1;
		}

		mSecondTickTask = new SecondTickTask(this);
		if (mThreads == 1) {
			mTimerThreads[0].schedule(mSecondTickTask, 1000, 1000);
		} else {
			mTimerThreads[1].schedule(mSecondTickTask, 1000, 1000);
		}
	}

	public void queue( long pTimeBeforeFirstExecution, 
							 long pDelayBetweenExecutions, 
							 DistributorTimerTask pTask ) 
	{
		TimerTaskWrapper tTask = new TimerTaskWrapper( pTask );
		
		if (pDelayBetweenExecutions >= 1000) {
			mTimerThreads[0].schedule(tTask, pTimeBeforeFirstExecution, pDelayBetweenExecutions);
		} else {
			mTimerThreads[mIndex++].schedule(tTask, pTimeBeforeFirstExecution, pDelayBetweenExecutions);
			if (mThreads == 1) {
				mIndex = 0;
			} else if (mIndex >= mThreads) {
				mIndex = 1;
			}
		}
	}

	public void queue( long pDelay, DistributorTimerTask pTask) {

		TimerTaskWrapper tTask = new TimerTaskWrapper( pTask );
		if (pDelay >= 1000) {
			mTimerThreads[0].schedule(tTask, pDelay);
		} else {
			mTimerThreads[mIndex++].schedule(tTask, pDelay);
			if (mThreads == 1) {
				mIndex = 0;
			} else if (mIndex >= mThreads) {
				mIndex = 1;
			}
		}
	}

	class TimerTaskWrapper extends TimerTask 
	{
		DistributorTimerTask mDistributorTimerTask;
		
		TimerTaskWrapper( DistributorTimerTask pTask ) {
			mDistributorTimerTask = pTask;
		}
		
		@Override
		public void run() 
		{
			if (mDistributorTimerTask.mCanceled) {
				this.cancel();
				return;
			}
			DistributorConnection tConnection = null;
			tConnection = DistributorConnectionController.getAndLockDistributor(mDistributorTimerTask.mDistributorConnectionId);
			if (tConnection != null) {
				try { mDistributorTimerTask.execute( tConnection ); }
				finally { DistributorConnectionController.unlockDistributor(tConnection); }
			}
		}
	}

	
	class SecondTickTask extends TimerTask {
		DistributorTimers mInstance;

		SecondTickTask(DistributorTimers pDistributorTimers) {
			mInstance = pDistributorTimers;
		}

		@Override
		public void run() {
			mInstance.mSecondTicks.getAndIncrement();
		}
	}

}
