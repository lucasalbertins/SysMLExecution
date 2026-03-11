package ForkJoinExample {
	private import ScalarValues::*;
	
	attribute def TurnKeyToOn;
	attribute def BrakePressure;
	
	action def MonitorBrakePedal { out pressure : BrakePressure; }
	action def MonitorTraction { out modFreq : Real; }
	action def Braking { in brakePressure : BrakePressure; in modulationFrequency : Real; }
	
	action def Brake {
		first start;
		then action TurnOn;
		
		then fork Fork1;
			then monitorBrakePedal;
			then monitorTraction;
			then braking;
		
		action monitorBrakePedal : MonitorBrakePedal {
			out brakePressure;
		}
		then joinNode;
		
		action monitorTraction : MonitorTraction {
			out modulationFrequency;
		}
		then joinNode;
		
		flow Flow1 from monitorBrakePedal.brakePressure to braking.brakePressure;
		flow Flow2 from monitorTraction.modulationFrequency to braking.modulationFrequency; 
		
		action braking : Braking {
			in brakePressure; 
			in modulationFrequency;
		}
		then joinNode;
		
		join joinNode;
		then done;
	}
}