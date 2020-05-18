package com.hoddmimes.distributor.snoop;

import com.hoddmimes.distributor.Distributor;
import com.hoddmimes.distributor.DistributorUpdateCallbackIf;

public interface SnoopUpdateMsgListner
{
    public void addUpdateListener(DistributorUpdateCallbackIf pCallbackListner );
    public void removeUpdateListener(DistributorUpdateCallbackIf pCallbackListner );
}
