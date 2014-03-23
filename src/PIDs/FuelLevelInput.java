/** 
 * FuelLevelInput.java
 * OBD-II ��⿡ �ڵ����� �ǽð� ���� ��û�ϴ� PID�� ������ ���� �Ŀ� �°� ��� ��� (���� ���ᷮ)
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
