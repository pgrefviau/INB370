package asgn2Vehicles;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;

public abstract class Vehicle {

	protected enum VehiculeState {NEW, QUEUED, PARKED, ARCHIVED}
	
	protected VehiculeState state = VehiculeState.NEW;
	protected String vehID;
	
	protected int arrivalTime;
	protected int departureTime;
	protected int parkingTime;
	
	protected boolean isDissatisfied = true;
	
	protected boolean hasBeenParked = false;
	protected boolean hasBeenQueued = false;
	
	@Override
	public boolean equals(Object obj) 
	{
		if(!(obj instanceof Vehicle))
			return false;
		
		if(obj == this)
			return true;
		
		return ((Vehicle)obj).vehID.equals(this.vehID);
	}
	
	@Override
	public int hashCode() 
	{
		return Integer.valueOf(this.vehID).hashCode();
	}
	
	public Vehicle(String vehID, int arrivalTime) throws VehicleException
	{
		if(arrivalTime <= 0)
			throw new VehicleException("The arrival time for the vehicle has to be strictly positive");
		
		this.vehID = vehID;
		this.arrivalTime = arrivalTime;
	}
	
	//Transition vehicle to parked state (mutator) Parking starts on arrival or on exit from the queue, but time is set here
	public void enterParkedState(int parkingTime, int intendedDuration) throws VehicleException
	{
		if(!isQueued() && !isNew())
			throw new VehicleException("Cannot transition to parked state: the vehicule must be in Queued or New state");
		
		if(parkingTime < 0)
			throw new VehicleException("Parking time value must be positive");
		
		if(intendedDuration < Constants.MINIMUM_STAY)
			throw new VehicleException("Stay duration cannot be less than " + Constants.MINIMUM_STAY);
		
		this.parkingTime = parkingTime;
		this.departureTime = this.parkingTime + intendedDuration;
		this.state = VehiculeState.PARKED;
		this.hasBeenParked = true;
		this.isDissatisfied = false;
	}
	
	//Transition vehicle to queued state (mutator) Queuing formally starts on arrival and ceases with a call to exitQueuedState
	public void	enterQueuedState() throws VehicleException
	{
		if(!isNew())
			throw new VehicleException("Cannot transition to queued state: the vehicule is not in New state");
		
		this.state = VehiculeState.QUEUED;
		this.hasBeenQueued = true;
	}
	
	//Transition vehicle from parked state (mutator)
	public void	exitParkedState(int departureTime) throws VehicleException
	{
		if(!isParked())
			throw new VehicleException("Cannot transition from parked state: the vehicule is not in that state");
		
		
		// it is reverse departureTime NOT departureTime (sheng)
		if(departureTime < this.parkingTime)
			throw new VehicleException("The departure time cannot be before the parking time");
		
		
		this.state = VehiculeState.ARCHIVED;
		this.departureTime = departureTime;
	}
	
	//Transition vehicle from queued state (mutator) 
	//Queuing formally starts on arrival with a call to enterQueuedState Here we exit and set the time at which the vehicle left the queue
	public void	exitQueuedState(int exitTime) throws VehicleException
	{
		if(!isQueued())
			throw new VehicleException("Cannot transition from queued state: the vehicule is not in that state");
		// you forgot if
		if(exitTime < this.arrivalTime)
			throw new VehicleException("The departure time cannot be before the parking time");
	}
	
	//Simple getter for the arrival time
	public int	getArrivalTime()
	{
		return this.arrivalTime;
	}
	
	//Simple getter for the departure time from the car park Note: result may be 0 before parking, show intended departure time while parked; and actual when archived
	public int	getDepartureTime()
	{
		return this.departureTime;
	}
	
	//Simple getter for the parking time Note: result may be 0 before parking
	public int	getParkingTime()
	{
		return this.parkingTime;
	}
	
	//Simple getter for the vehicle ID
	public String	getVehID()
	{
		return this.vehID;
	}
	
	//Boolean status indicating whether vehicle is currently parked
	public boolean	isParked()
	{
		return this.state == VehiculeState.PARKED;
	}
	
	//Boolean status indicating whether vehicle is currently queued	
	public boolean	isQueued()
	{
		return this.state == VehiculeState.QUEUED;
	}

	//Boolean status indicating whether vehicle is currently archived	
	private boolean	isArchived()
	{
		return this.state == VehiculeState.ARCHIVED;
	}
	
	//Boolean status indicating whether vehicle is new	
	private boolean	isNew()
	{
		return this.state == VehiculeState.NEW;
	}
	
	// Boolean status indicating whether customer is satisfied or not Satisfied if they park; dissatisfied if turned away, or queuing for too long 
	// Note that calls to this method may not reflect final status
	public boolean	isSatisfied()
	{
		return hasBeenParked;
	}
	
	@Override
	public String toString() 
	{
		return super.toString();
	}
	
	//Boolean status indicating whether vehicle was ever parked Will return false for vehicles in queue or turned away
	public boolean	wasParked()
	{
		return this.hasBeenParked;
	}
	
	//Boolean status indicating whether vehicle was ever queued
	public boolean	wasQueued()
	{
		return this.hasBeenQueued;
	}
	
	
}
