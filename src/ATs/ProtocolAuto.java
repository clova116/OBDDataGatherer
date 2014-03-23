/** 
 * ProtocolAuto.java
 * OBD-II����� ������� ��� ������ �� �� �ִ�  AT command(Protocol�� �ڵ����� �����ϴ� ���)  
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
