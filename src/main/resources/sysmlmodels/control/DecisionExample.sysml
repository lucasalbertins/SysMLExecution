package DecisionExample {
	private import ScalarValues::*;
	
	attribute def BatteryCharged;
	
	part battery;
	part powerSystem;
	
	action def MonitorBattery { out charge : Real default 80.0; }
	action def AddCharge { in charge : Real; }
	action def EndCharging;
	
	action def ChargeBattery {
		first start;
		
		then action monitor : MonitorBattery {
			out batteryCharge : Real = 80.0;
		}
		
		then decide decision1;
			if monitor.batteryCharge < 100 then addCharge;
			if monitor.batteryCharge >= 100 then endCharging;
			
		action addCharge : AddCharge {
			in charge = monitor.batteryCharge;
		}
		
		action endCharging : EndCharging;
		then done;
	}
	
	action test : ChargeBattery;
}