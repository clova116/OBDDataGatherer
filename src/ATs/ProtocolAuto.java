/** 
 * ProtocolAuto.java
 * OBD-II모듈의 블루투스 통신 설정을 할 수 있는  AT command(Protocol을 자동으로 설정하는 기능)  
 */
package ATs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class ProtocolAuto extends OBDIIBase {

	public ProtocolAuto(BluetoothSocket mBluetoothSocket){
		super("AT SP 0", "Protocol Auto", 2, mBluetoothSocket);
	}

	@Override
	public String process() {
		// TODO Auto-generated method stub
		return result;
		
	}
}
