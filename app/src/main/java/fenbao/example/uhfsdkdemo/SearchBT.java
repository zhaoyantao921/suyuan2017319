package fenbao.example.uhfsdkdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.suyuan.shou.suyuan.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fenbao.example.lvrenyang.myprinter.Global;
import fenbao.example.lvrenyang.myprinter.WorkService;
import fenbao.example.lvrenyang.utils.DataUtils;
import fenbao.example.lvrenyang.utils.TimeUtils;


public class SearchBT extends Activity implements OnClickListener {

    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private LinearLayout linearLayoutdevices;//设备列表
    private BroadcastReceiver broadcastReceiver=null;
    private static ArrayList<String> listQR=null;
    private IntentFilter intentFilter=null;
    private static Handler mHandler=null;
    private static String TAG="连接蓝牙打印机";
    private static String oneQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TAG);
        setContentView(R.layout.searchbtlayout);//主视图
        findViewById(R.id.buttonSearchBT).setOnClickListener(this);//按钮
        progressBar = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
        linearLayoutdevices=(LinearLayout)findViewById(R.id.linearlayoutdevices);
        progressDialog =new ProgressDialog(this);
        initBroadcast();//初始化广播
        listQR= getIntent().getStringArrayListExtra("listRFID");
       // oneQR=getIntent().getStringExtra("oneRFID");
        mHandler =new MHandler(this);
        WorkService.addHandler(mHandler);
        Toast.makeText(SearchBT.this,"点击图标搜索", Toast.LENGTH_LONG);
    }

    private void initBroadcast() {
        //新建广播接收器，覆写接收函数；
        broadcastReceiver =new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();//得到intent的行为就是一个字符串
                BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //如果 是 蓝牙的发现行为
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    if(device==null)
                        return;
                    final String address =device.getAddress();//获取地址
                    String name=device.getName();
                    if(name==null) name="BT";
                    else if(name.equals(address))name="BT";
                    //生成一个按钮
                    Button button=new Button(context);
                    button.setText("    "+name+":"+address);//把搜索到的蓝牙设备以按钮的形式显示出来
                    button.setGravity(android.view.Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setBackgroundResource(R.drawable.bt11);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            WorkService.workThread.disconnectBt();//没有连接这个才能改变
                            //然后弹出进度条
                            progressDialog.setMessage("正在打印...");
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            WorkService.workThread.connectBt(address);


                        }
                    });
                    button.getBackground().setAlpha(100);//设置半透明效果
                    linearLayoutdevices.addView(button);//添加这个视图
                }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    progressBar.setIndeterminate(true);
                }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    progressBar.setIndeterminate(false);
                }

            }
        };
        //注册广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        uninitBroadcast();
    }

    @Override
    public void onClick(View v) {
        //点击搜寻
        switch (v.getId()) {
            case R.id.buttonSearchBT: {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    finish();
                    break;
                }

                if (!adapter.isEnabled()) {
                    if (adapter.enable()) {
                        while (!adapter.isEnabled())
                            ;
                        Log.v(TAG, "Enable BluetoothAdapter");
                    } else {
                        finish();
                        break;
                    }
                }
               if(null != WorkService.workThread) {
                   WorkService.workThread.disconnectBt();
               }
                adapter.cancelDiscovery();
                linearLayoutdevices.removeAllViews();
                TimeUtils.WaitMs(10);
                adapter.startDiscovery();
                break;
            }


        }
    }
    static class MHandler extends Handler {

        WeakReference<SearchBT> mActivity;

        MHandler(SearchBT activity) {
            mActivity = new WeakReference<SearchBT>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchBT theActivity = mActivity.get();
            switch (msg.what) {
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? Global.toast_success//异常。。。
                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Connect Result: " + result);
                    theActivity.progressDialog.cancel();

                    if (1 == result) {
                      //7.14.如果连接成功运行测试函数http://58.198.165.10:8080/Tracecc/t_monitor.jsp?RFID=3103010220151125014004002
					  
                        int i;
                        for (i = 0; i < listQR.size(); i++) {
                                String str = listQR.get(i);
                                if (!str.isEmpty())
                                    PrintTest(str);
                        }


                    }
                    break;
                }
                case Global.CMD_POS_SETQRCODERESULT:
                    break;


            }
        }
/*连接成功之后会在这里出现测试页*/
        void PrintTest(String param) {
            String str = "   NO."+param;
            //byte[] tmp1 = { 0x1b, 0x40, (byte) 0xB6, (byte) 0xFE, (byte) 0xCE,
            //(byte) 0xAC, (byte) 0xC2, (byte) 0xEB, 0x0A };
            byte[] tmp2 = { 0x1b, 0x21, 0x01};//格式
            byte[] tmp3 = { 0x0A, 0x0A, 0x0A, 0x0A };
            // byte[] buf = DataUtils.byteArraysToBytes(new byte[][] { tmp1, str.getBytes(), tmp2, str.getBytes(), tmp3 });
            byte[] buf = DataUtils.byteArraysToBytes(new byte[][] {str.getBytes(),tmp3 });

            if (WorkService.workThread.isConnected()) {
                Bundle data2 = new Bundle();
                data2.putByteArray(Global.BYTESPARA1, buf);
                data2.putInt(Global.INTPARA1, 0);
                data2.putInt(Global.INTPARA2, buf.length);
                /*这里可以搞一个对话框询问是否进行打印*/
                Bundle data = new Bundle();
                data.putString(Global.STRPARA1,"http://58.198.165.10:8080/Tracecc/t_monitor.jsp?RFID="+param);//
                data.putInt(Global.INTPARA1, 5);//   宽度控制单个模块宽度
                data.putInt(Global.INTPARA2, 4 ); // 版本控制模块数量
                data.putInt(Global.INTPARA3, 1);//  容错
                WorkService.workThread.handleCmd(Global.CMD_POS_SETQRCODE, data);
                WorkService.workThread.handleCmd(Global.CMD_WRITE, data2);

                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }





            } else {
                Toast.makeText(mActivity.get(), Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }
        }
       /* void PrintTest2(String param2) {
            param2 = null;
            //byte[] tmp1 = { 0x1b, 0x40, (byte) 0xB6, (byte) 0xFE, (byte) 0xCE,
            //(byte) 0xAC, (byte) 0xC2, (byte) 0xEB, 0x0A };
            byte[] tmp2 = { 0x1b, 0x21, 0x01};
            //byte[] tmp3 = { 0x0A, 0x0A, 0x0A, 0x0A };
            // byte[] buf = DataUtils.byteArraysToBytes(new byte[][] { tmp1, str.getBytes(), tmp2, str.getBytes(), tmp3 });
            byte[] buf = DataUtils.byteArraysToBytes(new byte[][] {param2.getBytes(),tmp2 });

            if (WorkService.workThread.isConnected()) {

                Bundle data2 = new Bundle();
                data2.putByteArray(Global.BYTESPARA1, buf);
                data2.putInt(Global.INTPARA1, 0);
                data2.putInt(Global.INTPARA2, buf.length);
                WorkService.workThread.handleCmd(Global.CMD_WRITE, data2);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(mActivity.get(), Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }
        }*/
    }

}
