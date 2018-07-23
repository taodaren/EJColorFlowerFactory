package com.box_tech.fireworksmachine;

import android.content.Context;

import com.box_tech.fireworksmachine.utils.Util;
import com.box_tech.sun_lcd.SunLcd;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class SunLcdUnitTest {
    @Test
    public void makeBinaryTest()throws Exception{
        final Context context = RuntimeEnvironment.application;
        byte[] b = SunLcd.makeBinary(context, "801234");
        System.out.println(b.length);
        for(int i = 0; i < 256; i += 16){
            System.out.println(Util.hex(b,  i,i+16));
        }
    }

    @Test
    public void bitVectorUnitTest() throws Exception{
        final String id = "801234";
        final String text = "http://60.205.226.109/index.php/index/api/add_device/device_id/"+id;
        final int n = 33;

        Map<EncodeHintType, Object> hints = new LinkedHashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.MAX_SIZE, n);
        hints.put(EncodeHintType.MIN_SIZE, n);
        BitMatrix bm = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, n, n, hints);
        System.out.println("bm "+bm.getWidth()+","+bm.getHeight());
        System.out.println("ww "+ Arrays.toString(bm.getEnclosingRectangle()));
        for(int y=0;y<n;y++){
            for(int x=0;x<n;x++){
                boolean b = bm.get(x,y);
                System.out.print(b?"■":"  ");
            }
            System.out.print("\n");
        }
    }
}
