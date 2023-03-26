package com.hoddmimes.distributor.console;

import javax.swing.*;

public class SchedulerThread extends Thread
{
    long     mIntervalMs;
    Runnable mTask;
    volatile boolean mExitNow = false;

    public SchedulerThread(long pInterval, Runnable pTask ) {
        mIntervalMs = pInterval;
        mTask = pTask;
    }

    void setTimeToStop() {
        mExitNow = true;
    }

    @Override
    public void run() {
       while( true ) {
           try{ Thread.sleep( mIntervalMs ); }
           catch (InterruptedException e) {}
           if (mExitNow) {
               return;
           }
           SwingUtilities.invokeLater( mTask );
       }
    }

}
