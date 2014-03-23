/** 
 * VehicleIdentificationNumber.java
 * OBD-II 모듈에 자동차의 실시간 값을 요청하는 PID와 응답한 값을 식에 맞게 계산 기능 (자동차 고유 ID)
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
