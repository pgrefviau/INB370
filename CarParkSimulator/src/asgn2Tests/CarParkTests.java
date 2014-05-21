package asgn2Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;


import asgn2CarParks.CarPark;
import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;



public class CarParkTests {

	private final static int ARRIVAL_TIME_MAX_INCREMENT = 3;
	private final static int DEFAULT_RANDOM_VEHICLES_POOL_SIZE = 10;
	
	private CarPark carPark;
	
	private static int idCounter = 0; 
	private static int vehicleArrivalTime = 0;
	private static int vehiclePoolIndex = 0;
	private static List<Vehicle> vehiclePool = new ArrayList<Vehicle>(); 
	
	private static int getRandomArrivalTimeIncrement()
	{
		Random rand = new Random();
		return rand.nextInt(ARRIVAL_TIME_MAX_INCREMENT) + 1;
	}

	// Get a random, valid arrival time for a vehicle
	private static int getInitialVehicleArrivalTime()
	{	
		vehicleArrivalTime = getRandomArrivalTimeIncrement();
		return vehicleArrivalTime;
	}
	
	// Get valid arrival, subsequent, increasing arrival time for a vehicle 
	private static int getNextVehicleArrivalTime()
	{
		vehicleArrivalTime += getRandomArrivalTimeIncrement();
		return vehicleArrivalTime;
	}
	
	// Get a incremental unique id for a vehicle 
	private static String getNextId()
	{
		return String.valueOf(idCounter++);
	}
	
	// Generate a random vehicle (car, small car or motorcycle) with valid and consistent attributes
	private Vehicle generateRandomVehicle(boolean isFirstVehicle) throws VehicleException
	{
		Random rand = new Random();
		String vehiculeId = getNextId(); 
		int arrivalTime = isFirstVehicle ? getInitialVehicleArrivalTime() : getNextVehicleArrivalTime() ;
		
		if(rand.nextBoolean())
			return new Car(vehiculeId, arrivalTime, rand.nextBoolean());
		else
			return new MotorCycle(vehiculeId, arrivalTime);
	}
	
	private Car generateRandomCar() throws VehicleException
	{
		return new Car(getNextId(), getNextVehicleArrivalTime(), false);
	}
	
	private Car generateRandomSmallCar() throws VehicleException
	{
		return new Car(getNextId(), getNextVehicleArrivalTime(), true);
	}
	
	private MotorCycle generateRandomMotorCycle() throws VehicleException
	{
		return new MotorCycle(getNextId(), getNextVehicleArrivalTime());
	}
	
	// Generate a fixed number of random vehicles to place in the pool. 
	private void generateNewVehicles()
	{
		generateNewVehicles(DEFAULT_RANDOM_VEHICLES_POOL_SIZE);
	}
	
	// Generate a certain number of random vehicles to place in the pool. 
	// Useful the number of random vehicles needed in the pool is greater than the default value 
	private void generateNewVehicles(int poolSize)
	{
		vehiclePool.clear();
		vehiclePoolIndex = 0;
		try 
		{
			vehiclePool.add(generateRandomVehicle(true));
			for(int i = 0; i < poolSize; i++)
				vehiclePool.add(generateRandomVehicle(false));
			
			
		} catch (VehicleException e) {
			
		}
	}
	
	// Get the next random vehicle in the pool
	private Vehicle getNextVehicle()
	{
		return vehiclePool.get(vehiclePoolIndex++);
	}
	
	@Before @Test
	public void setUpCarDefaultPark() 
	{	
		carPark = new CarPark();
		generateNewVehicles();
	}
	
	@Test(expected = VehicleException.class)
	// Test if vehicle to be archived is not in the correct state
	public void testIncorrectStateArchiveDepartingVehicleAttempt() throws VehicleException, SimulationException
	{
		
		Vehicle v = getNextVehicle();
		int stayDuration = Constants.MINIMUM_STAY;
		int departureTime = v.getArrivalTime() + stayDuration;
		
		v.enterParkedState(v.getArrivalTime(), stayDuration);
		v.enterQueuedState();
		carPark.archiveDepartingVehicles(departureTime, true);
	}
	
	@Test(expected = SimulationException.class)
	public void testNotInCarParkArchiveDepartingVehicleAttempt() throws SimulationException
	{
		
		Vehicle v = getNextVehicle();
		//////////////////////////////////////////////////////////////////////////////// TODO
		//carPark.archiveDepartingVehicles(departureTime, true);
	}
	
	@Test(expected = SimulationException.class)
	// Test if new vehicle is currently is in invalid state (queued or parked)
	public void testIncorrectStateArchiveNewVehicleAttempt() throws SimulationException, VehicleException
	{
		Vehicle v = getNextVehicle();
		v.enterQueuedState();
		carPark.archiveNewVehicle(v);
	}
	
	@Test(expected = VehicleException.class)
	// Test if queued vehicle is currently in invalid state (anything but queued)
	public void testIncorrectStateArchiveQueueFailuresAttempt() throws SimulationException, VehicleException 
	{
		Vehicle v = getNextVehicle();
		carPark.enterQueue(v);
		v.enterParkedState(v.getArrivalTime(), Constants.MINIMUM_STAY);
		carPark.archiveQueueFailures(0);
	}
	
	@Test
	public void testCarParkFull() throws VehicleException, SimulationException
	{
		int maxNbOfCars = Constants.DEFAULT_MAX_CAR_SPACES;
		int maxNbOfMotorCycles = Constants.DEFAULT_MAX_MOTORCYCLE_SPACES;
		int maxNbOfSmallCars = Constants.DEFAULT_MAX_SMALL_CAR_SPACES;
		int total = maxNbOfSmallCars +maxNbOfMotorCycles + maxNbOfCars;
		
		carPark = new CarPark(maxNbOfCars, 
				 			  maxNbOfMotorCycles, 
				 			  maxNbOfSmallCars,
				 			  Constants.DEFAULT_MAX_QUEUE_SIZE);
		
		String carId = getNextId(); 
		
		for(int i = 0; i < maxNbOfCars; i++)
		{
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new Car(carId, arrivalTime, false);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		for(int i = 0; i < maxNbOfMotorCycles; i++)
		{
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new MotorCycle(carId, arrivalTime);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		for(int i = 0; i < maxNbOfSmallCars; i++)
		{
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new Car(carId, arrivalTime, true);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		assertTrue(carPark.carParkFull());
	}
	
	@Test
	public void testCarParkEmpty() throws SimulationException, VehicleException 
	{
		assertTrue(carPark.carParkEmpty());
		
		carPark.parkVehicle(getNextVehicle(), 0, Constants.MINIMUM_STAY);
		assertFalse(carPark.carParkEmpty());
		
		carPark.archiveDepartingVehicles(Constants.MINIMUM_STAY, true);
		assertTrue(carPark.carParkEmpty());
	}
	
	@Test(expected = SimulationException.class)
	//  Test adding a vehicle in queue when queue is full
	public void testAddingVehicleToFullQueue() throws SimulationException, VehicleException
	{
		int maxQueueSize = Constants.DEFAULT_MAX_QUEUE_SIZE;
		
		carPark = new CarPark(Constants.DEFAULT_MAX_CAR_SPACES, 
							  Constants.DEFAULT_MAX_SMALL_CAR_SPACES, 
							  Constants.DEFAULT_MAX_MOTORCYCLE_SPACES, 
							  maxQueueSize);
		
		generateNewVehicles(maxQueueSize + 1);
		
		for(int i = 0; i < maxQueueSize; i++)
			carPark.enterQueue(getNextVehicle());
		
		carPark.enterQueue(getNextVehicle());
	}
	
	@Test(expected = VehicleException.class)
	// Test adding a vehicle to the queue if it's not in the correct state
	public void testIncorrectStateAddingVehicleToQueue() throws SimulationException, VehicleException 
	{
		
		Vehicle v = getNextVehicle();
		v.enterParkedState(0, Constants.MINIMUM_STAY);
		carPark.enterQueue(v);
	}
	
	@Test(expected = VehicleException.class)
	// Test removing a vehicle from the queue if it's not in the queued state
	public void testIncorrectStateRemovingVehicleFromQueue() throws SimulationException, VehicleException
	{
		Vehicle v = getNextVehicle();
		carPark.exitQueue(v, v.getArrivalTime() + 1);
	}
	
	@Test(expected = VehicleException.class)
	// Test removing a vehicle from the queue with a departure time that's lower or equal to its arrival time
	public void testIncorrectDepartureTimeRemovingVehicleFromQueue() throws SimulationException, VehicleException 
	{
		Vehicle v = getNextVehicle();
		carPark.exitQueue(v, v.getArrivalTime());
	}
	
	
}
