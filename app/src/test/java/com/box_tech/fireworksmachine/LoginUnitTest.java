package com.box_tech.fireworksmachine;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.box_tech.fireworksmachine.login.Server.ResetPassword;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;
import com.box_tech.fireworksmachine.login.Server.Login;
import com.box_tech.fireworksmachine.login.Server.Register;
import com.box_tech.fireworksmachine.login.Server.GetVerifyCode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by scc on 2018/3/15.
 * 登录代码测试
 */

@RunWith(RobolectricTestRunner.class)
public class LoginUnitTest {

    private String login_test(String phoneNumber, String password){
        final Thread testThread = Thread.currentThread();
        final String[] ret = new String[1];
        new Login.Request(phoneNumber, password, null, new GeneralRequest.OnFinished<Login.Result>(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull Login.Result result) {
                System.out.println("login OK "+result.getData().getToken()+" member_id "+result.getData().getMember_id());
                testThread.interrupt();
                ret[0] = "OK";
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("login Failed "+message);
                testThread.interrupt();
                ret[0] = message;
            }
        }).run();

        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            System.out.println("test end" );
        }
        return ret[0];
    }

    @Test
    public void login_test()throws Exception{
        assertEquals(login_test("13810932887", "12345678"), "OK");
        assertEquals(login_test("138109327", "123"), "手机号码或密码不正确");
    }


    @Test
    public void register_test()throws Exception{
        new Register.Request("13810932887", "12345678", "12345678", "8963",null,
                new GeneralRequest.OnFinished<GeneralResult>(){
                    @Override
                    public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                        System.out.println("register  "+result.getMessage());
                    }

                    @Override
                    public void onFailed(@Nullable Activity activity, @NonNull String message) {
                        System.out.println("register Failed "+message);
                    }
                }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    public void find_password_test()throws Exception{
        new ResetPassword.Request("13810932887", "123", "123", "8963",null,
            new GeneralRequest.OnFinished<GeneralResult>(){
                @Override
                public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                    System.out.println("register "+result.getMessage());
                }

                @Override
                public void onFailed(@Nullable Activity activity, @NonNull String message) {
                    System.out.println("register Failed "+message);
                }
            }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    public void send_message_test()throws Exception{
        new GetVerifyCode.Request("13810932887", null,
                new GeneralRequest.OnFinished<GeneralResult>() {
                    @Override
                    public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {

                        System.out.println("send message OK ");
                    }

                    @Override
                    public void onFailed(@Nullable Activity activity, @NonNull String message) {

                        System.out.println("send message Failed "+message);
                    }
                }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
