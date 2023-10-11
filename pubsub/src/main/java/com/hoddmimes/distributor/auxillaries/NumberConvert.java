package com.hoddmimes.distributor.auxillaries;

public class NumberConvert
{
    public static byte[] short2Bytes(short value) {
        return new byte[] {
                (byte)(value),
                (byte) (value >> 8)};
    }

    public static byte[] int2Bytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >>  8),
                (byte) (value >>  0) };
    }

    public static byte[] int2BytesSwap(int value) {
        return new byte[] {
                (byte) (value >> 0),
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24) };
    }


    public static byte[] long2Bytes(long value) {
        return new byte[] {
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56)};
    }

    public static byte[] double2Bytes(double dblValue) {
        long tValue = Double.doubleToRawLongBits(dblValue);
        return long2Bytes( tValue );
    }

    public static long bytes2Long( byte[] b ) {
        long value = ((long) b[7] << 56)
                | ((long) b[6] & 0xff) << 48
                | ((long) b[5] & 0xff) << 40
                | ((long) b[4] & 0xff) << 32
                | ((long) b[3] & 0xff) << 24
                | ((long) b[2] & 0xff) << 16
                | ((long) b[1] & 0xff) << 8
                | ((long) b[0] & 0xff);
        return value;
    }

    public static int bytes2Int( byte[] bytes ) {
        int value = 0;
        value += (bytes[0] & 0x000000FF) << 24 ;
        value += (bytes[1] & 0x000000FF) << 16;
        value += (bytes[2] & 0x000000FF) << 8;
        value += (bytes[3] & 0x000000FF) << 0;
        return value;
    }

    public static short bytes2Short( byte[] bytes ) {
        short value = 0;
        value += (bytes[0] & 0x000000FF);
        value += (bytes[1] & 0x000000FF) << 8;
        return value;
    }




    public static double bytes2Double(byte[] data) {
        long tValue = bytes2Long( data );
        return Double.longBitsToDouble(tValue);
    }


    public static void main( String[] args ) {
        byte[] b;
        int x;


       b = int2Bytes( 11223344);
       x = bytes2Int(b);


        b = short2Bytes( (short) 27271);
        if (bytes2Short(b) != 27271) {
            System.out.println("short failed");
        }

        b = int2Bytes( 237132 );
        if (bytes2Int(b) != 237132) {
            System.out.println("int failed");
        }

        b = long2Bytes( 4661902331959473445L );
        if (bytes2Long(b) != 4661902331959473445L) {
            System.out.println("Long failed");
        }

        b = double2Bytes( 4711.4711d );
        if (bytes2Double(b) != 4711.4711d) {
            System.out.println("Double failed");
        }
        System.out.println("[All done]");
    }
}



