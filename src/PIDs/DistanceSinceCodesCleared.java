/** 
 * DistanceSinceCodesCleared.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (�ڵ� Ŭ���� �� ���� �� ������ �Ÿ�)
 * 
 * Distance traveled since codes cleared
 * PID : 01 31
 * Response value : 41 31 BA 2E 7E8 04 41 31 B5 68
 * Formula : (A*256)+B (km)
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class DistanceSinceCodesCleared extends OBDIIBase {
	public DistanceSinceCodesCleared(BluetoothSocket mBluetoothSocket){
		super("0131", "DistanceSinceCodesCleared", 15, mBluetoothSocket);
	}

	@Override
	public String process(){
	    String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16); 
		int b = Integer.parseInt(strArr[3].trim(), 16);
		int valueOfDistance = ((a*256)+b);	  
		result = Integer.toString(valueOfDistance);
		//result += "km";
		return result;      
	}
}
