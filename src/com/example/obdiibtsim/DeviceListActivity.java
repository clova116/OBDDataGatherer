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
 * DeviceListActivity Ŭ����
 * ������� ������ �� �� �ִ� ��ġ���� �˻��Ͽ� ������� ȭ�鿡 �����ش�.
 */

public class DeviceListActivity extends Activity {

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	//local ������� ����� �˻� �Ѵ�.
	//local�� ���� ��������� �޷� �ִ����� pair�� �� �������� ���Ѵ�.
	private BluetoothAdapter mBtAdapter;

	//���� ����̽� ������ ����Ʈ�信 �����ֱ� ���� ����� ���� ����
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;

	//���ο� ����̽� ������ ����Ʈ�信 �����ֱ� ���� ����� ���� ����
	private ArrayAdapter<String> mNewDevicesArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_device_list);

		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);
		//local Bluetooth adapter�� ��´�.
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (!mBtAdapter.isEnabled()) {
			mBtAdapter.enable();
		}

		//��ư�� ������ �� doDiscovery() �޼ҵ带 ȣ���Ͽ� �˻� ����
		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});


		//���� ����̽� ������ ����Ʈ�信 �����ֱ� ���� ����� ��ü ����
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

		//ListView(���� ����̽�)�� ã�� set��Ų��.
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);	//XML ���̾ƿ��� ���ǵ� ����Ʈ�� ��ü ����

		//���� ����̽� ���� ��������
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		//���� ����̽��� �ִٸ� �̸� ArrayAdapter�� �߰��Ѵ�.
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				//���� ����̽� ������ ����Ϳ� �߰�
				mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else {
			String noDevices = "���� ����̽��� �����ϴ�.";
			mPairedDevicesArrayAdapter.add(noDevices);
		}


		//���ο� ����̽� ������ ����Ʈ�信 �����ֱ� ���� ����� ��ü ����
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_device_name);

		//ListView(���Ӱ� ã�� ����̽���)�� ã�� set��Ų��.
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		// ������� ����̽��� ã������ �� ����Ʈ�� ���޹ޱ� ���� ����Ʈ ���� ���
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// �˻������� ������ �� ����Ʈ�� ���޹ޱ� ���� ����Ʈ ���� ���
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
	}

	//��Ƽ��Ƽ�� �簳�� ��
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

		// ������� ����̽��� ã�� ���� ���� �ʴ´ٸ� �˻� ����
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}



	//������� �˻��� �����ϱ� ���� �޼ҵ�
	private void doDiscovery() {
		// ������
		if (true) {
			Log.d("DeviceListActivity", "doDiscovery()");
		}

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle("����̽� �˻� ��...");

		// Turn on sub-title for new devices
		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		// �˻� ����(������� ��ġ�� �߰��߱� ������)
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		//������� �˻� ���� ��û
		mBtAdapter.startDiscovery();
	}


	//ListView�� �ִ� ����̽��� �� �ϳ��� �������� �� ����Ǵ� �޼ҵ� 
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			mBtAdapter.cancelDiscovery();	//�˻� ����

			//���õ� �����ۿ��� MAC �ּ� Ȯ��(MAC �ּҴ�  View���� ������ 17 chars�̴�.)
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
				//���⿡ �ڷ� ��ư�� �������� �ؾ��� �ൿ�� �����Ѵ�
				setResult(1);
				finish();
				return true;
			}
		}
		return super.onKeyDown( KeyCode, event );
	}
	/*
	 * ����̽�discovery�� �����Ϸ��� startDiscovery()�� ȣ���ϸ� �ȴ�. 
	 * �� ������ �񵿱���̶� �޼ҵ带 ȣ���ϸ� discovery�� ���������� ���۵Ǿ��� ����� �˷��ִ� boolean���� ��ٷ� �����ش�. 
	 * Discovery������ ���� 12�ʰ��� inquiry scan�� �߰ߵ� �� ����̽��� ���� �̸��� �������� ���� page scan���� �̷������.
	 * 
	 * ���ø����̼��� �� �߰ߵ� ����̽��� ���� ������ �ޱ� ����  ACTION_FOUND ����Ʈ�� ���� BroadcastReceiver�� ����ؾ߸� �Ѵ�. 
	 * �� ����̽����� �ý����� ACTION_FOUND ����Ʈ�� ��ε�ĳ��Ʈ �Ѵ�. 
	 * �� ����Ʈ�� ���� BluetoothDevice��  BluetoothClass�� ����ִ� EXTRA_DEVICE�� EXTRA_CLASS �ʵ带 �����Ѵ�. 
	 * 
	 */
	//����̽� discovery�� ������ ����̽����� �߰ߵǾ��� �� ��ε�ĳ��Ʈ�� ó���ϴ� �ڵ鷯�� ����ϴ� ��� 
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {	//������� ����̽��� �˻��Ǿ��� ��
				//����Ʈ�� ���޵� BluetoothDevice ��ü ����
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// ã���� ������� ����̽��� ���Ǿ� ���� �ʴٸ�
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					//����Ʈ�並 ���� ����Ϳ� ����̽� ���� �߰�
					mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {	//�˻��� ������ ��
				setProgressBarIndeterminateVisibility(false);
				setTitle("������ ����̽� ����");

				//ã���� ����̽��� ������ ����̽��� ���ٴ� �޽��� ǥ��
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					String noDevices = "����̽��� ã�� ���߽��ϴ�.";
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};
}
