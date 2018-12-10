package com.example.zhuodp.sreenshot;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnNormalSreenShot;
    private ImageView imgScreenShotResult;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListener();

    }

    private void findView(){
        btnNormalSreenShot = (Button)findViewById(R.id.btn_normalScreenShot);
        imgScreenShotResult =(ImageView)findViewById(R.id.id_screenShotResult);

    }
    private void setListener(){
        btnNormalSreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //方案1
                //imgScreenShotResult.setImageBitmap(screenShotWholeScreen());
                //方案2
                imgScreenShotResult.setImageBitmap(screenShotSingleView(btnNormalSreenShot));
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

    

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
