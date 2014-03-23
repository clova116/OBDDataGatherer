package com.example.obdiibtsim;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * DeviceListActivity 클래스
 * 블루투스 연결을 할 수 있는 장치들을 검색하여 목록으로 화면에 보여준다.
 */

public class DeviceListActivity extends Activity {

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	//local 블루투스 모듈을 검색 한다.
	//local은 폰에 블루투스가 달려 있는지와 pair가 된 모듈까지를 말한다.
	private BluetoothAdapter mBtAdapter;

	//페어링된 디바이스 정보를 리스트뷰에 보여주기 위한 어댑터 변수 선언
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;

	//새로운 디바이스 정보를 리스트뷰에 보여주기 위한 어댑터 변수 선언
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_device_list);

		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);
		//local Bluetooth adapter를 얻는다.
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (!mBtAdapter.isEnabled()) {
			mBtAdapter.enable();
		}

		//버튼을 눌렀을 때 doDiscovery() 메소드를 호출하여 검색 시작
		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});


		//페어링된 디바이스 정보를 리스트뷰에 보여주기 위한 어댑터 객체 생성
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

		//ListView(페어링된 디바이스)를 찾고 set시킨다.
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);	//XML 레이아웃에 정의된 리스트뷰 객체 참조

		//페어링된 디바이스 정보 가져오기
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		//페어링된 디바이스가 있다면 이를 ArrayAdapter에 추가한다.
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				//페어링된 디바이스 정보를 어댑터에 추가
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = "페어링된 디바이스가 없습니다.";
			mPairedDevicesArrayAdapter.add(noDevices);
		}


		//새로운 디바이스 정보를 리스트뷰에 보여주기 위한 어댑터 객체 생성
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

		//ListView(새롭게 찾은 디바이스들)를 찾고 set시킨다.
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		// 블루투스 디바이스가 찾아졌을 때 인텐트를 전달받기 위한 인텐트 필터 등록
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// 검색과정이 끝났을 때 인텐트를 전달받기 위한 인텐트 필터 등록
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
	}

	//액티비티가 재개될 때
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 블루투스 디바이스를 찾는 것을 하지 않는다면 검색 중지
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}



	//블루투스 검색을 실행하기 위한 메소드
	private void doDiscovery() {
		// 디버깅용
		if (true) {
			Log.d("DeviceListActivity", "doDiscovery()");
		}

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle("디바이스 검색 중...");

		// Turn on sub-title for new devices
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		// 검색 중지(블루투스 장치를 발견했기 때문에)
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		//블루투스 검색 시작 요청
		mBtAdapter.startDiscovery();
	}


	//ListView에 있는 디바이스들 중 하나를 선택했을 때 수행되는 메소드 
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			mBtAdapter.cancelDiscovery();	//검색 중지

			//선택된 아이템에서 MAC 주소 확인(MAC 주소는  View에서 마지막 17 chars이다.)
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);

			Intent i = getIntent();
			i.putExtra("address", address);
			System.out.println("ADDRESS : " + address);
			setResult(0, i);
			finish();
		}
	};
	public boolean onKeyDown( int KeyCode, KeyEvent event )
	{
		if( event.getAction() == KeyEvent.ACTION_DOWN ){
			if( KeyCode == KeyEvent.KEYCODE_BACK ){
				//여기에 뒤로 버튼을 눌렀을때 해야할 행동을 지정한다
				setResult(1);
				finish();
				return true;
			}
		}
		return super.onKeyDown( KeyCode, event );
	}
	/*
	 * 디바이스discovery를 시작하려면 startDiscovery()를 호출하면 된다. 
	 * 이 과정은 비동기식이라 메소드를 호출하면 discovery가 성공적으로 시작되었나 결과를 알려주는 boolean값을 곧바로 돌려준다. 
	 * Discovery과정은 보통 12초간의 inquiry scan후 발견된 각 디바이스에 대해 이름을 가져오기 위한 page scan으로 이루어진다.
	 * 
	 * 어플리케이션은 각 발견된 디바이스에 대한 정보를 받기 위해  ACTION_FOUND 인텐트를 위한 BroadcastReceiver를 등록해야만 한다. 
	 * 각 디바이스마다 시스템이 ACTION_FOUND 인텐트를 브로드캐스트 한다. 
	 * 이 인텐트는 각각 BluetoothDevice와  BluetoothClass가 들어있는 EXTRA_DEVICE와 EXTRA_CLASS 필드를 전달한다. 
	 * 
	 */
	//디바이스 discovery가 끝나서 디바이스들이 발견되었을 때 브로드캐스트를 처리하는 핸들러를 등록하는 방법 
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {	//블루투스 디바이스가 검색되었을 때
				//인텐트로 전달된 BluetoothDevice 객체 참조
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 찾아진 블루투스 디바이스가 페어링되어 있지 않다면
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					//리스트뷰를 위한 어댑터에 디바이스 정보 추가
					mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {	//검색이 끝났을 때
				setProgressBarIndeterminateVisibility(false);
				setTitle("연결할 디바이스 선택");

				//찾아진 디바이스가 없으면 디바이스가 없다는 메시지 표시
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = "디바이스를 찾지 못했습니다.";
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};
}
