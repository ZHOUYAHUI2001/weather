package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.city_manager.CityManagerActivity;
import com.example.myapplication.db.DBManager;

import java.util.ArrayList;
import java.util.List;
//获取数据库数据
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addCityIv, moreIv;//添加各个城市、跳转到更多的界面
    LinearLayout pointLayout;//指示器里的小点点
    RelativeLayout outLayout;//
    ViewPager mainVp;
    //ViewPager的数据源
    List<Fragment> fragmentList;
    //表示需要显示的城市的集合
    List<String> cityList;
    //表示ViewPager的页数指数器显示集合
    List<ImageView> imgList;
    private CityFragmentPagerAdapter adapter;
    private SharedPreferences pref;
    private int bgNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCityIv = findViewById(R.id.main_iv_add);
        moreIv = findViewById(R.id.main_iv_more);
        pointLayout = findViewById(R.id.main_layout_point);
        outLayout = findViewById(R.id.main_out_layout);
        mainVp = findViewById(R.id.main_vp);
        //添加点击事件
        addCityIv.setOnClickListener(this);
        moreIv.setOnClickListener(this);
        //初始化Fragment
        fragmentList = new ArrayList<>();
        cityList = DBManager.queryAllCityName();  //获取数据库包含的城市信息列表
        //初始化imgList
        imgList = new ArrayList<>();

        if (cityList.size() == 0) {
            cityList.add("北京");
        }
        /* 因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下*/
        try {
            Intent intent = getIntent();
            String city = intent.getStringExtra("city");
            if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {//如果不包含city且不为空
                cityList.add(city);//则添加
            }
        } catch (Exception e) {
            Log.i("animee", "程序出现问题了！！");
        }
//        初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainVp.setAdapter(adapter);//设置给ViewPager对象——传入adapter
//        创建小圆点指示器
        initPoint();
//        设置最后一个城市信息
        mainVp.setCurrentItem(fragmentList.size() - 1);
//        设置ViewPager页面监听（让小圆点跟页面走）
        setPagerListener();
    }

    private void setPagerListener() {
        /* 设置监听事件*/
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imgList.size(); i++) {//遍历小圆点的集合，获取每一个
                    imgList.get(i).setImageResource(R.mipmap.a1);
                }
                imgList.get(position).setImageResource(R.mipmap.a2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initPoint() {
//        创建小圆点 ViewPager页面指示器的函数（页面数）
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pIv = new ImageView(this);//在ImageView上创建小圆点对象
            pIv.setImageResource(R.mipmap.a1);//设置要展示的背景
            pIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //设置外间距
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
            lp.setMargins(0, 0, 20, 0);
            imgList.add(pIv);
            pointLayout.addView(pIv);
        }
        //当前原点为绿色（进入时默认选中的是最后一个）
        imgList.get(imgList.size() - 1).setImageResource(R.mipmap.a2);
    }

    private void initPager() {
        /* 创建Fragment对象，添加到ViewPager数据源当中*/
        for (int i = 0; i < cityList.size(); i++) {//遍历citylist的长度，然后创建对应的Fragment页面
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city", cityList.get(i));
            cwFrag.setArguments(bundle);//传入bundle
            //加入集合中
            fragmentList.add(cwFrag);
        }
    }

    @Override
    //跳转
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.main_iv_add:
                intent.setClass(this, CityManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.main_iv_more:

                clearCache();
                break;

        }

    }
/***************************************************************************************************/
    private void clearCache() {
        /* 刷新：清除添加的函数*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("确定刷新吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> list = DBManager.queryAllCityName();//得到所有城市的名称
                cityList.clear();//重写加载之前，清空原本数据源
                cityList.addAll(list);//添加新的数据源
                fragmentList.clear();//先清空原来的集合
                initPager();//创建对应的fragment的内容，添加到fragmentList当中
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"已刷新！",Toast.LENGTH_SHORT).show();
                //跳转到主界面
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);//跳转
            }
        }).setNegativeButton("取消",null);
        builder.create().show();
    }
    /***************************************************************************************************/
    /* 当页面重写加载时会调用的函数，这个函数在页面获取焦点之前进行调用，此处完成ViewPager页数的更新*/
    @Override
    protected void onRestart() {
        super.onRestart();
        //获取数据库当中还剩下的城市集合
        List<String> list = DBManager.queryAllCityName();//得到剩下的所有城市的名称
        //判断是否删除了所有曾是，如果是，则自动添加北京
        if (list.size() == 0) {
            list.add("北京");
        }
        cityList.clear();//重写加载之前，清空原本数据源
        cityList.addAll(list);//添加新的数据源
//        剩余城市也要创建对应的fragment页面
        fragmentList.clear();//先清空原来的集合
        initPager();//创建对应的fragment的内容，添加到fragmentList当中
        adapter.notifyDataSetChanged();
//        页面数量发生改变，指示器（显示页面的小点点）的数量也会发生变化，重写设置添加指示器
        imgList.clear();//删除原来的指示器（数据库）
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
        mainVp.setCurrentItem(fragmentList.size() - 1);
    }
}
