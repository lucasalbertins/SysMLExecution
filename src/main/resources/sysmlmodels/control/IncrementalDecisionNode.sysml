package IncrementalDecisionNode {
	private import ScalarValues::*;
	
	attribute def BatteryCharge :> Real;
	
	part battery;
	part powerSystem;
	
	action def MonitorBattery { out charge : Real; }
	action def AddCharge { in charge : Real; }
	action def EndCharging;
	
	action def ComputeIncrement { 
		in currentIn : Real; 
		out currentOut : Real = currentIn + 20.0; 
	}
	
	action def ChargeBattery {
		attribute currentCharge : BatteryCharge;
		
		first start;
		
		then merge continueCharging;
		
		then action monitor : MonitorBattery {
			out batteryCharge : Real = currentCharge;
		}
		
		then decide decision1;
			if monitor.batteryCharge < 100.0 then addCharge;
			if monitor.batteryCharge >= 100.0 then endCharging;
			
		action addCharge : AddCharge {
			in charge = monitor.batteryCharge;
		}
		
		then action increment : ComputeIncrement {
			in currentIn = currentCharge;
		}
		
		then action updateState {
			out newCharge : Real = increment.currentOut;
			currentCharge = newCharge; 
		}
		then continueCharging;
		
		action endCharging : EndCharging;
		then done;
	}
	
	part def Part1;

	part part1 : Part1 {
		perform action chargeBattery : ChargeBattery {
			attribute :>> currentCharge = 90.0;
		}
	}
}