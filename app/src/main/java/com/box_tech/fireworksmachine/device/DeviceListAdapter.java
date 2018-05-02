package com.box_tech.fireworksmachine.device;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.box_tech.fireworksmachine.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by scc on 2018/3/2.
 *
 */

public class DeviceListAdapter extends BaseExpandableListAdapter {
    private final List<Device> mDeviceList;
    private final Activity mActivity;
    private ISendCommand mSendCommand = null;

    public DeviceListAdapter(@NonNull Activity activity, @NonNull List<Device> deviceList){
        mActivity = activity;
        mDeviceList = deviceList;
    }

    public void setSendCommand(ISendCommand sendCommand){
        mSendCommand = sendCommand;
    }

    @Override
    public int getGroupCount() {
        return mDeviceList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return mDeviceList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mDeviceList.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        Device device = mDeviceList.get(i);
        View root = null;
        if(view != null){
            TextView tv = view.findViewById(R.id.tv_title_device_id);
            if(tv!=null){
                root = view;
            }
        }

        if(root==null){
            root = LayoutInflater.from(mActivity).inflate( R.layout.device_group_view, viewGroup, false);
        }

        TextView tv = root.findViewById(R.id.tv_title_device_id);
        tv.setText(String.format(Locale.US, "%d", (device.config ==null)?0:device.config.mID));
        ImageView iv = root.findViewById(R.id.iv_device_connected);
        iv.setSelected(device.isConnected());
        return root;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        View root = view;
        if(root==null){
            root = LayoutInflater.from(mActivity).inflate( R.layout.device_item_view, viewGroup, false);
        }

        Device device = mDeviceList.get(i);
        DeviceView dv = root.findViewById(R.id.dv);
        dv.setOnSendCommand(mSendCommand);
        dv.setDevice(device);

        return root;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return mDeviceList.isEmpty();
    }

    @Override
    public void onGroupExpanded(int i) {
    }

    @Override
    public void onGroupCollapsed(int i) {
    }
}
