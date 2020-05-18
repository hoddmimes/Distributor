package com.hoddmimes.distributor.auxillaries;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InetAddressConverter
{
    private static Pattern IP_Address_Pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+)");

    public static InetAddress stringToInetAddr( String pAddrStr ) {
        if (pAddrStr == null) {
            return null;
        }
        Matcher m = IP_Address_Pattern.matcher( pAddrStr);
        if (!m.matches()) {
            throw new RuntimeException("Inavlid Inet address \"" + pAddrStr +"\"");
        }
        try {
        return  InetAddress.getByName( pAddrStr);
        }
        catch( Exception e) {
            new RuntimeException( e);
        }
        return null;
    }

    public static int inetAddrToInt( InetAddress  pInetAddr ) {
        return arrToInt(pInetAddr.getAddress());
    }

    public static InetAddress intToInetAddr( int pValue ) {
        try {return InetAddress.getByAddress( intToArr( pValue ));}
        catch( Exception e) {
            throw new RuntimeException( e );
        }
    }

    public static String intToAddrString( int pValue ) {
        return intToInetAddr( pValue ).getHostAddress().toString();
    }

    private static byte[] intToArr(int pValue) {
        byte arr[] = new byte[4];
        arr[0] = (byte) ((pValue >> 24) & 0xff);
        arr[1] = (byte) ((pValue >> 16) & 0xff);
        arr[2] = (byte) ((pValue >> 8) & 0xff);
        arr[3] = (byte) (pValue & 0xff);
        return arr;
    }

    private static int arrToInt(byte[] arr) {
        return  ((arr[0] & 0xff) << 24) +
                ((arr[1] & 0xff) << 16) +
                ((arr[2] & 0xff) << 8) +
                (arr[3] & 0xff);
    }


    public static void main( String args[] ) {
        InetAddress tInetAddr = null;

        try {tInetAddr = InetAddress.getByName("192.168.42.100");}
        catch( UnknownHostException e) {e.printStackTrace();}

        System.out.println( stringToInetAddr("192.168.42.100").getHostAddress().toString());
        System.out.println( Integer.toHexString(inetAddrToInt(tInetAddr)));
        System.out.println( intToInetAddr( 0xc0a82a64 ).getHostAddress().toString());
        System.out.println( intToAddrString( 0xc0a82a64 ));
    }
}
