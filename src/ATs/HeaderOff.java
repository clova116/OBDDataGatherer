/** 
 * HeaderOff.java
 * OBD-II����� ������� ��� ������ �� �� �ִ�  AT command(Header off ���)  
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