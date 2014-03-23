/** 
 * VehicleSpeed.java
 * OBD-II 모듈에 자동차의 실시간 값을 요청하는 PID와 응답한 값을 식에 맞게 계산 기능 (자동차 고유 ID)
 * 
 * Vehicle speed
 * PID : 010D
 * Response value : 7E9 03 41 0D 00 7E8 03 41 0D 00 
 * Formula : A (km/h)
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class VehicleSpeed extends OBDIIBase{
	public VehicleSpeed(BluetoothSocket mBluetoothSocket){
		super("010D", "VehicleSpeed", 11, mBluetoothSocket);
	}
	
	@Override
	public String process(){
		 String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16); 
	    System.out.println("speed " + a);
	    int valueOfSpeed = a;
		 result = Integer.toString(valueOfSpeed);
		 //result += "km/h";
		return result;      
	}
}
