package com.box_tech.sun_lcd;

public class Zlib {
    static {
        System.loadLibrary("zlib-sunlcd");
    }
    private static final int BLOCK_SIZE = 1024;
    private static native byte[] compress(byte[] data, int start, int len);
    public static byte[][] createCompressedChunck(byte[] data){
        final int n = (data.length+BLOCK_SIZE-1)/BLOCK_SIZE;
        byte[][] r = new byte[n][];
        for(int i=0;i<n;i++){
            int len = Math.min(data.length-i*BLOCK_SIZE, BLOCK_SIZE);
            byte[] zipped = compress(data, i*BLOCK_SIZE, len);
            if(zipped==null){
                return null;
            }
            r[i] = zipped;
        }
        return r;
    }
}
