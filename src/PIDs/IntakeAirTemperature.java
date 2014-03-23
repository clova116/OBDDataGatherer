/** 
 * IntakeAirTemperature.java
 * OBD-II 모듈에 자동차의 실시간 값을 요청하는 PID와 응답한 값을 식에 맞게 계산 기능 (코드 클리어 후 부터 총 주행한 거리)
 * 
 * IntakeAirTemperature
 * PID : 01 0F
 * Response value : 7E8 03 41 0F 54
 * Formula : A-40 (°C)
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
		//result += "℃";
		return result;      
	}
}
