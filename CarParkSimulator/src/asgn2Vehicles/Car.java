package asgn2Vehicles;

import asgn2Exceptions.VehicleException;

public class Car extends Vehicle{

	private boolean isSmall;
	
	public Car(String vehID, int arrivalTime, boolean small) throws VehicleException
	{       
		super("C" + vehID,arrivalTime);
		this.isSmall = small;
                
                if(this.isSmall)
                    this.vehID = "S" + vehID;
	}   
	
	public boolean isSmall() {
		return this.isSmall;
	}
}
