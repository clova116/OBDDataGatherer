/** 
 * Initialization.java
 * OBD-II모듈의 블루투스 통신 설정을 할 수 있는  AT command(Initialization 기능)  
 */
package ATs;

import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;

public class Initialization extends OBDIIBase{
	
	public Initialization(BluetoothSocket mBluetoothSocket) {
		super("AT Z", "Initialization" ,2, mBluetoothSocket);
	}

	@Override
	public String process() {
		// TODO Auto-generated method stub
		return result;
		
	}
}
