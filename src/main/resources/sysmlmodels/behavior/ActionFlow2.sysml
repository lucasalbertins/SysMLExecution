package ActionFlow {
	
	part Part {
		action def Action0 {
        first start;
        then action action1;
        then action action2;
        then action action3;
        then action action4;
        then done;
	}
	
		
		perform action action0 : Action0;
	}
}
