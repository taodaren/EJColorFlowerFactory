package com.box_tech.fireworksmachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.Locale;

/**
 * Created by scc on 2018/3/23.
 * 设备ID列表
 */

class DeviceIdListAdapter extends BaseAdapter {
    private long[] mIdList;
    private final LayoutInflater mLayoutInflater;
    private final OnClickListener mOnClickListener;

    DeviceIdListAdapter(LayoutInflater layoutInflater, OnClickListener listener){
        mLayoutInflater = layoutInflater;
        mOnClickListener = listener;
    }

    public interface OnClickListener{
        void onClick(long device_id);
    }

    void setValue(long[] idList){
        mIdList = idList;
    }

    @Override
    public int getCount() {
        return (mIdList==null)?0:mIdList.length;
    }

    @Override
    public Object getItem(int position) {
        if(mIdList!=null){
            return mIdList[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mIdList!=null && position < mIdList.length){
            return mIdList[position];
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root;

        if(convertView==null){
            root = mLayoutInflater.inflate(R.layout.device_id_item , parent, false);
        }
        else{
            root = convertView;
        }

        if(mIdList!=null){
            Button b = root.findViewById(R.id.device_id);
            b.setText(String.format(Locale.CHINA, "设备\n%d", mIdList[position]));
            b.setTag(mIdList[position]);
            b.setOnLongClickListener(mLongClick);
        }

        return root;
    }


    private final View.OnLongClickListener mLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            long device_id = (long)v.getTag();
            if(mOnClickListener!=null)
                mOnClickListener.onClick(device_id);
            return true;
        }
    };
}
