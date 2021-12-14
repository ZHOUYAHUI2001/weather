package com.example.myapplication.city_manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.WeatherBean;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity implements View.OnClickListener{
    EditText searchEt;
    ImageView submitIv;
    GridView searchGv;
    String[]hotCitys = {"北京","上海","广州","深圳","珠海","佛山","南京","苏州","厦门",
    "长沙","成都","福州","杭州","武汉","青岛","西安","太原","沈阳","重庆","天津","南宁"};
    private ArrayAdapter<String> adapter;
    String url1 = "https://wis.qq.com/weather/common?source=pc&weather_type=observe|index|rise|alarm|air|tips|forecast_24h&province=";
    String url2 = "&city=";
    String city;
    String provice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        searchEt = findViewById(R.id.search_et);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);
        //为submitIv设置点击事件
        submitIv.setOnClickListener(this);
        //设置适配器
        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCitys);
        //当前的上下文对象，加载到布局R.layout.item_hotcity2中，数据源是hotCitys
        searchGv.setAdapter(adapter);
        setListener();//设置监听
    }
    /*设置监听事件，接收在MainActivity*/
    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取指定位置的内容
                city = hotCitys[position];
                //获取省份
                provice=GetProvice(city);
                //判断city是否是有效数据
                String url = url1+provice+url2+city;
                //进行数据的加载
                loadData(url);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv_submit:
                city = searchEt.getText().toString();
                //输入是否为空
                if (!TextUtils.isEmpty(city)) {
                    //是否能获取到该城市，通过看返回的数据是否符合要求（执行父类BaseActivity的loadData的方法，看是成功还是失败）
                    //拼接合适字符串（网址已经由CityWeatherFragment复制过来了）
                    provice=GetProvice(city);
                    String url = url1+provice+url2+city;
                    //调用父类加载网络数据的方法
                    loadData(url);
                    //成功调用onSuccess方法
                }else {
                    Toast.makeText(this,"输入内容不能为空！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(String result) {//result是获取到的监听数据
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);//解析成WeatherBean类，得到weatherBean
        if (weatherBean.getData()!=null) {//这个城市存在，跳转到主页面
            //开个新的栈，把原来栈的内容全部清空
            Intent intent = new Intent(this, MainActivity.class);//得到Intent对象
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//清空原来的栈再开一个新的栈
            city=provice+" "+city;
            //把新设置的城市信息传递到MainActivity中
            intent.putExtra("city",city);
            startActivity(intent);
        }else{
            Toast.makeText(this,"暂时未收入此城市天气信息...",Toast.LENGTH_SHORT).show();
        }
    }

    private String GetProvice(String city) {
        String[]Citys = {"北京","上海","广东省 广州","广东省 深圳","广东省 珠海","广东省 佛山",
                "江苏省 南京","江苏省 苏州","福建省 厦门","湖南省 长沙","四川省 成都","福建省 福州","浙江省 杭州",
                "湖北省 武汉","山东省 青岛","陕西省 西安","山西省 太原","辽宁省 沈阳","重庆","天津","广西省 南宁"};
        for(int i=0;i<Citys.length;i++){
            if(Citys[i].contains(city)){
                if(Citys[i].split(" ").length>1)
                {   provice =Citys[i].split(" ")[0];
                }
                else
                {
                    provice = Citys[i].split(" ")[0];
                }
                break;
            }
        }
        return provice;
    }
}
