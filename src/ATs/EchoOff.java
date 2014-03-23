/** 
 * EchoOff.java
 * OBD-II����� ������� ��� ������ �� �� �ִ�  AT command(Echo off ���)  
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
