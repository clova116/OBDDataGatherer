/**
 * PID 이름에 따라 해당되는 PID 객체를 생성하는 factory class
 */

package OBDII;

import PIDs.*;
import ATs.*;
import android.bluetooth.BluetoothSocket;

public class OBDIIFactory {

	public static OBDIIBase create(String pidName, BluetoothSocket mBluetoothSocket){
		if(pidName.equals("EngineRPM"))
			return new EngineRPM(mBluetoothSocket);		
		else if(pidName.equals("DistanceSinceCodesCleared"))
			return new DistanceSinceCodesCleared(mBluetoothSocket);		
		else if(pidName.equals("FuelLevelInput"))
			return new FuelLevelInput(mBluetoothSocket);		
		else if(pidName.equals("IntakeAirTemperature"))
			return new IntakeAirTemperature(mBluetoothSocket);		
		else if(pidName.equals("IntakeManifoldAbsolutePressure"))
			return new IntakeManifoldAbsolutePressure(mBluetoothSocket);		
		else if(pidName.equals("RuntimeSinceEngineStart"))
			return new RuntimeSinceEngineStart(mBluetoothSocket);
		else if(pidName.equals("VehicleIdentificationNumber"))
			return new VehicleIdentificationNumber(mBluetoothSocket);
		else if(pidName.equals("VehicleSpeed"))
			return new VehicleSpeed(mBluetoothSocket);		
		else if(pidName.equals("EchoOff"))
			return new EchoOff(mBluetoothSocket);
		else if(pidName.equals("HeaderOff"))
			return new HeaderOff(mBluetoothSocket);
		else if(pidName.equals("Initialization"))
			return new Initialization(mBluetoothSocket);
		else if(pidName.equals("ProtocolAuto"))
			return new ProtocolAuto(mBluetoothSocket);
		return null;
	}
	public static OBDIIBase create(String pidName, BluetoothSocket mBluetoothSocket, String pidValue){
		return new CustomCarInfo(pidName, mBluetoothSocket, pidValue);
	}
}
