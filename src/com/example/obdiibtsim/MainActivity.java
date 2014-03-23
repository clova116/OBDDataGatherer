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
	 * UI와 관련된 위젯들
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
	 * excel file 관련
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
	 * 결과와 선택된 정보데이터의 이름을 담는다
	 * 정보데이터의 이름은 EngineRPM이 기본값으로 주어진다
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
				//타이밍 문제가 있을 수 있기 때문에 메인에서만 접근 가능한 키를 하나 만들어서, 이 키가 활성화 된 경우면 쓰레드가 메세지를 던지도록 설정
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
		 * UI 설정
		 */
		statusTxt = (TextView) findViewById(R.id.statusTxt);
		connBtn = (Button) findViewById(R.id.connectBtn);
		startBtn = (Button) findViewById(R.id.startBtn);
		disconnBtn = (Button) findViewById(R.id.disconnBtn);

		startBtn.setEnabled(false);
		disconnBtn.setEnabled(false);

		/**
		 * Excel 설정
		 */
		main = new MainActivity();

		//어댑터 생성
		adapter = new ListAdapter(this, pidList);
		//리스트뷰에 어댑터 연결
		pidListView = (ListView)findViewById(R.id.pidLists);
		pidListView.setAdapter(adapter);

		/**
		 * 1. 연결 버튼을 누르면 디바이스 선택부터 함
		 * 2. 기타 선택 결과는 결과 처리 부분에서 수행
		 */		
		connBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent deviceIntent = new Intent(MainActivity.this, DeviceListActivity.class);
				startActivityForResult(deviceIntent, 0);
			}        	
		});	

		/**
		 * 시작 버튼을 누르면
		 * 1. 화면의 리스트 중에서 체크된 것들로 arrayList<OBDIIBase> pidList를 생성
		 * 2. 이 List를 활용해서 엑셀 판을 생성
		 * 3. pidList를 순서대로 값을 던지고 받음. 방식은 아래의 새 객체 생성 후 던지고 받기. 받은 값은 string array에 기록
		 * 4. 한 줄 다 하고 나면 엑셀에 한 줄 기록
		 * 5. 3,4를 계속 반복
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
		 * 중단 버튼을 누르면 수집한 자료의 엑셀 파일 저장을 완료하고 연결도 끊는다
		 * 1. 소켓 닫음 -> 쓰레드 멈춤
		 * 2. 지금까지 기록하던 엑셀 파일을 닫음
		 * 3. 엑셀 파일이 저장되었음을 토스트로 알려줌
		 * 4. 연결이 끊어졌음을 알림 
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
		// WorkSheet 생성
		sh = wb.createSheet("OBD", 0);

		// 셀형식, 생성, 테두리, 헤더		
		textFormat = new WritableCellFormat();
		textFormat.setAlignment(Alignment.CENTRE);
		textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);	

		// 열 넓이 설정(열 위치, 넓이)
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
		// 워크시트 쓰기
		wb.write();
		// 워크시트 닫기
		wb.close();
	}
	public void texcelWrite(File file, List<Map<String, Object>> data, ArrayList<PidInfo> usingList) throws WriteException, IOException {
		int row = 0;
		wb = Workbook.createWorkbook(file);
		// WorkSheet 생성
		sh = wb.createSheet("OBD", 0);

		// 셀형식, 생성, 테두리, 헤더		
		textFormat = new WritableCellFormat();
		textFormat.setAlignment(Alignment.CENTRE);
		textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);	

		// 열 넓이 설정(열 위치, 넓이)
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
		// 워크시트 쓰기
		wb.write();
		// 워크시트 닫기
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
	 * 옵션 버튼을 눌렀을 때 메뉴가 뜨도록
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 메뉴 중 블루투스 연결 버튼을 누르면 블루투스 디바이스 선택을 수행하도록
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
	 * StartActivityForResult 메소드로 수행한 결과에 따라 일을 처리하는 부분
	 * requestCode : Activity 구분용 코드
	 * resultCode : 각 Activity에서의 처리 결과 코드
	 * data : 각 Activity에서 복귀할 때 전달하고자 하는 정보를 담아오는 변수
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * requestCode 0 : device List Activity
		 * requestCode 1 : add custom Pid
		 * resultCode 0 : 성공
		 * resultCode 1 : 취소 혹은 실패
		 */		
		switch(requestCode) {
		case 0:
			switch (resultCode) {
			case 0:
				/**
				 * 선택한 device의 맥 주소를 통해 블루투스 소켓을 생성하고, 생성한 소켓에 연결을 시도한다
				 * 성공 - 화면에 연결 성공 표시, OBDII 모듈과 통신을 위한 초기화 과정 수행
				 * 실패 - 연결 실패 표시
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
				 * 받아온 pidName과 pidValue로 custom pid list에 계속 추가해주면 된다
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
	 * OBDII 모듈을 초기화 시키는 과정
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