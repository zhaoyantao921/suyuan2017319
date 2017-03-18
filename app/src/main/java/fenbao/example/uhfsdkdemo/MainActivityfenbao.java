package fenbao.example.uhfsdkdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.suyuan.shou.suyuan.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import fenbao.example.lvrenyang.myprinter.Global;
import fenbao.example.lvrenyang.myprinter.WorkService;
import fenbao.example.lvrenyang.utils.FileUtils;


/**
 * 6.22新加入线程 同步中转站
 *7.14修验证地址
 */
public class MainActivityfenbao extends Activity {


	private UhfReader reader ; //超高频读写器
	private String Title="RFID采集";
	private ScreenStateReceiver screenReceiver ;
	private static Handler mHandler = null;


	byte[] accessPassword = Tools.HexString2Bytes("00000000");
	private ProgressDialog progressDialog;
	private String StrMyTag=null;
	private ImageButton imageButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setOverflowShowingAlways();
		setContentView(R.layout.mainlayoutfenbao);
               this.setTitle(Title);
		Util.initSoundPool(this);
		imageButton=(ImageButton)findViewById(R.id.imageButton);
		imageButton.setOnClickListener(new OnClickListener()  {
			@Override
			public void onClick(View v)throws NullPointerException {
				Util.play(1,0);//滴一声
				progressDialog= ProgressDialog.show(MainActivityfenbao.this,"提示","请放置货物标签");
				progressDialog.setCancelable(true);
				reader= UhfReader.getInstance();//获取读写器实例
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(reader==null){
					return;
				}

				byte[] Mytag= reader.readFrom6C(1, 1, 7, accessPassword);
				String StrMyTag= Tools.Bytes2HexString(Mytag, Mytag.length);

				if (StrMyTag.length()==28){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();

					}
					Util.play(1,3);//滴一声
					progressDialog.dismiss();
					Intent intent = new Intent(MainActivityfenbao.this, PakgeActivity.class);
					DisplayMessage(StrMyTag);
					//把epc的值传入下一个activity中
					try {
						intent.putExtra("writeData", StrMyTag.substring(2, 18));
						startActivity(intent);
					}catch (Exception e){
						return;
					}

				}
			}
		});
		//添加广播，默认屏灭时休眠，屏亮时唤醒
		screenReceiver = new ScreenStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		//registerReceiver(screenReceiver, filter);

		InitGlobalString();
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);

		if (null == WorkService.workThread) {
			Intent intent = new Intent(this, WorkService.class);
			startService(intent);
		}
		handleIntent(getIntent());


	}
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				handleSendText(intent); // Handle text being sent
			} else if (type.startsWith("image/")) {
				handleSendImage(intent); // Handle single image being sent
			} else {
				handleSendRaw(intent);
			}
		}
	}
	//print.................................................
	private void handleSendText(Intent intent) {
		Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (textUri != null) {

			if (WorkService.workThread.isConnected()) {
				byte[] buffer = { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x01 }; // 设置中文，切换双字节编码。
				Bundle data = new Bundle();
				data.putByteArray(Global.BYTESPARA1, buffer);
				data.putInt(Global.INTPARA1, 0);
				data.putInt(Global.INTPARA2, buffer.length);
				WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
			}
			if (WorkService.workThread.isConnected()) {
				String path = textUri.getPath();
				String strText = FileUtils.ReadToString(path);
				byte buffer[] = strText.getBytes();

				Bundle data = new Bundle();
				data.putByteArray(Global.BYTESPARA1, buffer);
				data.putInt(Global.INTPARA1, 0);
				data.putInt(Global.INTPARA2, buffer.length);
				data.putInt(Global.INTPARA3, 128);
				WorkService.workThread.handleCmd(
						Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

			} else {
				Toast.makeText(this, Global.toast_notconnect,
						Toast.LENGTH_SHORT).show();
			}

			finish();
		}
	}
	private void handleSendRaw(Intent intent) {
		Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (textUri != null) {

			if (WorkService.workThread.isConnected()) {
				String path = textUri.getPath();
				byte buffer[] = FileUtils.ReadToMem(path);
				Bundle data = new Bundle();
				data.putByteArray(Global.BYTESPARA1, buffer);
				data.putInt(Global.INTPARA1, 0);
				data.putInt(Global.INTPARA2, buffer.length);
				data.putInt(Global.INTPARA3, 256);
				WorkService.workThread.handleCmd(
						Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

			} else {
				Toast.makeText(this, Global.toast_notconnect,
						Toast.LENGTH_SHORT).show();
			}


		}
	}

	private void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			String path = getRealPathFromURI(imageUri);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			opts.inJustDecodeBounds = false;
			if (opts.outWidth > 1200) {
				opts.inSampleSize = opts.outWidth / 1200;
			}

			Bitmap mBitmap = BitmapFactory.decodeFile(path);

			if (mBitmap != null) {
				if (WorkService.workThread.isConnected()) {
					Bundle data = new Bundle();
					data.putParcelable(Global.PARCE1, mBitmap);
					data.putInt(Global.INTPARA1, 384);
					data.putInt(Global.INTPARA2, 0);
					WorkService.workThread.handleCmd(
							Global.CMD_POS_PRINTPICTURE, data);
				} else {
					Toast.makeText(this, Global.toast_notconnect,
							Toast.LENGTH_SHORT).show();
				}
			}
			finish();
		}
	}
	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.MediaColumns.DATA };
		CursorLoader loader = new CursorLoader(this, contentUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		cursor.close();
		return path;
	}

	private void InitGlobalString() {

		Global.toast_success = getString(R.string.toast_success);
		Global.toast_fail = getString(R.string.toast_fail);
		Global.toast_notconnect = getString(R.string.toast_notconnect);
		Global.toast_usbpermit =getString(R.string.toast_usbpermit);
	}


	@Override
	protected void onPause() {
		//startFlag = false;
		super.onPause();

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

	static class MHandler extends Handler {
		WeakReference<MainActivityfenbao> mActivity;

		MHandler(MainActivityfenbao activity) {
			mActivity = new WeakReference<MainActivityfenbao>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivityfenbao theActivity = mActivity.get();
			switch (msg.what) {

			}
		}
	}
	private void DisplayMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}



}
