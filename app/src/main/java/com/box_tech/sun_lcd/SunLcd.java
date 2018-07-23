package com.box_tech.sun_lcd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.box_tech.fireworksmachine.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cn.box_tech.zhihuiyuan.zxing.BarcodeEncoder;

public class SunLcd {
    private final static String TAG = "SunLcd";
    private static int _bitmap565bytes(@NonNull Bitmap[] bitmaps){
        int r = 0;
        for(Bitmap b : bitmaps){
            // System.out.println("w h "+b.getWidth()+","+b.getHeight());
            r += b.getWidth()*b.getHeight();
        }
        return r * 2;
    }

    @NonNull
    private static Bitmap makeQRCodeBitmap(String id) throws Exception{
        final String text = "http://60.205.226.109/index.php/index/api/add_device/device_id/"+id;

        try{
            BitMatrix bm = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 140, 140, null);
            return new BarcodeEncoder().createBitmap(bm);
        }catch (WriterException e){
            e.printStackTrace();
            throw new Exception("生成设备ID的二维码图片失败");
        }
    }

    private static final int[] lcd_ids = new int[]{
            R.raw.lcd1,
            R.raw.lcd2,
            R.raw.lcd3,
            R.raw.lcd4,
            R.raw.lcd5,
            R.raw.lcd6,
            R.raw.lcd7,
            R.raw.lcd8
    };

    @NonNull
    public static byte[] makeBinary(@NonNull Context context, @NonNull String id) throws Exception{
        Bitmap[] bitmaps = new Bitmap[9];
        for(int i=0;i<lcd_ids.length;i++){
            Bitmap b = BitmapFactory.decodeStream (context.getResources().openRawResource(lcd_ids[i]));
            bitmaps[i] = b;// b.copy(Bitmap.Config.ARGB_8888, true);
        }
        bitmaps[8] = makeQRCodeBitmap(id);

        return makeBinary(bitmaps);
    }

    @NonNull
    static byte[] makeBinary(@NonNull Bitmap[] bitmaps){
        return makeBinary(bitmaps, 0x000351FC);
    }

    @NonNull
    static byte[] makeBinary(@NonNull Bitmap[] bitmaps, long address){
        final int n = bitmaps.length;
        final int buffer_size = 6+4*n + _bitmap565bytes(bitmaps);
        Log.d(TAG, "buffer size = " + buffer_size + " " + n);
        byte[] buffer = new byte[buffer_size];
        buffer[0] = (byte)(address >> 24);
        buffer[1] = (byte)((address >> 16) & 0xff);
        buffer[2] = (byte)((address >> 8) & 0xff);
        buffer[3] = (byte)((address) & 0xff);
        buffer[4] = (byte)(n >> 8);
        buffer[5] = (byte)(n & 0xff);
        for(int i=0;i<n;i++){
            Bitmap b = bitmaps[i];
            buffer[6+i*4] = (byte)(b.getWidth() >> 8);
            buffer[7+i*4] = (byte)(b.getWidth() & 0xff);
            buffer[8+i*4] = (byte)(b.getHeight() >> 8);
            buffer[9+i*4] = (byte)(b.getHeight() & 0xff);
        }

        int idx = 6+4*n;
        for(Bitmap b : bitmaps){
            int[] pixels = new int[b.getWidth()*b.getHeight()];
            // 在测试模式下，无法正常工作
            b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            for(int h = 0; h < b.getHeight(); h++){
                for(int w = 0; w < b.getWidth(); w++){
                    //int rgb = b.getPixel(w, h);
                    int rgb = pixels[w+h*b.getWidth()];
                    int R = ((rgb >> 16) & 0xff)>>3;
                    int G = ((rgb >>  8) & 0xff)>>2;
                    int B = ((rgb      ) & 0xff)>>3;
                    int rgb565 = (R<<11)|(G<<5)|B;
                    buffer[idx] = (byte)(rgb565>>8);
                    buffer[idx+1] = (byte)(rgb565&0xff);
                    idx += 2;
                }
            }
        }

        return buffer;
    }
}
