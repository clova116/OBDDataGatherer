/**
 * OBDIIBase.java
 * OBD-II모듈과 직접 통신하는 부분으로 PID값을 모듈로 보내고 응답값을 받은 후 오류 여부를 확인하는 기능
 */
package OBDII;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public abstract class OBDIIBase {
	private static final String TAG = "OBDIIBase";
	private static final boolean D = true;

	private String pid;
	protected String result="";
	private int validLength;
	private String pidName; 
	private InputStream mmInStream = null;
	private OutputStream mmOutStream = null;
	private byte[] buffer = new byte[1024];	

	private static final int OBDII_NORMAL = 0;
	private static final int OBDII_ERROR = 1;
	private static final int OBDII_CAN_ERROR = 2;

	/** 생성자
	 * @param pid : 차량 정보를 받아올 때 필요한 pid 문자열
	 * @param pidName : 차량 정보 종류
	 * @param validLength : response의 valid한 길이
	 * @param mBluetoothSocket : 블루투스 소켓
	 */
	public OBDIIBase(String pid, String pidName, int validLength, BluetoothSocket mBluetoothSocket){
		this.pid = pid;
		this.pidName = pidName;
		this.validLength = validLength;

		try {
			this.mmInStream = mBluetoothSocket.getInputStream();
			this.mmOutStream = mBluetoothSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public abstract String process();

	/**
	 * 응답으로 받은 값을 정제하는 부분
	 * 값을 쓸 때 5회를 초과하여 실패 : device와의 통신 실패로 간주
	 * 성공 : 응답을 받아오고, 받아온 값이 정상 값인지 판단. 판단 후 fomular를 적용하는 process 메소드 실행
	 */
	public String preprocess() {
		int count = 0;
		do{
			if(count>5) return "요청 시도 5회 초과";
			else count++;
			writer();
			try {
				reader();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(OBDII_NORMAL != checkValue());
		return process();			
	}
	
	/**
	 * 응답으로 얻은 값이 올바른 값인지를 판단
	 */
	public int checkValue(){
		if(result.contains("CAN"))
			return OBDII_CAN_ERROR;
		else if(result.contains("NO") || result.contains("SEARCH")
				||result.contains("STOP") ||result.contains("?") 
				||!(result.length()>=validLength)){
			System.out.println("INVALID RESULT VALUE");
			return OBDII_ERROR;
		}
		else {
			System.out.println("VALID VALUE");
			System.out.println(result.length() + " : " + validLength);
			return OBDII_NORMAL;
		}
	}

	/** 
	 * instream에 쌓인 데이터를 읽어오는 부분
	 * OBDII는 response를 보낼 때 terminator > 를 같이 보내기 때문에, 이 값을 받아올 때까지 Instream에서 read를 수행한다 
	 */
	public void reader() throws IOException {
		result="";
		while(!result.contains(">")){
			int bufferSize = mmInStream.read(buffer);
			result += new String(buffer, 0, bufferSize);
		}
		if(D)Log.d(TAG, "inputStream : "+ result);
	}	

	/**
	 * 원하는 정보의 pid를 outstream에 쓰는 부분
	 * pid와 끝문자 \r를 더하여 outstream에 쓴다
	 * 일반 pid 외에 AT 명령어일 경우는 처리시간이 다소 필요하므로 sleep을 건다
	 */
	public void writer() {
		byte[] bytes =  (pid+"\r").getBytes();

		try {
			mmOutStream.write(bytes);
			mmOutStream.flush();
		} catch (IOException e) {
		}
		if(pid.contains("AT")) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	

	/**
	 * getters
	 */
	public String getPidName(){
		return this.pidName;
	}

	public String getPid(){
		return pid;
	}

	/**
	 * preprocess를 수행하여 값을 쓰고 읽은 후, 그 결과가 쓰여진 result를 반환 
	 */
	public String getResult(){
		preprocess();
		return result;
	}
}
