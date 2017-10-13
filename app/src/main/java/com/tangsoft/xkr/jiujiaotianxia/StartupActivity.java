package com.tangsoft.xkr.jiujiaotianxia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-05-09.
 */

public class StartupActivity extends AppCompatActivity {

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.viewGroup)
    ViewGroup group;
    ArrayList<View> list;
    ImageView imageView;
    ImageView[] imageViews;
    private ImageView mStartButton = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);
        welcome();
    }

    public void welcome() {

        LayoutInflater inflater = getLayoutInflater();
        list = new ArrayList<View>();
        list.add(inflater.inflate(R.layout.welcome_item1, null));
        View last = inflater.inflate(R.layout.welcome_item2, null);

        mStartButton = (ImageView) last.findViewById(R.id.button_start);
        if (mStartButton != null) {
            mStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMainActivity();
                }
            });
        }
        list.add(last);

        int margin=12;
        int width=12;
        imageViews = new ImageView[list.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);
        for (int which = 0; which < list.size(); which++) {
            imageView = new ImageView(this);
            imageView.setMinimumWidth(width);
            imageView.setMinimumHeight(width);
            imageView.setMaxWidth(width*3/2);
            imageView.setMaxHeight(width*3/2);
            imageView.setClickable(false);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageViews[which] = imageView;

            group.addView(imageView);
        }


        viewPager.setAdapter(new MyAdapter());
        viewPager.addOnPageChangeListener(new MyListener());
    }

    void gotoMainActivity(){
        SharedPreferences preferences = getSharedPreferences("welcome",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("welcome",true);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView(list.get(arg1));
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            arg0.addView(list.get(arg1));
            return list.get(arg1);
        }
    }

    class MyListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int which) {

        }
    }
}
