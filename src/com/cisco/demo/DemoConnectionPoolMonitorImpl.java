package com.cisco.demo;

import com.netflix.astyanax.connectionpool.impl.Slf4jConnectionPoolMonitorImpl;

public class DemoConnectionPoolMonitorImpl extends Slf4jConnectionPoolMonitorImpl {
	@Override 
	public void incOperationFailure(com.netflix.astyanax.connectionpool.Host host, java.lang.Exception reason) {
		super.incOperationFailure(host, reason);
		reason.printStackTrace();
	 }
}
