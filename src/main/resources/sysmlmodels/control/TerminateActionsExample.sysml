package TerminateActionsExample {
	private import ScalarValues::Boolean;
	
	action monitorCriticalActivity;
	action criticalActivity;
	
	action def MonitoredActivity {
		first start;

		then fork fork1;
			then monitorCriticalActivity;
			then criticalActivity;
					
		monitorCriticalActivity;
		then terminate;
			
		criticalActivity;
		then terminate;
	}
	
	action monitored : MonitoredActivity;
}