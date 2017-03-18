package fenbao.example.uhfsdkdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.suyuan.shou.suyuan.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;


/**
 * 6.22加入了同步中转站的功能
 * 7.01加入客户流向选择；
 */
public class PakgeActivity extends Activity {

    private static final String TAG ="PackageActivity" ;
    private Button button_ok;
    private String Title = "分包管理";
    private Spinner spinnerTrans;
    private ArrayAdapter<String> adatpterTrans;
    private Spinner spinnerUsers;
    private ArrayAdapter<String> adapterUsers;
    private TextView tv_writeData;
    private String[] StringTrans = {"000","不中转"};
    private String Data_trans = "000";//中转站
    private String Data_pakage = "000";//包数
    private ListView list_RFID;
    private String writeData;
    private ArrayList<String> listRFID;//用于存生成的rfid
    private ProgressDialog progressDialog;
    private Handler handler;
    private Button button_clear;
    private Button button_searchBT;

    private String getFlowURL="http://58.198.165.34:8080/myhttp/getFLOW.jsp?param1=11&param2=11";
    private ProgressDialog initprogressDialog;
    private String getUser="http://58.198.165.34:8080/myhttp/getUser.jsp?param1=11&param2=11";
    private String[] StringUsers={"0","客户"};
    private int m;
    private String postRfidUrl="http://58.198.165.34:8080/myhttp/doRFID.jsp?";
    private String sendid="0";
    private String ifbox=null;
    private Button button_post;
    private ProgressDialog snedprogressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(Title);
        setContentView(R.layout.pakgelayout);
        this.tv_writeData = (TextView) findViewById(R.id.textview_data);
        Bundle extras = getIntent().getExtras();
        writeData = extras.getString("writeData");
        tv_writeData.setText("标签：" + writeData);//31 02 04 03 20160620 16位
        listRFID = new ArrayList<String>();
        list_RFID = (ListView) findViewById(R.id.lv);
        initview();//加载视图函数
        initprogressDialog= ProgressDialog.show(this,"提示","更新中，请稍后");
        listener();
        final EditText center = (EditText) findViewById(R.id.editText_center);

        handler=new Handler(){
            @SuppressLint("ShowToast")
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x101){

                    list_RFID.setAdapter(new ArrayAdapter<String>(PakgeActivity.this,android.R.layout.simple_list_item_checked,listRFID));
                    progressDialog.dismiss();
                    //Toast.makeText(PakgeActivity.this, "分包标签生成成功", Toast.LENGTH_LONG);
                }else if(msg.what==0x102){
                    if(listRFID.isEmpty()){
                        Toast.makeText(PakgeActivity.this,"列表已经为空", Toast.LENGTH_LONG);
                    }else {
                        listRFID.clear();
                        list_RFID.setAdapter(new ArrayAdapter<String>(PakgeActivity.this, android.R.layout.simple_list_item_checked, listRFID));
                        progressDialog.dismiss();
                    }
                }else if(msg.what==0x103){
                    //initprogressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "中转站更新成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    adatpterTrans = new ArrayAdapter<String>(PakgeActivity.this, android.R.layout.simple_spinner_dropdown_item, StringTrans);
                    spinnerTrans.setAdapter(adatpterTrans);
                    spinnerTrans.setVisibility(View.VISIBLE);
                    spinnerTrans.setSelection(0);

                }else  if (msg.what==0x104||msg.what==0x106){
                    initprogressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "更新失败请检测网络连接", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if (msg.what==0x105){
                    initprogressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "用户列表更新成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    adapterUsers = new ArrayAdapter<String>(PakgeActivity.this, android.R.layout.simple_spinner_dropdown_item, StringUsers);
                    spinnerUsers.setAdapter(adapterUsers);
                    spinnerUsers.setVisibility(View.VISIBLE);
                    spinnerUsers.setSelection(0);

                }else if(msg.what==0x110){
                    snedprogressDialog.dismiss();
                    DisplayMessage("完成");
                }
                super.handleMessage(msg);
            }
        };
        MyThread t=new MyThread();
        t.start();
        GetUser t2=new GetUser();
        t2.start();
    }



    /*事件监听函数*/
    private void listener() {
        spinnerTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //确定了trans位；
                String[] str=null;
                str=StringTrans[position].split(" ");
                DisplayMessage(str[0]);
                if(str[0].length()==1){
                    Data_trans="00"+str[0];
                }else if(str[0].length()==2){
                    Data_trans="0"+str[0];
                }else {
                    Data_trans=str[0];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Data_trans="000";
            }
        });
        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] str=null;
                str=StringUsers[i].split(" ");
                DisplayMessage(str[0]);
                sendid=str[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        button_ok.setOnClickListener(new View.OnClickListener()  {
                    @Override
                 public void onClick(View v)  {
                 progressDialog = ProgressDialog.show(PakgeActivity.this, "提示", "正在生成分包条码", true);//显示进度条
                        final EditText center = (EditText) findViewById(R.id.editText_center);//m只能是1-999的数字
                        if(center.getText().toString().equals(""))
                        {
                            Message message=new Message();
                            message.what=0x101;
                            handler.sendMessage(message);
                            DisplayMessage("请输入数字");
                            return;
                        }
                        m = Integer.parseInt(center.getText().toString());
                        if(m<2){ifbox="0";}else{ifbox="1";}
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 0x101;
                                handler.sendMessage(message);
                                if( m<=1){
                                    String str = writeData.substring(0, 16) + Data_trans  + "001000";
                                    //每次生成一个str，加入
                                    listRFID.add(str);
                                } else if (1<m&&m <10) {//分包数小于10

                                    Data_pakage = "00" + (m);
                                    for (int i = 0; i < m; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + "00" + String.valueOf(i);
                                        //每次生成一个str，加入
                                        listRFID.add(str);
                                    }

                                } else if (m < 100) {//分包数大于10
                                    Data_pakage = "0" + String.valueOf(m);
                                    for (int i = 0; i < 10; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + "00" + String.valueOf(i);
                                        //每次生成一个str，加入
                                        listRFID.add(str);
                                    }
                                    for (int i = 10; i < m; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + "0" + String.valueOf(i);
                                        //每次生成一个str
                                        listRFID.add(str);
                                    }
                                } else if (m >= 100) {
                                    Data_pakage = String.valueOf(m);
                                    for (int i = 0; i < 10; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + "00" + String.valueOf(i);
                                        //每次生成一个str，加入
                                        listRFID.add(str);
                                    }
                                    for (int i = 10; i < m; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + "0" + String.valueOf(i);
                                        //每次生成一个str
                                        listRFID.add(str);
                                    }
                                    for (int i = 100; i < m; i++) {
                                        String str = writeData.substring(0, 16) + Data_trans + Data_pakage + String.valueOf(i);
                                        //每次生成一个str
                                        listRFID.add(str);
                                    }
                                } else {
                                    DisplayMessage("请输入1-999之间的数字！");
                                }

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        t.start();
                    }
                });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread =new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.what=0x102;
                        handler.sendMessage(message);
                    }
                });
                thread.start();
                spinnerUsers.setSelection(0);
                spinnerTrans.setSelection(0);
            }
        });
        button_searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*这里定义打印功能*/
                //把listRFID传到SearchBT中  ArrayList<String> listRFID
                Intent intent=new Intent();
                intent.putStringArrayListExtra("listRFID",listRFID);
                intent.setClass(PakgeActivity.this,SearchBT.class);
                startActivity(intent);
            }
        });
        button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snedprogressDialog= ProgressDialog.show(PakgeActivity.this,"提示","确认订单中");
                postRfid postrfid=new postRfid();
                postrfid.start();
            }
        });

        list_RFID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str=listRFID.get(i);
                DisplayMessage(str);
                /*Intent intentone=new Intent();
                intentone.putExtra("oneRFID",str);
                intentone.setClass(PakgeActivity.this,SearchBT.class);
                startActivity(intentone);*/
            }
        });


    }

    /*加载视图函数*/
    private void initview() {
        this.button_clear=(Button)findViewById(R.id.button_clear);
        this.button_ok = (Button) findViewById(R.id.ok);
        this.button_searchBT=(Button)findViewById(R.id.searchBT);
        this.spinnerTrans = (Spinner) findViewById(R.id.spinner_Trans);
        this.spinnerUsers=(Spinner)findViewById(R.id.spinner_Users);
        this.button_post=(Button)findViewById(R.id.post);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    class MyThread extends Thread {
        Document doc;
        String elementText;
        @Override
        public void run() {
            super.run();
            try {
                doc= Jsoup.connect(getFlowURL).timeout(3000).post();
                Document content = Jsoup.parse(doc.toString());
                elementText=content.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(elementText!=null) {
                StringTrans = elementText.split("#");
                Message message = new Message();
                message.what = 0x103;//成功
                handler.sendMessage(message);
            }else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 0x104;//失败
                handler.sendMessage(message);
            }
        }
    }
    private void DisplayMessage(String str) {
        Toast.makeText(PakgeActivity.this, str, Toast.LENGTH_SHORT).show();
    }
    class GetUser extends Thread {
        Document doc;
        String elementText;
        @Override
        public void run() {
            super.run();
            try {
                doc= Jsoup.connect(getUser).timeout(3000).post();
                Document content = Jsoup.parse(doc.toString());
                elementText=content.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(elementText!=null) {
                StringUsers = elementText.split("#");
                Message message = new Message();
                message.what = 0x105;//成功
                handler.sendMessage(message);
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
    }
    class postRfid extends Thread {
        String param1;//RFID_after
        String param2=sendid;
        String param3=ifbox;
        @Override
        public void run() {
            super.run();
            for(int i=0;i<listRFID.size();i++){
                param1=listRFID.get(i);
                try {
                    Jsoup.connect(postRfidUrl+"param1="+param1+"&param2="+param2+"&param3="+param3).timeout(3000).post();
                    Thread.sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message=new Message();
            message.what=0x110;
            handler.sendMessage(message);

        }

    }
}
