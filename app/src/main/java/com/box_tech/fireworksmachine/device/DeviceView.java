package com.box_tech.fireworksmachine.device;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Device View
 */

public class DeviceView extends LinearLayout implements View.OnClickListener {
    private Device mDevice;
    private ISendCommand mSendCommand = null;
    Handler handler;

    public DeviceView(Context context) {
        super(context);
        initView(context);
    }

    public DeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DeviceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initView(context);
    }

    private void initView(Context context) {
        handler = new Handler();
        LayoutInflater.from(context).inflate(R.layout.device_view, this);
        setupClickListeners(this);
    }

    public void setDevice(Device device) {
        mDevice = device;
        updateView(this);
    }

    private void updateView(View root) {
        // 更新温度
        final String temperature, temperatureL, temperatureH;
        if (mDevice != null && mDevice.state != null) {
            temperature = String.format(Locale.US, "%d", mDevice.state.mTemperature);
        } else {
            temperature = "???";
        }
        if (mDevice != null && mDevice.config != null) {
            temperatureL = String.format(Locale.US, "%d", mDevice.config.mTemperatureThresholdLow);
            temperatureH = String.format(Locale.US, "%d", mDevice.config.mTemperatureThresholdHigh);
        } else {
            temperatureL = "???";
            temperatureH = "???";
        }
        ((TextView) root.findViewById(R.id.tv_temperature)).setText(String.format(Locale.US, "%s/%s~%s", temperature, temperatureL, temperatureH));

        // 更新电机1~4速度
        final int[] motor_id = new int[]{
                R.id.tv_motor_speed_1,
                R.id.tv_motor_speed_2,
                R.id.tv_motor_speed_3,
                R.id.tv_motor_speed_4};

        for (int i = 0; i < motor_id.length; i++) {
            final String speed, default_speed;
            if (mDevice != null && mDevice.state != null) {
                speed = String.format(Locale.US, "%d", mDevice.state.mMotorSpeed[i]);
            } else {
                speed = "??";
            }
            if (mDevice != null && mDevice.config != null) {
                default_speed = String.format(Locale.US, "%d",
                        mDevice.config.mMotorDefaultSpeed[i]);
            } else {
                default_speed = "??";
            }

            ((TextView) root.findViewById(motor_id[i])).setText(
                    String.format(Locale.US, "%s/%s", speed, default_speed));
        }

        if (mDevice == null || mDevice.state == null) {
            ((TextView) root.findViewById(R.id.tv_supply_voltage)).setText("??");
            ((TextView) root.findViewById(R.id.tv_pitch)).setText("??");
            ((TextView) root.findViewById(R.id.tv_ultrasonic_distance)).setText("?.?");
            ((TextView) root.findViewById(R.id.tv_infrared_distance)).setText("?.?");
            ((TextView) root.findViewById(R.id.tv_rest_time)).setText("??");
        } else {
            ((TextView) root.findViewById(R.id.tv_supply_voltage)).setText(String.format(Locale.US, "%.0f", mDevice.state.mSupplyVoltage * 10));
            ((TextView) root.findViewById(R.id.tv_pitch)).setText(String.format(Locale.US, "%d", mDevice.state.mPitch));
            ((TextView) root.findViewById(R.id.tv_ultrasonic_distance)).setText(String.format(Locale.US, "%.1f", mDevice.state.mUltrasonicDistance / 10.0));
            ((TextView) root.findViewById(R.id.tv_infrared_distance)).setText(String.format(Locale.US, "%.1f", mDevice.state.mInfraredDistance / 10.0));
            ((TextView) root.findViewById(R.id.tv_rest_time)).setText(mDevice.state.strOfRestTime());
        }

        if (mDevice == null || mDevice.config == null) {
            ((TextView) root.findViewById(R.id.tv_dmx_addr)).setText("???");
            ((TextView) root.findViewById(R.id.tv_device_id)).setText("?????");
            ((TextView) root.findViewById(R.id.tv_gualiao_time)).setText("??.?");
        } else {
            ((TextView) root.findViewById(R.id.tv_dmx_addr)).setText(String.format(Locale.US, "%d", mDevice.config.mDMXAddress));
            ((TextView) root.findViewById(R.id.tv_device_id)).setText(String.format(Locale.US, "%d", mDevice.config.mID));
            ((TextView) root.findViewById(R.id.tv_gualiao_time)).setText(String.format(Locale.US, "%.1f", mDevice.config.mGualiaoTime / 10.0));
        }
    }

    private final static Map<Integer, Integer> mButton2Layout = new ArrayMap<>();

    static {
        mButton2Layout.put(R.id.bt_edit_temperature_threshold, R.layout.edit_temperature_threshold);
    }

    private void setupClickListeners(final View root) {
        for (int id : mButton2Layout.keySet()) {
            root.findViewById(id).setOnClickListener(this);
        }

        root.findViewById(R.id.bt_jet).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice == null || mDevice.getConfig() == null || mSendCommand == null) {
                    Toast.makeText(v.getContext(), "暂时无法编辑", Toast.LENGTH_SHORT).show();
                    return;
                }

                final long device_id = mDevice.getConfig().mID;

                if (mDevice.getState().mRestTime == 0) {
                    Toast.makeText(v.getContext(), "没有剩余时间", Toast.LENGTH_SHORT).show();
                } else {
                    mSendCommand.sendCommand(mDevice,
                            Protocol.jet_package(device_id, 0, 10 * 600, 100));
                }
            }
        });


        root.findViewById(R.id.bt_jet_stop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDevice == null || mDevice.getConfig() == null || mSendCommand == null) {
                    Toast.makeText(v.getContext(), "暂时无法编辑", Toast.LENGTH_SHORT).show();
                    return;
                }

                final long device_id = mDevice.getConfig().mID;
                mSendCommand.sendCommand(mDevice,
                        Protocol.jet_stop_package(device_id));
            }
        });
    }

    private void onClickSetupView(int id, final View editView) {
        switch (id) {
            case R.id.bt_edit_temperature_threshold:
                WheelView etLow = editView.findViewById(R.id.et_temperature_threshold_low);
                WheelView etHeight = editView.findViewById(R.id.et_temperature_threshold_high);
                final List<String> values = new LinkedList<>();
                for (int t = 400; t <= 700; t++) {
                    values.add(String.valueOf(t));
                }
                etLow.setItems(values);
                etHeight.setItems(values);

                etLow.selectIndex(values.indexOf(String.valueOf(mDevice.getConfig().mTemperatureThresholdLow)));
                etHeight.selectIndex(values.indexOf(String.valueOf(mDevice.getConfig().mTemperatureThresholdHigh)));
                break;
        }
    }

    private void onSetValue(int id, final View editView) {
        if (mDevice == null) {
            return;
        }
        final long device_id = mDevice.getConfig().mID;
        switch (id) {
            case R.id.bt_edit_temperature_threshold:
                WheelView etLow = editView.findViewById(R.id.et_temperature_threshold_low);
                WheelView etHigh = editView.findViewById(R.id.et_temperature_threshold_high);
                int low = Integer.parseInt(etLow.getItems().get(etLow.getSelectedPosition()));
                int high = Integer.parseInt(etHigh.getItems().get(etHigh.getSelectedPosition()));
                if (low > high) {
                    int t = low;
                    low = high;
                    high = t;
                }
                byte[] pkg = Protocol.set_temperature_threshold_package(device_id, low, high);
                if (mSendCommand != null) mSendCommand.sendCommand(mDevice, pkg);
                break;
        }
    }

    void setOnSendCommand(ISendCommand impl) {
        mSendCommand = impl;
    }

    @Override
    public void onClick(View view) {
        if (mDevice == null || mDevice.getConfig() == null || mSendCommand == null) {
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
