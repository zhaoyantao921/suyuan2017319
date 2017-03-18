package chutang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.suyuan.shou.suyuan.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MoreHandleActivity extends Activity implements OnClickListener {


    private String[] StringPool = {};
    private String[] StringType = {};
    private String[] StringFlow = {};
    private String[] Info;
    private Spinner spinnerPool;
    private Spinner spinnerType;
    private Spinner spinnerFlow;

    private Button buttonRead;
    private Button buttonWrite;

    private EditText editReadData;//读取数据展示区
    private Button buttonClear;

    private ArrayAdapter<String> adatpterPool;
    private ArrayAdapter<String> adatpterType;
    private ArrayAdapter<String> adatpterFlow;
    private Button buttonBack;
    private String Data_area = "00";
    private String Data_base = "00";
    private String Data_pool = "00";
    private String Data_type = "00";
    private String Data_flow = null;
    private UhfReader reader;
    private String Title = "出塘管理";
    private EditText edit_weight;
    private String baseURL = "http://58.198.165.34:8080/myhttp/doUHF.jsp";//处理页面

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogwrite;
    private Handler handler;

    private TextView tvUser;
    private TextView tvArea;
    private TextView tvBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.morelayout);
        this.setTitle(Title);



        initView();



        listener();
        reader = UhfReader.getInstance();
        if (reader == null) {
            Toast.makeText(getApplicationContext(), "serialport init fail", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void initView() {

        this.edit_weight = (EditText) findViewById(R.id.editText);
        this.buttonRead = (Button) findViewById(R.id.button_read);
        this.buttonWrite = (Button) findViewById(R.id.button_write);
        this.buttonClear = (Button) findViewById(R.id.button_readClear);
        this.buttonBack = (Button) findViewById(R.id.button_back);
        this.editReadData = (EditText) findViewById(R.id.linearLayout_readData);

        this.tvUser = (TextView) findViewById(R.id.tvuser);
        this.tvArea = (TextView) findViewById(R.id.tvarea);
        this.tvBase = (TextView) findViewById(R.id.tvbase);


        this.spinnerPool = (Spinner) findViewById(R.id.spinner_pool);
        this.adatpterPool = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StringPool);
        this.adatpterPool.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerPool.setAdapter(adatpterPool);
        this.spinnerPool.setVisibility(View.VISIBLE);

        this.spinnerType = (Spinner) findViewById(R.id.spinner_type);
        this.adatpterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StringType);
        this.adatpterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerType.setAdapter(adatpterType);
        this.spinnerType.setVisibility(View.VISIBLE);
        /*流向表*/
        this.spinnerFlow = (Spinner) findViewById(R.id.spinner_flow);
        this.adatpterFlow = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StringFlow);
        this.adatpterFlow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerFlow.setAdapter(adatpterFlow);
        this.spinnerFlow.setVisibility(View.VISIBLE);
    }


    private void listener() {
        this.buttonClear.setOnClickListener(this);
        this.buttonRead.setOnClickListener(this);
        this.buttonWrite.setOnClickListener(this);
        this.buttonBack.setOnClickListener(this);

        spinnerPool.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] str = null;
                str = StringPool[position].split(" ");
                if (str[0].length() == 1) {
                    Data_pool = "0" + str[0];
                    DisplayMessage("池塘" + Data_pool);
                } else {
                    Data_pool = str[0];
                    DisplayMessage("池塘" + Data_pool);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Data_pool = "00";
            }
        });
		/*种类表被选择*/
        spinnerType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 10) {
                    Data_type = "0" + String.valueOf(position);
                    DisplayMessage("类型" + Data_type);
                } else {
                    Data_type = String.valueOf(position);
                    DisplayMessage("类型" + Data_type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Data_type = "00";
            }
        });
		/*流向表被选择*/
        spinnerFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] str = null;
                str = StringFlow[position].split(" ");
                if (str[0].length() == 1) {
                    Data_flow = "0" + str[0];
                    DisplayMessage("流向" + Data_flow);
                    //DisplayMessage(Data_area+Data_base+Data_pool+Data_type+"000000000000000000");
                } else {
                    Data_flow = str[0];
                    DisplayMessage("流向" + Data_flow);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Data_flow = "00";
            }
        });
    }


    @Override
    public void onClick(View v) {
        byte[] accessPassword = Tools.HexString2Bytes("00000000");
        switch (v.getId()) {

            case R.id.button_read:

                if (accessPassword.length != 4) {
                    Toast.makeText(getApplicationContext(), "密码为4个字节", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] data = reader.readFrom6C(1, 1, 7, accessPassword);
                if (data != null && data.length > 1) {
                    String dataStr = Tools.Bytes2HexString(data, data.length);

                    editReadData.setText(dataStr.substring(2) + "\n");
                } else {

                    if (data != null) {
                        editReadData.setText("读数据失败，错误码：" + (data[0] & 0xff) + "\n");
                        return;
                    }
                    editReadData.setText("读取数据失败，返回值为空" + "\n");
                }
                break;
/*
*
* 写入数据接口 为writeData     String类型
* */
            case R.id.button_write:
                String weight;
                weight = edit_weight.getText().toString();
                if (weight == null) {
                    weight = "0";
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date curDate = new Date(System.currentTimeMillis());
                String Data_time = dateFormat.format(curDate);
                accessPassword = Tools.HexString2Bytes("00000000");
                //首先检测密码长度是不是4得倍数
                if (accessPassword.length != 4) {
                    Toast.makeText(getApplicationContext(), "密码为4个字节", Toast.LENGTH_SHORT).show();
                    return;
                }
                String writeData = "30" + Data_area + Data_base + Data_pool + Data_type + Data_time + "0000000000";//////////28位
                //////////////////
                if (writeData.length() % 4 != 0) {
                    Toast.makeText(getApplicationContext(), "写入数据长度是以字为单位1word = 2bytes", Toast.LENGTH_SHORT).show();
                }
                //把16禁止字符串转换成byte[]
                byte[] dataBytes = Tools.HexString2Bytes(writeData);

                //dataLen = dataBytes/2 dataLen  这里是写入语句
                boolean writeFlag = reader.writeTo6C(accessPassword, 1, 1, dataBytes.length / 2, dataBytes);

                if (writeFlag) {
                    editReadData.setText("写入数据成功" + "\n");
				/*写入成功的时候把信息上传到数据库中 doUHF.jsp*/
                    progressDialog = ProgressDialog.show(MoreHandleActivity.this, "提示", "开始上传数据库");
                    progressDialog.setCancelable(true);
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 0x101) {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "上传成功", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            if (msg.what == 0x102) {
                                progressDialog.dismiss();//用于消除初始化汽车列表对话框
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "上传失败", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            super.handleMessage(msg);
                        }
                    };
                    //开始异步任务
                    String myRFID = writeData.substring(2, 27);//RFID
                    String myFlow = Data_flow + "#" + weight;
                    HttpAsyncTask asyncTask = new HttpAsyncTask(editReadData);
                    asyncTask.execute(baseURL, myRFID, myFlow);
                    Message message = new Message();
                    message.what = 0x101;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(message);

                } else {
                    editReadData.setText("写入数据失败" + "\n");
                }
                break;

            case R.id.button_readClear:
                editReadData.setText("");
                break;

            case R.id.button_back:
                finish();
                Intent intent =new Intent();
                intent.setClass(MoreHandleActivity.this, com.suyuan.shou.suyuan.MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }

    }

    private void DisplayMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


}


