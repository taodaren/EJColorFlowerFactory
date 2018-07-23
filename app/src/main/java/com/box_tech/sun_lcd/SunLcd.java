package com.box_tech.sun_lcd;

import android.support.annotation.NonNull;
import com.box_tech.fireworksmachine.utils.CRC16;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.LinkedHashMap;
import java.util.Map;

public class SunLcd {
    @NonNull
    public static byte[] makeQRCodeBitVector(String id) throws Exception{
        final String text = "http://60.205.226.109/index.php/index/api/add_device/device_id/"+id;
        final int n = 33;

        try{
            Map<EncodeHintType, Object> hints = new LinkedHashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.MAX_SIZE, n);
            hints.put(EncodeHintType.MIN_SIZE, n);
            BitMatrix bm = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, n, n, hints);
            if(bm.getWidth() != n){
                throw new Exception("生成设备ID的二维码图片失败");
            }

            final int data_len = (n*n+7)/8;
            byte[] r = new byte[1+data_len+2];
            r[0] = n;

            int i=0;
            for(int y=0;y<n;y++){
                for(int x=0;x<n;x++){
                    if(bm.get(x,y)){
                        r[1+i/8] |= (byte)(1<<(i%8));
                    }
                    i++;
                }
            }

            int v = CRC16.calculate(r, 1+data_len);
            r[1+data_len] = (byte)(v & 0xff);
            r[2+data_len] = (byte)(v >> 8);

            return r;
        }catch (WriterException e){
            e.printStackTrace();
            throw new Exception("生成设备ID的二维码图片失败");
        }
    }
}
