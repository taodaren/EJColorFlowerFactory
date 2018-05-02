package com.box_tech.fireworksmachine.utils;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by scc on 2018/3/19.
 *  自动切换选择状态
 */

public class AutoToggleCheckedTextView extends AppCompatCheckedTextView {
    public AutoToggleCheckedTextView(Context context){
        super(context);
        init();
    }

    public AutoToggleCheckedTextView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }

    public AutoToggleCheckedTextView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoToggleCheckedTextView.this.toggle();
            }
        });
    }
}
