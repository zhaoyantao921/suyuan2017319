package com.suyuan.shou.suyuan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView imageView;// 游标动画图片
    private int bmpW;// 动画图片宽度
    private int offset = 0;// 动画图片偏移量
    private final int pageCount = 4;
    private int currIndex = 0;// 当前页卡编号
    private ImageView textView1, textView2, textView3, textView4;//tab头标里的文字
    private View view1, view2, view3,view4;
    private List<View> views;// view数组

    private ViewPager viewPager=null; // 对应的viewPager
    private ArrayList<String> titleContainer = new ArrayList<String>();

    private PagerTabStrip tabs=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        InitImageView();
        InitTextView();
        InitViewPager();
    }

    private void InitViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.layout1, null);
        view2 = inflater.inflate(R.layout.layout2, null);
        view3 = inflater.inflate(R.layout.layout3, null);
        view4 = inflater.inflate(R.layout.layout4, null);
        /*出塘模块*/
        view1.findViewById(R.id.imageViewchutang).setOnClickListener(this);
        /*分包模块*/
        view2.findViewById(R.id.imageButtonfenbao).setOnClickListener(this);
        /*运输模块*/
        view3.findViewById(R.id.imageViewyunshu).setOnClickListener(this);
        /*设置页面的监听事件*/
        view4.findViewById(R.id.exit).setOnClickListener(this);
        view4.findViewById(R.id.update).setOnClickListener(this);
        view4.findViewById(R.id.version).setOnClickListener(this);
        view4.findViewById(R.id.help).setOnClickListener(this);
        view4.findViewById(R.id.link).setOnClickListener(this);
        view4.findViewById(R.id.setactivity).setOnClickListener(this);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void InitTextView() {
        textView1 = (ImageView) findViewById(R.id.text1);
        textView2 = (ImageView) findViewById(R.id.text2);
        textView3 = (ImageView) findViewById(R.id.text3);
        textView4 = (ImageView) findViewById(R.id.text4);
        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
        textView3.setOnClickListener(new MyOnClickListener(2));
        textView4.setOnClickListener(new MyOnClickListener(3));

    }
    /*初始化动画*/
    private void InitImageView() {
        imageView=(ImageView)findViewById(R.id.cursor);//找到游标
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / pageCount - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置

    }

    @Override//因为activity继承了点击监听接口
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.version:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.suyuan100);
                dialog.setTitle("提示");
                dialog.setMessage("版本号：v.1");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    //匿名内部类点击监听事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.update:
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIcon(R.drawable.suyuan100);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在检测更新");
                progressDialog.setCancelable(false);
                progressDialog.show();
                int FLAG=1;
                if(upsofe(FLAG)==0){
                    progressDialog.dismiss();
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
                    dialog2.setIcon(R.drawable.suyuan100);
                    dialog2.setTitle("提示");
                    dialog2.setMessage("已是最新版本");
                    dialog2.setCancelable(true);
                    dialog2.show();
                };
                break;
            case R.id.help:
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:10086"));//拨号功能
                startActivity(intent1);
                break;
            case R.id.link:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://kingofzyt.usa3v.com/web8/"));
                startActivity(intent);
                break;
            case R.id.imageViewchutang://出塘
                Intent intent3=new Intent(MainActivity.this,chutang.MainActivitychutang.class);
                startActivity(intent3);
                break;
            case R.id.imageButtonfenbao://分包
                Intent intent4=new Intent(MainActivity.this,fenbao.example.uhfsdkdemo.MainActivityfenbao.class);
                startActivity(intent4);
                break;
            case R.id.imageViewyunshu://运输
                Intent intent5=new Intent(MainActivity.this,com.example.scannertest.MainActivityyunshu.class);
                startActivity(intent5);
                break;
            case R.id.exit:
                final AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
                dialog2.setIcon(R.drawable.suyuan100);
                dialog2.setTitle("提示");
                dialog2.setMessage("退出软件？");
                dialog2.setCancelable(false);
                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    //匿名内部类点击监听事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog2.show();
                break;
            case R.id.setactivity:
                Intent intentseting=new Intent(MainActivity.this,chutang.SettingActivity.class);
                startActivity(intentseting);
                break;

        }
    }

    private int upsofe(int FLAG) {
        try{
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        /*下载新的更新包*/
        FLAG=0;
        return FLAG;
    }

    /*头标点击监听*/
    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }
    public class MyViewPagerAdapter extends PagerAdapter{
        private List<View> viewList;
        public MyViewPagerAdapter(List<View> viewList) {
            this.viewList=viewList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position),0);
            return viewList.get(position);
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(one * currIndex, one
                    * arg0, 0, 0);
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
            Log.v("TAG", "您选择了" + viewPager.getCurrentItem() + "页卡");
        }

    }
}
