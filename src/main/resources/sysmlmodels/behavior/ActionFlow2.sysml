package ActionFlow {
	
	part def PartDef;
	action def Action1;
	action def Action2;
	action def Action3;
	action def Action4;
	
	part Part : PartDef {
		action def Action0 {
        action action1 : Action1;
        action action2 : Action2;
        action action3 : Action3;
        action action4 : Action4;
	}
		perform action action0 : Action0;
	}
}
