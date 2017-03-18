package com.example.scannertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.google.zxing.client.android.CaptureActivity;
import com.suyuan.shou.suyuan.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/*用于绑定传感器与商品信息的app还有车辆信息*/
/*6.22新增MyThread用于同步汽车列表*/
/*修改图标*/
public class MainActivityyunshu extends Activity {
	private int membank=1;//数据区
	private int addr = 1;//起始地址
	private int length = 7;//读取数据的长度
	private UhfReader reader;
	private String baseURL="http://58.198.165.33:8080/myhttp/doGET.jsp";
	private String getCarURL="http://58.198.165.33:8080/myhttp/getCAR.jsp?param1=car&param2=11";
	private Handler handler;
	public static final int SCAN_CODE = 1;
	byte[] accessPassword = Tools.HexString2Bytes("00000000");
	private ProgressDialog progressDialog;
	private ProgressDialog initprogressDialog;
	private ArrayAdapter<String> car_adapter;
	private String[] Str_Car={"车辆信息列表"};//用于保存汽车列表
	private String NumCar=null;//用于保存车辆编号；
	private Spinner car_spinner;
	private EditText editText;//读取重量
	private String weight=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initprogressDialog=ProgressDialog.show(MainActivityyunshu.this,"提示","程序初始化中...");
		setContentView(R.layout.activity_mainyunshu);
		car_spinner= (Spinner) findViewById(R.id.spinner);
		car_spinner.setVisibility(View.VISIBLE);
		editText =(EditText)findViewById(R.id.edit_weight);
		ImageButton button = (ImageButton) findViewById(R.id.scan_button);
		//main UI 线程 处理其他线程传来的消息
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0x101){
					progressDialog.dismiss();
					Toast toast = Toast.makeText(getApplicationContext(),
							"绑定成功", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				if(msg.what==0x102){
					initprogressDialog.dismiss();//用于消除初始化汽车列表对话框
					car_adapter = new ArrayAdapter<String>(MainActivityyunshu.this, android.R.layout.simple_spinner_dropdown_item, Str_Car);
					car_spinner.setAdapter(car_adapter);
					Toast toast = Toast.makeText(getApplicationContext(),
							"初始化成功", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

				if(msg.what==0x103){
					initprogressDialog.dismiss();
					Toast toast = Toast.makeText(getApplicationContext(),
							"初始化失败，请检测网络连接！", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				super.handleMessage(msg);
			}
		};
		MyThread t=new MyThread();
		t.start();
		//汽车条目选择监听。。。。。。
		car_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				DisplayMessage("选择的是："+i);//把获取的i的值作为第三个参数
				NumCar=i+"";
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				DisplayMessage("未选择车辆信息，请先选择车辆信息！");
			}
		});
		//扫码监听。。。。。
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				weight=editText.getText().toString();
				Intent intent = new Intent(MainActivityyunshu.this, CaptureActivity.class);
				startActivityForResult(intent, SCAN_CODE);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SCAN_CODE:
			final TextView scanResult = (TextView) findViewById(R.id.scan_result);

			if (resultCode == RESULT_OK) {
				final String result = data.getStringExtra("scan_result");
				scanResult.setText(result);
				//弹出对话框提示要放置传感器
				new AlertDialog.Builder(MainActivityyunshu.this).setTitle("提示")
						.setMessage("请放置传感器")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//显示进度条
								progressDialog=ProgressDialog.show(MainActivityyunshu.this,"提示","正在绑定商品与传感器");
								reader=UhfReader.getInstance();//获取读写器实例
								reader.setOutputPower(26);//设置功率
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								if(reader==null){
									return;
								}
								byte[] Mytag= reader.readFrom6C(membank, addr, length, accessPassword);
								String StrMyTag =  Tools.Bytes2HexString(Mytag, Mytag.length);
								scanResult.append("传感器："+StrMyTag);
								//开启提交线程，提交到doGET.jsp处理页面
								/*****************************************************/
								DisplayMessage("开始发送数据....");
								HttpAsyncTask asyncTask = new HttpAsyncTask(scanResult);//结果反馈
								// 将URL和两个字符串参数传递进去
								String param1 =MyStrSub(result)+NumCar;//怎么检测RFID=后面的东西
								String param2 =StrMyTag+weight;
								//还有第三个参数。。。。
								asyncTask.execute(baseURL, param1, param2);
								Message message=new Message();
								message.what=0x101;
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								handler.sendMessage(message);
								/************************************/
							}
						})
						.setCancelable(true)
						.show();

			} else if (resultCode == RESULT_CANCELED) {
				scanResult.setText("没有扫描出结果");
			}
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		if(reader != null){
			reader.close();
		}
		super.onDestroy();
	}
	private void DisplayMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	private String MyStrSub(String str){
		String mystr="";
		int i =str.indexOf("=");
        mystr=str.substring(i+1);
		return mystr;
	}

	class MyThread extends Thread{
		Document doc;
		String   elementText;
		@Override
		public void run() {
			super.run();
			try {
				doc= Jsoup.connect(getCarURL).timeout(3000).post();
				Document content = Jsoup.parse(doc.toString());
				elementText=content.text();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(elementText!=null) {
				Str_Car = elementText.split(" ");
				Message message = new Message();
				message.what = 0x102;
				handler.sendMessage(message);
			}else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message message = new Message();
				message.what = 0x103;
				handler.sendMessage(message);

			}
		}
	}

}
