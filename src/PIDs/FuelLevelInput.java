/** 
 * FuelLevelInput.java
 * OBD-II 모듈에 자동차의 실시간 값을 요청하는 PID와 응답한 값을 식에 맞게 계산 기능 (남은 연료량)
 * 
 * Fuel Level Input
 * PID : 01 2F
 * Response value : 7E8 03 41 2F 68
 * Formula : A*100/255 (%)
 */
package PIDs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class FuelLevelInput extends OBDIIBase{
	public FuelLevelInput(BluetoothSocket mBluetoothSocket){
		super("012F", "FuelLevelInput", 12, mBluetoothSocket);
	}
	
	@Override
	public String process(){
	    String[] strArr = result.split(" ");
	    int a = Integer.parseInt(strArr[2].trim(), 16);		
	    int valueOfFuelLevel = a*100/255;	
		result = Integer.toString(valueOfFuelLevel);
		//result += "%";
		return result;      
	}
}
