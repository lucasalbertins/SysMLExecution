package SimpleForkSuccession {

	action def testAction {
		first start then action1;
		action action1;		
		then fork f1;
			then action2;
			then action3;
			then action4;

		first action5 then done;
		
		action action2;
		action action3;
		action action4;
		action action5;
	}
}
