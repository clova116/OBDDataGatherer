/** 
 * RuntimeSinceEngineStart.java
 * OBD-II 모듈에 자동차의 실시간 값을 요청하는 PID와 응답한 값을 식에 맞게 계산 기능 (시동을 킨 후 주행 시간)
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
