
package asgn2CarParks;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	// use int
	private int numCars = 0;
	private int numSmallCars = 0;
	private int numMotorCycles = 0;
	private int numDissatisfied = 0;
	
	// TODO: Check that this never goes over max maxSmallCarSpaces at any time
	private int motorCycleOverflow;
	
	// TODO: Check that this never goes over max maxCarSpaces at any time
	private int smallCarOverflow;
	
	private int count = 0;
	
	private LinkedList<Vehicle> past = new LinkedList<Vehicle>();
	private LinkedList<Vehicle> queue = new LinkedList<Vehicle>();
	private LinkedList<Vehicle> spaces = new LinkedList<Vehicle>();
	
	private String status;

	public CarPark() {
		this(Constants.DEFAULT_MAX_CAR_SPACES, 
			 Constants.DEFAULT_MAX_SMALL_CAR_SPACES, 
			 Constants.DEFAULT_MAX_MOTORCYCLE_SPACES,
			 Constants.DEFAULT_MAX_QUEUE_SIZE);
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
		//We use a separate list to avoid ConcurrentModificationException on the queue;
		List<Vehicle> tempParkedCars = new LinkedList<Vehicle>(spaces);
		
		// Iterate through the parked vehicles
		for(Vehicle v : tempParkedCars)
		{
			if(time >= v.getDepartureTime())
				unparkVehicle(v, time);
		}
	}
	
	public void	archiveNewVehicle(Vehicle v)
	{	
		past.add(v);
		this.numDissatisfied++;
	}
	
	//Archive vehicles which have stayed in the queue too long
	public void	archiveQueueFailures(int time) throws SimulationException, VehicleException
	{
		//We use a separate list to avoid ConcurrentModificationException on the queue;
		List<Vehicle> tempQueueCopy = new LinkedList<Vehicle>(queue);
		
		for(Vehicle v : tempQueueCopy)
		{
			int timeSpentInQueue = time - v.getArrivalTime();
			if(timeSpentInQueue > Constants.MAXIMUM_QUEUE_TIME)
			{	
				exitQueue(v, time);
				past.add(v);
				this.numDissatisfied++;
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
		return this.count >= (this.maxCarSpaces + this.maxMotorCycleSpaces + this.maxSmallCarSpaces);
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
		v.exitQueuedState(exitTime);
		
		Iterator<Vehicle> it = queue.iterator();
		while(it.hasNext())
		{
			 if(it.next().equals(v))
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
		//TODO: Fix that
		return initialState();
	}
	
	//Simple getter for number of cars in the car park
	public int	getNumCars()
	{
		return this.numCars;
	}
	
	//Simple getter for number of motorcycles in the car park
	public int	getNumMotorCycles()
	{
		return this.numMotorCycles + this.motorCycleOverflow;
	}
	
	//Simple getter for number of small cars in the car park
	public int	getNumSmallCars()
	{
		return this.numSmallCars + this.smallCarOverflow;
	}
	
	//Simple status showing number of vehicles in the queue
	public int	numVehiclesInQueue()
	{
		return this.queue.size();
	}
	
	// Indicates if there is place left for motorcycles in the carpark (including the small car spots overflow)
	private boolean hasPlaceLeftForMotorCycles()
	{
		return !areMotorCycleSpotsFull() || !areSmallCarSpotsFull();
	}
	
	// Indicates if there is place left for small cars in the carpark (including the car spots overflow)
	private boolean hasPlaceLeftForSmallCars()
	{
		return !areSmallCarSpotsFull() || !areCarSpotsFull();
	}
	
	// Indicates if there is place left for small cars in the carpark (including the car spots overflow)
	private boolean hasPlaceLeftForCars()
	{
		return !areCarSpotsFull();
	}
	
	// Indicates if the spots reserved for motorcycles are filled
	private boolean areMotorCycleSpotsFull()
	{
		return this.numMotorCycles >= this.maxMotorCycleSpaces;
	}
	
	// Indicates if the spots reserved for small cars are filled (including motorcycle overflows if any)
	private boolean areSmallCarSpotsFull()
	{
		return (this.motorCycleOverflow + this.numSmallCars) >= this.maxSmallCarSpaces;
	}
	
	// Indicates if the spots reserved for cars are filled (including small cars overflows if any)
	private boolean areCarSpotsFull()
	{
		return (this.smallCarOverflow + this.numCars) >= this.maxCarSpaces;
	}
	
	// Increments the number of total motorcycles, considering the overflow if necessary
	private void incrementNumberOfMotorCycles()
	{
		if(!areMotorCycleSpotsFull())
			this.numMotorCycles++;
		else
			this.motorCycleOverflow++;
	}
	
	// Decrements the number of total motorcycles, considering the overflow if necessary	
	private void decrementNumberOfMotorCycles()
	{
		if(!isMotorCycleOverflowEmpty())
			this.numMotorCycles--;
		else
			this.motorCycleOverflow--;
	}
	
	// Increments the number of total small cars, considering the overflow if necessary
	private void incrementNumberOfSmallCars()
	{
		if(!areSmallCarSpotsFull())
			this.numSmallCars++;
		else
			this.smallCarOverflow++;
	}

	// Decrements the number of total small cars, considering the overflow if necessary	
	private void decrementNumberOfSmallCars()
	{
		if(isSmallCarOverflowEmpty())
			this.numSmallCars--;
		else
			this.smallCarOverflow--;
	}
	
	// Increments the number of total cars
	private void incrementNumberOfCars()
	{
		this.numCars++;
	}
	
	// Decrements the number of total cars
	private void decrementNumberOfCars()
	{
		this.numCars--;
	}
	
	// Indicates if the overflow buffer for the motocycles (onto the small cars spots) is empty
	private boolean isMotorCycleOverflowEmpty()
	{
		return this.motorCycleOverflow == 0;
	}
	
	// Indicates if the overflow buffer for the small cars (onto the cars spots) is empty
	private boolean isSmallCarOverflowEmpty()
	{
		return this.smallCarOverflow == 0;
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
				incrementNumberOfSmallCars();
			else
				incrementNumberOfCars();
		}
		
		if(v instanceof MotorCycle)
			incrementNumberOfMotorCycles();
			
		count++;	
	}
	
	//Silently process elements in the queue, whether empty or not.
	public void	processQueue(int time, Simulator sim) throws SimulationException, VehicleException
	{
		//We use a separate list to avoid ConcurrentModificationException on the queue;
		List<Vehicle> tempQueueCopy = new LinkedList<Vehicle>(queue);
			
		for(Vehicle v : tempQueueCopy)
		{
			if(spacesAvailable(v))
			{
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
		if(carParkFull())
			return false;
		
		if(v instanceof Car)
		{			
			Car car = (Car) v;
			if(car.isSmall())
				return hasPlaceLeftForSmallCars();
			else
				return hasPlaceLeftForCars();
		}
		
		if(v instanceof MotorCycle)
			return hasPlaceLeftForMotorCycles();

		// TODO: Throw exception ?
		return false;
	}
	
	@Override
	public String toString() 
	{
		return super.toString();
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
				decrementNumberOfSmallCars();
			else
				decrementNumberOfCars();
		}
		
		if(v instanceof MotorCycle)
			decrementNumberOfMotorCycles();
		
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
