package com.hoddmimes.distributor.management;

import com.hoddmimes.distributor.generated.messages.DistributorMessagesFactory;
import com.hoddmimes.distributor.messaging.MessageInterface;
import com.hoddmimes.distributor.tcpip.TcpIpClient;
import com.hoddmimes.distributor.tcpip.TcpIpConnection;
import com.hoddmimes.distributor.tcpip.TcpIpConnectionCallbackInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TpsTransciev implements TcpIpConnectionCallbackInterface
{
    static final Logger cLogger = LogManager.getLogger( TpsTransciev.class.getSimpleName());
    String mHost;
    int mPort;
    MessageInterface mResponseMsg;

    TpsTransciev( String pHost, int pPort ) {
        mHost = pHost;
        mPort = pPort;
        mResponseMsg = null;
    }


    MessageInterface transciev( MessageInterface pRqstMsg ) throws IOException {
        TcpIpConnection tClient = null;
        synchronized (this) {
            tClient = TcpIpClient.connect(mHost, mPort, this);
            tClient.write(pRqstMsg.messageToBytes());
            try {
                this.wait(10000L);
            } catch (InterruptedException e) {}
        }
        if (tClient != null) {
            tClient.close();
        }
        return mResponseMsg;
    }

    @Override
    public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) {
        DistributorMessagesFactory tMsgFactory = new DistributorMessagesFactory();
        this.mResponseMsg = tMsgFactory.createMessage( pBuffer );
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void tcpipClientError(TcpIpConnection pConnection, IOException e) {
        cLogger.error(e);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
