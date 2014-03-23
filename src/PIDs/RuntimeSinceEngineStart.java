/** 
 * RuntimeSinceEngineStart.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (�õ��� Ų �� ���� �ð�)
 * 
 * Run time since engine start
 * PID : 01 1F
 * Response value :  7E9 04 41 1F 01 12 7E8 04 41 1F 01 11
 * Formula : (A*256)+B(sec)
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class RuntimeSinceEngineStart extends OBDIIBase{
	public RuntimeSinceEngineStart(BluetoothSocket mBluetoothSocket){
		super("011F", "RuntimeSinceEngineStart" ,15, mBluetoothSocket);
	}
	
	@Override
	public String process(){
	    String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16); 
		int b = Integer.parseInt(strArr[3].trim(), 16);
		int valueOfRuntime = ((a*256)+b);	
		result = Integer.toString(valueOfRuntime);	
		//result += "Sec";
		return result;      
	}
}
