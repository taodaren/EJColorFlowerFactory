package com.box_tech.fireworksmachine;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.box_tech.fireworksmachine.device.Device;
import com.box_tech.fireworksmachine.device.DeviceConfig;
import com.box_tech.fireworksmachine.device.DeviceListAdapter;
import com.box_tech.fireworksmachine.device.DeviceState;
import com.box_tech.fireworksmachine.device.ISendCommand;
import com.box_tech.fireworksmachine.device.OnReceivePackage;
import com.box_tech.fireworksmachine.device.Protocol;
import com.box_tech.fireworksmachine.device.Server.GetDeviceList;
import com.box_tech.fireworksmachine.login.LoginSession;
import com.box_tech.fireworksmachine.page.DeviceConfigFragment;
import com.box_tech.fireworksmachine.page.PageAdapter;
import com.box_tech.fireworksmachine.page.group.GroupFragment;
import com.box_tech.fireworksmachine.utils.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.AlertDialog.THEME_HOLO_DARK;

public class MainActivity extends BLEManagerActivity implements ISendCommand,
        DeviceConfigFragment.OnFragmentInteractionListener,
        GroupFragment.OnFragmentInteractionListener {
    private final static String TAG = "MainActivity";

    private final static int ACK_TIMEOUT = 1000;
    @SuppressWarnings("SpellCheckingInspection") private final static ParcelUuid UUID_GATT_SERVICE = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    @SuppressWarnings("SpellCheckingInspection") private final static UUID UUID_GATT_CHARACTERISTIC_NOTIFY = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    @SuppressWarnings("SpellCheckingInspection") private final static UUID UUID_GATT_CHARACTERISTIC_WRITE = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private final List<Device> mDevList = new LinkedList<>();
    private final Map<String, ProtocolWithDevice> mProtocolMap = new ArrayMap<>();

    private DeviceListAdapter mDevListAdapter;
    private final List<String> mFoundDevAddressList = new LinkedList<>();
    private ArrayAdapter<String> mFoundDevAddressListAdapter;
    private ProgressBar mFoundDeviceProgressBar;
    private long mMemberID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 保持常亮的屏幕的状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mDevListAdapter = new DeviceListAdapter(this, mDevList);
        mDevListAdapter.setSendCommand(this);

        LoginSession session = Settings.getLoginSessionInfo(this);
        mMemberID = session.getMember_id();

        addScanFilter(UUID_GATT_SERVICE);

        ViewPager vp = findViewById(R.id.page);
        vp.setAdapter(new PageAdapter(this.getSupportFragmentManager()));
        vp.addOnPageChangeListener(mPageChangeListener);
        mPageChangeListener.onPageSelected(0);
        setupCategoryClick();

        showDeviceAddressSelectDialog();
    }

    @Override
    protected void onDestroy() {
        if (mSelectDialog != null) {
            mSelectDialog.dismiss();
            mSelectDialog = null;
        }
        super.onDestroy();
    }

    private final int[] mTabId = new int[]{
            R.id.tab_device,
            R.id.tab_control,
            R.id.tab_shop,
            R.id.tab_my
    };

    private void setupCategoryClick() {
        for (int i = 0; i < mTabId.length; i++) {
            TextView tv = findViewById(mTabId[i]);
            final int position = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPager vp = findViewById(R.id.page);
                    vp.setCurrentItem(position);
                }
            });
        }
    }

    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mTabId.length; i++) {
                TextView tv = findViewById(mTabId[i]);
                tv.setSelected(i == position);
            }
        }
    };


    @Override
    public void scanDevice() {
        refresh();
        if (mDevList.isEmpty()) {
            showDeviceAddressSelectDialog();
        }
    }

    @Override
    public DeviceListAdapter getDeviceListAdapter() {
        return mDevListAdapter;
    }

    @Override
    public void turnDeviceList(long[] device_id, boolean onoff) {
    }

    @Override
    public long getMemberID() {
        return mMemberID;
    }

    private Dialog mSelectDialog;

    private void showDeviceAddressSelectDialog() {
        final View view = View.inflate(this, R.layout.device_list_dialog, null);
        final ListView lv = view.findViewById(R.id.lv_device_list);
        mFoundDevAddressListAdapter = new ArrayAdapter<String>(this,
                R.layout.device_address_item, R.id.tv_device_address, mFoundDevAddressList) {
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CheckedTextView t = view.findViewById(R.id.tv_device_address);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckedTextView t = (CheckedTextView) v;
                        t.toggle();
                        lv.setItemChecked(position, t.isChecked());
                    }
                });
                return view;
            }
        };
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(mFoundDevAddressListAdapter);
        mFoundDeviceProgressBar = view.findViewById(R.id.pb_refresh_progress);

        mSelectDialog = new AlertDialog.Builder(MainActivity.this, THEME_HOLO_DARK)
                .setTitle("选择设备")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SparseBooleanArray selected_array = lv.getCheckedItemPositions();
                        for (int i = 0; i < mFoundDevAddressList.size(); i++) {
                            if (selected_array.get(i)) {
                                Log.i(TAG, "select " + mFoundDevAddressList.get(i));
                                addDevice(mFoundDevAddressList.get(i), 0);
                            } else {
                                Log.i(TAG, "not select " + mFoundDevAddressList.get(i));
                            }
                        }
                        mFoundDevAddressListAdapter = null;
                        mFoundDeviceProgressBar = null;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFoundDevAddressListAdapter = null;
                        mFoundDeviceProgressBar = null;
                    }
                })
                .create();
        mSelectDialog.show();
    }

    @Override
    protected void stopScan() {
        if (mFoundDeviceProgressBar != null) {
            mFoundDeviceProgressBar.setVisibility(View.GONE);
            mFoundDeviceProgressBar = null;
        }
    }

    private class ProtocolWithDevice extends Protocol {
        final Device device;

        ProtocolWithDevice(@NonNull Device device) {
            this.device = device;
        }

        @Override
        protected void onReceivePackage(@NonNull DeviceState state) {
            device.setState(state);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDevListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onReceivePackage(@NonNull DeviceConfig config) {
            device.setConfig(config);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDevListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onReceivePackage(@NonNull final byte[] pkg, int pkg_len) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doMatch(device.getAddress(), pkg);
                }
            });
        }
    }

    private static boolean contain(GetDeviceList.Result.DeviceInformation[] list, String mac) {
        for (GetDeviceList.Result.DeviceInformation d : list) {
            if (d.getMac().equals(mac)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setRegisteredDevice(GetDeviceList.Result.DeviceInformation[] list) {
        List<String> rmMacList = new LinkedList<>();
        List<Device> rmDevList = new LinkedList<>();
        for (String mac : mProtocolMap.keySet()) {
            if (!contain(list, mac)) {
                rmMacList.add(mac);
                rmDevList.add(getDevice(mac));
            }
        }
        for (String mac : rmMacList) {
            mProtocolMap.remove(mac);
        }
        for (Device dev : rmDevList) {
            mDevList.remove(dev);
        }

        for (GetDeviceList.Result.DeviceInformation dev : list) {
            addDevice(dev.getMac(), dev.getId());
        }
    }

    private void addDevice(final String mac, long id) {
        if (!mProtocolMap.containsKey(mac)) {
            final Device dev = new Device(mac);
            dev.setId(id);
            mDevList.add(dev);
            mDevListAdapter.notifyDataSetChanged();
            addDeviceByMac(mac);
            mProtocolMap.put(mac, new ProtocolWithDevice(dev));
        }
    }

    @Override
    protected void onFoundDevice(final BluetoothDevice device, @Nullable List<ParcelUuid> serviceUuids) {
        String name = device.getName();
        String mac = device.getAddress();
        if (name.indexOf("EEJING-CHJ") != 0) {
            return;
        }
        if (mFoundDevAddressListAdapter != null) {
            if (!mFoundDevAddressList.contains(mac)) {
                mFoundDevAddressList.add(mac);
                mFoundDevAddressListAdapter.notifyDataSetChanged();
            }
        }
//        super.onFoundDevice(device, serviceUuids);
    }

//    private boolean getStatusFlag = true;
    private Device getDevice(String mac) {
        for (Device device : mDevList) {
            if (device.getAddress().equals(mac)) {
                return device;
            }
        }
        return null;
    }

    private boolean mRequestConfig;

    @Override
    protected void onDeviceReady(final String mac) {
        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
            Device device = getDevice(mac);
            if (device != null) {
                device.setConnected(true);
                mDevListAdapter.notifyDataSetChanged();
            }
            registerRefreshStatus(mac);
        }
    }

    private void registerRefreshStatus(final String mac) {
        registerPeriod(mac + "-status", new Runnable() {
            @Override
            public void run() {
                ProtocolWithDevice pd = mProtocolMap.get(mac);
                if (pd == null) {
                    return;
                }
                Device device = getDevice(mac);
                if (device != null) {
                    DeviceConfig config = device.getConfig();
                    long id = (config == null) ? 0 : config.mID;
                    if (config == null || mRequestConfig) {
                        mRequestConfig = !send(mac, Protocol.get_config_package(id), true);
                    }
                    send(mac, Protocol.get_status_package(id), true);
                }
            }
        }, 2000);
    }

    @Override
    protected void onDeviceConnect(String mac) {
        Log.i(TAG, "onDeviceConnect " + mac);
        registerRefreshStatus(mac);

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(true);
            mDevListAdapter.notifyDataSetChanged();
        } else {
            Log.i(TAG, "onDeviceConnect no device");
        }
    }

    @Override
    protected void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "-status");

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(false);
            mDevListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onReceive(String mac, byte[] data) {
        super.onReceive(mac, data);
        Protocol p = mProtocolMap.get(mac);
        if (p != null) {
            p.onReceive(data);
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        String mac = device.getAddress();
        Protocol p = mProtocolMap.get(mac);
        if (p != null) {
            send(device.getAddress(), pkg, true);
            mRequestConfig = true;
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        mPackageNeedAckList.push(new PackageNeedAck(device.getAddress(), pkg, callback));
        sendCommand(device, pkg);
    }

    private static class PackageNeedAck {
        final byte[] cmd_pkg;
        final String mac;
        final long send_time;
        final OnReceivePackage callback;

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
            this.send_time = System.currentTimeMillis();
        }

        boolean isTimeout() {
            return (System.currentTimeMillis() - send_time) > ACK_TIMEOUT;
        }
    }

    private final LinkedList<PackageNeedAck> mPackageNeedAckList = new LinkedList<>();

    private void doMatch(String mac, byte[] ack_pkg) {
        Log.i(TAG, "doMatch " + Util.hex(ack_pkg, ack_pkg.length) + " from " + mPackageNeedAckList.size());
        for (PackageNeedAck p : mPackageNeedAckList) {
            Log.i(TAG, "doMatch check " + Util.hex(p.cmd_pkg, p.cmd_pkg.length));
            if (p.mac.equals(mac) && Protocol.isMatch(p.cmd_pkg, ack_pkg)) {
                Log.i(TAG, "doMatch match");
                mPackageNeedAckList.remove(p);
                p.callback.ack(ack_pkg);
                return;
            }
        }
    }

    private void doTimeoutCheck() {
        for (PackageNeedAck p : mPackageNeedAckList) {
            if (p.isTimeout()) {
                mPackageNeedAckList.remove(p);
                p.callback.timeout();
                return;
            }
        }
    }

    @Override
    protected void poll() {
        super.poll();
        doTimeoutCheck();
    }
}
