package com.hoddmimes.distributor.api;

public interface AsyncEvent 
{
	public void execute( DistributorConnection pDistributorConnection);
	public String getTaskName();
}
