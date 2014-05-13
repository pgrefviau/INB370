package asgn2Vehicles;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;

public abstract class Vehicle extends java.lang.Object {
	
	
	private int arrivalTime;
	String vehID;
	private int parkingTime , queuingTime;
	
	private int departureTime;
	
	private Boolean parked;
	private Boolean queued;
	private Boolean wasParked, wasQueued;

	

	public Vehicle(java.lang.String vehID,int arrivalTime)
		        throws VehicleException{
	
		this.vehID = vehID;
		this.arrivalTime = arrivalTime;
		
		if(arrivalTime <= 0){
			throw new VehicleException("The arrival time for the vehicle has to be strictly positive");
		}
		
	}
	

	
	


	public void enterParkedState(int parkingTime, int intendedDuration)
				throws VehicleException{

		
		
		if(isParked() || isQueued()){
			throw new VehicleException("the vehicle is already parked or queued");
		}
		
		else if (parkingTime < 0){
			
			throw new VehicleException
			("Parking Time can not be less than Zero");
		}
		
		else if( intendedDuration < Constants.MINIMUM_STAY ){
			throw new VehicleException
			("Intended Duration can not be lesser than " + Constants.MINIMUM_STAY  );
		}
		
		this.parkingTime = parkingTime;
		this.departureTime = this.parkingTime + intendedDuration;
		
	
	}
	
	public void enterQueuedState()
            throws VehicleException{
		if(isParked() || isQueued())
			throw new VehicleException
			("the vehicle is already parked or queued");
		
	}
	
	public void exitParkedState(int departureTime)
			throws VehicleException{
		
		this.departureTime = departureTime;
		
		if ( !isParked()){
			throw new VehicleException
			("The Vehicle is can not be in exit parked state "
					+ "because it is not parked yet");
		}
		
		else if(isQueued()){
			throw new VehicleException
			("The Vehicle is can not be in exit parked state "
					+ "because it is in the Queued ");
		}
		
		
		// how to do reversed departureTime
		else if ( departureTime < parkingTime){
			throw new VehicleException
			("Inviliad Departure Time,"
					+ " it can not be lesser than Parking Time ");
		}
		
	}
	
	public void exitQueuedState(int  exitTime)
            throws VehicleException{
		
		
		
		if ( isParked()){
			throw new VehicleException
			("The Vehicle is  in exit parked state "
					+ "so it can not Exit Queued.");
		}
		
		else if(!isQueued()){
			throw new VehicleException
			("Can not Exist the Queued because"
					+ " the Vechicle is not yet in the Queued ");
		}
		
		
		// how to do reversed departureTime
		else if (exitTime < arrivalTime){
			throw new VehicleException
			
			("Inviliad Exist Time,"
					+ " it can not be lesser than Arrival Time ");
		}
	}
	
	
	public int getArrivalTime(){
		return arrivalTime;
	}
	
	public int getDepartureTime(){
		return departureTime;
	}
	
	public int getParkingTime(){
		return parkingTime;
	}
	
	public java.lang.String getVehID(){
		return vehID;
	}
	
	public boolean isParked(){
		
		if (parked == true){
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public boolean isQueued(){
		
		if (queued == true){
			return true;
		}
		
		else{
			return false;
		}
	}
	
	public boolean isSatisfied(){
		
		
		 if(!isParked() || 
				 queuingTime > Constants.MAXIMUM_QUEUE_TIME ){
			 return false;
		 }
		 
		 return true;
	}
	
	@Override
	public String toString() 
	{
		return super.toString();
	}
	
	
	
	public boolean wasParked(){
		if (isParked() || wasParked == true){
			return true;
		}
		return false;
	}
	
	public boolean wasQueued(){
		if(isQueued() || wasQueued == true){
			return true;
		}
		return false;
	}
	
}
