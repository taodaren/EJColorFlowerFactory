package com.box_tech.fireworksmachine;

import android.content.Context;

import com.box_tech.fireworksmachine.utils.Util;
import com.box_tech.sun_lcd.SunLcd;
import com.box_tech.sun_lcd.Zlib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

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
    public void zipp()throws Exception{
        final Context context = RuntimeEnvironment.application;
        byte[] b = SunLcd.makeBinary(context, "801234");
        byte[][] r = Zlib.createCompressedChunck(b);
        assertTrue(r != null);
    }
}
