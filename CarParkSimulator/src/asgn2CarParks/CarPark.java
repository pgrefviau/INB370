
package asgn2CarParks;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Simulators.Simulator;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;


public class CarPark{		
	
	private static int idCounter = 0;
	
	private int maxCarSpaces;
	private int maxSmallCarSpaces;
	private int maxMotorCycleSpaces;
	private int maxQueueSize;
	
	private int numCars = 0;
	private int numSmallCars = 0;
	private int numMotorCycles = 0;
	private int numDissatisfied = 0;
	
	private int count = 0;
	
	private LinkedList<Vehicle> past;
	private LinkedList<Vehicle> queue;
	private LinkedList<Vehicle> spaces;
	
	private String status;
	

	public CarPark() {
		this.maxCarSpaces = Constants.DEFAULT_MAX_CAR_SPACES;
		this.maxSmallCarSpaces = Constants.DEFAULT_MAX_SMALL_CAR_SPACES;
		this.maxMotorCycleSpaces = Constants.DEFAULT_MAX_MOTORCYCLE_SPACES;
		this.maxQueueSize = Constants.DEFAULT_MAX_QUEUE_SIZE;
	}
	
	public CarPark(int maxCarSpaces, int maxSmallCarSpaces, int maxMotorCycleSpaces, int maxQueueSize)
	{
		this.maxCarSpaces = maxCarSpaces;
		this.maxSmallCarSpaces = maxSmallCarSpaces;
		this.maxMotorCycleSpaces = maxMotorCycleSpaces;
		this.maxQueueSize = maxQueueSize;
	}


	public void archiveDepartingVehicles(int time, boolean force) throws VehicleException
	{
		
		// Iterate through the parked vehicles
		Iterator<Vehicle> it = spaces.iterator();
		while(it.hasNext())
		{
			Vehicle v = it.next();
			if(v.getDepartureTime() >= time)
				unparkVehicle(v, time);
		}
	}
	
	public void	archiveNewVehicle(Vehicle v)
	{
		past.add(v);
	}
	
	//Archive vehicles which have stayed in the queue too long
	public void	archiveQueueFailures(int time) throws SimulationException, VehicleException
	{
		Iterator<Vehicle> it = queue.iterator();
		while(it.hasNext())
		{
			Vehicle v = it.next();
			int timeSpentInQueue = time - v.getArrivalTime();
			
			//TODO: Watch out for ConcurrentModificationExpcetion here --> Thorough testing
			if(timeSpentInQueue > Constants.MAXIMUM_QUEUE_TIME)
			{	
				exitQueue(v, time);
				past.add(v);
			}
		}
	}
	
	//Simple status showing whether carPark is empty
	public boolean carParkEmpty()
	{
		return this.count == 0;
	}
	
	//Simple status showing whether carPark is full
	public boolean	carParkFull()
	{
		return this.count >= (this.maxCarSpaces + this.maxMotorCycleSpaces);
	}
	
	//Method to add vehicle successfully to the queue 
	//Precondition is a test that spaces are available 
	//Includes transition through Vehicle.enterQueuedState
	public void	enterQueue(Vehicle v) throws SimulationException, VehicleException
	{
		if(queueFull())
			throw new SimulationException("The queue is full");
		
		v.enterQueuedState();
		queue.addLast(v);
	}
	
	//Method to remove vehicle from the queue after which it will be parked or removed altogether.
	public void	exitQueue(Vehicle v, int exitTime) throws SimulationException, VehicleException
	{
		
		//TODO: Iterator n stuff 
		
		v.exitQueuedState(exitTime);
		Iterator<Vehicle> it = queue.iterator();
		while(it.hasNext())
		{
			 if(it.next().getVehID() == v.getVehID())
			 {
				 it.remove();
				 return;
			 }
		}
	}
	
	//State dump intended for use in logging the final state of the carpark
	//All spaces and queue positions should be empty and so we dump the archive
	public String  finalState()
	{
		
	}
	
	//Simple getter for number of cars in the car park
	public int	getNumCars()
	{
		return this.numCars;
	}
	
	
	//Simple getter for number of motorcycles in the car park
	public int	getNumMotorCycles()
	{
		return this.numMotorCycles;
	}
	
	//Simple getter for number of small cars in the car park
	public int	getNumSmallCars()
	{
		return this.numSmallCars;
	}
	
	//Simple status showing number of vehicles in the queue
	public int	numVehiclesInQueue()
	{
		return this.queue.size();
	}
	
	//Method to add vehicle successfully to the car park store.
	public void	parkVehicle(Vehicle v, int time, int intendedDuration) throws SimulationException, VehicleException
	{
		
		if(!spacesAvailable(v))
			throw new SimulationException("There are no places left for this kind of vehicule within the carpark");
		
		v.enterParkedState(time, intendedDuration);
		spaces.addLast(v);
		
		if(v instanceof Car)
		{
			if(((Car)v).isSmall())
				this.numSmallCars++;
			
			this.numCars++;
		}
		
		if(v instanceof MotorCycle)
			this.numMotorCycles++;
			
		count++;	
	}
	
	//Silently process elements in the queue, whether empty or not.
	public void	processQueue(int time, Simulator sim) throws SimulationException, VehicleException
	{
		Iterator<Vehicle> it = queue.iterator();
		
		while(it.hasNext())
		{
			Vehicle v = it.next();
			if(spacesAvailable(v))
			{
				//TODO: Watch out here too for ConcurentModificationException
				exitQueue(v, time);
				parkVehicle(v, time, sim.setDuration());
			}
		}
	}
	
	//Simple status showing whether queue is empty
	public boolean	queueEmpty()
	{
		return this.queue.size() == 0;
	}
	
	
	//Simple status showing whether queue is full
	public boolean	queueFull()
	{
		return this.queue.size() >= maxQueueSize;
	}
	
	
	//Method determines, given a vehicle of a particular type, whether there are spaces available for that type in the car park under the parking policy in the class header.
	public boolean	spacesAvailable(Vehicle v)
	{
		if(v instanceof Car)
		{			
			Car car = (Car) v;
			if(car.isSmall())
				return this.numSmallCars < this.maxSmallCarSpaces;
			else
				return this.numCars < this.maxCarSpaces;
		}
		
		if(v instanceof MotorCycle)
			return this.numMotorCycles < this.maxMotorCycleSpaces;

		
		// TODO: Throw exception ?
		return false;
	}
	
	@Override
	public String toString() 
	{
		
	}
	
	public static String getNextId()
	{
		return String.valueOf(idCounter++);
	}
	
	//Method to try to create new vehicles (one trial per vehicle type per time point) and to then try to park or queue (or archive) any vehicles that are created
	public void	tryProcessNewVehicles(int time, Simulator sim) throws VehicleException, SimulationException
	{
		
		Vehicle newVehicle;
		
		if(sim.newCarTrial())
		{
			newVehicle = new Car(getNextId(), time, sim.smallCarTrial());
			processNewVehicule(newVehicle, time, sim);
		}
		
		if(sim.motorCycleTrial())
		{
			newVehicle = new MotorCycle(getNextId(), time);
			processNewVehicule(newVehicle, time, sim);
		}
	}
	
	// Try to insert the vehicle into the different locations
	private void processNewVehicule(Vehicle v, int time, Simulator sim) throws SimulationException, VehicleException
	{
		if(spacesAvailable(v))
			parkVehicle(v, time, sim.setDuration());
		else if(!queueFull())
			enterQueue(v);
		else
			archiveNewVehicle(v);
	}
	
	
	//Method to remove vehicle from the carpark.
	public void	unparkVehicle(Vehicle v, int departureTime) throws VehicleException
	{
		//TODO: Iterate through list and remove given vehicle
		Iterator<Vehicle> it = spaces.iterator();
		while(it.hasNext())
		{
			if(it.next().equals(v))
			{
				it.remove();
				break;
			}	
		}
			
		past.addLast(v);
		v.exitParkedState(departureTime);
		
		if(v instanceof Car)
		{
			if(((Car)v).isSmall())
				this.numSmallCars--;
			
			this.numCars--;
		}
		
		if(v instanceof MotorCycle)
			this.numMotorCycles--;
		
		count--;
	}
	
	
	/**
     * **USE, BUT YOU MAY NEED TO CHANGE THE VAR NAMES ***
	 * Method used to provide the current status of the car park. 
	 * Uses private status String set whenever a transition occurs. 
	 * Example follows (using high probability for car creation). At time 262, 
	 * we have 276 vehicles existing, 91 in car park (P), 84 cars in car park (C), 
	 * of which 14 are small (S), 7 MotorCycles in car park (M), 48 dissatisfied (D),
	 * 176 archived (A), queue of size 9 (CCCCCCCCC), and on this iteration we have 
	 * seen: car C go from Parked (P) to Archived (A), C go from queued (Q) to Parked (P),
	 * and small car S arrive (new N) and go straight into the car park<br>
	 * 262::276::P:91::C:84::S:14::M:7::D:48::A:176::Q:9CCCCCCCCC|C:P>A||C:Q>P||S:N>P|
	 * @return String containing current state 
	 */
	public String getStatus(int time) 
	{
		
		String str = time +"::"
		+ this.count + "::" 
		+ "P:" + this.spaces.size() + "::"
		+ "C:" + this.numCars + "::S:" + this.numSmallCars 
		+ "::M:" + this.numMotorCycles 
		+ "::D:" + this.numDissatisfied 
		+ "::A:" + this.past.size()  
		+ "::Q:" + this.queue.size(); 
		
		for (Vehicle v : this.queue) {
			if (v instanceof Car) {
				if (((Car)v).isSmall()) {
					str += "S";
				} else {
					str += "C";
				}
			} else {
				str += "M";
			}
		}
		str += this.status;
		this.status="";
		return str+"\n";
	}
	

	/**
     * SAME COMMENTS
	 * State dump intended for use in logging the initial state of the carpark.
	 * Mainly concerned with parameters. 
	 * @return String containing dump of initial carpark state 
	 */
	public String initialState() {
		return "CarPark [maxCarSpaces: " + this.maxCarSpaces
				+ " maxSmallCarSpaces: " + this.maxSmallCarSpaces 
				+ " maxMotorCycleSpaces: " + this.maxMotorCycleSpaces 
				+ " maxQueueSize: " + this.maxQueueSize + "]";
	}

}
