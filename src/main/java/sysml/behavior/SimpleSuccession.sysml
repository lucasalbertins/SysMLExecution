package SimpleSuccession {

	action def testAction {
		first start then action1;
  		action action1;
  		first action1 then action2;
  		action action2;
  		first action2 then action3;
  		action action3;
  		first action3 then action4;
  		action action4;
  		first action4 then done;
	}
	
	action test : testAction;
}
