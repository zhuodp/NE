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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.zhuodp.sreenshot.ScreenShotUtils.ScreenShotHelper;
import com.example.zhuodp.sreenshot.ScreenShotUtils.ShotApplication;

import java.io.File;
import java.io.FileOutputStream;
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
                imgScreenShotResult.setImageBitmap(screenShotNormalScreen());
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
                imgScreenShotResult.setImageBitmap(screenShotOnLinearLayout(linearLayout));
            }
        });

        //针对ListView
        btnListViewScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgScreenShotResult.setImageBitmap(screenShotOnListView(listView));
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

    //方案1：不带状态栏
    private Bitmap screenShotNormalScreen(){
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
    public static Bitmap screenShotOnLinearLayout(LinearLayout linearLayout) {
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

    //方案二：针对RecyclerView的截取(采用List存储Item的视图，需要考虑OOM的情况，经测试1000个item足以导致内存不足)
    /** https://gist.github.com/PrashamTrivedi/809d2541776c8c141d9a */
    public static Bitmap screenShotOnRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }
    //方案三：针对ListView的截取
    /**
     * http://stackoverflow.com/questions/12742343/android-get-screenshot-of-all-listview-items
     */
    public static Bitmap screenShotOnListView(ListView listview)
    {

        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();

        for (int i = 0; i < itemscount; i++) {

            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }

        Bitmap bigbitmap =
                Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();

            bmp.recycle();
            bmp = null;
        }

        return bigbitmap;
    }


    //https://android-notes.github.io/2016/12/03/android%E9%95%BF%E6%88%AA%E5%B1%8F%E5%8E%9F%E7%90%86/的demo
    private void start(){
        Log.d("zhuodp","start函数被调用");
        final View view=findViewById(R.id.relativeLayout);
        final ScrollableViewScreenShotUtil scrollableViewRECUtil=new ScrollableViewScreenShotUtil(view,ScrollableViewScreenShotUtil.VERTICAL);
        scrollableViewRECUtil.start(new ScrollableViewScreenShotUtil.OnRecFinishedListener() {
            @Override
            public void onRecFinish(Bitmap bitmap) {
                Log.d("zhuodo","onRecFinish");
                File f= Environment.getExternalStorageDirectory();
                System.out.print(f.getAbsoluteFile().toString());
                Toast.makeText(getApplicationContext(),f.getAbsolutePath(),Toast.LENGTH_LONG).show();
                try {
                    Log.d("zhuodp",f.toString()+"aaa/rec"+System.currentTimeMillis()+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG,60,new FileOutputStream(new File(f,"aaa/rec"+System.currentTimeMillis()+".jpg")));
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                    imgScreenShotResult.setImageBitmap(bitmap); //zhuodp: 尝试把结果直接绘制到屏幕上
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        // scrollableViewRECUtil
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollableViewRECUtil.stop();
            }
        },9*1000);
    }
    private void screenShotOnScrollableView(){

        File file=new File(Environment.getExternalStorageDirectory(),"aaa");
        Log.d("zhuodp",file.toString());
        if(!file.exists()) {
            file.mkdirs();
        }
        /*if(file!=null) {
            for (File f : file.listFiles()) {
                f.delete();
            }
        }*/
        Log.d("zhuodp","screenShotSrollableView被调用");

        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Log.d("zhuodp","onGlobalLayout被调用");
                start();
            }
        });
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);
            //Intent intent = new Intent(getApplicationContext(), Service1.class);
            //startService(intent);
            //Log.i(TAG, "start service Service1");
        }else{
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            screenShotHelper.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        }
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }else if(data != null && resultCode != 0){
                Log.i(TAG, "user agree the application to capture screen");
                //Service1.mResultCode = resultCode;
                //Service1.mResultData = data;
                result = resultCode;
                intent = data;
                ((ShotApplication)getApplication()).setResult(resultCode);
                ((ShotApplication)getApplication()).setIntent(data);

                Log.i(TAG, "start service Service1");


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











    //长截屏
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
