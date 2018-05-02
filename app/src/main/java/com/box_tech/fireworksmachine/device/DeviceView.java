package com.box_tech.fireworksmachine.device;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.box_tech.fireworksmachine.R;
import com.lantouzi.wheelview.WheelView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by scc on 2018/3/5.
 * Device View
 */

public class DeviceView extends LinearLayout implements View.OnClickListener{
    private Device mDevice;
    private ISendCommand mSendCommand = null;

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
            ((TextView)root.findViewById(R.id.tv_supply_voltage)).setText("??.?");
            ((TextView)root.findViewById(R.id.tv_pitch)).setText("??");
            ((TextView)root.findViewById(R.id.tv_ultrasonic_distance)).setText("?.?");
            ((TextView)root.findViewById(R.id.tv_infrared_distance)).setText("?.?");
            ((TextView)root.findViewById(R.id.tv_rest_time)).setText("??");
        }
        else{
            ((TextView)root.findViewById(R.id.tv_supply_voltage)).setText(String.format(Locale.US,"%.1f", mDevice.state.mSupplyVoltage));
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

    private void setupClickListeners(View root){
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
    }

    private void onClickSetupView(int id, View editView){
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
                NumberPickerView[] etID = new NumberPickerView[]{
                    editView.findViewById(R.id.et_id_number_1),
                    editView.findViewById(R.id.et_id_number_2),
                    editView.findViewById(R.id.et_id_number_3),
                    editView.findViewById(R.id.et_id_number_4),
                    editView.findViewById(R.id.et_id_number_5),
                        editView.findViewById(R.id.et_id_number_6),
                };

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
