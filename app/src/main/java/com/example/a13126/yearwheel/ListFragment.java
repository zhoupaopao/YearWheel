package com.example.a13126.yearwheel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a13126.yearwheel.pullableview.PullableListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 13126 on 2018/5/30.
 */

public class ListFragment extends Fragment {
    //读取本地文件，将数据拿出来，组成列表
    private PullableListView YWlist;
    private ArrayList<String>arrayList=new ArrayList<>();
    private ListAdapter adapter;
    PullToRefreshLayout refresh_view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        YWlist=view.findViewById(R.id.YWlist);
        refresh_view=view.findViewById(R.id.refresh_view);
        //要读取文件中的内容
        readmsg();
        doadapter();
        return view;

    }

    private void doadapter() {
        adapter=new ListAdapter(getActivity(),arrayList);
        refresh_view.setOnRefreshListener(new MyListener(){


            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
//                super.onLoadMore(pullToRefreshLayout);
//                for(int i=0;i<10;i++){
//                    arrayList.add(i+"");
//                }
                adapter.notifyDataSetChanged();
                //上拉加载
                new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        // 千万别忘了告诉控件刷新完毕了哦！
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

                    }
                }.sendEmptyMessageDelayed(0, 1000);

//                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
//                super.onRefresh(pullToRefreshLayout);
                readmsg1();
                Log.i("onRefresh: ", "onRefresh: ");
                new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        // 千万别忘了告诉控件刷新完毕了哦！
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);

                    }
                }.sendEmptyMessageDelayed(0, 1000);
            }
        });
        YWlist.setAdapter(adapter);
    }

    private void readmsg() {
//        String mmm=readFileData("message1.text");
        String path="/sdcard/YWMessage/message.text";
        String res= loadFromSDFile(path);
        Log.i("readmsg: ", res);
        String[] meslist=res.split(";");
        arrayList.clear();
        for(int i=0;i<meslist.length-1;i++){
            arrayList.add(meslist[i]);
            Log.i("readmsg: ", meslist[i]);
        }
    }
    private void readmsg1() {
//        String mmm=readFileData("message1.text");
        String path="/sdcard/YWMessage/message.text";
        String res= loadFromSDFile(path);
        Log.i("readmsg: ", res);
        String[] meslist=res.split(";");
        arrayList.clear();
        for(int i=0;i<meslist.length-1;i++){
            arrayList.add(meslist[i]);
            Log.i("readmsg: ", meslist[i]);
        }
        adapter.notifyDataSetChanged();
    }
    private String loadFromSDFile(String path) {
        String result="";

        try {
            File file=new File(path);
            int length= (int) file.length();
            byte[]buff=new byte[length];
            FileInputStream fileInputStream=new FileInputStream(file);
            fileInputStream.read(buff);
            fileInputStream.close();
            result=new String(buff,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"找不到指定文件",Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private String readFileData(String filename) {
        String result="";
        try {
            FileInputStream fileInputStream=getActivity().openFileInput(filename);
            int length=fileInputStream.available();
            byte[] buffer=new byte[length];
            fileInputStream.read(buffer);
            result=new String (buffer,"UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }
}
