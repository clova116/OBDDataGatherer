/** 
 * EchoOff.java
 * OBD-II모듈의 블루투스 통신 설정을 할 수 있는  AT command(Echo off 기능)  
 */
package ATs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class EchoOff extends OBDIIBase{
	
	public EchoOff(BluetoothSocket mBluetoothSocket) {
		super("AT E0", "Echo Off", 2, mBluetoothSocket);
	}

	@Override
	public String process() {
		// TODO Auto-generated method stub
		return result;
		
	}
	
}
