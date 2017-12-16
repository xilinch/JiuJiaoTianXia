package com.tangsoft.xkr.jiujiaotianxia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;


/**
 * Created by xilinch on 17-12-16.
 */

public class DrawActivity extends Activity implements SensorEventListener {

    private TextView tv_title;
    private TextView tv_introduce;
    private LinearLayout back;
    private ImageView iv1;
    private ImageView iv2;
    private Handler myHandler = new Handler();
    private SensorManager mSensorManager;
    public static final String TAG_URL = "TAG_URL";
    private boolean isCQ = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        initView();
        initListener();
    }

    private void initView(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        back = (LinearLayout) findViewById(R.id.back);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 为方向传感器注册监听器
        if(mSensorManager != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private void initListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(getIntent() != null && getIntent().getExtras() != null){
            final String url = getIntent().getExtras().getString("");
            tv_introduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DrawActivity.this, WebViewActivity.class);
                    intent.putExtra(TAG_URL, url);
                    DrawActivity.this.startActivity(intent);
                }
            });
        }
    }

    /**
     * 显示前后
     */
    private synchronized void showBf(){
        if(!isCQ){
            try {
                Glide.with(DrawActivity.this).load("file:///android_asset/bf.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(iv1, 1));
                iv2.setVisibility(View.INVISIBLE);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCQ();
                    }
                }, 5750);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 显示左右
     */
    private synchronized void showLf(){
        if(!isCQ){
            iv2.setVisibility(View.INVISIBLE);
            Glide.with(DrawActivity.this).load("file:///android_asset/lf.gif").diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(iv1, 1));
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showCQ();
                }
            }, 5750);
        }

    }


    /**
     * 显示出签
     */
    private synchronized void showCQ(){
        iv2.setVisibility(View.VISIBLE);
        Glide.with(DrawActivity.this).load("file:///android_asset/cq.gif").into(new GlideDrawableImageViewTarget(iv2, 1));
        isCQ = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent != null){
            float[] values = sensorEvent.values;
            int type = sensorEvent.sensor.getType();
            StringBuilder sb = null;
            switch (type){
                case Sensor.TYPE_ACCELEROMETER:
                    sb = new StringBuilder();
                    sb.append("加速度传感器返回数据：");
                    sb.append("X方向的加速度：");
                    sb.append(values[0]);
                    sb.append("Y方向的加速度：");
                    sb.append(values[1]);
                    sb.append("Z方向的加速度：");
                    sb.append(values[2]);
                    if(values[0] > 30 || values[0] <= -20){
                        //视为左右
                        showLf();
                        Toast.makeText(DrawActivity.this,"左右",Toast.LENGTH_SHORT).show();
                        System.out.println(sb.toString());
                    } else if (values[1] > 30 || values[1] <= -20 || values[1] > 30 || values[1] <= -20){
                        showBf();
                        Toast.makeText(DrawActivity.this,"前后",Toast.LENGTH_SHORT).show();
                        System.out.println(sb.toString());
                    }
                    break;
                case Sensor.TYPE_ORIENTATION:
                    sb = new StringBuilder();
                    sb.append("\n方向传感器返回数据：");
                    sb.append("\n绕Z轴转过的角度：");
                    sb.append(values[0]);
                    sb.append("\n绕X轴转过的角度：");
                    sb.append(values[1]);
                    sb.append("\n绕Y轴转过的角度：");
                    sb.append(values[2]);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sb = new StringBuilder();
                    sb.append("\n陀螺仪传感器返回数据：");
                    sb.append("\n绕X轴旋转的角速度：");
                    sb.append(values[0]);
                    sb.append("\n绕Y轴旋转的角速度：");
                    sb.append(values[1]);
                    sb.append("\n绕Z轴旋转的角速度：");
                    sb.append(values[2]);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sb = new StringBuilder();
                    sb.append("\n磁场传感器返回数据：");
                    sb.append("\nX轴方向上的磁场强度：");
                    sb.append(values[0]);
                    sb.append("\nY轴方向上的磁场强度：");
                    sb.append(values[1]);
                    sb.append("\nZ轴方向上的磁场强度：");
                    sb.append(values[2]);
                    break;
                case Sensor.TYPE_GRAVITY:
                    sb = new StringBuilder();
                    sb.append("\n重力传感器返回数据：");
                    sb.append("\nX轴方向上的重力：");
                    sb.append(values[0]);
                    sb.append("\nY轴方向上的重力：");
                    sb.append(values[1]);
                    sb.append("\nZ轴方向上的重力：");
                    sb.append(values[2]);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sb = new StringBuilder();
                    sb.append("\n线性加速度传感器返回数据：");
                    sb.append("\nX轴方向上的线性加速度：");
                    sb.append(values[0]);
                    sb.append("\nY轴方向上的线性加速度：");
                    sb.append(values[1]);
                    sb.append("\nZ轴方向上的线性加速度：");
                    sb.append(values[2]);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sb = new StringBuilder();
                    sb.append("\n温度传感器返回数据：");
                    sb.append("\n当前温度为：");
                    sb.append(values[0]);
                    break;
                case Sensor.TYPE_LIGHT:
                    sb = new StringBuilder();
                    sb.append("\n光传感器返回数据：");
                    sb.append("\n当前光的强度为：");
                    sb.append(values[0]);
                    break;
                case Sensor.TYPE_PRESSURE:
                    sb = new StringBuilder();
                    sb.append("\n压力传感器返回数据：");
                    sb.append("\n当前压力为：");
                    sb.append(values[0]);
                    break;
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }
}
