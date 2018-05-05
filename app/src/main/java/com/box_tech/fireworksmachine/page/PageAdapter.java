package com.box_tech.fireworksmachine.page;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.box_tech.fireworksmachine.page.group.GroupFragment;
import com.box_tech.fireworksmachine.page.my.MyFragment;
import com.box_tech.fireworksmachine.page.shop.ShopFragment;


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
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return DeviceConfigFragment.newInstance();
        }
        if(position==1){
            return GroupFragment.newInstance();
        }
        if(position==2){
            return ShopFragment.newInstance();
        }
        if(position==3){
            return MyFragment.newInstance();
        }
        return null;
    }
}
