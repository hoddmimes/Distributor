package com.hoddmimes.distributor.bdxgwy;

public class BdxGwyAuthEntry
{
    private String mGwyName;
    private String mIpAddress;

    public BdxGwyAuthEntry( String pGwyName, String pIpAddress  ) {
        mGwyName = pGwyName;
        mIpAddress = pIpAddress;
    }

    public boolean equal( String pInboundGwyName, String pInboundIpAddress ) {
        if (mGwyName.equals(pInboundGwyName) && ((mIpAddress == null) || (mIpAddress.equals( pInboundIpAddress)))) {
            return true;
        }
        return false;
    }
}
