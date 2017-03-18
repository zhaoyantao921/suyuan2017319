package chutang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.suyuan.shou.suyuan.R;

import net.GetRypeList;
import net.Login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import dao.Pool;
import dao.Type;
/*
7.03登录验证
7.04准备写入获取
池塘
流向
种类 三个线程 任务
把获得的参数传递给第二个activity
7.05参数请求传递完成
7.10增加密码 用户图标
7.13修复登录bug
7.15隐藏标题栏
7.19增加进度条
* */

public class MainActivitychutang extends Activity  {

    private UhfReader reader;//读写器实例
    private ImageButton imageButton;
    private ScreenStateReceiver screenReceiver ;
    private String[] StringUserInfo;
    private String[] StringTYPE;
    private String [] StringPOOL;
    private String[] StringFLOW;
    private Handler handler;
    private ProgressDialog loginprogressdialog;
    private String getUsers="http://58.198.165.34:8080/myhttp/Login.jsp?";
    private EditText TextViewName;
    private EditText TextViewPassword;

    private int progress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //在setContentView之前执行
        //初始化进程对话框 提示准备好标签以供出塘写入数据
        setContentView(R.layout.mainlayoutchutang);//主页面，一个大图标

        final Intent intent=new Intent(MainActivitychutang.this,MoreHandleActivity.class);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                }
            }
        };
        setOverflowShowingAlways();
        Util.initSoundPool(this);
        screenReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        TextViewName=(EditText)findViewById(R.id.TextViewName);
        TextViewPassword=(EditText)findViewById(R.id.TextViewPassword);
        final ProgressDialog pd=ProgressDialog.show(MainActivitychutang.this,getResources().getString(R.string.message_conneting),getResources().getString(R.string.connecting_to_server));
        new GetRypeList(new GetRypeList.SuccessCallback() {
            @Override
            public void onSuccess(List<Type> typeList) {

                intent.putExtra("typeList",(Serializable) typeList);
                for (int i=0;i<typeList.size();i++){
                    StringTYPE[i]=typeList.get(i).getTypename();
                }
                intent.putExtra("StringTYPE",StringTYPE);
                pd.dismiss();

            }
        }, new GetRypeList.FailCallback() {
            @Override
            public void onFail() {
                DisplayMessage("类型列表更新失败");
            }
        });
        imageButton=(ImageButton)findViewById(R.id.imagebt);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入下一个activity
                Util.play(1,0);
                loginprogressdialog=ProgressDialog.show(MainActivitychutang.this,"提示","登录中");

                if(TextUtils.isEmpty(TextViewName.getText())){
                    DisplayMessage("用户名不能为空");
                }else if(TextUtils.isEmpty(TextViewPassword.getText())){
                    DisplayMessage("密码不能为空");
                }else if(TextViewName.getText()!=null&&TextViewPassword.getText()!=null){
                    final ProgressDialog pd=ProgressDialog.show(MainActivitychutang.this,getResources().getString(R.string.message_conneting),getResources().getString(R.string.connecting_to_server));
                    String name=TextViewName.getText().toString();
                    String password=TextViewPassword.getText().toString();
                    new Login(name, password, new Login.SuccessCallback() {
                        @Override
                        public void onSuccess(List<Pool> poolList) {
                            pd.dismiss();
                            intent.putExtra("poolList",(Serializable) poolList);
                            for (int i=0;i<poolList.size();i++){
                                StringPOOL[i]=poolList.get(i).getPoolid();
                            }


                            Log.d("pool",poolList.toString());
                            startActivity(intent);
                            finish();
                        }
                    }, new Login.FailCallback() {
                        @Override
                        public void onFail() {
                            pd.dismiss();
                            DisplayMessage("失败");
                        }
                    });



                }


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void DisplayMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    /*获池塘列表 */
    class MyThread extends Thread{
        Document doc;
        Document doc1;
        String   elementText;
        String   elementText1;
        @Override
        public void run() {
            super.run();
            try {//getUsers="http://58.198.165.34:8080/myhttp/Login.jsp?"
                doc= Jsoup.connect(getUsers).timeout(3000).post();
                doc1= Jsoup.connect("http://58.198.165.34:8080/myhttp/getPOOL.jsp?param1="+
                        TextViewName.getText().toString()).timeout(3000).post();
                Document content = Jsoup.parse(doc.toString());
                Document content1 = Jsoup.parse(doc1.toString());
                elementText=content.text();//转化成字符串
                elementText1=content1.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(elementText!=null) {
                StringUserInfo = elementText.split(" ");
                StringPOOL = elementText1.split("#");
                Message message = new Message();
                message.what = 0x101;//成功
                handler.sendMessage(message);
            }else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0x102;//失败
                handler.sendMessage(message);
            }
        }
    }
    //获取类型列表
    class GetType extends Thread{
        Document doc;
        String   elementText;
        @Override
        public void run() {
            super.run();
            try {
                doc= Jsoup.connect("http://58.198.165.34:8080/myhttp/getTYPE.jsp?param1=11&param2=11").timeout(3000).post();
                Document content = Jsoup.parse(doc.toString());
                elementText=content.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(elementText!=null) {
                StringTYPE = elementText.split("#");
                Message message = new Message();
                message.what = 0x105;//成功
                handler.sendMessage(message);
                progress=progress+10;
                progressBar.setProgress(progress);
            }else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0x106;//失败
                handler.sendMessage(message);
            }
        }
    }//OK
    // /获取流向列表
    class GetFlow extends Thread{
        Document doc;
        String   elementText;
        @Override
        public void run() {
            super.run();
            try {
                doc= Jsoup.connect("http://58.198.165.34:8080/myhttp/getFLOW.jsp?param1=11&param2=11").timeout(3000).post();
                Document content = Jsoup.parse(doc.toString());
                elementText=content.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(elementText!=null) {
                StringFLOW = elementText.split("#");
                Message message = new Message();
                message.what = 0x107;//成功
                handler.sendMessage(message);
                progress=progress+10;
                progressBar.setProgress(progress);
            }else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0x108;//失败
                handler.sendMessage(message);
            }
        }
    }//OK


    //从新启动函数
    void restart(){
        finish();
        Intent intent=new Intent(MainActivitychutang.this, MainActivitychutang.class);
        startActivity(intent);
    }





}
