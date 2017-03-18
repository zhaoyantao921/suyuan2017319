package chutang;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.suyuan.shou.suyuan.R;

/**
 * Created by zyt on 16/4/1.
 */
public class SettingPower extends Activity implements OnClickListener {

	private Button buttonMin;
	private Button buttonPlus;
	private Button buttonSet;
	private EditText editValues ;
	private int value = 26 ;//
	private UhfReader reader ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting_power);
		super.onCreate(savedInstanceState);
		initView();
		reader = UhfReader.getInstance();
	}
	
	private void initView(){
		buttonMin = (Button) findViewById(R.id.button_min);
		buttonPlus = (Button) findViewById(R.id.button_plus);
		buttonSet = (Button) findViewById(R.id.button_set);
		editValues = (EditText) findViewById(R.id.editText_power);
		
		buttonMin.setOnClickListener(this);
		buttonPlus.setOnClickListener(this);
		buttonSet.setOnClickListener(this);
		value =  getSharedValue();
		editValues.setText("" +value);
		
	}
	

	private int getSharedValue(){
		SharedPreferences shared = getSharedPreferences("power", 0);
		return shared.getInt("value", 26);
	}


	private void saveSharedValue(int value){
		SharedPreferences shared = getSharedPreferences("power", 0);
		Editor editor = shared.edit();
		editor.putInt("value", value);
		editor.commit();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_min:
			if(value > 16){
				value = value - 1;
			}
			editValues.setText(value + "");
			break;
		case R.id.button_plus:
			if(value < 26){
				value = value + 1;
			}
			editValues.setText(value + "");
			break;
		case R.id.button_set:
			if(reader.setOutputPower(value)){
				saveSharedValue(value);
				Toast.makeText(getApplicationContext(), "成功", 0).show();
			}else{
				Toast.makeText(getApplicationContext(), "失败", 0).show();
			}
			break;

		default:
			break;
		}
		
	}
	
	
}
