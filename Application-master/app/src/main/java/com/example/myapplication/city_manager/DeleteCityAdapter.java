package com.example.myapplication.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import java.util.List;

public class DeleteCityAdapter extends BaseAdapter{
    Context context;//传入上下文对象
    List<String>mDatas;//传入数据源
    List<String>deleteCitys;//表示要删除的城市集合

    public DeleteCityAdapter(Context context, List<String> mDatas,List<String>deleteCitys) {
        this.context = context;
        this.mDatas = mDatas;
        this.deleteCitys = deleteCitys;
    }

    @Override
    public int getCount() {
        return mDatas.size();//返回集合的长度
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);//返回指定位置的数据
    }

    @Override
    public long getItemId(int position) {
        return position;//返回位置
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {//判断是否由能够赋用的convertView,没有的话去声明
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final String city = mDatas.get(position);
        //设置到TextView上
        holder.tv.setText(city);
        //此处仅是临时的删除
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//将mDatas指定位置的这条记录删除掉
                mDatas.remove(city);//一旦执行到此处的删除，则先记录到deleteCitys当中
                deleteCitys.add(city);
                notifyDataSetChanged(); //删除了提示适配器更新（只是在listview中删除了，尚未对数据库进行操作，也没有保存这条删除的记录，只有在点击右上角的√时才对数据库进行操作）
            }
        });
        return convertView;
    }
    //ViewHolder进行声明
    class ViewHolder{
        TextView tv;//一个TextView
        ImageView iv;
        //通过构造方法传进进来itemView
        public ViewHolder(View itemView){
            //初始化这两个控件
            tv = itemView.findViewById(R.id.item_delete_tv);
            iv = itemView.findViewById(R.id.item_delete_iv);
        }
    }
}
