/** 
 * Initialization.java
 * OBD-II����� ������� ��� ������ �� �� �ִ�  AT command(Initialization ���)  
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
