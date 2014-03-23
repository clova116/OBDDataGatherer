package PIDs;
import android.bluetooth.BluetoothSocket;
import OBDII.OBDIIBase;
/** 
 * custom pid object that user added manually
 * the result variable represents the response string from the car without processing it 
 */

public class CustomCarInfo extends OBDIIBase{
	public CustomCarInfo(String pidName, BluetoothSocket mBluetoothSocket,String pidValue) {
		super(pidValue, pidName, 4, mBluetoothSocket);
	}
	@Override
	public String process(){
		return result;     
	}
}




