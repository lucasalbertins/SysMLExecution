package CloseAirSupport {
	
	action def Patrolling;
	action def ReportThreat;
	action def TakeOffAndPatrol;
	action def ReportAerialObservations;
	action def CollateTacticalDecisions;
	action def ConfirmTargetIdentification;
	action def DesignateObjective;
	action def ReceiveFireOrder;
	action def FireMunition;
	action def HeadToObjective;
	action def ObjectiveDestroyed;
	action def ObserveDamages;
	action def CollateObservations;

	action def ExecuteMission {
		first start;
		
		then fork initFork;
			then patrolling;
			then takeOffAndPatrol;

		action takeOffAndPatrol : TakeOffAndPatrol;
		then join02;

		action patrolling : Patrolling;
		then action reportThreat : ReportThreat;
		then fork receiveThreatDetection;
			then join01;
			then join02;

		join join02;
		then action reportAerialObservations : ReportAerialObservations;
		then join01;

		join join01;
		then action collateTacticalDecisions : CollateTacticalDecisions;
		then fork takeTacticalDecisions;
			then identifyTarget;
			then takePosition;

		fork identifyTarget;
			then confirmTargetIdentification;
			then join03;
			
		action confirmTargetIdentification : ConfirmTargetIdentification;

		fork takePosition;
			then designateObjective;
			then join03;

		action designateObjective : DesignateObjective;
		then join05;

		join join03;
		then fork giveFireOrder;
			then receiveFireOrder;
			then join04; 

		action receiveFireOrder : ReceiveFireOrder;
		then action fireMunition : FireMunition;
		then join05;

		join join05;
		then action headToObjective : HeadToObjective;
		then action objectiveDestroyed : ObjectiveDestroyed;
		then action observeDamages : ObserveDamages;
		then join04;

		join join04;
		then action collateObservations : CollateObservations;
		
		then done;
	}
	
	action executeMission : ExecuteMission;
}