/** 
 * EngineRPM.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (Engine RPM��)
 * 
 * Engine RPM
 * PID : 010C
 * Response value : 41 0C 0C 1D
 * 					7E8 04 41 0C 0C 1D
 * Formula : ((A*256)+B)/4 (rpm)
 */

package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;


public class EngineRPM extends OBDIIBase {
	public EngineRPM( BluetoothSocket mBluetoothSocket){
		super("010C", "EngineRPM", 15, mBluetoothSocket);
	}

	@Override
	public String process(){
	    String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16); 
		int b = Integer.parseInt(strArr[3].trim(), 16);
		int valueOfRPM = ((a*256)+b)/4;	
		result = Integer.toString(valueOfRPM);
		//System.out.println(valueOfResult+" rpm");
		//result += "rpm";
		return result;     
	}
}
