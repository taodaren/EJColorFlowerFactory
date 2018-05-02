package com.box_tech.fireworksmachine;

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
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.box_tech.fireworksmachine.device.Device;
import com.box_tech.fireworksmachine.device.DeviceConfig;
import com.box_tech.fireworksmachine.device.DeviceListAdapter;
import com.box_tech.fireworksmachine.device.DeviceState;
import com.box_tech.fireworksmachine.device.ISendCommand;
import com.box_tech.fireworksmachine.device.OnReceivePackage;
import com.box_tech.fireworksmachine.device.Protocol;
import com.box_tech.fireworksmachine.page.DeviceConfigFragment;
import com.box_tech.fireworksmachine.page.PageAdapter;
import com.box_tech.fireworksmachine.page.group.GroupFragment;
import com.box_tech.fireworksmachine.utils.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends BLEManagerActivity implements ISendCommand,
        DeviceConfigFragment.OnFragmentInteractionListener,
        GroupFragment.OnFragmentInteractionListener{
    private final static String TAG = "MainActivity";
    private final List<Device> mDeviceList = new LinkedList<>();
    private final Map<String, Protocol> mProtocolList = new ArrayMap<>();

    private final static int ACK_TIMEOUT = 1000;
    private final static ParcelUuid UUID_GATT_SERVICE
            = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    //private final static UUID UUID_GATT_CHARACTERISTIC_NOTIFY
    //        = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final static UUID UUID_GATT_CHARACTERISTIC_WRITE
            = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private DeviceListAdapter mDeviceListAdapter;
    private final List<String> mFoundDeviceAddressList = new LinkedList<>();
    private ArrayAdapter<String> mFoundDeviceAddressListAdapter = null;
    private ProgressBar mFoundDeviceProgressBar = null;
    private long mMemberID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceListAdapter = new DeviceListAdapter(this, mDeviceList );
        mDeviceListAdapter.setSendCommand(this);

        mMemberID = Settings.get_member_id(this);

        addScanFilter(UUID_GATT_SERVICE);

        ViewPager vp = findViewById(R.id.page);
        vp.setAdapter(new PageAdapter(this.getSupportFragmentManager()));

        showDeviceAddressSelectDialog();
    }


    @Override
    public void scanDevice() {
        refresh();
        if(mDeviceList.isEmpty()){
            showDeviceAddressSelectDialog();
        }
    }

    @Override
    public DeviceListAdapter getDeviceListAdapter() {
        return mDeviceListAdapter;
    }

    @Override
    public void turnDeviceList(long[] device_id, boolean onoff) {

    }

    @Override
    public long getMemberID() {
        return mMemberID;
    }

    private void showDeviceAddressSelectDialog(){
        final View view = View.inflate(this, R.layout.device_list_dialog, null);
        final ListView lv = view.findViewById(R.id.lv_device_list);
        mFoundDeviceAddressListAdapter = new ArrayAdapter<String>(this,
                R.layout.device_address_item, R.id.tv_device_address, mFoundDeviceAddressList){
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CheckedTextView t = view.findViewById(R.id.tv_device_address);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckedTextView t = (CheckedTextView)v;
                        t.toggle();
                        lv.setItemChecked(position, t.isChecked());
                    }
                });
                return view;
            }
        };
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(mFoundDeviceAddressListAdapter);
        mFoundDeviceProgressBar = view.findViewById(R.id.pb_refresh_progress);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("选择设备")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SparseBooleanArray selected_array = lv.getCheckedItemPositions();
                        for(int i = 0; i< mFoundDeviceAddressList.size(); i++){
                            if(selected_array.get(i)){
                                Log.i(TAG, "select "+mFoundDeviceAddressList.get(i));
                                add_device(mFoundDeviceAddressList.get(i));
                            }
                            else{
                                Log.i(TAG, "not select "+mFoundDeviceAddressList.get(i));
                            }
                        }
                        mFoundDeviceAddressListAdapter = null;
                        mFoundDeviceProgressBar = null;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFoundDeviceAddressListAdapter = null;
                        mFoundDeviceProgressBar = null;
                    }
                })
                .show();
    }

    @Override
    protected void onStopScan(){
        if(mFoundDeviceProgressBar!=null){
            mFoundDeviceProgressBar.setVisibility(View.GONE);
            mFoundDeviceProgressBar = null;
        }
    }

    private void add_device(final String mac){
        if( !mProtocolList.containsKey(mac) ){
            final Device d = new Device(mac);
            mDeviceList.add(d);
            mDeviceListAdapter.notifyDataSetChanged();
            addDevice(mac);

            mProtocolList.put(mac, new Protocol(){
                @Override
                protected void onReceivePackage(@NonNull DeviceState state) {
                    d.setState(state);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                protected void onReceivePackage(@NonNull DeviceConfig config){
                    d.setConfig(config);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                protected void onReceivePackage( @NonNull final byte[] pkg, int pkg_len) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doMatch(mac, pkg);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onFoundDevice(final BluetoothDevice device, @Nullable List<ParcelUuid> serviceUuids){
        String name = device.getName();
        String mac = device.getAddress();
        if( name.indexOf("EEJING-CHJ")!=0){
            return;
        }
        if(mFoundDeviceAddressListAdapter != null){
            if( ! mFoundDeviceAddressList.contains(mac)){
                mFoundDeviceAddressList.add(mac);
                mFoundDeviceAddressListAdapter.notifyDataSetChanged();
            }
        }
        //super.onFoundDevice(device, serviceUuids);
    }

    //private boolean getStatusFlag = true;
    private Device getDevice(String mac){
        for(Device device : mDeviceList){
            if(device.getAddress().equals(mac)){
                return device;
            }
        }
        return null;
    }

    private boolean mRequestConfig = false;

    @Override
    protected void onDeviceReady(final String mac){
        if( setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE ) ){
            Device device = getDevice(mac);
            if(device!=null){
                device.setConnected(true);
                mDeviceListAdapter.notifyDataSetChanged();
            }

            registerPeriod(mac + "-status", new Runnable() {
                        @Override
                        public void run() {
                            Device device = getDevice(mac);
                            if(device!=null){
                                DeviceConfig config = device.getConfig();
                                long id = (config==null)?0:config.mID;
                                if(config==null || mRequestConfig){
                                    mRequestConfig = ! send(mac, Protocol.get_config_package(id));
                                }
                                send(mac, Protocol.get_status_package(id));
                            }
                        }
                    },
                    2000);
        }
    }

    @Override
    protected void onDeviceDisconnect(String mac){
        unregisterPeriod(mac + "-status");

        Device device = getDevice(mac);
        if(device!=null){
            device.setConnected(false);
            mDeviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onReceive(String mac, byte[] data){
        super.onReceive(mac, data);
        Protocol p = mProtocolList.get(mac);
        if(p!=null){
            p.onReceive(data);
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        send(device.getAddress(), pkg);
        mRequestConfig = true;
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        mPackageNeedAckList.push(new PackageNeedAck(device.getAddress(), pkg, callback));
        send(device.getAddress(), pkg);
    }

    private static class PackageNeedAck{
        final byte[] cmd_pkg;
        final String mac;
        final long send_time;
        final OnReceivePackage callback;

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback){
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
            this.send_time = System.currentTimeMillis();
        }

        boolean isTimeout(){
            return (System.currentTimeMillis()-send_time)>ACK_TIMEOUT;
        }
    }

    private final LinkedList<PackageNeedAck> mPackageNeedAckList = new LinkedList<>();

    private void doMatch(String mac, byte[] ack_pkg){
        Log.i(TAG, "doMatch "+ Util.hex(ack_pkg, ack_pkg.length) + " from " + mPackageNeedAckList.size());
        for(PackageNeedAck p : mPackageNeedAckList){
            Log.i(TAG, "doMatch check "+ Util.hex(p.cmd_pkg, p.cmd_pkg.length));
            if( p.mac.equals(mac) && Protocol.isMatch(p.cmd_pkg, ack_pkg) ){
                Log.i(TAG, "doMatch match");
                mPackageNeedAckList.remove(p);
                p.callback.ack(ack_pkg);
                return;
            }
        }
    }

    private void doTimeoutCheck(){
        for(PackageNeedAck p : mPackageNeedAckList){
            if( p.isTimeout() ){
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
