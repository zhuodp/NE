package com.example.zhuodp.sreenshot;

/*
    2018/12/10 --zhuodapei
    方案三 ： 利用Android 5.0之后的录屏API进行截屏，取某一帧作为截屏结果
    优点：
        ·状态栏也可以截取
        ·可以在其他APP之上进行截取
    缺点：
        Android5.0之下使用受限
        比较笨重

 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Button btnNormalSreenShot;
    private Button btnLinearLayoutScreenShot;
    private ImageView imgScreenShotResult;
    private ListView listView  ;
    ArrayList<Integer> listViewData =new ArrayList<Integer>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initData();
        setListener();

    }







    private void findView(){
        btnNormalSreenShot = (Button)findViewById(R.id.btn_normalScreenShot);
        btnLinearLayoutScreenShot =(Button)findViewById(R.id.btn_linearLayoutScreenShot);
        imgScreenShotResult =(ImageView)findViewById(R.id.id_screenShotResult);
        listView = (ListView)findViewById(R.id.listView);

    }
    private void initData(){
        for(int i =0;i<10;i++){
            listViewData.add(i);
        }

        ArrayAdapter<Integer> listViewAdapter =new ArrayAdapter<Integer>(
                MainActivity.this,android.R.layout.simple_list_item_1,listViewData);

        listView.setAdapter(listViewAdapter);
    }
    private void setListener(){
        btnNormalSreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //方案1：普通截屏
                imgScreenShotResult.setImageBitmap(screenShotWholeScreen());
                //方案1衍生：截取View
                //imgScreenShotResult.setImageBitmap(screenShotSingleView(btnNormalSreenShot));
                //方案2 ：针对WebView
                //imgScreenShotResult.setImageBitmap(screenShotOnWebView());
            }
        });

        //针对LinearLayout
        btnLinearLayoutScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                imgScreenShotResult.setImageBitmap(getLinearLayoutBitmap(linearLayout));
            }
        });


    }

    //方案1：不带状态栏
    private Bitmap screenShotWholeScreen(){
        View mView = getWindow().getDecorView();
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        Bitmap mScreenShotResult = mView.getDrawingCache();
        if(mScreenShotResult==null){
            Log.e("MainActivity","截屏Bitmap为null");
            return null;

        }

        mScreenShotResult.setHasAlpha(false);
        mScreenShotResult.prepareToDraw();

        return mScreenShotResult;
    }
    //方案1的衍生：截取某个view在屏幕内的可见区域(初步测试可行)
    private Bitmap screenShotSingleView(View view){
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    //方案二：针对WebView
    private Bitmap screenShotOnWebView(WebView webView){
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
                snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }

    //长截屏;
    //方案一：针对LinearLayout和ScrollView(未考虑内部嵌套其他可拖动view)
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            h += linearLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(linearLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
