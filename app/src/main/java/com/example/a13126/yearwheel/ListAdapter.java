package com.example.a13126.yearwheel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 13126 on 2018/6/3.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String>arrayList;
    ImageView ppic;
    String imgurl="";
    ViewHolder finalViewHolder=new ViewHolder();
    Drawable drawable1;
     public ListAdapter(Context context, ArrayList<String>arrayList){
        this.context=context;
        this.arrayList=arrayList;
     }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;

        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.list_item_fragment,null);
            viewHolder=new ViewHolder();
            viewHolder.nowdate=view.findViewById(R.id.dateString);
            viewHolder.xinqing=view.findViewById(R.id.nowxq);
            viewHolder.tizhong=view.findViewById(R.id.tizhong);
            viewHolder.pic=view.findViewById(R.id.img);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        String[]msg=arrayList.get(i).split(",");
        viewHolder.tizhong.setText(msg[0]+"斤");
        viewHolder.xinqing.setText(msg[1]);
        viewHolder.nowdate.setText(msg[3]);
        imgurl=msg[2];
        Glide.with(context).load(imgurl).into(viewHolder.pic);
//        finalViewHolder = viewHolder;
        //需要在子线程中进行网络操作（耗时操作）
//                        MyThread myThread=new MyThread();
//                myThread.start();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                finalViewHolder.pic.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Drawable drawable1 = loadImageFromNetwork(imgurl);
//                        finalViewHolder.pic.setImageDrawable(drawable1) ;
//                    }
//                });
//            }
//        }).start();

        return view;
    }
    class MyThread extends Thread{
        @Override
        public void run() {
           drawable1 = loadImageFromNetwork(imgurl);
           //更新图片需要在主线程中进行
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);

        }
    }
    public class ViewHolder{
        TextView nowdate;
        TextView tizhong;
        TextView xinqing;
        ImageView pic;
    }
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        String []imglist=imageUrl.split("/");
        String imgname=imglist[imglist.length-1];
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), imgname);
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable ;
    }
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            //更新UI
            switch (msg.what)
            {
                case 1:
                    finalViewHolder.pic.setImageDrawable(drawable1) ;
                    break;
            }
        };
    };
}
