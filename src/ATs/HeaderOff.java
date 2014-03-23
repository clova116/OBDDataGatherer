/** 
 * HeaderOff.java
 * OBD-II모듈의 블루투스 통신 설정을 할 수 있는  AT command(Header off 기능)  
 */
package ATs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class HeaderOff extends OBDIIBase{
	
	public HeaderOff(BluetoothSocket mBluetoothSocket) {
		super("AT H0", "Header Off", 2, mBluetoothSocket);
	}

	@Override
	public String process() {
		// TODO Auto-generated method stub
		return result;
		
	}
}