package com.hoddmimes.distributor.auxillaries;

import java.net.InetAddress;

public class Application
{

    public static int getId( InetAddress pInetAddress) {
        long tPid = getPID();
        int tAddr = InetAddressConverter.inetAddrToInt( pInetAddress );
        int id = ((tAddr & 0xffff) << 16) + (int) (tPid & 0xffff);
        return id;
    }

    static private long getPID() {
        // With Java 9+ ProcessHandle.current().pid();
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            }
            catch (Exception e) {
                return System.nanoTime();
            }
        }
        return System.nanoTime();
    }

}
