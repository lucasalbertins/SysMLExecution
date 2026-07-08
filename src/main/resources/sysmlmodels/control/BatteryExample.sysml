package BatteryExample {
	
	private import ScalarValues::*;
	
	action def MonitorBattery { out charge : Real; }
	action def AddCharge { in charge : Real; }
	action def EndCharging;
	part def Device;

	part device : Device {
		out attribute battery : Real = 30.0;
		out attribute isCharging : Boolean = true;
		
		action def ChargeBattery {
			first start;
			then merge continueCharging;
			then action monitor : MonitorBattery {
				out batteryCharge : Real;
				assign batteryCharge := device.battery;
			}

			then decide decision;
				if monitor.batteryCharge < 100.0 then addCharge;
				if monitor.batteryCharge >= 100.0 then endCharging;
			
			action addCharge : AddCharge {
				in charge = monitor.batteryCharge;
				assign device.battery := charge * 3 - 40;
			}
			then continueCharging;

			action endCharging : EndCharging {
				assign device.battery := 100.0;
				assign device.isCharging := false;
			}
			then done;
		}
		perform action chargeBattery : ChargeBattery;
	}
}
