package vehiclePkg {
    private import ISQ::*;
    private import SI::*;

    part def Vehicle {
        attribute mass :> ISQ::mass;
    }    
    part vehicle1 : Vehicle {
        attribute :>> mass = 1350[SI::kg];
    }
}