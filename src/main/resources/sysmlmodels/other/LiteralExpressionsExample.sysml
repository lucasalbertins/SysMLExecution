package LiteralExpressionsExample {
	private import SI::*;
	private import ScalarValues::*;

	enum def ColorEnum {
		enum Red;
		enum Blue;
		enum Green;
	}

	attribute def speed;
	attribute def maxLoad;
	attribute def modelName;
	attribute def isElectric;
	attribute def serialNumber;
	attribute def vehicleColor;
	
	part def Vehicle {
		attribute speed: Real = 120.0;
		attribute maxLoad: MassUnit = 500 [kg];
		attribute modelName: String = "X-Series";
		attribute isElectric: Boolean = true;
		attribute serialNumber: Integer = 123456;
		attribute vehicleColor: ColorEnum = ColorEnum::Red;
	}
}
