package com.box_tech.fireworksmachine.page.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.device.Server.GroupInfo;

import java.util.Locale;

/**
 * Created by scc on 2018/3/23.
 * 设备ID列表
 */

class DeviceIdListWithAdderAdapter extends BaseAdapter {
    private final static int VIEW_TYPE_DEVICE = 1;
    private final static int VIEW_TYPE_ADD = 2;
    private GroupInfo mGroupInfo;
    private final LayoutInflater mLayoutInflater;
    private final OnClickListener mOnClickListener;

    DeviceIdListWithAdderAdapter(LayoutInflater layoutInflater, OnClickListener listener){
        mLayoutInflater = layoutInflater;
        mOnClickListener = listener;
    }

    public interface OnClickListener{
        void onRemoveDevice(GroupInfo info, long device_id);
        void onClickAddDevice(GroupInfo info);
    }

    void setValue(GroupInfo info){
        mGroupInfo = info;
    }

    private long[] getIdList(){
        if(mGroupInfo==null) return null;
        if(mGroupInfo.getDevice_list().length==0) return null;
        return mGroupInfo.getDevice_list();
    }

    @Override
    public int getCount() {
        long[] idList = getIdList();
        return ((idList==null)?0:idList.length)+1;
    }

    private boolean isLast(int position){
        return (position+1) == getCount();
    }

    @Override
    public Object getItem(int position) {
        if(isLast(position)){
            return 0;
        }

        long[] idList = getIdList();
        if(idList!=null){
            return idList[position];
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        long[] idList = getIdList();
        if(idList!=null && position < idList.length){
            return idList[position];
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return isLast(position)?VIEW_TYPE_ADD:VIEW_TYPE_DEVICE;
    }

    @Override
    public int getViewTypeCount() {
        long[] idList = getIdList();
        return (idList==null)?1:2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = null;

        switch (getItemViewType(position)){
            case VIEW_TYPE_ADD:
                if(convertView==null){
                    root = mLayoutInflater.inflate(R.layout.device_add_item , parent, false);
                }
                else{
                    root = convertView;
                }
                ImageButton ib = root.findViewById(R.id.add_device);
                ib.setOnClickListener(mClick);
                break;
            case VIEW_TYPE_DEVICE:
                if(convertView==null){
                    root = mLayoutInflater.inflate(R.layout.device_id_item , parent, false);
                }
                else{
                    root = convertView;
                }

                long[] idList = getIdList();
                if(idList!=null){
                    Button b = root.findViewById(R.id.device_id);
                    b.setText(String.format(Locale.CHINA, "设备\n%d", idList[position]));
                    b.setTag(idList[position]);
                    b.setOnLongClickListener(mLongClick);
                }
                break;
        }

        return root;
    }

    private final View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mGroupInfo!=null)
                mOnClickListener.onClickAddDevice(mGroupInfo);
        }
    };

    private final View.OnLongClickListener mLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            long device_id = (long)v.getTag();
            if(mGroupInfo!=null)
                mOnClickListener.onRemoveDevice(mGroupInfo, device_id);
            return true;
        }
    };
}
