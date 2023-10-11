package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.generated.messages.MgmtDistributorBdx;

import java.util.List;

public interface MgmtServiceInterface
{
    List<MgmtDistributorBdx> getMgmtDistributorBdx();
    MgmtDistributorBdx getDistributorBdx( long mDistributorId );
}
