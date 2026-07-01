package NewDecisionExample {

   private import ScalarValues::*;
   attribute def BatteryCharged;
   part powerSystem;
   action def MonitorBattery { out charge : Real; }
   action def AddCharge { in charge : Real; }
   action def EndCharging;
   part def Device;
   part def Part1;
   
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
           
           then decide decision1;
               if monitor.batteryCharge < 100.0 then addCharge;
               if monitor.batteryCharge >= 100.0 then endCharging;
               
           action addCharge : AddCharge {
               in charge = monitor.batteryCharge;
               assign device.battery := device.battery * 3 - 40;
           }
           
           then continueCharging;
           action endCharging : EndCharging {
           	assign device.isCharging := false;
           	assign battery := 100.0;
           }
           then done;
       }
   }
   
   part part1 : Part1 {
       perform action chargeBattery : device::ChargeBattery;
   }
}
