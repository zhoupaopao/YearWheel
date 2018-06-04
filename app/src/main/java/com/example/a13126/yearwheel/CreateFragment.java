package com.example.a13126.yearwheel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.BuildBean;
import com.dou361.dialogui.bean.TieBean;
import com.dou361.dialogui.listener.DialogUIItemListener;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dou361.dialogui.DialogUIUtils.showToast;

/**
 * Created by 13126 on 2018/5/30.
 */

public class CreateFragment extends Fragment {
    private ImageView addimg;
    private EditText ettz;
    private EditText etxq;
    private Button submit;
    //    public static final int REQUEST_CODE_SELECT = 100;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private String url = "http://project.thinghigh.cn/index.php/api/v1/uploadTxt";
    private BuildBean dialog;
    String lastestIMGurl;

    //创建数据
    //体重，图片，一句话，日期（自动获取）
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        addimg = view.findViewById(R.id.addimg);
        ettz = view.findViewById(R.id.ettz);
        etxq = view.findViewById(R.id.etxq);
        submit = view.findViewById(R.id.submit);
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TieBean> strings = new ArrayList<TieBean>();
                strings.add(new TieBean("拍照"));
                strings.add(new TieBean("相册"));
                DialogUIUtils.showSheet(getActivity(), strings, "取消", Gravity.BOTTOM, true, true, new DialogUIItemListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
//                        showToast(text + "---" + position);
                        switch (position) {
                            case 0: // 直接调起相机
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(1);
                                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(1);
                                Intent intent1 = new Intent(getActivity(), ImageGridActivity.class);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onBottomBtnClick() {
                        showToast("取消");
                    }
                }).show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = DialogUIUtils.showLoading(getActivity(), "加载中...", false, true, true, true);
                dialog.show();

                uploadMultiFile(lastestIMGurl);
            }
        });
        return view;

    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */

    public static String compressImage(String filePath) {

        //原文件
        File oldFile = new File(filePath);


        //压缩文件路径 照片路径/
        String targetPath = oldFile.getPath();
        int quality = 70;//压缩比例0-100
        Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片
//        int degree = getRotateAngle(filePath);//获取相片拍摄角度
//
//        if (degree != 0) {//旋转照片角度，防止头像横着显示
//            bm = setRotateAngle(degree,bm);
//        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private void uploadMultiFile(String imgUrl) {
        String imageType = "multipart/form-data";
        File file = new File(imgUrl);//imgUrl为图片位置
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image[]", "123.jpg", fileBody)
                //下面这个是传送base64文件的
//                .addFormDataPart("image[]", "data:image/jpeg;base64,"+imgba)
//                .addFormDataPart("imagetype", imageType)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("onFailure", "onFailure: ");
                Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                DialogUIUtils.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
//                Log.i("result", "http://ring.thinghigh.cn"+htmlStr);
                com.alibaba.fastjson.JSONObject jsonObject = (com.alibaba.fastjson.JSONObject) JSON.parse(htmlStr);
                com.alibaba.fastjson.JSONObject datamsg = jsonObject.getJSONObject("data");
                String img_name = datamsg.getString("path");
//                String img_name=jsonObject.getString("data");
//                Toast.makeText(ChooseUpPicActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                final String IMAGE_URL = img_name;
                Log.i("result", IMAGE_URL);
                //这个是个子线程，不能在子线程里面弹出toast，需要到主线程中去
                Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //放在UI线程弹Toast
                                Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_SHORT).show();
                            }
                        });

                new Thread(new Runnable() {
                    //                    Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                    @Override
                    public void run() {
                        dialog.dialog.dismiss();


                        //将文件等信息写入文件
                        //先将字符串拼接起来
                        StringBuffer message = new StringBuffer("");
                        //线程安全，可能效率不如string
                        String tizhong = ettz.getText().toString().trim() + ",";
                        String xinqing = etxq.getText().toString().trim() + ",";
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        String riqi = simpleDateFormat.format(date);
                        message = message.append(tizhong).append(xinqing).append(IMAGE_URL + ",").append(riqi+";");
                        String filepath = "/sdcard/YWMessage/";
                        writeTxtToFile(message, filepath, "message.text");


                        // post() 特别关键，就是到UI主线程去更新图片
//                        last_pic.post(new Runnable(){
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                last_pic.setImageDrawable(drawable) ;
//                                dialog.dialog.dismiss();
//                            }}) ;
                    }

                }).start();

            }
        });
    }

    private void writeTxtToFile(StringBuffer message, String filepath, String filename) {
        //先要判断是否有这个文件夹，没有就创建，然后创建文件
        makeFilePath(filepath, filename);
        //总的文件路径
        String strFilepath = filepath + filename;
        //每次写入都换行
        String contentStr = message.toString() + "\r\n";
        File file = new File(strFilepath);
        try {
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilepath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf=new RandomAccessFile(file,"rwd");
            raf.seek(file.length());
            raf.write(contentStr.getBytes());
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeFilePath(String filepath, String filename) {
        File file = null;
        //生成文件夹,看看是否存在
        makeRootDirectory(filepath);
        //文件夹有了，看看文件是否有了
        file = new File(filepath + filename);
        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeRootDirectory(String filepath) {
        File file = null;
        file = new File(filepath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        String[] imglist = imageUrl.split("/");
        String imgname = imglist[imglist.length - 1];
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

        return drawable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    String picurl = images.get(0).path;
                    Log.i("onActivityResult: ", picurl);
                    //下面的方法有压缩功能
                    Log.i("onActivityResult: ", compressImage(picurl));

//                    selImageList.addAll(images);
//                    adapter.setImages(selImageList);
                    Glide.with(getActivity())                             //配置上下文
                            .load(Uri.fromFile(new File(picurl)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                            .error(R.mipmap.default_image)           //设置错误图片
                            .placeholder(R.mipmap.default_image)     //设置占位图片
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                            .into(addimg);
                    lastestIMGurl = picurl;
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
//                    selImageList.clear();
//                    selImageList.addAll(images);
//                    adapter.setImages(selImageList);
                    String picurl = images.get(0).path;
                    Log.i("onActivityResult: ", picurl);
//                    Log.i("onActivityResult: ", compressImage(picurl));
                    Glide.with(getActivity())                             //配置上下文
                            .load(Uri.fromFile(new File(picurl)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                            .error(R.mipmap.default_image)           //设置错误图片
                            .placeholder(R.mipmap.default_image)     //设置占位图片
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                            .into(addimg);
                    lastestIMGurl = picurl;
                }
            }
        }
    }
}
