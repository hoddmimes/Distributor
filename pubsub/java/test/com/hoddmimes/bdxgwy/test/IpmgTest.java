package com.hoddmimes.bdxgwy.test;

import com.hoddmimes.distributor.api.Ipmg;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

public class IpmgTest
{

    public static void main( String[] args ) {
        IpmgTest t = new IpmgTest();
        try {t.test();}
        catch(Exception e) { e.printStackTrace();}
    }


    private void test() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss.SSS");
        InetAddress tMcaAddr = InetAddress.getByName("224.42.42.100");
        Ipmg mca = new Ipmg( tMcaAddr, "eno1", 4242, 32676, 128 );
        ByteBuffer tBuffer = ByteBuffer.allocate(1024);

        while( true ) {
            ByteBuffer xtaBuf = ByteBuffer.allocate( 132 );
            String tStr = "XTA msg " + sdf.format(System.currentTimeMillis());
            xtaBuf.put(  tStr.getBytes() );
            mca.send( xtaBuf );
            System.out.println( tStr );
            SocketAddress tSockAddr = mca.receive(tBuffer);
            System.out.println( sdf.format( System.currentTimeMillis()) + "   BUFFER lim:" + tBuffer.limit() + " pos: " + tBuffer.position());
            try { Thread.sleep(1000L);}
            catch(InterruptedException e) {}
        }
    }
}
