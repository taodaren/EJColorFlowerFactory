package com.box_tech.fireworksmachine.device;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.device.Server.BindDevice;
import com.box_tech.fireworksmachine.login.LoginSession;
import com.box_tech.fireworksmachine.utils.CRC16;
import com.box_tech.sun_lcd.SunLcd;
import com.box_tech.sun_lcd.Zlib;
import com.lantouzi.wheelview.WheelView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.box_tech.zhihuiyuan.zxing.activity.CaptureActivity;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by scc on 2018/3/5.
 * Device View
 */

public class DeviceView extends LinearLayout implements View.OnClickListener{
    private final static String TAG = "DeviceView";
    private Device mDevice;
    private ISendCommand mSendCommand = null;
    private Button mLCDButton = null;
    private long mIDByMac = 0;
    Handler handler;

    public DeviceView(Context context){
        super(context);
        initView(context);
    }
    public DeviceView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView(context);
    }
    public DeviceView(Context context, AttributeSet attrs, int defaultStyle){
        super(context, attrs, defaultStyle);
        initView(context);
    }

    private void initView(Context context){
        handler = new Handler();
        LayoutInflater.from(context).inflate(R.layout.device_view, this);
        setupClickListeners(this);
    }

    public void setDevice(Device device){
        mDevice = device;
        updateView(this);
    }

    private void updateView(View root){
        // 更新温度
        final String temperature, temperatureL, temperatureH;
        if(mDevice!=null && mDevice.state != null){
            temperature = String.format(Locale.US,"%d", mDevice.state.mTemperature);
        }
        else{
            temperature = "???";
        }
        if(mDevice!=null && mDevice.config != null){
            temperatureL = String.format(Locale.US,"%d", mDevice.config.mTemperatureThresholdLow);
            temperatureH = String.format(Locale.US,"%d", mDevice.config.mTemperatureThresholdHigh);
        }
        else{
            temperatureL = "???";
            temperatureH = "???";
        }
        ((TextView)root.findViewById(R.id.tv_temperature)).setText(String.format(Locale.US, "%s/%s~%s", temperature, temperatureL, temperatureH));

        // 更新电机1~4速度
        final int[] motor_id = new int[]{
                R.id.tv_motor_speed_1,
                R.id.tv_motor_speed_2,
                R.id.tv_motor_speed_3,
                R.id.tv_motor_speed_4};

        for(int i = 0; i<motor_id.length; i++ ){
            final String speed, default_speed;
            if(mDevice!=null && mDevice.state != null){
                speed = String.format(Locale.US,"%d", mDevice.state.mMotorSpeed[i]);
            }
            else{
                speed = "??";
            }
            if(mDevice!=null && mDevice.config != null){
                default_speed = String.format(Locale.US,"%d",
                        mDevice.config.mMotorDefaultSpeed[i]);
            }
            else{
                default_speed = "??";
            }

            ((TextView)root.findViewById(motor_id[i])).setText(
                    String.format(Locale.US, "%s/%s", speed, default_speed));
        }

        if(mDevice == null || mDevice.state ==null){
            ((TextView)root.findViewById(R.id.tv_supply_voltage)).setText("??");
            ((TextView)root.findViewById(R.id.tv_pitch)).setText("??");
            ((TextView)root.findViewById(R.id.tv_ultrasonic_distance)).setText("?.?");
            ((TextView)root.findViewById(R.id.tv_infrared_distance)).setText("?.?");
            ((TextView)root.findViewById(R.id.tv_rest_time)).setText("??");
        }
        else{
            ((TextView)root.findViewById(R.id.tv_supply_voltage)).setText(String.format(Locale.US,"%.0f", mDevice.state.mSupplyVoltage*10));
            ((TextView)root.findViewById(R.id.tv_pitch)).setText(String.format(Locale.US,"%d", mDevice.state.mPitch));
            ((TextView)root.findViewById(R.id.tv_ultrasonic_distance)).setText(String.format(Locale.US,"%.1f", mDevice.state.mUltrasonicDistance/10.0));
            ((TextView)root.findViewById(R.id.tv_infrared_distance)).setText(String.format(Locale.US,"%.1f", mDevice.state.mInfraredDistance/10.0));
            ((TextView)root.findViewById(R.id.tv_rest_time)).setText(mDevice.state.strOfRestTime());
        }

        if(mDevice == null || mDevice.config ==null){
            ((TextView)root.findViewById(R.id.tv_dmx_addr)).setText("???");
            ((TextView)root.findViewById(R.id.tv_device_id)).setText("?????");
            ((TextView)root.findViewById(R.id.tv_gualiao_time)).setText("??.?");
        }
        else{
            ((TextView)root.findViewById(R.id.tv_dmx_addr)).setText(String.format(Locale.US,"%d", mDevice.config.mDMXAddress));
            ((TextView)root.findViewById(R.id.tv_device_id)).setText(String.format(Locale.US,"%d", mDevice.config.mID));
            ((TextView)root.findViewById(R.id.tv_gualiao_time)).setText(String.format(Locale.US, "%.1f", mDevice.config.mGualiaoTime /10.0));
        }
    }

    private final static Map<Integer, Integer> mButton2Layout = new ArrayMap<>();
    static{
        mButton2Layout.put(R.id.bt_edit_temperature_threshold, R.layout.edit_temperature_threshold);
        mButton2Layout.put(R.id.bt_edit_motor_1_default_speed, R.layout.edit_motor_speed);
        mButton2Layout.put(R.id.bt_edit_motor_2_default_speed, R.layout.edit_motor_speed);
        mButton2Layout.put(R.id.bt_edit_motor_3_default_speed, R.layout.edit_motor_speed);
        mButton2Layout.put(R.id.bt_edit_motor_4_default_speed, R.layout.edit_motor_speed);
        mButton2Layout.put(R.id.bt_edit_dmx_addr, R.layout.edit_dmx);
        mButton2Layout.put(R.id.bt_edit_device_id, R.layout.edit_device_id);
        mButton2Layout.put(R.id.bt_jet, R.layout.edit_jet);
        mButton2Layout.put(R.id.bt_edit_gualiao_time, R.layout.edit_gualiao_time);
        mButton2Layout.put(R.id.bt_add_material, R.layout.edit_add_material);
    }

    private void startBindDevice(){
        LoginSession session = Settings.getLoginSessionInfo(getContext());
        final String mac = mDevice.getAddress();
        new BindDevice.Request(mac,  session.getToken(), null, new BindDevice.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull final BindDevice.Result result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long id = Integer.parseInt(result.getData());
                        mIDByMac = id;
                        Log.d(TAG, "设备ID为 "+id);
                        startSetDeviceID(id);
                    }
                });
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull final String message) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取ID失败:"+message, Toast.LENGTH_SHORT).show();
                        mLCDButton.setEnabled(false);
                    }
                });
            }
        }).run();
    }

    private void startSetDeviceID(long id){
        if( mDevice.getId() != id ){
            if(mSendCommand!=null ){
                mSendCommand.sendCommand(mDevice,
                        Protocol.set_id_package(mDevice.getId(), id));
            }
            mDevice.setId(id);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEnterLCDMode();
                }
            }, 200);
        }
        else{
            startEnterLCDMode();
        }
    }

    private void startEnterLCDMode(){
        if(mSendCommand!=null){
            final long device_id = mDevice.getConfig().mID;
            mSendCommand.sendCommand(mDevice,
                    Protocol.enter_lcd_mode_package(device_id), new OnReceivePackage() {
                        @Override
                        public void ack(@NonNull byte[] pkg) {
                            if( Protocol.parse_enter_lcd_mode_ack(pkg, pkg.length)){
                                startFlash_Step_Head();
                            }
                            else{
                                Log.e(TAG, "进入液晶屏失败");
                                mLCDButton.setEnabled(true);
                            }
                        }

                        @Override
                        public void timeout() {
                            Log.e(TAG, "进入液晶屏超时");
                            new CommandChain(){
                                @Override
                                void result(boolean sucess, int step) {
                                    if(sucess){
                                        Log.d(TAG, "已经在液晶屏模式");
                                        startFlash_Step_Head();
                                    }
                                    else{
                                        mLCDButton.setEnabled(true);
                                    }
                                }
                            }
                                    .add(Protocol.alloc_cach_package(1), true, "TRY ALLOC CACH")
                                    .start();
                        }
                    });
        }
    }

    private interface OnLcdCommandCallback{
        void result(boolean success);
    }

    private static class PackageInfo{
        byte[] pkg = null;
        boolean need_ack = false;
        int delay = 0;
        String description = "";
        boolean mark = false;
        int retry_count = 0;

        PackageInfo(){}
        PackageInfo(byte[] pkg, boolean need_ack, String description){
            this.pkg = pkg;
            this.need_ack = need_ack;
            this.description = description;
        }
        PackageInfo(byte[] pkg, boolean need_ack, String description, int retry_count){
            this.pkg = pkg;
            this.need_ack = need_ack;
            this.description = description;
            this.retry_count = retry_count;
        }
        PackageInfo(int delay){
            this.delay = delay;
            this.description = "延时 "+delay+" ms";
        }
    }

    private class CommandChain{
        private List<PackageInfo> packages = new LinkedList<>();
        private int cur_package = 0;
        private int mark_point = 0;
        CommandChain add(byte[] cmd, boolean need_ack, String description){
            packages.add(new PackageInfo(cmd,need_ack, description));
            return this;
        }
        CommandChain add(byte[] cmd, boolean need_ack, String description, int retry_count){
            packages.add(new PackageInfo(cmd,need_ack, description, retry_count));
            return this;
        }
        CommandChain delay(int delay){
            packages.add(new PackageInfo(delay));
            return this;
        }
        CommandChain setMark(){
            PackageInfo pi = new PackageInfo();
            pi.mark = true;
            pi.description = "mark";
            packages.add(pi);
            return this;
        }

        private void start_next(){
            if(cur_package+1<packages.size()){
                cur_package += 1;
                start();
            }
            else{
                result(true, cur_package);
            }
        }
        void start(){
            final PackageInfo pkg_info = packages.get(cur_package);
            if(pkg_info.mark){
                mark_point = cur_package;
                start_next();
                return;
            }
            if(pkg_info.pkg == null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start_next();
                    }
                }, pkg_info.delay);
                return;
            }

            if(mSendCommand!=null){
                if(pkg_info.need_ack){
                    Log.d(TAG, "["+cur_package+"]发送 " + pkg_info.description);
                    mSendCommand.sendCommand(mDevice, pkg_info.pkg, new OnReceivePackage() {
                        @Override
                        public void ack(@NonNull byte[] pkg) {
                            if( Protocol.parseLcdResult(pkg, pkg.length)){
                                start_next();
                            }
                            else{
                                Log.e(TAG, "["+cur_package+"]失败 " + pkg_info.description);
                                if(pkg_info.retry_count>0){
                                    pkg_info.retry_count -= 1;
                                    Log.d(TAG, "["+cur_package+"]重试 " + pkg_info.description + "==>"+mark_point);
                                    cur_package = mark_point;
                                    start_next();
                                }
                                else{
                                    result(false, cur_package);
                                }
                            }
                        }

                        @Override
                        public void timeout() {
                            Log.e(TAG, "["+cur_package+"]超时 " + pkg_info.description);
                            if(pkg_info.retry_count>0){
                                pkg_info.retry_count -= 1;
                                Log.d(TAG, "["+cur_package+"]重试 " + pkg_info.description + "==>"+mark_point);
                                cur_package = mark_point;
                                start_next();
                            }
                            else{
                                result(false, cur_package);
                            }
                        }
                    });
                }
                else{
                    mSendCommand.sendCommand(mDevice, pkg_info.pkg);
                    start_next();
                }
            }
            else{
                result(false, cur_package);
            }
        }
        void result(boolean success, int step){}
    }

    private static void addCommand(@NonNull byte[] b, int wait_time, boolean encrypted, String description, CommandChain commandChain){
        commandChain
                .setMark()
                .add(Protocol.alloc_cach_package(b.length), true, description + " :START", 3);

        for(int i=0;i<b.length;i+=16){
            int n = Math.min(b.length-i, 16);
            byte[] x = new byte[n];
            System.arraycopy(b, i, x, 0, n);
            commandChain.add(Protocol.recv_data_package(x), true, description + " :DATA["+i+"]"+n, 3);
        }
        commandChain
                .add(Protocol.cach_crc_package(CRC16.calculate(b, b.length)), true, description + " :CRC", 3);
        if(encrypted){
            commandChain.add(Protocol.extract_and_send_to_lcd_package(), true, description + " :EXTRACT_SEND");
        }
        else{
            commandChain.add(Protocol.cach_send_to_lcd_package(), true, description + " :SEND");
        }
        if(wait_time>0){
            commandChain.delay(wait_time);
        }
    }

    private void startflashLCD(final OnLcdCommandCallback callback){
        final int head_size = 42;

        byte[] mBinary = null;
        byte[][] mBinaryChunks = null;
        try{
            mBinary = SunLcd.makeBinary(getContext(), ""+mIDByMac);
        }catch (Exception e){
            e.printStackTrace();
            callback.result(false);
        }

        if(mBinary!=null){
            byte[] x = new byte[mBinary.length-head_size];
            System.arraycopy(mBinary, head_size, x, 0, x.length);
            mBinaryChunks = Zlib.createCompressedChunck(x);
        }

        if(mBinaryChunks==null){
            callback.result(false);
            return ;
        }

        CommandChain commandChain = new CommandChain(){
            @Override
            void result(boolean sucess, int step) {
                Log.d(TAG, "命令步骤 "+step + " success="+sucess);
                callback.result(sucess);
            }
        };

        String cmd = "FS_HEAD("+head_size+");\r\n";
        addCommand(cmd.getBytes(), 200, false, "FS_HEAD", commandChain);
        byte[] head = new byte[head_size];
        System.arraycopy(mBinary, 0, head, 0, head_size);
        addCommand(head, 500, false, "FS_HEAD_DATA", commandChain);
        cmd = "FS_DLOAD("+(mBinary.length-head_size)+");\r\n";
        addCommand(cmd.getBytes(), 500, false, "FS_DLOAD", commandChain);
        for(byte[] chunk : mBinaryChunks){
            addCommand(chunk, 0, true, "CHUNK "+chunk.length, commandChain);
        }

        commandChain.add(Protocol.exit_lcd_mode_package(), true, "EXIT LCD MODE");

        commandChain.start();
    }

    private void startFlash_Step_Head(){
        startflashLCD( new OnLcdCommandCallback() {
            @Override
            public void result(boolean success) {
                mLCDButton.setEnabled(true);
            }
        });
    }

    private void setupClickListeners(final View root){
        for(int id : mButton2Layout.keySet()){
            root.findViewById(id).setOnClickListener(this);
        }

        root.findViewById(R.id.bt_jet_stop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendCommand!=null){
                    final long device_id = mDevice.getConfig().mID;
                    mSendCommand.sendCommand(mDevice,
                            Protocol.jet_stop_package(device_id));
                }
            }
        });

        root.findViewById(R.id.bt_lcd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if( mDevice == null || mDevice.getConfig() == null || mSendCommand == null ){
                    Toast.makeText(v.getContext(), "暂时无法进入LCD模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLCDButton = (Button)v;
                v.setEnabled(false);
                startBindDevice();
            }
        });
    }

    private void onClickSetupView(int id, final View editView){
        switch (id){
            case R.id.bt_edit_temperature_threshold:
                WheelView etLow = editView.findViewById(R.id.et_temperature_threshold_low);
                WheelView etHeight = editView.findViewById(R.id.et_temperature_threshold_high);
                final List<String> values = new LinkedList<>();
                for(int t = 400; t <= 700; t ++ ){
                    values.add(String.valueOf(t));
                }
                etLow.setItems(values);
                etHeight.setItems(values);

                etLow.selectIndex(values.indexOf(String.valueOf(mDevice.getConfig().mTemperatureThresholdLow)));
                etHeight.selectIndex(values.indexOf(String.valueOf(mDevice.getConfig().mTemperatureThresholdHigh)));
                break;
            case R.id.bt_edit_motor_1_default_speed:
            case R.id.bt_edit_motor_2_default_speed:
            case R.id.bt_edit_motor_3_default_speed:
            case R.id.bt_edit_motor_4_default_speed:
                WheelView[] etMotor = new WheelView[]{
                        editView.findViewById(R.id.et_motor_1),
                        editView.findViewById(R.id.et_motor_2),
                        editView.findViewById(R.id.et_motor_3),
                        editView.findViewById(R.id.et_motor_4)
                };
                final List<String> speeds = new LinkedList<>();
                for(int t = 0; t <= 100; t ++ ){
                    speeds.add(String.valueOf(t));
                }
                for(int i=0;i<etMotor.length;i++){
                    etMotor[i].setItems(speeds);
                    etMotor[i].setMinSelectableIndex(1);
                    etMotor[i].setMaxSelectableIndex(speeds.size()-2);
                    etMotor[i].selectIndex(speeds.indexOf(String.valueOf(mDevice.getConfig().mMotorDefaultSpeed[i])));
                }
                break;
            case R.id.bt_edit_dmx_addr:
                WheelView etDMX = editView.findViewById(R.id.et_dmx);
                final List<String> addresses = new LinkedList<>();
                for(int a = 0; a <= 511; a ++ ){
                    addresses.add(String.valueOf(a));
                }
                etDMX.setItems(addresses);
                etDMX.selectIndex(addresses.indexOf(String.valueOf(mDevice.getConfig().mDMXAddress)));
                break;
            case R.id.bt_edit_device_id:
                final NumberPickerView[] etID = new NumberPickerView[]{
                    editView.findViewById(R.id.et_id_number_1),
                    editView.findViewById(R.id.et_id_number_2),
                    editView.findViewById(R.id.et_id_number_3),
                    editView.findViewById(R.id.et_id_number_4),
                    editView.findViewById(R.id.et_id_number_5),
                        editView.findViewById(R.id.et_id_number_6),
                };

                ImageButton btScanID = editView.findViewById(R.id.bt_scan_id);
                btScanID.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CaptureActivity.viewToFill = etID;
                        Intent intent = new Intent(editView.getContext(), CaptureActivity.class);
                        editView.getContext().startActivity(intent);
                    }
                });

                final String[] numbers = new String[10];
                for(int i=0;i<10;i++){
                    numbers[i] = String.valueOf(i);
                }

                long ID = mDevice.getConfig().mID;
                for(NumberPickerView et : etID){
                    et.setDisplayedValues(numbers);
                    et.setMinValue(0);
                    et.setMaxValue(numbers.length-1);
                    et.setValue((int)(ID%10));
                    ID /= 10;
                }
                break;
            case R.id.bt_jet:
                WheelView etDelay = editView.findViewById(R.id.et_jet_delay);
                NumberPickerView etDurationMin = editView.findViewById(R.id.et_jet_duration_min);
                NumberPickerView etDurationSec = editView.findViewById(R.id.et_jet_duration_sec);
                WheelView etHigh = editView.findViewById(R.id.et_jet_high);
                final List<String> delays = new ArrayList<>(500);
                for(int i=0;i<500;i++){
                    delays.add(String.valueOf(i*10));
                }
                etDelay.setItems(delays);

                final String[] durations_min = new String[11];
                for(int i=0;i<durations_min.length;i++){
                    durations_min[i] = String.valueOf(i);
                }
                etDurationMin.setDisplayedValues(durations_min);
                etDurationMin.setMinValue(0);
                etDurationMin.setMaxValue(durations_min.length-1);
                etDurationMin.setValue(2);

                final String[] durations_sec = new String[60];
                for(int i=0;i<durations_sec.length;i++){
                    durations_sec[i] = String.valueOf(i);
                }
                etDurationSec.setDisplayedValues(durations_sec);
                etDurationSec.setMinValue(0);
                etDurationSec.setMaxValue(durations_sec.length-1);
                etDurationSec.setValue(0);

                final List<String> high = new ArrayList<>(11);
                for(int i=0;i<=10;i++){
                    high.add(String.valueOf(i));
                }
                etHigh.setItems(high);
                etHigh.selectIndex(5);
                break;
            case R.id.bt_edit_gualiao_time:
                WheelView etGualiao = editView.findViewById(R.id.et_gualiao_time);
                final List<String> gualiao_values = new LinkedList<>();
                for(int t = 0; t <= 100; t ++ ){
                    gualiao_values.add(String.format(Locale.US, "%.1f", t/10.0));
                }
                etGualiao.setItems(gualiao_values);
                etGualiao.selectIndex(mDevice.getConfig().mGualiaoTime);
                break;
            case R.id.bt_add_material:
                NumberPickerView etMaterialTime = editView.findViewById(R.id.et_material_time);
                final String[] material_time_list = new String[10];
                for(int i=0;i<material_time_list.length;i++){
                    material_time_list[i] = String.valueOf(i+1);
                }
                etMaterialTime.setDisplayedValues(material_time_list);
                etMaterialTime.setMinValue(0);
                etMaterialTime.setMaxValue(material_time_list.length-1);
                etMaterialTime.setValue(4);
                break;
        }
    }

    private void onSetValue(int id, final View editView){
        if(mDevice==null){
            return;
        }
        final WeakReference<Context> context = new WeakReference<>(editView.getContext());
        final long device_id = mDevice.getConfig().mID;
        switch (id){
            case R.id.bt_edit_temperature_threshold:
                WheelView etLow = editView.findViewById(R.id.et_temperature_threshold_low);
                WheelView etHigh = editView.findViewById(R.id.et_temperature_threshold_high);
                int low = Integer.parseInt(etLow.getItems().get(etLow.getSelectedPosition()));
                int high = Integer.parseInt(etHigh.getItems().get(etHigh.getSelectedPosition()));
                if(low>high){
                    int t = low;
                    low = high;
                    high = t;
                }
                byte[] pkg = Protocol.set_temperature_threshold_package(device_id, low, high);
                if(mSendCommand!=null) mSendCommand.sendCommand(mDevice, pkg);
                break;
            case R.id.bt_edit_motor_1_default_speed:
            case R.id.bt_edit_motor_2_default_speed:
            case R.id.bt_edit_motor_3_default_speed:
            case R.id.bt_edit_motor_4_default_speed:
                WheelView[] etMotor = new WheelView[]{
                        editView.findViewById(R.id.et_motor_1),
                        editView.findViewById(R.id.et_motor_2),
                        editView.findViewById(R.id.et_motor_3),
                        editView.findViewById(R.id.et_motor_4)
                };
                int[] speeds = new int[etMotor.length];
                for(int i=0;i<speeds.length;i++){
                    speeds[i] = Integer.parseInt(
                            etMotor[i].getItems().get(etMotor[i].getSelectedPosition()));
                }
                if(mSendCommand!=null){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.set_motor_default_speed_package(device_id, speeds));
                }
                break;
            case R.id.bt_edit_dmx_addr:
                WheelView etDMX = editView.findViewById(R.id.et_dmx);
                int address = Integer.parseInt(
                        etDMX.getItems().get(etDMX.getSelectedPosition()));
                if(mSendCommand!=null){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.set_dmx_address_package(device_id, address));
                }
                break;
            case R.id.bt_edit_device_id:
                NumberPickerView[] etID = new NumberPickerView[]{
                        editView.findViewById(R.id.et_id_number_1),
                        editView.findViewById(R.id.et_id_number_2),
                        editView.findViewById(R.id.et_id_number_3),
                        editView.findViewById(R.id.et_id_number_4),
                        editView.findViewById(R.id.et_id_number_5),
                        editView.findViewById(R.id.et_id_number_6),
                };
                long new_id = 0;
                long f = 1;
                for(NumberPickerView et : etID){
                    new_id += f * et.getValue();
                    f *= 10;
                }
                if(mSendCommand!=null && device_id != new_id){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.set_id_package(device_id, new_id));
                }
                break;
            case R.id.bt_jet:
                WheelView etDelay = editView.findViewById(R.id.et_jet_delay);
                NumberPickerView etDurationMin = editView.findViewById(R.id.et_jet_duration_min);
                NumberPickerView etDurationSec = editView.findViewById(R.id.et_jet_duration_sec);
                WheelView etHigh2 = editView.findViewById(R.id.et_jet_high);

                final int delay = Integer.parseInt(etDelay.getItems().get(etDelay.getSelectedPosition()));
                final int durationMin = etDurationMin.getValue();
                final int durationSec = etDurationSec.getValue();
                final int high2 = 10*Integer.parseInt(etHigh2.getItems().get(etHigh2.getSelectedPosition()));
                final int duration = 60*durationMin+durationSec;

                if(mSendCommand!=null){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.jet_package(device_id, delay, 10*duration, high2));
                }
                break;
            case R.id.bt_edit_gualiao_time:
                WheelView etGualiao = editView.findViewById(R.id.et_gualiao_time);
                final int gualiao = etGualiao.getSelectedPosition();
                if(mSendCommand!=null){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.set_gualiao_time_package(device_id, gualiao));
                }
                break;
            case R.id.bt_add_material:
                NumberPickerView etMaterialTime = editView.findViewById(R.id.et_material_time);
                final int materialTime = (etMaterialTime.getValue()+1)*60;
                if(mSendCommand!=null){
                    mSendCommand.sendCommand(mDevice,
                            Protocol.get_timestamp_package(device_id), new OnReceivePackage() {
                                @Override
                                public void ack(@NonNull byte[] pkg) {
                                    long stamp = Protocol.parseTimestamp(pkg, pkg.length);
                                    if(stamp>=0){
                                        mSendCommand.sendCommand(mDevice, Protocol.add_material(device_id, materialTime, stamp ));
                                    }
                                }

                                @Override
                                public void timeout() {
                                    Context c = context.get();
                                    if(c!=null){
                                        Toast.makeText(c, "操作超时", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    void setOnSendCommand(ISendCommand impl){
        mSendCommand = impl;
    }

    @Override
    public void onClick(View view){
        if( mDevice == null || mDevice.getConfig() == null || mSendCommand == null ){
            Toast.makeText(view.getContext(), "暂时无法编辑", Toast.LENGTH_SHORT).show();
            return;
        }
        final int id = view.getId();
        final View editView = View.inflate(view.getContext(), mButton2Layout.get(id), null);
        onClickSetupView(id, editView);
        new AlertDialog.Builder(view.getContext())
                .setView(editView)
                .setTitle("设置值")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSetValue(id, editView);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
