package com.box_tech.fireworksmachine.login;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.box_tech.fireworksmachine.R;
import com.box_tech.fireworksmachine.utils.GeneralRequest;
import com.box_tech.fireworksmachine.utils.InternetPermissionRequest;

/**
 * Created by scc on 2018/3/21.
 * 请求之前的准备
 */

class PrepareRequest<ResultType> {
    final Button mButtonGo;
    private final EditText mLastEdit;
    private final EditText[] mEditList;
    private final String[] mValues;
    private boolean mTaskPending = false;
    final Activity mActivity;

    PrepareRequest(@NonNull Activity activity, @IdRes int button_go,
                          @IdRes int last_edit, @IdRes int... edit_list){
        mActivity = activity;
        mButtonGo = activity.findViewById(button_go);
        mLastEdit = activity.findViewById(last_edit);
        mEditList = new EditText[edit_list.length];
        for(int i = 0; i<edit_list.length;i++){
            mEditList[i] = activity.findViewById(edit_list[i]);
        }

        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptGo();
            }
        });

        if(mLastEdit != null){
            mLastEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                        attemptGo();
                        return true;
                    }
                    return false;
                }
            });
            mValues = new String[edit_list.length+1];
        }
        else{
            mValues = new String[edit_list.length];
        }
    }

    private void attemptGo(){
        if(mTaskPending){
            return;
        }

        View focusView = null;
        int i = 0;
        String v;

        for(EditText et : mEditList){
            et.setError(null);
            v = et.getText().toString();
            mValues[i++] = v.trim();
            if(TextUtils.isEmpty(v)){
                et.setError(mActivity.getString(R.string.error_cannot_empty));
                if(focusView==null){
                    focusView = et;
                }
            }
            else{
                et.setError(null);
            }
        }

        if(mLastEdit!=null){
            v = mLastEdit.getText().toString();
            mValues[i] = v.trim();
            if(TextUtils.isEmpty(v)){
                mLastEdit.setError(mActivity.getString(R.string.error_cannot_empty));
                if(focusView==null){
                    focusView = mLastEdit;
                }
            }
            else{
                mLastEdit.setError(null);
            }
        }

        if(focusView != null){
            focusView.requestFocus();
        }
        else{
            new InternetPermissionRequest(mActivity){
                @Override
                protected void onOK() {
                    super.onOK();
                    go(mValues);
                }
            }.request();
        }
    }

    void onCreateRequest(GeneralRequest.OnFinished<ResultType> callback, String... values){
    }

    void onOK(@NonNull Activity activity, @NonNull ResultType result){
    }

    void onFailed(@NonNull Activity activity, @NonNull String result){
    }

    private final GeneralRequest.OnFinished<ResultType> callback = new GeneralRequest.OnFinished<ResultType>() {
        @Override
        public void onOK(@Nullable Activity activity, @NonNull ResultType result) {
            if(activity!=null){
                onFinal();
                PrepareRequest.this.onOK(activity, result);
            }
        }

        @Override
        public void onFailed(@Nullable Activity activity, @NonNull String message) {
            if(activity!=null){
                onFinal();
                PrepareRequest.this.onFailed(activity, message);
            }
        }
    };

    private void go(String ... values){
        mTaskPending = true;
        mButtonGo.setEnabled(false);
        onCreateRequest(callback, values);
    }

    void onFinal(){
        mButtonGo.setEnabled(true);
        mTaskPending = false;
    }
}
