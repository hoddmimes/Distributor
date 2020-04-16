import java.net.InetAddress;

public class InetAddrTest
{

    public static void main( String[] arg ) {
        InetAddrTest t = new InetAddrTest();
        t.test();
    }

    private static int inetAddrToInt( InetAddress addr)
    {
        byte[] ba = addr.getAddress();
        int tIntAddr = 0;
        for( int i = 0; i < Integer.BYTES; i++) {
            tIntAddr += (int) ((ba[i] & 0xFF) << (Byte.SIZE * i));
        }
        return tIntAddr;
    }

    private static InetAddress intToInetAddr( int pIntAddr )
    {
        byte[] ba = new byte[4];
        for( int i = 0; i < Integer.BYTES; i++) {
            ba[i] = (byte) ((pIntAddr >> (Byte.SIZE * i)) & 0xFF);
        }
        InetAddress tInetAddr = null;
        try { tInetAddr = InetAddress.getByAddress( ba );}
        catch( Exception e) { e.printStackTrace(); };
        return tInetAddr;
    }



    private void test() {
        try {
            InetAddress tInetAddr = InetAddress.getByName("192.168.1.38");
            int tAddr = inetAddrToInt( tInetAddr );
            tInetAddr = intToInetAddr( tAddr );


        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
