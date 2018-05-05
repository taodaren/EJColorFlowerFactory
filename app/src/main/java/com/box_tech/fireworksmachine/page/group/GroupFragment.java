package com.box_tech.fireworksmachine.page.group;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.box_tech.fireworksmachine.EditDeviceTodGroupActivity;
import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.device.Server.AddGroup;
import com.box_tech.fireworksmachine.device.Server.GetGroupList;
import com.box_tech.fireworksmachine.device.Server.GroupInfo;
import com.box_tech.fireworksmachine.device.Server.RemoveGroup;
import com.box_tech.fireworksmachine.page.OnFragmentInteractionBaseListener;
import com.box_tech.fireworksmachine.utils.GeneralResult;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Activity mActivity;
    private GroupInfo[] mGroupInfo;
    private GetGroupList.Request mGetGroupListRequest;
    private AddGroup.Request mAddGroupRequest;
    private RemoveGroup.Request mRemoveGroupRequest;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupFragment.
     */
    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.page_group, container, false);
        SwipeRefreshLayout srl = root.findViewById(R.id.srl_group);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        ListView lv = root.findViewById(R.id.group_list);
        GroupListAdapter adapter = new GroupListAdapter(inflater);
        adapter.setListener(new GroupListAdapter.OnClickListener() {
            @Override
            public void onRemoveDevice(long group_id, long device_id) {
                System.out.println("onRemoveDevice "+group_id+" "+device_id);
            }

            @Override
            public void onRemoveGroup(final long group_id) {
                System.out.println("onRemoveGroup "+group_id);

                new AlertDialog.Builder(mActivity)
                        .setMessage("确定要删除组?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeGroup(group_id);
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onClickAddDevice(long group_id) {
                System.out.println("onClickAddDevice "+group_id+" ");
                EditDeviceTodGroupActivity.start_me(mActivity, group_id);
            }

            @Override
            public void onClickOnOff(GroupInfo groupInfo, boolean onoff) {
                mListener.turnDeviceList(groupInfo.getDevice_list(), onoff);
            }
        });
        lv.setAdapter(adapter);
        View foot = inflater.inflate(R.layout.add_group_item, lv, false);
        Button add_group = foot.findViewById(R.id.add_group);
        add_group.setOnClickListener(mClickAddGroup);
        lv.addFooterView(foot);

        return root;
    }

    private final View.OnClickListener mClickAddGroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addGroup();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        View root = getView();
        if(mGroupInfo == null && root != null){
            SwipeRefreshLayout srl = root.findViewById(R.id.srl_group);
            srl.setRefreshing(true);
            refresh();
        }
        else if(root!=null){
            update();
        }
    }

    private boolean isBusy(){
        return mGetGroupListRequest != null ||
                mAddGroupRequest != null ||
                mRemoveGroupRequest != null;
    }

    private void refresh(){
        System.out.println("refresh "+mListener.getMemberID()+" "+mActivity);
        if(isBusy()){
            return;
        }
        //TODO: need token
        mGetGroupListRequest = new GetGroupList.Request(mListener.getMemberID(), "", mActivity, new GetGroupList.OnFinished() {
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GetGroupList.Result result) {
                switch (result.getCode()){
                    case GetGroupList.ResultCode.OK:
                        mGroupInfo = result.getData();
                        update();
                        break;
                    default:
                        update(result.getMessage());
                        break;
                }

                stopRefresh();
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                stopRefresh();
                update(message);
            }
        });
        mGetGroupListRequest.run();
    }

    private void addGroup(){
        if(isBusy()){
            return;
        }
        final EditText etGroupName = new EditText(mActivity);
        new AlertDialog.Builder(mActivity)
                .setTitle("组名")
                .setView(etGroupName)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addGroup(etGroupName.getText().toString());
                    }
                })
                .create()
                .show();
    }

    private void addGroup(String name){
        //TODO: need token
        mAddGroupRequest = new AddGroup.Request(mListener.getMemberID(), "", name, mActivity, new AddGroup.OnFinished(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                mAddGroupRequest = null;
                if(result.getCode() == GeneralResult.RESULT_OK){
                    GroupFragment.this.refresh();
                }
                else{
                    onAddGroupFailed(result.getMessage());
                }
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                mAddGroupRequest = null;
                onAddGroupFailed(message);
            }
        });
        mAddGroupRequest.run();
    }

    private void onAddGroupFailed(String message){
        if(mActivity!=null){
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeGroup(long group_id){
        //TODO: need token
        mRemoveGroupRequest = new RemoveGroup.Request(mListener.getMemberID(), "", group_id, mActivity, new RemoveGroup.OnFinished(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {
                mRemoveGroupRequest = null;
                if(result.getCode() == GeneralResult.RESULT_OK){
                    GroupFragment.this.refresh();
                }
                else{
                    onRemoveGroupFailed(result.getMessage());
                }
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                mRemoveGroupRequest = null;
                onRemoveGroupFailed(message);
            }
        });
        mRemoveGroupRequest.run();
    }

    private void onRemoveGroupFailed(String message){
        if(mActivity!=null){
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRefresh(){
        View root = getView();
        if(root!=null){
            SwipeRefreshLayout srl = root.findViewById(R.id.srl_group);
            srl.setRefreshing(false);
        }
        mGetGroupListRequest = null;
    }

    private void update(){
        View root = getView();
        if(root!=null && mGroupInfo != null) {
            ListView lv = root.findViewById(R.id.group_list);
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter)lv.getAdapter();
            GroupListAdapter adapter = (GroupListAdapter) headerViewListAdapter.getWrappedAdapter();
            adapter.setValue(mGroupInfo);
            adapter.notifyDataSetChanged();
        }
    }

    private void update(@NonNull String message){
        if(mActivity!=null){
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            mActivity = (Activity)context;
        }
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
        mActivity = null;
    }


    public interface OnFragmentInteractionListener extends OnFragmentInteractionBaseListener {
        void turnDeviceList(long[] device_id, boolean onoff);
    }
}
