package com.hoddmimes.distributor.samples;

import com.hoddmimes.distributor.DistributorConnectionConfiguration;
import com.hoddmimes.distributor.bdxgwy.BdxGatewayParameterEntry;
import com.hoddmimes.distributor.bdxgwy.BdxGwyMulticastGroupParameterEntry;
import com.hoddmimes.distributor.bdxgwy.BroadcastGateway;

import java.util.ArrayList;

public class TestBdxGateway 
{
	ArrayList<BdxGwyMulticastGroupParameterEntry> mMultiCastGroups;
	ArrayList<BdxGatewayParameterEntry> mBroadcastGateways;
	BroadcastGateway mBdxGwy;
	
	
	public TestBdxGateway() {
		
	}
	private void execute() {
		mMultiCastGroups = new ArrayList<BdxGwyMulticastGroupParameterEntry>();
		mBroadcastGateways = new ArrayList<BdxGatewayParameterEntry>();
		
		mMultiCastGroups.add( new BdxGwyMulticastGroupParameterEntry(DistributorConnectionConfiguration.DEFAULT_MCA_ADDRESS, DistributorConnectionConfiguration.DEFAULT_MCA_PORT));
		mBroadcastGateways.add( new BdxGatewayParameterEntry("BdxGwyHomeA", "192.168.42.10",11888));
		mBroadcastGateways.add( new BdxGatewayParameterEntry("BdxGwyHomeB", "192.168.42.11",11888));
		mBdxGwy = new BroadcastGateway( 11900, 11888, "BdxGwyHomeA", null, mBroadcastGateways, mMultiCastGroups);
		while( true ) {
			try{ Thread.currentThread().sleep(10000L); }
			catch( InterruptedException e) {}
		}
	}
	
	public static void main(String[] args) {
		TestBdxGateway t = new TestBdxGateway();
		t.execute();

	}

}
