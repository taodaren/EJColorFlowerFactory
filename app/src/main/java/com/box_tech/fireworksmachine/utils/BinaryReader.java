package com.box_tech.fireworksmachine.utils;


import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scc on 2018/3/13.
 * 二进制数据读取
 */

public class BinaryReader {
    private final InputStream mInputStream;
    public BinaryReader(@NonNull InputStream inputStream){
        mInputStream = inputStream;
    }

    public void skip(int n)throws IOException{
        if( mInputStream.skip(n) != n ){
            throw new IOException("skip failed");
        }
    }

    public int readUnsignedChar()throws IOException{
        return mInputStream.read();
    }

    public int readUnsignedShortLSB()throws IOException{
        int a = mInputStream.read();
        int b = mInputStream.read();
        return a|(b<<8);
    }

    public int readSignedShortLSB()throws IOException{
        int a = mInputStream.read();
        int b = mInputStream.read();
        int c = a|(b<<8);
        if( (c & 0x8000) != 0 ){
            c -= 0x10000;
        }
        return c;
    }

    public long readUnsignedIntLSB()throws IOException{
        long a = mInputStream.read();
        long b = mInputStream.read();
        long c = mInputStream.read();
        long d = mInputStream.read();
        return a|(b<<8)|(c<<16)|(d<<24);
    }
}
