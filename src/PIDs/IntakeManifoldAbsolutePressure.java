/** 
 * IntakeManifoldAbsolutePressure.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (�ڵ����� IMAP ��)
 * 
 * IntakeManifoldAbsolutePressure
 * PID : 01 0B
 * Response value : 7E8 03 41 0B 31
 * Formula : A (kPa(absolute))
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class IntakeManifoldAbsolutePressure extends OBDIIBase{

	public IntakeManifoldAbsolutePressure(BluetoothSocket mBluetoothSocket) {
		super("010B", "IntakeManifoldAbsolutePressure", 12, mBluetoothSocket);
	}
	
	@Override
	public String process() {	
	    String[] strArr = result.split(" ");
	    int valueOfPressure = Integer.parseInt(strArr[2].trim(), 16);
		result = Integer.toString(valueOfPressure);		
		//result += "kPa";
		return result;      
	}
}
