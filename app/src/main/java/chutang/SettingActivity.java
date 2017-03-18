package chutang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.NewSendCommendManager;
import com.android.hdhe.uhf.reader.UhfReader;
import com.suyuan.shou.suyuan.R;

/**
 * Created by zyt on 16/4/1.
 */
public class SettingActivity extends Activity implements OnClickListener {
	private ImageButton button1;
	private ImageButton button2;
	private ImageButton button3;
	private ImageButton button4;
	private Spinner spinnerSensitive;
	private Spinner spinnerPower;
	private Spinner spinnerWorkArea;
	private EditText editFrequency;
	private String[] powers = {"26dbm","24dbm","20dbm","18.5dbm","17dbm","15.5dbm","14dbm","12.5dbm"};
	private String[] sensitives = null;
	
	private String[] areas = null;
	private ArrayAdapter<String> adapterSensitive;
	private ArrayAdapter<String> adapterPower;
	private ArrayAdapter<String> adapterArea;
	private UhfReader reader ;
	private int sensitive = 0;
	private int power = 0 ;
	private int area = 0;
	private int frequency = 0;
	private TextView textTips ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting_activity);
		super.onCreate(savedInstanceState);
		reader = UhfReader.getInstance();
		initView();
	}
		
	private void initView(){
		button1 = (ImageButton) findViewById(R.id.button_min);
		button2 = (ImageButton) findViewById(R.id.button_plus);
		button3 = (ImageButton) findViewById(R.id.button_set);
		button4 = (ImageButton) findViewById(R.id.buttonfour);
		
		textTips = (TextView) findViewById(R.id.textViewTips);
		spinnerSensitive = (Spinner) findViewById(R.id.spinner1);
		spinnerPower = (Spinner) findViewById(R.id.spinner2);
		spinnerWorkArea = (Spinner) findViewById(R.id.spinner3);
		editFrequency = (EditText) findViewById(R.id.edit4);
		sensitives = getResources().getStringArray(R.array.arr_sensitivity);
		areas = getResources().getStringArray(R.array.arr_area);
		
		adapterSensitive = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sensitives);
		adapterPower = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, powers);
		adapterArea = new ArrayAdapter<String>(this	, android.R.layout.simple_dropdown_item_1line, areas);
		spinnerSensitive.setAdapter(adapterSensitive);
		spinnerPower.setAdapter(adapterPower);
		spinnerWorkArea.setAdapter(adapterArea);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		spinnerWorkArea.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
									   int position, long id) {
//				if(position == 5){
//					area = position + 1;
//				}else{
//					area = position ;
//				}
				switch (position) {
				case 0:
					area = 1;
					textTips.setText(R.string.china2Freq);
					break;
				case 1:
					area = 2;
					textTips.setText(R.string.usaFreq);
					break;
				case 2:
					area = 3;
					textTips.setText(R.string.euFreq);
					break;
				case 3:
					area = 4;
					textTips.setText(R.string.china1Freq);
					break;
				case 4:
					area = 6;
					textTips.setText(R.string.koreaFreq);
					break;

				default:
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinnerSensitive.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				
				Log.e("", sensitives[position]);
				switch (position) {
				case 0:
					sensitive = NewSendCommendManager.SENSITIVE_HIHG;
					break;
				case 1:
					sensitive = NewSendCommendManager.SENSITIVE_MIDDLE;
					break;
				case 2:
					sensitive = NewSendCommendManager.SENSITIVE_LOW;
					break;
				case 3:
					sensitive = NewSendCommendManager.SENSITIVE_VERY_LOW;
					break;

				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinnerPower.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				Log.e("", powers[position]);
				switch (position) {
				case 0:
					power = 2600;
					break;
				case 1:
					power =2400;
					break;
				case 2:
					power = 2000;
					break;
				case 3:
					power = 1850;
					break;
				case 4:
					power = 1700;
					break;
				case 5:
					power = 1550;
					break;
				case 6:
					power = 1400;
					break;
				case 7:
					power = 1250;
					break;

				default:
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		Log.e("", "sensitive = " + sensitive+ "; power =  " + power);
		switch (v.getId()) {
		case R.id.button_min:
			//reader.setSensitivity(sensitive);
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		case R.id.button_plus:
			//reader.setOutputPower(power);
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		case R.id.button_set:
//			reader.setWorkArea(area);
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		case R.id.buttonfour:
			String freqStr = editFrequency.getText().toString();
			if(freqStr == null || "".equals(freqStr)){
				Toast.makeText(getApplicationContext(), R.string.freqSetting, 0).show();
				return;
			}
//			reader.setFrequency(frequency, 0, 0);
			Toast.makeText(getApplicationContext(), R.string.setSuccess, 0).show();
			break;
		default:
			break;
		}
		
	}
}
