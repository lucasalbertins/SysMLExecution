package NewDecisionExample {

   private import ScalarValues::*;
   action def MonitorBattery { out charge : Real; }
   action def AddCharge { in charge : Real; }
   action def EndCharging;
   action def ChargeBattery;
   attribute def BatteryCharged;
   part powerSystem;
   part def Device;
   part def Part1;
   
   part device : Device {
       out attribute battery : Real = 30.0;
       out attribute isCharging : Boolean = true;
       
       action def Machine {
               first start;
               then action chargeBattery : ChargeBattery {
                   merge continueCharging;
                   then action monitor : MonitorBattery {
                       out batteryCharge : Real;
                       assign batteryCharge := device.battery;
                   }
                   then decide decision;
                       if monitor.batteryCharge < 100.0 then addCharge;
                       if monitor.batteryCharge >= 100.0 then endCharging;
               
               action addCharge : AddCharge {
                       in charge = monitor.batteryCharge;
                       assign device.battery := device.battery * 3 - 40;
                   }
                   then continueCharging;
                   action endCharging : EndCharging {
                       assign device.isCharging := false;
                   }
                   then done;
            }
            then done;
        }
   }
   
   part part1 : Part1 {
       perform action machine : device::Machine;
   }
}