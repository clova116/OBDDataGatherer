/**
 * OBDIIBase.java
 * OBD-II���� ���� ����ϴ� �κ����� PID���� ���� ������ ���䰪�� ���� �� ���� ���θ� Ȯ���ϴ� ���
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

	/** ������
	 * @param pid : ���� ������ �޾ƿ� �� �ʿ��� pid ���ڿ�
	 * @param pidName : ���� ���� ����
	 * @param validLength : response�� valid�� ����
	 * @param mBluetoothSocket : ������� ����
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
	 * �������� ���� ���� �����ϴ� �κ�
	 * ���� �� �� 5ȸ�� �ʰ��Ͽ� ���� : device���� ��� ���з� ����
	 * ���� : ������ �޾ƿ���, �޾ƿ� ���� ���� ������ �Ǵ�. �Ǵ� �� fomular�� �����ϴ� process �޼ҵ� ����
	 */
	public String preprocess() {
		int count = 0;
		do{
			if(count>5) return "��û �õ� 5ȸ �ʰ�";
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
	 * �������� ���� ���� �ùٸ� �������� �Ǵ�
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
	 * instream�� ���� �����͸� �о���� �κ�
	 * OBDII�� response�� ���� �� terminator > �� ���� ������ ������, �� ���� �޾ƿ� ������ Instream���� read�� �����Ѵ� 
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
	 * ���ϴ� ������ pid�� outstream�� ���� �κ�
	 * pid�� ������ \r�� ���Ͽ� outstream�� ����
	 * �Ϲ� pid �ܿ� AT ��ɾ��� ���� ó���ð��� �ټ� �ʿ��ϹǷ� sleep�� �Ǵ�
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
	 * preprocess�� �����Ͽ� ���� ���� ���� ��, �� ����� ������ result�� ��ȯ 
	 */
	public String getResult(){
		preprocess();
		return result;
	}
}
