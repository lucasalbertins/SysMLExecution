package ActionFlow {
	
	part def Part0;
	part def Part1;
	part def Part2;
	
	part def PartDef1;
	part def PartDef2;
	
	part def PartDef0 {
		perform action0;
		
		part part1 : PartDef1 {
			perform action0.action1;
			perform action0.action4;
		}
		
		part part2 : PartDef2 {
			perform action0.action2;
			perform action0.action3;
		}
	}
	
	action action0 {
        action action1;
        action action2;
        action action3;
        action action4;
        
        first start;
        then ref action action1;
        then ref action action2;
        then ref action action3;
        then ref action action4;
        then done;
	}
}
