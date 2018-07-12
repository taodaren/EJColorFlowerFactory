package com.box_tech.fireworksmachine;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.box_tech.fireworksmachine.device.Server.AddDevice;
import com.box_tech.fireworksmachine.device.Server.AddDeviceToGroup;
import com.box_tech.fireworksmachine.device.Server.AddGroup;
import com.box_tech.fireworksmachine.device.Server.BindDevice;
import com.box_tech.fireworksmachine.device.Server.GetDeviceList;
import com.box_tech.fireworksmachine.device.Server.GetGroupList;
import com.box_tech.fireworksmachine.device.Server.GetUngroupedDeviceList;
import com.box_tech.fireworksmachine.device.Server.GoEditDeviceToGroup;
import com.box_tech.fireworksmachine.device.Server.RemoveDevice;
import com.box_tech.fireworksmachine.device.Server.RemoveGroup;
import com.box_tech.fireworksmachine.device.Server.RenameGroup;
import com.box_tech.fireworksmachine.login.Server.Login;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.GeneralResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;



/**
 * Created by scc on 2018/3/15.
 * 测试设备管理服务器接口
 */

@RunWith(RobolectricTestRunner.class)
public class DeviceServerUnitTest {
    private long mMemberID = 0;
    private String mToken;

    private void login() throws Exception{
        String phoneNumber ="13810932887";
        String password ="1234";

        final Thread testThread = Thread.currentThread();
        final long[] ret = new long[1];
        final String[] token = new String[1];

        new Login.Request(phoneNumber, password, null, new GeneralRequest.OnFinished<Login.Result>(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull Login.Result result) {
                if(result.getCode()== GeneralResult.RESULT_OK){
                    System.out.println("login OK "+result.getData().getToken()+" member_id "+result.getData().getMember_id());
                    ret[0] = result.getData().getMember_id();
                    token[0] = result.getData().getToken();
                }
                else{
                    System.out.println("login Failed "+result.getMessage());
                }
                testThread.interrupt();
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("login Failed "+message);
                testThread.interrupt();
            }
        }).run();

        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            System.out.println("test end" );
        }

        if(ret[0]==0){
            throw new Exception("登入失败");
        }

        mMemberID = ret[0];
        mToken = token[0];
    }

    @Test
    public void get_device_list_test() throws Exception{
        login();
        new GetDeviceList.Request(mMemberID,  mToken,null, new GetDeviceList.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GetDeviceList.Result result) {
                System.out.println("get device list "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("get device list "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void add_device_test()throws Exception{
        login();
        new AddDevice.Request(mMemberID,  mToken, 810303, null, new AddDevice.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add device "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add device "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void remove_device_test()throws Exception{
        login();
        new RemoveDevice.Request(mMemberID,  mToken, 810303, null, new RemoveDevice.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add device "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add device "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void add_group_test()throws Exception{
        login();
        new AddGroup.Request(mMemberID,  mToken, "g1", null, new AddGroup.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add group "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add group "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void rename_group_test()throws Exception{
        login();
        new RenameGroup.Request(66, "g2", null, new RenameGroup.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add group "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add group "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Test
    public void remove_group_test()throws Exception{
        login();
        new RemoveGroup.Request(mMemberID,  mToken, 56, null, new RemoveGroup.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add device "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add device "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void get_group_list_test()throws Exception{
        login();
        new GetGroupList.Request(mMemberID,  mToken, null, new GetGroupList.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GetGroupList.Result result) {
                System.out.println("get group list "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("get group list "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void get_ungrouped_device_list_test()throws Exception{
        login();
        new GetUngroupedDeviceList.Request(mMemberID,  mToken, null, new GetUngroupedDeviceList.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GetUngroupedDeviceList.Result result) {
                System.out.println("get group list "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("get group list "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void add_device_to_group_test()throws Exception{
        login();
        new AddDeviceToGroup.Request(mMemberID,  mToken, 810252, 3, null, new AddDeviceToGroup.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add group "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add group "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }


    @Test
    public void go_edit_device_to_group_test()throws Exception{
        login();
        new GoEditDeviceToGroup.Request(mMemberID,  mToken,  3, null, new GoEditDeviceToGroup.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GoEditDeviceToGroup.Result result) {
                System.out.println("add group "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add group "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    @Test
    public void bind_device_test()throws Exception{
        login();
        String mac = "00:00:00:00:00:01";
        new BindDevice.Request(mac,  mToken, null, new AddDevice.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                System.out.println("add device "+result.getMessage());
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                System.out.println("add device "+message);
            }
        }).run();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
