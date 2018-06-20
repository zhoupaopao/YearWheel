package com.example.a13126.yearwheel;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lenovo on 2018/6/19.
 */

public class VideoFragment extends Fragment {
    Button play_video;
    private ArrayList<String> arrayList=new ArrayList<>();
    ImageView img;
    RelativeLayout pci;
    TextView dateString;
    int k;
    TextView tizhong;
    TextView nowxq;
    int j=0;
    MediaPlayer mediaPlayer=new MediaPlayer();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video,container,false);
        AsyncPlayer asyncPlayer= new AsyncPlayer("");
//                asyncPlayer.stop();

        String path="/sdcard/Music/123.mp3";
//               String mmurl="http://m10.music.126.net/20180619120640/f1405d9134f8cf8151de612aff17b525/ymusic/fed2/a985/cf41/63d8b110e26da939f299de6d14186ecd.mp3";
        Uri uri=Uri.parse(path);
//                Log.i("readmsg123: ", arrayList.get(1).split(",")[2]);
//                Glide.with(getActivity()).load("http://project.thinghigh.cn/uploads/20180608/c18972c8bcf2eaee8c1eb38674477076.jpg").into(img);
//        asyncPlayer.play(getActivity(),uri,true,AudioManager.STREAM_VOICE_CALL);
        play_video=view.findViewById(R.id.show_video);
        img=view.findViewById(R.id.img);
        pci=view.findViewById(R.id.pci);
        nowxq=view.findViewById(R.id.nowxq);

        dateString=view.findViewById(R.id.dateString);
        tizhong=view.findViewById(R.id.tizhong);
        //要读取文件中的内容
        readmsg();
        play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pci.setVisibility(View.VISIBLE);
                play_video.setVisibility(View.GONE);


//                MediaPlayer.create(this,  Uri.parse("storage/sdcard0/music.mp3"))
               //开始循环展示图片

//                   String[]szitem=listitem.split(",");
//                   tizhong.setText(szitem[0]);
//                   nowxq.setText(szitem[1]);
//                   dateString.setText(szitem[3]);
//                   Glide.with(getActivity()).load(szitem[2]).into(img);

                    try {
                        mediaPlayer.setDataSource("/sdcard/Music/123.mp3");
                        mediaPlayer.setLooping(true);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                final Timer timer=new Timer();

                 k=0;
                timer.schedule(new TimerTask()
                   {
                       public void run()
                       {
                           final String listitem=arrayList.get(k);
                           Message mm=new Message();
                           Bundle bundle=new Bundle();
                           bundle.putString("string",listitem);
                           mm.setData(bundle);
                           mm.what=0;
                           handler.sendMessage(mm);

                           if(k==arrayList.size()-1){
                               try {
                                   Thread.sleep(2000);
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               }
                               timer.cancel();
                               mediaPlayer.stop();
                                //需要到主线程中去进行操作
                               handler.post(runnable);
                               //多少秒之后播放
//                               mediaPlayer.seekTo();
                           }else{
                               Log.i("run: ", "run: ");
                               k++;
                           }

                       }
                   },0,2000);

            }
        });
        return view;
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            play_video.setVisibility(View.VISIBLE);
            pci.setVisibility(View.GONE);
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                Bundle dd=msg.getData();
                String listii=dd.getString("string");
                String[]szitem=listii.split(",");
                   tizhong.setText(szitem[0]+"斤");
                   nowxq.setText(szitem[1]);
                   dateString.setText(szitem[3]);
                   Glide.with(getActivity()).load(szitem[2]).into(img);
            }
            super.handleMessage(msg);
        }
    };
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();//释放音频
    }
}
