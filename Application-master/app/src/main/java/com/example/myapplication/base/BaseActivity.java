package com.example.myapplication.base;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import androidx.appcompat.app.AppCompatActivity;
//封装联网操作
public class BaseActivity extends AppCompatActivity implements Callback.CommonCallback<String>{

    public void loadData(String url){
        //封装请求参数
        RequestParams params = new RequestParams(url);
        //进行联网请求
        x.http().get(params,this);
    }
    @Override
    public void onSuccess(String result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
