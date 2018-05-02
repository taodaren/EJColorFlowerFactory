package com.box_tech.fireworksmachine.page.group;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ToggleButton;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.device.Server.GroupInfo;


/**
 * Created by scc on 2018/3/23.
 * 组适配器
 */

class GroupListAdapter extends BaseAdapter {
    private GroupInfo[] mGroupInfo;
    private OnClickListener mListener;
    private final LayoutInflater mLayoutInflater;

    interface OnClickListener {
        void onRemoveDevice(long group_id, long device_id);
        void onRemoveGroup(long group_id);
        void onClickAddDevice(long group_id);
        void onClickOnOff(GroupInfo groupInfo, boolean onoff);
    }

    GroupListAdapter(LayoutInflater layoutInflater){
        mLayoutInflater = layoutInflater;
    }

    void setValue(GroupInfo[] groupInfo){
        mGroupInfo = groupInfo;
    }
    void setListener(OnClickListener listener){
        mListener = listener;
    }

    @Override
    public int getCount() {
        return (mGroupInfo==null)?0:mGroupInfo.length;
    }

    @Override
    public Object getItem(int position) {
        if(mGroupInfo!=null){
            return mGroupInfo[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mGroupInfo!=null){
            return mGroupInfo[position].getGroup_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View root;
        final GridView gv;
        final DeviceIdListWithAdderAdapter adapter;
        final Button tvGroupName;
        if(convertView==null){
            root = mLayoutInflater.inflate(R.layout.group_item, parent, false);
            gv = root.findViewById(R.id.device_id_list);
            adapter = new DeviceIdListWithAdderAdapter(mLayoutInflater, mOnClickIdList);
            gv.setAdapter(adapter);
            root.findViewById(R.id.onoff).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        mListener.onClickOnOff((GroupInfo)v.getTag(), ((ToggleButton)v).isChecked());
                    }
                }
            });
            tvGroupName = root.findViewById(R.id.group_name);
            tvGroupName.setOnLongClickListener(mLongClickGroupName);
        }
        else{
            root = convertView;
            gv = root.findViewById(R.id.device_id_list);
            adapter = (DeviceIdListWithAdderAdapter)gv.getAdapter();
            tvGroupName = root.findViewById(R.id.group_name);
        }

        GroupInfo info = mGroupInfo[position];

        tvGroupName.setText(info.getGroup_name());
        tvGroupName.setTag(info.getGroup_id());

        adapter.setValue(info);
        adapter.notifyDataSetChanged();

        root.findViewById(R.id.onoff).setTag(info);

        return root;
    }

    private final View.OnLongClickListener mLongClickGroupName = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            long group_id = (long)v.getTag();
            mListener.onRemoveGroup(group_id);
            return true;
        }
    };


    private final DeviceIdListWithAdderAdapter.OnClickListener mOnClickIdList = new DeviceIdListWithAdderAdapter.OnClickListener() {
        @Override
        public void onRemoveDevice(GroupInfo info, long device_id) {
            mListener.onRemoveDevice(info.getGroup_id(), device_id);
        }

        @Override
        public void onClickAddDevice(GroupInfo info) {
            mListener.onClickAddDevice(info.getGroup_id());
        }
    };
}
