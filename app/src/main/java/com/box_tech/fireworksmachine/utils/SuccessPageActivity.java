package com.box_tech.fireworksmachine.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.box_tech.fireworksmachine.R;

import java.io.Serializable;

public class SuccessPageActivity extends AppCompatActivity {
    private final static int STAY_TIME = 1000;
    private Handler mHandler;

    public static void start_me(Context context, @StringRes int message, Class<? extends Activity> return_activity){
        Intent intent = new Intent(context, SuccessPageActivity.class);
        intent.putExtra("message", message);
        intent.putExtra("return", return_activity);
        context.startActivity(intent);
    }

    private void go(){
        Serializable o = getIntent().getSerializableExtra("return");
        if( o instanceof Class<?>){
            Intent intent = new Intent(this, (Class<?>)o);
            this.startActivity(intent);
            finish();
        }
        else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_page);

        Intent intent = getIntent();
        int message = intent.getIntExtra("message", -1);
        if(message>0){
            TextView tv = findViewById(R.id.message);
            tv.setText(message);
        }

        mHandler = new Handler();
        mHandler.postDelayed(mGo, STAY_TIME);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mGo);
        super.onDestroy();
    }

    private final Runnable mGo = new Runnable() {
        @Override
        public void run() {
            go();
        }
    };
}
