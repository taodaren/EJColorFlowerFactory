package com.box_tech.fireworksmachine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.box_tech.fireworksmachine.device.Server.AddDeviceToGroup;
import com.box_tech.fireworksmachine.device.Server.GoEditDeviceToGroup;
import com.box_tech.fireworksmachine.device.Server.RemoveDevice;
import com.box_tech.fireworksmachine.utils.GeneralResult;

public class EditDeviceTodGroupActivity extends AppCompatActivity {
    private GoEditDeviceToGroup.Request mGoEditDeviceToGroupRequest;
    private long member_id;
    private long group_id;
    private DeviceIdListAdapter mUngroupedAdapter;
    private DeviceIdListAdapter mGroupedAdapter;

    public static void start_me(Context context, long group_id){
        Intent intent = new Intent(context, EditDeviceTodGroupActivity.class);
        intent.putExtra("group_id", group_id);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device_tod_group);

        member_id = Settings.get_member_id(this);
        group_id = getIntent().getLongExtra("group_id", 0);

        ListView lvUngrouped = findViewById(R.id.ungrouped);
        ListView lvGrouped = findViewById(R.id.grouped);

        mUngroupedAdapter = new DeviceIdListAdapter(getLayoutInflater(), mClickUngrouped);
        mGroupedAdapter   = new DeviceIdListAdapter(getLayoutInflater(), mClickGrouped);

        lvUngrouped.setAdapter(mUngroupedAdapter);
        lvGrouped.setAdapter(mGroupedAdapter);

        getData();
    }

    private final DeviceIdListAdapter.OnClickListener mClickUngrouped = new DeviceIdListAdapter.OnClickListener() {
        @Override
        public void onClick(long device_id) {
            addDeviceToGroup(device_id);
        }
    };

    private final DeviceIdListAdapter.OnClickListener mClickGrouped = new DeviceIdListAdapter.OnClickListener() {
        @Override
        public void onClick(long device_id) {
            removeDeviceFromGroup(device_id);
        }
    };

    private void getData(){
        mGoEditDeviceToGroupRequest = new GoEditDeviceToGroup.Request(member_id, group_id, this, new GoEditDeviceToGroup.OnFinished(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GoEditDeviceToGroup.Result result) {
                mGoEditDeviceToGroupRequest = null;
                if(result.getCode() == GeneralResult.RESULT_OK ){
                    EditDeviceTodGroupActivity.this.onOK(result.getData().getList(), result.getData().getPossess());
                }
                else{
                    EditDeviceTodGroupActivity.this.onFailed(result.getMessage());
                }
            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {
                mGoEditDeviceToGroupRequest = null;
                EditDeviceTodGroupActivity.this.onFailed(message);
            }
        });

        mGoEditDeviceToGroupRequest.run();
    }

    private void onOK(long[] ungrouped, long[] grouped){
        mUngroupedAdapter.setValue(ungrouped);
        mGroupedAdapter.setValue(grouped);

        mUngroupedAdapter.notifyDataSetChanged();
        mGroupedAdapter.notifyDataSetChanged();
    }

    private void onFailed(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addDeviceToGroup(long device_id){
        new AddDeviceToGroup.Request(member_id, device_id, group_id, this, new AddDeviceToGroup.OnFinished(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {

            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {

            }
        }).run();
    }


    private void removeDeviceFromGroup(long device_id){
        new RemoveDevice.Request(member_id, device_id, this, new RemoveDevice.OnFinished(){
            @Override
            public void onOK(@Nullable Activity activity, @NonNull GeneralResult result) {

            }

            @Override
            public void onFailed(@Nullable Activity activity, @NonNull String message) {

            }
        }).run();
    }
}
