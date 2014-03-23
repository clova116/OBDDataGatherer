package com.example.obdiibtsim;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import OBDII.OBDIIBase;
import OBDII.OBDIIFactory;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket mBluetoothSocket = null;
	private BluetoothDevice mBluetoothDevice = null;
	private static final UUID MY_UUID =
			UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * UI�� ���õ� ������
	 */
	private String address;
	private String customPidName;
	private String customPidValue;
	private TextView statusTxt;
	private Button connBtn;
	private Button startBtn;
	private Button disconnBtn;
	private ListView pidListView;
	private Toast mToast = null;

	/**
	 * excel file ����
	 */
	private MainActivity main;
	private File file;
	WritableWorkbook wb;
	WritableSheet sh;
	Label label;
	WritableCellFormat textFormat;
	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	
	private boolean isTesting = false;


	ListAdapter adapter;
	ArrayList<PidInfo> pidList;	
	ArrayList<PidInfo> TusingList;
	ArrayList<OBDIIBase> usingList;

	/**
	 * ����� ���õ� ������������ �̸��� ��´�
	 * ������������ �̸��� EngineRPM�� �⺻������ �־�����
	 */

	OBDIIBTThread myThread = null;
	TOBDIIBTThread tmyThread = null;


	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String[] result = (String[])msg.obj;
				if(isTesting) {
					tsetData(result);
					System.out.println("data size : " + data.size());
				}
				else {					
					setData(result);
					System.out.println("data size : " + data.size());
				}
				//Ÿ�̹� ������ ���� �� �ֱ� ������ ���ο����� ���� ������ Ű�� �ϳ� ����, �� Ű�� Ȱ��ȭ �� ���� �����尡 �޼����� �������� ����
				break;
			default:
				break;
			}
		}
	};
	private void tsetData(String[] result) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i=0; i<result.length; i++) {
			statusTxt.setText(TusingList.get(i).getName() + " : " + result[i]);
			map.put(TusingList.get(i).getName(), result[i]);	
		}				
		data.add(map);	
	}
	private void setData(String[] result) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i=0; i<result.length; i++) {
			statusTxt.setText(usingList.get(0).getPidName() + " : " + result[0]);
			map.put(usingList.get(i).getPidName(), result[i]);	
		}				
		data.add(map);	
//		for(int i=0;i<data.size();i++) {
//			Map<String, Object> tmp = data.get(i);
//			System.out.println(tmp.get("EngineRPM"));
//		}
	}
	
	public BluetoothSocket getBTSocket() {
		return this.mBluetoothSocket;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}
		pidList = new ArrayList<PidInfo>();
		supportingPids();

		/**
		 * UI ����
		 */
		statusTxt = (TextView) findViewById(R.id.statusTxt);
		connBtn = (Button) findViewById(R.id.connectBtn);
		startBtn = (Button) findViewById(R.id.startBtn);
		disconnBtn = (Button) findViewById(R.id.disconnBtn);

		startBtn.setEnabled(false);
		disconnBtn.setEnabled(false);

		/**
		 * Excel ����
		 */
		main = new MainActivity();

		//����� ����
		adapter = new ListAdapter(this, pidList);
		//����Ʈ�信 ����� ����
		pidListView = (ListView)findViewById(R.id.pidLists);
		pidListView.setAdapter(adapter);

		/**
		 * 1. ���� ��ư�� ������ ����̽� ���ú��� ��
		 * 2. ��Ÿ ���� ����� ��� ó�� �κп��� ����
		 */		
		connBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent deviceIntent = new Intent(MainActivity.this, DeviceListActivity.class);
				startActivityForResult(deviceIntent, 0);
			}        	
		});	

		/**
		 * ���� ��ư�� ������
		 * 1. ȭ���� ����Ʈ �߿��� üũ�� �͵�� arrayList<OBDIIBase> pidList�� ����
		 * 2. �� List�� Ȱ���ؼ� ���� ���� ����
		 * 3. pidList�� ������� ���� ������ ����. ����� �Ʒ��� �� ��ü ���� �� ������ �ޱ�. ���� ���� string array�� ���
		 * 4. �� �� �� �ϰ� ���� ������ �� �� ���
		 * 5. 3,4�� ��� �ݺ�
		 */	
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {					
				excelSetUp();	
				if(isTesting) {
					TusingList = makeList(pidList);
					tmyThread = new TOBDIIBTThread(TusingList, mHandler);
					tmyThread.start();
				}
				else {
					usingList = makeUsingList(pidList);
					myThread = new OBDIIBTThread(usingList, mHandler);
					myThread.start();
				}
				startBtn.setEnabled(false);
				disconnBtn.setEnabled(true);
				statusTxt.setText("Now Running~");
			}       	
		});		

		/**
		 * �ߴ� ��ư�� ������ ������ �ڷ��� ���� ���� ������ �Ϸ��ϰ� ���ᵵ ���´�
		 * 1. ���� ���� -> ������ ����
		 * 2. ���ݱ��� ����ϴ� ���� ������ ����
		 * 3. ���� ������ ����Ǿ����� �佺Ʈ�� �˷���
		 * 4. ������ ���������� �˸� 
		 */
		disconnBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isTesting) {
					tmyThread.setSocket(false);
					tmyThread = null;
				}
				else {
					myThread.setSocket(false);
					myThread = null;						
				}
				try {
					mBluetoothSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
				try {
					if(isTesting) {
						main.texcelWrite(file,data,TusingList);
					}
					else {
						main.excelWrite(file,data,usingList);
					}
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				connBtn.setEnabled(true);
				disconnBtn.setEnabled(false);
				mToast = Toast.makeText(MainActivity.this, "'" + file.getName() + "' has created successfully!", Toast.LENGTH_SHORT);
				mToast.show();	
				statusTxt.setText("None");
			}        	
		});	
	}

	private void excelSetUp() {
		long stopTime = System.currentTimeMillis();
		Date date = new Date(stopTime);
		SimpleDateFormat nowFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = nowFormat.format(date);
		file = new File(Environment.getExternalStorageDirectory() + "/" + fileName + ".xls");
	}

	public void excelWrite(File file, List<Map<String, Object>> data, ArrayList<OBDIIBase> usingList) throws WriteException, IOException {
		int row = 0;
		wb = Workbook.createWorkbook(file);
		// WorkSheet ����
		sh = wb.createSheet("OBD", 0);

		// ������, ����, �׵θ�, ���		
		textFormat = new WritableCellFormat();
		textFormat.setAlignment(Alignment.CENTRE);
		textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);	

		// �� ���� ����(�� ��ġ, ����)
		for(int i=0; i<usingList.size(); i++) {
			sh.setColumnView(i, 20);
			label = new jxl.write.Label(i, row, usingList.get(i).getPidName(), textFormat);
			sh.addCell(label);
		}	
		row++;		
		for (Map<String, Object> tem : data) {
			for(int i=0; i<usingList.size(); i++) {
				label = new jxl.write.Label(i, row, (String) tem.get(usingList.get(i).getPidName()), textFormat);
				sh.addCell(label);
			}
			row++;
		}
		System.out.println("WRITE!!!!!");
		// ��ũ��Ʈ ����
		wb.write();
		// ��ũ��Ʈ �ݱ�
		wb.close();
	}
	public void texcelWrite(File file, List<Map<String, Object>> data, ArrayList<PidInfo> usingList) throws WriteException, IOException {
		int row = 0;
		wb = Workbook.createWorkbook(file);
		// WorkSheet ����
		sh = wb.createSheet("OBD", 0);

		// ������, ����, �׵θ�, ���		
		textFormat = new WritableCellFormat();
		textFormat.setAlignment(Alignment.CENTRE);
		textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);	

		// �� ���� ����(�� ��ġ, ����)
		for(int i=0; i<usingList.size(); i++) {
			sh.setColumnView(i, 20);
			label = new jxl.write.Label(i, row, usingList.get(i).getName(), textFormat);
			sh.addCell(label);
		}	
		row++;		
		for (Map<String, Object> tem : data) {			
			for(int i=0; i<usingList.size(); i++) {
				label = new jxl.write.Label(i, row, (String) tem.get(usingList.get(i).getName()), textFormat);
				sh.addCell(label);
			}
			row++;
		}
		System.out.println("WRITE!!!!!");
		// ��ũ��Ʈ ����
		wb.write();
		// ��ũ��Ʈ �ݱ�
		wb.close();
	}

	private ArrayList<OBDIIBase> makeUsingList(ArrayList<PidInfo> pidList) {
		ArrayList<OBDIIBase> usingList = new ArrayList<OBDIIBase>();
		
		usingList.add(OBDIIFactory.create("RuntimeSinceEngineStart", mBluetoothSocket));
		
		OBDIIBase carData;
		for(PidInfo pid : pidList) {
			if(pid.isChecked()) {
				if(pid.isSupport()) {
					carData = OBDIIFactory.create(pid.getName(), mBluetoothSocket);
				}
				else {
					carData = OBDIIFactory.create(pid.getName(), mBluetoothSocket, pid.getValue());
					System.out.println("custom car data created : " + pid.getName() + pid.getValue());
				}
				usingList.add(carData);
			}
		}
		return usingList;
	} 

	private ArrayList<PidInfo> makeList(ArrayList<PidInfo> pidList) {
		ArrayList<PidInfo> usingList = new ArrayList<PidInfo>();
		for(PidInfo pid : pidList) {
			if(pid.isChecked()) {
				usingList.add(pid);
			}
		}
		return usingList;
	} 

	private void supportingPids() {
		PidInfo info = new PidInfo("EngineRPM", "010C", true);
		pidList.add(info);
		info = new PidInfo("DistanceSinceCodesCleared", "0131", true);
		pidList.add(info);
		info = new PidInfo("FuelLevelInput", "012F", true);
		pidList.add(info);
		info = new PidInfo("IntakeAirTemperature","010F", true);
		pidList.add(info);
		info = new PidInfo("IntakeManifoldAbsolutePressure","010B", true);
		pidList.add(info);
//		info = new PidInfo("RuntimeSinceEngineStart" ,"011F", true);
//		pidList.add(info);
		info = new PidInfo("VehicleIdentificationNumber","0902", true);
		pidList.add(info);
		info = new PidInfo("VehicleSpeed", "010D", true);
		pidList.add(info);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * �ɼ� ��ư�� ������ �� �޴��� �ߵ���
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * �޴� �� ������� ���� ��ư�� ������ ������� ����̽� ������ �����ϵ���
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.custom_pid:
			Intent deviceIntent = new Intent(this, AddCustomPidActivity.class);
			startActivityForResult(deviceIntent, 1);
			return true;
		}
		return false;
	}

	/**
	 * StartActivityForResult �޼ҵ�� ������ ����� ���� ���� ó���ϴ� �κ�
	 * requestCode : Activity ���п� �ڵ�
	 * resultCode : �� Activity������ ó�� ��� �ڵ�
	 * data : �� Activity���� ������ �� �����ϰ��� �ϴ� ������ ��ƿ��� ����
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * requestCode 0 : device List Activity
		 * requestCode 1 : add custom Pid
		 * resultCode 0 : ����
		 * resultCode 1 : ��� Ȥ�� ����
		 */		
		switch(requestCode) {
		case 0:
			switch (resultCode) {
			case 0:
				/**
				 * ������ device�� �� �ּҸ� ���� ������� ������ �����ϰ�, ������ ���Ͽ� ������ �õ��Ѵ�
				 * ���� - ȭ�鿡 ���� ���� ǥ��, OBDII ���� ����� ���� �ʱ�ȭ ���� ����
				 * ���� - ���� ���� ǥ��
				 */
				address = data.getStringExtra("address");
				try {
					statusTxt.setText("BT Status : Connecting..");
					mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
					String deviceName = mBluetoothDevice.getName();
					mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
					mBluetoothSocket.connect();
					statusTxt.setText("BT Status : Connected to " + deviceName);
					startBtn.setEnabled(true);
					connBtn.setEnabled(false);					
					initializing();
				} catch (IOException e) {
					e.printStackTrace();
					statusTxt.setText("BT Status : Not Connected!");
					if(isTesting) {
						startBtn.setEnabled(true);
						connBtn.setEnabled(false);
					}
				}
				break;
			default:
				//finish();
				break;
			}
			break;
		case 1:
			switch (resultCode) {
			case 0:
				/**
				 * �޾ƿ� pidName�� pidValue�� custom pid list�� ��� �߰����ָ� �ȴ�
				 */
				customPidName = data.getStringExtra("customPidName");
				customPidValue = data.getStringExtra("customPidValue");
				PidInfo customPid = new PidInfo(customPidName, customPidValue, false);
				pidList.add(customPid);
				break;

			default:
				mToast = Toast.makeText(MainActivity.this, "You canceled to add a cutom pid", Toast.LENGTH_SHORT);
				mToast.show();	
			}
		}
	}
	/**
	 * OBDII ����� �ʱ�ȭ ��Ű�� ����
	 */
	private void initializing() {
		statusTxt.setText("Initializing...");
		OBDIIBase at = OBDIIFactory.create("Initialization", mBluetoothSocket);
		at.getResult();
		statusTxt.setText("Setting Protocol...");
		at = OBDIIFactory.create("ProtocolAuto", mBluetoothSocket);
		at.getResult();
		statusTxt.setText("Echo off...");
		at = OBDIIFactory.create("EchoOff", mBluetoothSocket);
		at.getResult();
		statusTxt.setText("Header off...");
		at = OBDIIFactory.create("HeaderOff", mBluetoothSocket);
		at.getResult();
		statusTxt.setText("Module Initialized Successfully");
	}
}