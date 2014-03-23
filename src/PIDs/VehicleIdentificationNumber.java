/** 
 * VehicleIdentificationNumber.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (�ڵ��� ���� ID)
 * 
 * Vehicle Identification Number
 * PID : 09 02
 * Response value :  
 * Formula : 
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

/**
 * Vehicle Identification Number
 * 09 02
 */
public class VehicleIdentificationNumber extends OBDIIBase{

	public VehicleIdentificationNumber(BluetoothSocket mBluetoothSocket) {
		super("0902", "VehicleIdentificationNumber", 30, mBluetoothSocket);
	}

	@Override
	public String process() {		
		return result;      
	}
}
