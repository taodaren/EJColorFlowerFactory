package com.box_tech.fireworksmachine.page;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.device.DeviceListAdapter;

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
                srl.setRefreshing(false);
            }
        });
        ExpandableListView lv = root.findViewById(R.id.lv_devices);
        lv.setAdapter(mListener.getDeviceListAdapter());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void scanDevice();
        DeviceListAdapter getDeviceListAdapter();
    }
}
