package com.example.a13126.yearwheel;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dou361.dialogui.DialogUIUtils;

import java.util.ArrayList;

//年轮app，记录每天的变化
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment>fragments;
    private ViewpagerAdapter adapter;
    private ArrayList<String>titlelist=new ArrayList<>();

//类似年轮的app，需要每天添加一张图片，最好如果今天没有添加可以发送个广播推送一下。
//实现原理，添加一张图片，然后填上现在的体重，上传到后台，返回一个图片的url，最好这个图片能做一下水印，上面有体重，然后我把这个数据存放在本地，在列表中展示，如果sp里面存放不了那么多数据的话就存放在一个文件里面

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initListener() {

    }

    private void initView() {
        Context mContext = getApplication();
        DialogUIUtils.init(mContext);
        viewPager=findViewById(R.id.viewpager);
        tabLayout=findViewById(R.id.tablayout);
        fragments=new ArrayList<>();
        fragments.add(new CreateFragment());
        fragments.add(new ListFragment());
        titlelist.add("创建");
        titlelist.add("展示");
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.black));

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setElevation(tabLayout, 10);
        tabLayout.setupWithViewPager(viewPager);
        adapter=new ViewpagerAdapter(getSupportFragmentManager(),fragments,titlelist);
        viewPager.setAdapter(adapter);
    }
}
