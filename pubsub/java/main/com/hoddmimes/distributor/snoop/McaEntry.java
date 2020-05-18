package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.DistributorEvent;
import com.hoddmimes.distributor.DistributorEventCallbackIf;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;

public class McaEntry implements DistributorEventCallbackIf, DistributorUpdateCallbackIf
{
    private static int MCA_ENTRY_ID = 1;

    private String  mMcaAddress;
    private int     mMcaPort;
    private int     mId;

    public McaEntry( String pAddress, int pPort ) {
        mMcaAddress = pAddress;
        mMcaPort = pPort;
        mId = MCA_ENTRY_ID++;
    }

    public String getMcaAddress() {
        return mMcaAddress;
    }

    public int getMcaPort() {
        return mMcaPort;
    }

    public int getId() {
        return mId;
    }

    @Override
    public void distributorEventCallback(DistributorEvent pDistributorEvent)
    {

    }

    @Override
    public void distributorUpdate(String pSubjectName, byte[] pData, Object pCallbackParameter, int pAppId, int pDeliveryQueueLength)
    {

    }
}
