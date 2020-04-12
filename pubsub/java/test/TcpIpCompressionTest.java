import com.hoddmimes.distributor.tcpip.*;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class TcpIpCompressionTest
{
    static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");


    public static void main( String[] args ) {
        Server srv = new Server();
        srv.startServer();

        Client clt = new Client();
        clt.startClient();

        while( true ) {
            try { Thread.sleep( 10000L ); }
            catch( InterruptedException e) {}
        }
    }


    static void log( String pMsg ) {
        System.out.println( sdf.format( System.currentTimeMillis()) + " " + pMsg );
        System.out.flush();
    }

    static class Client implements TcpIpConnectionCallbackInterface {

        Client() {

        }

        void startClient() {
            try {
                TcpIpConnection tClient = TcpIpClient.connect( TcpIpConnectionTypes.Compression, "127.0.0.1", 11234, this  );
                log("[Client] Successfully connected to server");
                sendMessage( tClient );
            }
            catch( IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessage( TcpIpConnection pClient ) {
            byte[] message = new byte[ 1024 ];
            for( int i = 0; i < message.length; i++ ) {
                message[i] = (byte) ((byte) 65 + (byte) ((i%25) & 0xff));
            }
            try {pClient.write( message, true);}
            catch( IOException e) { e.printStackTrace();}
        }

        @Override
        public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) {
            log("[Client] message received size: " + pBuffer.length +  " connection: " + pConnection );
            try{ Thread.sleep(2000L );}
            catch( InterruptedException e) {}
            sendMessage( pConnection );
        }

        @Override
        public void tcpipClientError(TcpIpConnection pConnection, IOException e) {
            log("[Client] client disconnected: " + pConnection );
            pConnection.close();
        }
    }

    static class Server implements TcpIpServerCallbackInterface {

        Server() {

        }

        void startServer() {
            try {
                TcpIpServer tServer = new TcpIpServer( TcpIpConnectionTypes.Compression, 11234, this  );
                log("[Server] Server successfully declared");
            }
            catch( IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void tcpipInboundConnection(TcpIpConnection pConnection) {
            log("[Server] Inbound connection: " + pConnection );
        }

        @Override
        public void tcpipMessageRead(TcpIpConnection pConnection, byte[] pBuffer) {
            log("[Server] msg rcv size: " + pBuffer.length +
                    " in comp rate: " + pConnection.getInputCompressionRate() + " out comp rate: " + pConnection.getOutputCompressionRate());
            try {pConnection.write( pBuffer, true);}
            catch( IOException e) { e.printStackTrace();}
        }

        @Override
        public void tcpipClientError(TcpIpConnection pConnection, IOException e) {
            log("[Server] client disconnected: " + pConnection + " reason: " + e.getMessage());
            pConnection.close();
        }
    }




}
