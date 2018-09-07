package com.box_tech.fireworksmachine.page;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.Settings;
import com.box_tech.fireworksmachine.device.DeviceListAdapter;
import com.box_tech.fireworksmachine.device.Server.GetDeviceList;
import com.box_tech.fireworksmachine.login.LoginSession;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceConfigFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceConfigFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public DeviceConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceConfigFragment.
     */
    public static DeviceConfigFragment newInstance() {
        return new DeviceConfigFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.page_device_config, container, false);
        final SwipeRefreshLayout srl = root.findViewById(R.id.srl_collect);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListener.scanDevice();
                //getDeviceListFromServer();
                srl.setRefreshing(false);
            }
        });
        ExpandableListView lv = root.findViewById(R.id.lv_devices);
        lv.setAdapter(mListener.getDeviceListAdapter());
        //getDeviceListFromServer();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getDeviceListFromServer(){
        Activity activity = getActivity();
        if(activity==null){
            return;
        }
        LoginSession session = Settings.getLoginSessionInfo(activity);
        new GetDeviceList.Request(session.getMember_id(),  session.getToken(),activity, new GetDeviceList.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GetDeviceList.Result result) {
                if(activity!=null){
                    GetDeviceList.Result.List data = result.getData();
                    if(data!=null){
                        onGetDeviceListFromServer(result.getData().getList());
                    }
                }
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                if(activity!=null){
                    Toast.makeText(activity, "从服务器获取设备列表失败："+message, Toast.LENGTH_SHORT).show();
                }
            }
        }).run();
    }

    private void onGetDeviceListFromServer(GetDeviceList.Result.DeviceInformation[] list){
        mListener.setRegisteredDevice(list);
    }

    public interface OnFragmentInteractionListener {
        void scanDevice();
        DeviceListAdapter getDeviceListAdapter();
        void setRegisteredDevice(GetDeviceList.Result.DeviceInformation[] list);
    }
}
