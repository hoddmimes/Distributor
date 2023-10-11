package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.DistributorException;
import com.hoddmimes.distributor.api.Ipmg;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TpsIpmc extends Thread {
    public interface TpsIpmcCallback {
        void ipmgReadComplete(byte[] data, SocketAddress pSourceAddress);

        void ipmgError(Exception e);
    }

    private Ipmg ipmg;
    private TpsIpmcCallback mCallbacks;
    private volatile boolean mTimeToDie = false;

    public TpsIpmc(String mc_address, int mc_port, String network_interface, TpsIpmcCallback pCallbacks) throws DistributorException {
        this.mCallbacks = pCallbacks;
        try {ipmg = new Ipmg(InetAddress.getByName(mc_address), network_interface, mc_port, 16384, 1);}
        catch( Exception e) {
            throw new DistributorException(e);
        }
        mTimeToDie = false;
        this.start();
    }

    public synchronized void publish(String pString) throws Exception {
        publish(pString.getBytes(StandardCharsets.UTF_8));
    }

    public synchronized void publish(byte[] pBytes) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(pBytes);
        bb.position(pBytes.length);
        this.ipmg.send(bb);
    }

    public void close() {
        mTimeToDie = true;
        this.interrupt();
    }

    @Override
    public void run() {

        while (!mTimeToDie) {
            try {
                ByteBuffer bb = ByteBuffer.allocate(512);
                InetSocketAddress tAddr = (InetSocketAddress) ipmg.receive(bb);
                byte[] tBuffer = new byte[bb.position()];
                System.arraycopy(bb.array(), 0, tBuffer, 0, bb.position());
                mCallbacks.ipmgReadComplete(tBuffer, tAddr);
            } catch (Exception e) {
                if ((e instanceof InterruptedException) && (mTimeToDie)) {
                    return;
                } else {
                    mCallbacks.ipmgError(e);
                }
            }
        }
    }
}

