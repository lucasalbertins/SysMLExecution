package 'BatterySystem' {
	private import ScalarValues::*;
	
	attribute def TurnSOS;
	
	part def Battery {
		attribute charge : Real;
		attribute sosTrigger : Real;
		attribute rechargeTrigger : Real;
		attribute consumeBattery : Real = 1;
		attribute chargeValue : Real = 5;
		attribute minCharge : Real = 0;
		attribute maxCharge : Real = 100;
		attribute keepOnValue : Real = 10;
		attribute keepRechargeValue : Real = 90;
	}
	action MonitorBattery { out charge : Real; }
	attribute def TurnRecharge;
	attribute def BatteryDepleted;
	attribute def TurnOn;

	state BatteryStates {
		in battery : Battery;
		
		entry; then on; 
		do MonitorBattery;
		state on{
			entry assign battery.charge := battery.charge - battery.consumeBattery;
		}
		accept TurnSOS
			if MonitorBattery.charge >= battery.sosTrigger 
			do then SOS;
		accept BatteryDepleted
			if MonitorBattery.charge == battery.minCharge 
			do assign battery.charge := battery.charge + battery.chargeValue then off;	
		accept
			when MonitorBattery.charge > battery.keepOnValue 
			then on;
		
		state SOS; then off;
		
		state off;
		
		accept TurnRecharge
			if MonitorBattery.charge <= battery.rechargeTrigger 
			do then Recharge;
		accept TurnOn
			if MonitorBattery.charge >= battery.minCharge 
			do then on;
			
		state Recharge{
			entry assign battery.charge := battery.charge + battery.chargeValue; 
		}
		accept TurnOn
			if MonitorBattery.charge >= battery.maxCharge
			do then on;
		accept
			when MonitorBattery.charge < battery.keepRechargeValue 
			then Recharge;
	}
}
