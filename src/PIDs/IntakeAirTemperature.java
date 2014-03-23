/** 
 * IntakeAirTemperature.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (�ڵ� Ŭ���� �� ���� �� ������ �Ÿ�)
 * 
 * IntakeAirTemperature
 * PID : 01 0F
 * Response value : 7E8 03 41 0F 54
 * Formula : A-40 (��C)
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class IntakeAirTemperature extends OBDIIBase{

	public IntakeAirTemperature(BluetoothSocket mBluetoothSocket) {
		super("010F", "IntakeAirTemperature", 12, mBluetoothSocket);
	}
	
	@Override
	public String process() {
	    String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16);		
		int valueOfTemp = a-40;
		result = Integer.toString(valueOfTemp);
		//result += "��";
		return result;      
	}
}
