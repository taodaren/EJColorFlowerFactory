package com.box_tech.fireworksmachine;


import android.net.Uri;
import com.box_tech.fireworksmachine.device.DeviceConfig;
import com.box_tech.fireworksmachine.device.DeviceState;
import com.box_tech.fireworksmachine.device.Protocol;
import com.box_tech.fireworksmachine.utils.Encryption;
import com.box_tech.fireworksmachine.utils.Util;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    @Test
    public void encryption_test() throws Exception {
        String data = "1234567812345678";
        String iv = "7701178393919486";

        String r = Encryption.encrypt(data, iv);
        String r2 = Encryption.decrypt(r, iv);
        System.out.println(data + " => "+r);
        assertEquals(r2, data);


        data = "aa";

        r = Encryption.encrypt(data, iv);
        r2 = Encryption.decrypt(r, iv);
        System.out.println(data + " => "+r);
        assertEquals(r2, data);
    }

    @Test
    public void base64_test()throws Exception{
        String data = "123";
        String r = Base64.encodeBase64URLSafeString(data.getBytes());
        System.out.println("r = " + r);
        String r2 = new String(Base64.decodeBase64(r));

        assertEquals(r2, data);
    }

    @Test
    public void uri_test()throws Exception{
        Uri uri = new Uri.Builder().encodedQuery("token=23ss3ww&member_id=3211").build();
        assertEquals(uri.getQueryParameter("token"), "23ss3ww");
        assertEquals(uri.getQueryParameter("member_id"), "3211");
        assertNull(uri.getQueryParameter("id"));
    }

    private final static int[] CipherBook = new int[]{
            30, 19, 3, 37, 17, 16, 35, 36, 16, 37, 22, 4, 42, 15, 21, 14, 29, 45, 20, 41, 38, 13,
            12, 5, 5, 40, 8, 1, 34, 7, 8, 2, 19, 13, 31, 27, 0, 20, 28, 24, 38, 36, 23, 10, 1, 14,
            43, 33, 13, 16, 15, 3, 37, 4, 25, 6, 40, 12, 5, 42, 25, 3, 31, 29, 18, 25, 39, 30, 24,
            47, 11, 47, 34, 22, 46, 8, 44, 39, 44, 21, 27, 9, 15, 2, 11, 28, 6, 19, 41, 21, 46, 45,
            39, 44, 31, 23, 18, 34, 33, 9, 24, 36, 23, 30, 17, 41, 22, 26, 9, 32, 6, 4, 26, 14, 18,
            40, 38, 11, 17, 12, 7, 35, 32, 27, 32, 2, 10, 0, 20, 0, 35, 45, 26, 1, 47, 33, 28, 46,
            42, 10, 43, 43, 29, 7
    };

    private void decrypt(int[] in, int len, int[] S, int[] out){
        int i,j,k;
        for(i=0;i<len;i++){
            int m = 0;
            for(j=0;j<8;j++){
                k = CipherBook[(i*8+j)%CipherBook.length];
                m |= (((S[k>>8] & (1<<(k&7)))!=0)?1:0)<<j;
            }

            //System.out.println(String.format("M=%02X i=%d", m, i));
            out[i] = in[i] ^ m;
        }
    }

    @Test
    public void message_decrypt_test()throws Exception{
        int[] message = new int[]{1,2,3,4,5,6,7,8,9};
        int[] S = new int[]{1,2,3,4,5,6};

        int[] encrypt_message = new int[message.length];
        int[] recover_message = new int[message.length];
        decrypt(message, message.length, S, encrypt_message );
        decrypt(encrypt_message, message.length, S, recover_message);
        System.out.println("encrypt "+ Util.hex(Util.int2byte(encrypt_message), encrypt_message.length));
        assertTrue(Arrays.equals(message, recover_message));

        Protocol protocol = new Protocol();
        protocol.onReceive(new byte[]{(byte)0xDC, (byte)encrypt_message.length});
        protocol.onReceive(Util.int2byte(S));
        protocol.onReceive(Util.int2byte(encrypt_message));
        assertTrue(Arrays.equals(Util.int2byte(message), protocol.getPkg()));

    }


    @Test
    public void message_decrypt2_test()throws Exception{
        Protocol protocol = new Protocol();
        byte[] message = Util.int2byte(new int[]{0xCD,0x0C,0x81,0x00,0x00,0x00,0x00,0x03,0xFF,0x78,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x14,0x04,0xA1,0xAD});
        byte[] encrypted_message = Util.int2byte(new int[]{
                0xDC,0x15,0x01,0x02,0x03,0x04,0x05,0x06,0xFD,0x1D,0x89,0x10,0x10,0x00,0x02,0x0B,0xFF,0x79,0x02,0x01,0x0A,0x3B,0x41,0x94,0x12,0x10,0x34,0xB0,0xA5});



        protocol.onReceive(encrypted_message);
        assertTrue(Arrays.equals(message, protocol.getPkg()));
    }

    @Test
    public void message_decrypt3_test()throws Exception{
        Protocol protocol = new Protocol();
        byte[] message = Protocol.command_package(1, 0, null);
        byte[] encrypted_message = Protocol.get_status_package(0);
        System.out.println("message "+ Util.hex(message, message.length));
        System.out.println("encrypt "+ Util.hex(encrypted_message, encrypted_message.length));
        protocol.onReceive(encrypted_message);
        byte[] recover = protocol.getPkg();
        System.out.println("recover "+ Util.hex(recover, recover.length));
        assertTrue(Arrays.equals(message, recover));
    }

    @Test
    public void parseState_test()throws Exception{
        byte[] pkg = Util.int2byte(new int[]{0xCD,0x0A,0x81,0x00,0x00,0x00,0x00,0xFF,0x03,0x13,0x00,0x00,0x00,0x00,0x00,0x14,0x05,0x1A,0x0B});
        DeviceState ds = Protocol.parseStatus(pkg, pkg.length);
        assertNotNull(ds);
        assertEquals(ds.mTemperature, 0x03ff);
        assertEquals(ds.mSupplyVoltage, (float)0x13/10, 1e-3);
    }

    @Test
    public void parseConfig_test()throws Exception{
        byte[] pkg = Util.int2byte(new int[]{0xCD,0x0E,0x82,0x00,0x00,0x00,0x00,0x04,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xD7,0x00,0x6D,0xCB});
        DeviceConfig config = Protocol.parseConfig(pkg, pkg.length);
        assertNotNull(config);
        assertEquals(config.mID, 260);
        assertEquals(config.mDMXAddress, 215);
    }

    @Test
    public void packageMatch_test() throws Exception{
        final long id = 0x12345678;
        byte[] cmd_pkg = Protocol.command_package(9, id, null);
        byte[] ack_pkg = Protocol.command_package(9, id, null);
        ack_pkg[2] = (byte)0x89;

        assertTrue(Protocol.isMatch(cmd_pkg, ack_pkg));

        cmd_pkg = Protocol.command_package(9, 0, null);
        assertTrue(Protocol.isMatch(cmd_pkg, ack_pkg));

        cmd_pkg = Protocol.command_package(9, 777, null);
        assertTrue( ! Protocol.isMatch(cmd_pkg, ack_pkg));
    }
}