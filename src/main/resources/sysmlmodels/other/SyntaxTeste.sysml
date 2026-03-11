package 'Part Definition Example' {
	part vehicle{
		attribute mass = engine.mass+transmission.mass;
		perform providePower;
		part engine{
			attribute mass;
			port torqueOutPort;
			perform providePower.generateTorque;
		}
		part transmission{
			attribute mass;
			port torqueInPort;
			perform providePower.amplifyTorque;
		}
		connect engine.torqueOutPort to transmission.torqueInPort;
	}
	action providePower{
		action generateTorque;
		action amplifyTorque;
	}
}