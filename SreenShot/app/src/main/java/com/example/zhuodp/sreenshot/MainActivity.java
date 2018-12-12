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
<<<<<<< HEAD
import android.graphics.Picture;
import android.graphics.PixelFormat;
=======
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
>>>>>>> dev
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
<<<<<<< HEAD
=======
import android.os.Environment;
>>>>>>> dev
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
<<<<<<< HEAD
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.View;
=======
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
>>>>>>> dev
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
<<<<<<< HEAD
=======
import android.widget.ListAdapter;
>>>>>>> dev
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

<<<<<<< HEAD
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

=======
import com.pgssoft.scrollscreenshot.ScrollScreenShot;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

>>>>>>> dev
public class MainActivity extends Activity {

    private Button btnNormalSreenShot;
    private Button btnLinearLayoutScreenShot;
<<<<<<< HEAD
    private ImageView imgScreenShotResult;
    private ListView listView  ;
=======
    private Button btnListViewScreenShot;
    private ImageView imgScreenShotResult;
    private ListView listView;
    private LinearLayout linearLayout;
>>>>>>> dev
    ArrayList<Integer> listViewData =new ArrayList<Integer>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initData();
        setListener();
    }







<<<<<<< HEAD
    private void findView(){
        btnNormalSreenShot = (Button)findViewById(R.id.btn_normalScreenShot);
        btnLinearLayoutScreenShot =(Button)findViewById(R.id.btn_linearLayoutScreenShot);
=======

    private void findView(){
        btnNormalSreenShot = (Button)findViewById(R.id.btn_normalScreenShot);
        btnLinearLayoutScreenShot =(Button)findViewById(R.id.btn_linearLayoutScreenShot);
        btnListViewScreenShot =(Button)findViewById(R.id.btn_listViewSreenShot);
>>>>>>> dev
        imgScreenShotResult =(ImageView)findViewById(R.id.id_screenShotResult);
        listView = (ListView)findViewById(R.id.listView);

    }
    private void initData(){
<<<<<<< HEAD
        for(int i =0;i<10;i++){
=======
        for(int i =0;i<90;i++){
>>>>>>> dev
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
<<<<<<< HEAD
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                imgScreenShotResult.setImageBitmap(getLinearLayoutBitmap(linearLayout));
=======
                linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                imgScreenShotResult.setImageBitmap(screenShotOnLinearLayout(linearLayout));
            }
        });

        btnListViewScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //imgScreenShotResult.setImageBitmap(screenShotOnListView(listView));
                Log.d("zhuodp","on Click");
                screenShotOnScrollableView();
                start();
>>>>>>> dev
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
<<<<<<< HEAD
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout) {
=======
    public static Bitmap screenShotOnLinearLayout(LinearLayout linearLayout) {
>>>>>>> dev
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

<<<<<<< HEAD




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


=======
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
    public static Bitmap screenShotOnListView(ListView listview) {

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
        },10*1000);
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


>>>>>>> dev
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
