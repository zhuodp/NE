package com.example.zhuodp.sreenshot.TestActivity;

/*
    2018/12/10 -- zhuodapei
    notes:  这个用来测试截屏方法的可行性
            点击各个button会根据不同方法生成截屏结果

 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.zhuodp.sreenshot.R;
import com.example.zhuodp.sreenshot.ScreenShotUtils.ScreenShotHelper;
import com.example.zhuodp.sreenshot.ScreenShotUtils.ShotApplication;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private Button btnNormalSreenShot;
    private Button btnLinearLayoutScreenShot;
    private Button btnListViewScreenShot;
    private Button btnWholeScreenScreenShot;
    private Button btnLongScreenScreenShot;
    private Button btnSingleViewScreenShot;
    private Button btnOutOfBoundsTest;
    private boolean longScreenShotControlFlag;
    public static ImageView imgScreenShotResult;
    private ListView listView;
    private LinearLayout linearLayout;


    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;

    private ScreenShotHelper screenShotHelper;
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
        btnListViewScreenShot =(Button)findViewById(R.id.btn_listViewSreenShot);
        btnWholeScreenScreenShot =(Button)findViewById(R.id.btn_wholeScreenScreenShot);
        btnLongScreenScreenShot =(Button)findViewById(R.id.btn_longScreenScreenShot);
        btnSingleViewScreenShot =(Button)findViewById(R.id.btn_singleViewScreenShot);
        btnOutOfBoundsTest =(Button)findViewById(R.id.btn_outOfBoundsTest);

        imgScreenShotResult =(ImageView)findViewById(R.id.id_screenShotResult);
        listView = (ListView)findViewById(R.id.listView);
        container = (LinearLayout)findViewById(R.id.linearLayout);

    }
    private void initData(){
        //初始化ListView数据
        for (int i =1;i<=25;i++){
            listViewData.add(i);
        }
        listView= (ListView) findViewById(R.id.listView);
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(MainActivity.this, android.R.layout.simple_expandable_list_item_1,listViewData);
        listView.setAdapter(arrayAdapter);

        screenShotHelper = new ScreenShotHelper();

        mMediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //获取录屏权限
        startIntent();
    }
    private void setListener(){
        btnNormalSreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //方案1：普通截屏
                imgScreenShotResult.setImageBitmap(screenShotHelper.screenShotOnDecorView(MainActivity.this));
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
                linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                imgScreenShotResult.setImageBitmap(screenShotHelper.screenShotOnLinearLayout(linearLayout));
            }
        });

        //针对ListView
        btnListViewScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgScreenShotResult.setImageBitmap(screenShotHelper.screenShotOnListView(listView));
                Log.d("zhuodp","on Click");
                //screenShotOnScrollableView();
                //start();

            }
        });

        //针对全屏
        btnWholeScreenScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("zhuodp","整屏截图");
                Bitmap bitmap = screenShotHelper.sreenShotOnWholeScreen(getApplicationContext());
                screenShotHelper.setUpMediaProjection(getApplicationContext());
                //这里需要考虑时序
            }
        });


        //滚动截取长屏
        btnLongScreenScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(longScreenShotControlFlag){
                    bitmaps.clear();
                    imgScreenShotResult.setImageBitmap(null);
                    isScreenShots = true;
                    btnLongScreenScreenShot.setBackgroundColor(getResources().getColor(R.color.red));
                    longScreenShotControlFlag = false;
                    autoScroll();
                }else{
                    isScreenShots = false;
                    longScreenShotControlFlag = true;
                    btnLongScreenScreenShot.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });

        //针对单个View
        btnSingleViewScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgScreenShotResult.setImageBitmap(screenShotHelper.screenShotOnSingleView(btnSingleViewScreenShot));
            }
        });



    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            screenShotHelper.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //屏幕录制权限的结果
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
                result = resultCode;
                intent = data;
                ((ShotApplication)getApplication()).setResult(resultCode);
                ((ShotApplication)getApplication()).setIntent(data);
            }
        }
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











    //长截屏（单个可拖动View滚动截屏）
    private boolean isScreenShots = false;
    private ViewGroup container;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private void autoScroll() {
        final int delay = 16;//延时16毫秒分发滑动事件
        final int step = 10;//每次滑动距离5像素，可以根据需要调整（若卡顿的话实际滚动距离可能小于5）
        final MotionEvent motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis()
                , SystemClock.uptimeMillis()
                , MotionEvent.ACTION_DOWN
                , listView.getWidth() / 2
                , listView.getHeight() / 2
                , 0);
        //先分发 MotionEvent.ACTION_DOWN 事件，我们指定为按下位置是listview的中间位置，当然其他位置也可以
        //
        //View mView = getWindow().getDecorView();
        listView.dispatchTouchEvent(motionEvent);
        /*
        注意：
        查看Listview源码可知 滑动距离大于ViewConfiguration.get(view.getContext()).getScaledTouchSlop()时listview才开始滚动
         private boolean startScrollIfNeeded(int x, int y, MotionEvent vtev) {
            // Check if we have moved far enough that it looks more like a
            // scroll than a tap
            final int deltaY = y - mMotionY;
            final int distance = Math.abs(deltaY);
            final boolean overscroll = mScrollY != 0;
            if ((overscroll || distance > mTouchSlop) && (getNestedScrollAxes() & SCROLL_AXIS_VERTICAL) == 0) {
                ...
                return true;
            }
            return false;
        }
         */
        motionEvent.setAction(MotionEvent.ACTION_MOVE);
        motionEvent.setLocation(motionEvent.getX(), motionEvent.getY() - (ViewConfiguration.get(listView.getContext()).getScaledTouchSlop()));
        listView.dispatchTouchEvent(motionEvent);

        final int startScrollY = (int) motionEvent.getY();

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScreenShots == false) {//注意：我们无法通过滚动距离来判断是否滚动到了最后，所以需要通过其他方式停止滚动
                    drawRemainAndAssemble(startScrollY, (int) motionEvent.getY());
                    return;
                }
                //滚动刚好一整屏时画到bitmap上
                drawIfNeeded(startScrollY, (int) motionEvent.getY());

                motionEvent.setAction(MotionEvent.ACTION_MOVE); //延时分发 MotionEvent.ACTION_MOVE 事件
                /*
                  改变motionEvent的y坐标，nextStep越大滚动越快，但太大可能会导致掉帧，导致实际滚动距离小于我们滑动的距离

                  因为我们是根据(curScrollY - startScrollY) % container.getHeight() == 0来判定是否刚好滚动了一屏幕的，
                  所以快要滚动到刚好一屏幕位置时，修改nextStep的值，使下次滚动后刚好是一屏幕的距离。
                  当然nextStep也可以一直是1，这时就不需要凑整了，但这样会导致滚动的特别慢
                 */
                int nextStep;
                int gap = (startScrollY - (int) motionEvent.getY() + step) % container.getHeight();
                if (gap > 0 && gap < step) {
                    nextStep = step - gap;
                } else {
                    nextStep = step;
                }

                motionEvent.setLocation((int) motionEvent.getX(), (int) motionEvent.getY() - nextStep);
                listView.dispatchTouchEvent(motionEvent);

                listView.postDelayed(this, delay);
            }
        }, delay);
    }
    private void drawRemainAndAssemble(int startScrollY, int curScrollY) {
        //最后的可能不足一屏幕，需要单独处理
        if ((curScrollY - startScrollY) % container.getHeight() != 0) {
            Bitmap film = Bitmap.createBitmap(container.getWidth(), container.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas();
            canvas.setBitmap(film);
            container.draw(canvas);

            int part = (startScrollY - curScrollY) / container.getHeight();
            int remainHeight = startScrollY - curScrollY - container.getHeight() * part;
            Bitmap remainBmp = Bitmap.createBitmap(film, 0, container.getHeight() - remainHeight, container.getWidth(), remainHeight);
            bitmaps.add(remainBmp);
        }

        assembleBmp();

    }
    private void assembleBmp() {
        int h = 0;
        for (Bitmap bitmap : bitmaps) {
            h += bitmap.getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(container.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        for (Bitmap b : bitmaps) {
            canvas.drawBitmap(b, 0, 0, null);
            canvas.translate(0, b.getHeight());
        }
        ViewGroup.LayoutParams params = imgScreenShotResult.getLayoutParams();
        params.width = bitmap.getWidth() * 2;
        params.height = bitmap.getHeight() * 2;
        imgScreenShotResult.requestLayout();
        imgScreenShotResult.setImageBitmap(bitmap);
    }
    private void drawIfNeeded(int startScrollY, int curScrollY) {

        if ((curScrollY - startScrollY) % container.getHeight() == 0) {
            //正好滚动满一屏

            //为了更通用，我们是把ListView的父布局（和ListView宽高相同）画到了bitmap上

            Bitmap film = Bitmap.createBitmap(container.getWidth(), container.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas();
            canvas.setBitmap(film);
            container.draw(canvas);
            bitmaps.add(film);
        }
    }



}
