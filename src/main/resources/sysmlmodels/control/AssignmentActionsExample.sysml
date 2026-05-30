package AssignmentActionsExample {
	private import ISQ::*;

	action def ComputeMotion {
		in attribute powerProfile :> ISQ::power[*];
		in attribute vehicleMass :> ISQ::mass;
		in attribute initialPosition :> ISQ::length;
		in attribute initialSpeed :> ISQ::speed;
		in attribute deltaT :> ISQ::time;
		out attribute positions :> ISQ::length[*] := ( );
	
		private attribute position := initialPosition;
		private attribute speed := initialSpeed;
		action def StraightLineDynamics;
	
		for vehiclePower in powerProfile {
			perform action dynamics : StraightLineDynamics {
				in power = vehiclePower;
				in mass = vehicleMass;
				in delta_t = deltaT;
				in x_in = position; in v_in = speed;
				out x_out; out v_out;
			}
		then assign position := dynamics.x_out;
		then assign speed := dynamics.v_out;
		then assign positions := position; // positions->including(position)
		}
	}
	action computeMotion : ComputeMotion;
}