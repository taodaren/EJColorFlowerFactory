package com.box_tech.fireworksmachine.page;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.box_tech.fireworksmachine.page.group.GroupFragment;


/**
 * Created by scc on 2018/3/23.
 *  页面适配器
 */

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return DeviceConfigFragment.newInstance();
        }
        if(position==1){
            return GroupFragment.newInstance();
        }
        return null;
    }
}
