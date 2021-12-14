package com.example.myapplication;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CityFragmentPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment>fragmentList;//传入ViewPager的数据源
    public CityFragmentPagerAdapter(FragmentManager fm, List<Fragment>fragmentLis) {
        super(fm);
        this.fragmentList = fragmentLis;
    }

    @Override
    //返回指定位置所对应的Fragment
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    int childCount = 0;   //表示ViewPager包含的页数
//    当ViewPager的页数发生改变时，必须要重写两个函数
    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();//获取此时子页面的数量
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {//获取item位置
        if (childCount>0) {//如果子页面的数量大于0
            childCount--;
            return POSITION_NONE;//返回一个位置的标识
        }
        return super.getItemPosition(object);
    }
}
