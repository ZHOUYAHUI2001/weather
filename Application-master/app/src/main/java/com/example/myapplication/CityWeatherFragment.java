package com.example.myapplication;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.bean.WeatherBean;
import com.example.myapplication.db.DBManager;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;
/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends BaseFragment{
    TextView tempTv,cityTv,conditionTv,windTv,tempRangeTv,dateTv;
             //温度,城市，天气情况，风力，温度范围，时间
    ImageView dayIv;//今天天气
    LinearLayout futureLayout;//未来天气情况
    ScrollView outLayout;
    //地址拼接
    String url1 = "https://wis.qq.com/weather/common?source=pc&weather_type=observe|index|rise|alarm|air|tips|forecast_24h&province=";
    String url2 = "&city=";
    String city;
    String provice;
    private WeatherBean.DataBean.IndexBean index;
    private SharedPreferences pref;
    private int bgNum;
    private CityFragmentPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);

//        可以通过activity传值获取到当前fragment加载的是那个地方的省份和天气情况
        Bundle bundle = getArguments();
        String provice_city = bundle.getString("city");//接收city
//        获取省份
        if(provice_city.split(" ").length>1)
        {
            provice =provice_city.split(" ")[0];
            city = provice_city.split(" ")[1];
        }
        else
        {
            city = provice_city.split(" ")[0];
            provice = provice_city.split(" ")[0];
        }
        String url = url1+provice+url2+city;//网址合并
//          调用父类获取数据的方法
        loadData(url);
        return view;
    }

    @Override
    public void onSuccess(String result) {
//        解析并展示数据
        try {
            parseShowData(result);//获取信息
        } catch (ParseException e) {
            e.printStackTrace();
        }
//         更新数据

        int i = DBManager.updateInfoByCity(city, result);//要更新的城市名和result信息
        if (i<=0) {
//            更新数据库失败，说明没有这条城市信息，增加这个城市记录
            DBManager.addCityInfo(city,result);
            System.out.println("增加这个城市记录------------------------------------------------------");
        }

    }


    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
//        数据库当中查找上一次信息显示在Fragment当中
        String s = DBManager.queryInfoByCity(city);
        if (!TextUtils.isEmpty(s)) {//若不为空
            try {
                parseShowData(s);//解析它
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
    private void parseShowData(String result) throws ParseException {
//        使用gson解析数据
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);//将数据解析到WeatherBean中
        WeatherBean.DataBean resultsBean = weatherBean.getData();
        //获取指数信息
        index = resultsBean.getIndex();
//        设置TextView
        cityTv.setText(city);
//        获取今日天气情况
        WeatherBean.DataBean.ObserveBean todayDataBean = resultsBean.getObserve();
        String time = changeTime(todayDataBean.getUpdate_time());
        dateTv.setText("发布时间  "+time);
        windTv.setText("湿度 "+todayDataBean.getHumidity()+"%");
        tempRangeTv.setText("气压  "+todayDataBean.getPressure()+"hPa");
        conditionTv.setText(todayDataBean.getWeather_short());
//        获取实时天气温度情况，需要处理字符串
        tempTv.setText(todayDataBean.getDegree()+"°C");
//        获取未来三天的天气情况，加载到layout当中
        WeatherBean.DataBean.Forecast24hBean futureList = resultsBean.getForecast_24h();
        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);//每一天的情况
        itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        futureLayout.addView(itemView);
        TextView idateTv = itemView.findViewById(R.id.item_center_tv_date);
        TextView iconTv = itemView.findViewById(R.id.item_center_tv_con);
        TextView itemprangeTv = itemView.findViewById(R.id.item_center_tv_temp);
        TextView wind = itemView.findViewById(R.id.item_center_tv_winddirection);
//          获取对应的位置的天气情况
        idateTv.setText(futureList.get_$2().getTime()+"   明天");
        iconTv.setText(futureList.get_$2().getDay_weather());
        wind.setText(futureList.get_$2().getDay_wind_direction());
        itemprangeTv.setText(futureList.get_$2().getMin_degree()+"~"+futureList.get_$2().getMax_degree()+"°C");

        View itemView3 = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        futureLayout.addView(itemView3);
        TextView idateTv3 = itemView3.findViewById(R.id.item_center_tv_date);
        TextView iconTv3 = itemView3.findViewById(R.id.item_center_tv_con);
        TextView itemprangeTv3 = itemView3.findViewById(R.id.item_center_tv_temp);
        TextView wind3 = itemView3.findViewById(R.id.item_center_tv_winddirection);
//          获取对应的位置的天气情况
        idateTv3.setText(futureList.get_$3().getTime()+"   后天");
        iconTv3.setText(futureList.get_$3().getDay_weather());
        wind3.setText(futureList.get_$3().getDay_wind_direction());
        itemprangeTv3.setText(futureList.get_$3().getMin_degree()+"~"+futureList.get_$3().getMax_degree()+"°C");

        View itemView2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        futureLayout.addView(itemView2);
        TextView idateTv2 = itemView2.findViewById(R.id.item_center_tv_date);
        TextView iconTv2 = itemView2.findViewById(R.id.item_center_tv_con);
        TextView itemprangeTv2 = itemView2.findViewById(R.id.item_center_tv_temp);
        TextView wind2 = itemView2.findViewById(R.id.item_center_tv_winddirection);
//          获取对应的位置的天气情况
        idateTv2.setText(futureList.get_$4().getTime()+" 大后天");
        iconTv2.setText(futureList.get_$4().getDay_weather());
        wind2.setText(futureList.get_$4().getDay_wind_direction());
        itemprangeTv2.setText(futureList.get_$4().getMin_degree()+"~"+futureList.get_$4().getMax_degree()+"°C");

    }

//    时间格式化
    private String changeTime(String update_time) throws ParseException {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat sf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sfstr = "";
            sfstr = sf2.format(sf1.parse(update_time));
            return sfstr;
    }

    private void initView(View view) {
        //用于初始化控件操作
        tempTv = view.findViewById(R.id.frag_tv_currenttemp);
        cityTv = view.findViewById(R.id.frag_tv_city);
        conditionTv = view.findViewById(R.id.frag_tv_condition);
        windTv = view.findViewById(R.id.frag_tv_wind);
        tempRangeTv = view.findViewById(R.id.frag_tv_temprange);
        dateTv = view.findViewById(R.id.frag_tv_date);
        futureLayout = view.findViewById(R.id.frag_center_layout);
        outLayout = view.findViewById(R.id.out_layout);

    }
}
