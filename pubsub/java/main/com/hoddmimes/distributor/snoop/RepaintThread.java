package com.hoddmimes.distributor.snoop;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RepaintThread extends Thread
{
    long     mIntervalMs;
    Runnable mRepaintTask;
    volatile boolean mExitNow = false;

    public RepaintThread( long pInterval, Runnable pRepaintTask ) {
        mIntervalMs = pInterval;
        mRepaintTask = pRepaintTask;
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
           SwingUtilities.invokeLater( mRepaintTask );
       }
    }

}
