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
import asgn2Simulators.Simulator;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;
import asgn2Vehicles.Vehicle;



public class CarParkTests {

	private final  int ARRIVAL_TIME_MAX_INCREMENT = 3;
	private final  int DEFAULT_RANDOM_VEHICLES_POOL_SIZE = 10;
	
	private CarPark carPark;
	private Simulator sim;
	
	private  int idCounter = 0; 
	private  int vehicleArrivalTime = 0;
	private  int vehiclePoolIndex = 0;
	private  List<Vehicle> vehiclePool = new ArrayList<Vehicle>(); 
	
	//////////////////// HELPER METHODS ////////////////////
	
	private  int getRandomArrivalTimeIncrement()
	{
		Random rand = new Random();
		return rand.nextInt(ARRIVAL_TIME_MAX_INCREMENT) + 1;
	}

	
	// Get valid arrival, subsequent, increasing arrival time for a vehicle 
	private  int getNextVehicleArrivalTime()
	{
		vehicleArrivalTime += getRandomArrivalTimeIncrement();
		return vehicleArrivalTime;
	}
	
	// Get a incremental unique id for a vehicle 
	private  String getNextId()
	{
		return String.valueOf(idCounter++);
	}
	
	// Generate a random vehicle (car, small car or motorcycle) with valid and consistent attributes
	private Vehicle generateRandomVehicle() throws VehicleException
	{
		Random rand = new Random();
		String vehiculeId = getNextId(); 
		int arrivalTime =  getNextVehicleArrivalTime() ;
		
		if(rand.nextBoolean())
			return new Car(vehiculeId, arrivalTime, rand.nextBoolean());
		else
			return new MotorCycle(vehiculeId, arrivalTime);
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
		vehicleArrivalTime = getRandomArrivalTimeIncrement();
		
		try 
		{
			vehiclePool.add(generateRandomVehicle());
			for(int i = 0; i < poolSize - 1 ; i++)
				vehiclePool.add(generateRandomVehicle());
			
			
		} catch (VehicleException e) {
			
		}
	}
	
	// Get the next random vehicle in the pool
	private Vehicle getNextVehicle()
	{
		return vehiclePool.get(vehiclePoolIndex++);
	}
	
	//////////////////// SET UP ////////////////////
	
	@Before @Test
	public void setUpCarDefaultPark() throws SimulationException 
	{	
		carPark = new CarPark();
		sim = new Simulator();
		
		generateNewVehicles();
	}
	
	//////////////////// TESTING THE EXCEPTIONS ////////////////////
	
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
	public void testVehicleNotInCarParkArchiveDepartingVehicleAttempt() throws SimulationException, VehicleException
	{
		Vehicle v = getNextVehicle();
		carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		carPark.archiveDepartingVehicles(v.getArrivalTime() + 1, true);
                carPark.unparkVehicle(v, v.getArrivalTime() + 1);
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
		
		for(int i = 0; i < maxQueueSize + 1; i++)
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
	
	
	//////////////////// TESTING ALL THE TRANSITIONS ////////////////////
	
	@Test
	public void testVehicleClearedOutOfQueueAfterMaxDuration() throws SimulationException, VehicleException
	{
		Vehicle v = getNextVehicle();
		carPark.enterQueue(v);
		assertTrue(v.isQueued());
		
		int beforeMaxQueueTimeReached = v.getArrivalTime() + Constants.MAXIMUM_QUEUE_TIME - 1;
		int onMaxQueueTimeReached = beforeMaxQueueTimeReached + 1;
		
		carPark.archiveQueueFailures(beforeMaxQueueTimeReached);
		assertTrue(v.isQueued());
		
		carPark.archiveQueueFailures(onMaxQueueTimeReached);
		assertFalse(v.isQueued());
	}
	
	@Test
	public void testVehicleClearedOutOfCarParkAfterMaxDuration() throws SimulationException, VehicleException
	{
		
		
		Vehicle v = getNextVehicle();
		carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		assertTrue(v.isParked());
		assertFalse(carPark.carParkEmpty());
		
		int beforeMaxParkTimeReached = v.getArrivalTime() + Constants.MINIMUM_STAY - 1;
		int onMaxParkTimeReached = beforeMaxParkTimeReached + 1;
		
		carPark.archiveDepartingVehicles(beforeMaxParkTimeReached, false);
		assertTrue(v.isParked());
		assertFalse(carPark.carParkEmpty());
		
		carPark.archiveDepartingVehicles(onMaxParkTimeReached, false);
		assertFalse(v.isParked());
		assertTrue(carPark.carParkEmpty());
	}
	
	@Test
	public void testVehicleArchivedIfCarParkAndQueueAreBothFull() throws SimulationException, VehicleException
	{
		carPark = new CarPark(0, 0, 0, 0);
		
		// Modified simulator to guarantee arrival of a vehicle
		sim = new Simulator(Constants.DEFAULT_SEED,Constants.DEFAULT_INTENDED_STAY_MEAN, Constants.DEFAULT_INTENDED_STAY_SD, 1, 0, 0); 
		
		carPark.tryProcessNewVehicles(1, sim);
		assertTrue(carPark.queueEmpty());
		assertTrue(carPark.carParkEmpty());
	}
	
	@Test
	public void testVehicleQueuedIfCarParkIsFull() throws SimulationException, VehicleException
	{
		carPark = new CarPark(0, 0, 0, Constants.DEFAULT_MAX_QUEUE_SIZE);
		// Modified simulator to guarantee arrival of a vehicle
		sim = new Simulator(Constants.DEFAULT_SEED,Constants.DEFAULT_INTENDED_STAY_MEAN, Constants.DEFAULT_INTENDED_STAY_SD, 1, 0, 0); 
		
		carPark.tryProcessNewVehicles(1, sim);
		assertFalse(carPark.queueEmpty());
		assertTrue(carPark.carParkEmpty());
	}
	
	@Test
	public void testVehicleParkedFromQueueIfCarParkSpaceAvailable() throws SimulationException, VehicleException
	{
		Vehicle v = getNextVehicle();
		carPark.enterQueue(v);
		assertTrue(v.isQueued());
		assertFalse(v.isParked());
		assertFalse(carPark.queueEmpty());
		assertTrue(carPark.carParkEmpty());
		
		carPark.processQueue(v.getArrivalTime() + 1, sim);
		assertTrue(v.isParked());
		assertFalse(v.isQueued());
		assertTrue(carPark.queueEmpty());
		assertFalse(carPark.carParkEmpty());
	}

	
	
	//////////////////// TESTING SPECIFIC FUNCTIONS ////////////////////

	@Test
	public void testCarParkEmpty() throws SimulationException, VehicleException 
	{
		assertTrue(carPark.carParkEmpty());
		
		carPark.parkVehicle(getNextVehicle(), 0, Constants.MINIMUM_STAY);
		assertFalse(carPark.carParkEmpty());
		
		carPark.archiveDepartingVehicles(Constants.MINIMUM_STAY, true);
		assertTrue(carPark.carParkEmpty());
	}
	
	
	@Test
	public void testCarParkFull() throws VehicleException, SimulationException
	{
		
		assertFalse(carPark.carParkFull());
		
		int maxNbOfCars = Constants.DEFAULT_MAX_CAR_SPACES;
		int maxNbOfMotorCycles = Constants.DEFAULT_MAX_MOTORCYCLE_SPACES;
		int maxNbOfSmallCars = Constants.DEFAULT_MAX_SMALL_CAR_SPACES;
		int total = maxNbOfSmallCars + maxNbOfMotorCycles + maxNbOfCars;
		
		carPark = new CarPark(maxNbOfCars, 
				 			  maxNbOfMotorCycles, 
				 			  maxNbOfSmallCars,
				 			  Constants.DEFAULT_MAX_QUEUE_SIZE);
		
		String carId = getNextId(); 
		
		for(int i = 0; i < maxNbOfCars; i++)
		{
			assertFalse(carPark.carParkFull());
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new Car(carId, arrivalTime, false);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		for(int i = 0; i < maxNbOfMotorCycles; i++)
		{
			assertFalse(carPark.carParkFull());
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new MotorCycle(carId, arrivalTime);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		for(int i = 0; i < maxNbOfSmallCars; i++)
		{
			assertFalse(carPark.carParkFull());
			int arrivalTime = getNextVehicleArrivalTime();
			Vehicle v = new Car(carId, arrivalTime, true);
			carPark.parkVehicle(v, arrivalTime, Constants.MINIMUM_STAY); 
		}
		
		assertTrue(carPark.carParkFull());
	}
	
	@Test
	public void testIfSatisfiedOnlyIfHasBeenParked() throws SimulationException, VehicleException
	{
		
		Vehicle v1 = getNextVehicle();
		Vehicle v2 = getNextVehicle();	
		Vehicle v3 = getNextVehicle();	
		assertFalse(v1.isSatisfied());
		assertFalse(v2.isSatisfied());
		assertFalse(v3.isSatisfied());
		
		carPark.enterQueue(v1);
		carPark.enterQueue(v2);
		carPark.archiveNewVehicle(v3);
		assertFalse(v1.isSatisfied());
		assertFalse(v2.isSatisfied());
		assertFalse(v3.isSatisfied());
		
		carPark.parkVehicle(v1, v1.getArrivalTime() + 1, Constants.MINIMUM_STAY);
		carPark.exitQueue(v2, v2.getArrivalTime() + 1);
		assertTrue(v1.isSatisfied());
		assertFalse(v2.isSatisfied());
		
		carPark.archiveDepartingVehicles(v1.getParkingTime() + 1, true);
		assertTrue(v1.isSatisfied());
	}
	
	@Test
	public void testArchiveDepartingVehicles() throws VehicleException, SimulationException {
            
            Vehicle v = getNextVehicle();
            int stayDuration = Constants.MINIMUM_STAY;
            int departureTime = v.getArrivalTime() + stayDuration + 1;
            assertTrue(departureTime - v.getArrivalTime() > Constants.MINIMUM_STAY);
            
            carPark.parkVehicle(v, v.getArrivalTime(), stayDuration);
            assertTrue(v.isParked());
            assertFalse(carPark.carParkEmpty());
            
            carPark.archiveDepartingVehicles(departureTime, true);
            assertFalse(v.isParked());
            assertTrue(carPark.carParkEmpty());
	}

	@Test
	public void testArchiveNewVehicle() throws SimulationException {
		
		Vehicle v = getNextVehicle();
        assertFalse(v.isParked() || v.isQueued());
        assertTrue(carPark.carParkEmpty());
        assertTrue(carPark.queueEmpty());
        
        carPark.archiveNewVehicle(v);
        assertFalse(v.isParked() || v.isQueued());
        assertTrue(carPark.carParkEmpty());
        assertTrue(carPark.queueEmpty());
	}       

	@Test
	public void testArchiveQueueFailures() throws SimulationException, VehicleException {
            
	    Vehicle v = getNextVehicle();
	    carPark.enterQueue(v);
	    assertTrue(v.isQueued());
	    
	    carPark.archiveQueueFailures(v.getArrivalTime() + Constants.MAXIMUM_QUEUE_TIME);
	    assertFalse(v.isQueued());
	    assertFalse(v.isParked());
	}


	@Test
	public void testEnterQueue() throws SimulationException, VehicleException {
		
		Vehicle v = getNextVehicle();
		assertFalse(v.isQueued());
		
		carPark.enterQueue(v);
		assertTrue(v.isQueued());
	}


	@Test
	public void testExitQueue() throws SimulationException, VehicleException {
		Vehicle v = getNextVehicle();
		assertFalse(v.isQueued());
		
		carPark.enterQueue(v);
		assertTrue(v.isQueued());
		
		carPark.exitQueue(v, v.getArrivalTime() + 1);
		assertFalse(v.isQueued());
	}


	@Test
	public void testGetNumCars() throws VehicleException, SimulationException {
		Random rand = new Random();
		int randomNumberOfCars = rand.nextInt(Constants.DEFAULT_MAX_CAR_SPACES-1) + 1;
		
		assertTrue(carPark.carParkEmpty());
		
		for(int i =0; i < randomNumberOfCars; i++)
		{
			Vehicle v = new Car(getNextId(), 1, false);
			carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		}
		
		assertEquals(randomNumberOfCars, carPark.getNumCars());
	}


	@Test
	public void testGetNumMotorCycles() throws VehicleException, SimulationException {
		Random rand = new Random();
		int randomNumberOfMotorCycles = rand.nextInt(Constants.DEFAULT_MAX_SMALL_CAR_SPACES-1) + 1;
		
		assertTrue(carPark.carParkEmpty());
		
		for(int i =0; i < randomNumberOfMotorCycles; i++)
		{
			Vehicle v = new MotorCycle(getNextId(), 1);
			carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		}
		
		assertEquals(randomNumberOfMotorCycles, carPark.getNumMotorCycles());
	}


	@Test
	public void testGetNumSmallCars() throws SimulationException, VehicleException {

		Random rand = new Random();
		int randomNumberOfSmallCars = rand.nextInt(Constants.DEFAULT_MAX_CAR_SPACES-1) + 1;
		
		assertTrue(carPark.carParkEmpty());
		
		for(int i =0; i < randomNumberOfSmallCars; i++)
		{
			Vehicle v = new Car(getNextId(), 1, true);
			carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		}
		
		assertEquals(randomNumberOfSmallCars, carPark.getNumSmallCars());
	}



	@Test
	public void testNumVehiclesInQueue() throws SimulationException, VehicleException {
		
		final int queueSize = Constants.DEFAULT_MAX_QUEUE_SIZE;
		carPark = new CarPark(0, 0, 0, queueSize);
		
		Random rand = new Random();
		int numberOfVehiclesToAdd = rand.nextInt(queueSize - 1) + 1;

		generateNewVehicles(numberOfVehiclesToAdd);
		
		for(int i =0; i < numberOfVehiclesToAdd; i++)		
			carPark.enterQueue(getNextVehicle());
		
		assertEquals(numberOfVehiclesToAdd, carPark.numVehiclesInQueue());
	}


	@Test
	public void testParkVehicle() throws SimulationException, VehicleException {
		Vehicle v = getNextVehicle();
		
		carPark.parkVehicle(v, v.getArrivalTime(), Constants.MINIMUM_STAY);
		assertTrue(v.isParked());
		assertFalse(carPark.carParkEmpty());
		
		int nbOfvehiculesInCarPark = 0;
		
		if(v instanceof Car)
			nbOfvehiculesInCarPark =  ((Car)v).isSmall() ? carPark.getNumSmallCars() : carPark.getNumCars();
		else if(v instanceof MotorCycle)
			nbOfvehiculesInCarPark = carPark.getNumMotorCycles();
		else
			fail();
		
		assertEquals(nbOfvehiculesInCarPark, 1);	
	}


	@Test
	public void testProcessQueue() throws SimulationException, VehicleException {
		
		carPark = new CarPark(1,0,0,2);
		Vehicle v1 = new Car(getNextId(), getNextVehicleArrivalTime(), false);
		Vehicle v2 = new Car(getNextId(), getNextVehicleArrivalTime(), false);
		
		carPark.enterQueue(v1);
		carPark.enterQueue(v2);
		assertTrue(v1.isQueued());
		assertTrue(v2.isQueued());
		assertFalse(carPark.queueEmpty());
		
		carPark.processQueue(v2.getArrivalTime() + 1, sim);		
		assertTrue(v1.isParked());
		assertTrue(v2.isQueued());
		assertFalse(carPark.queueEmpty());
	}


	@Test
	public void testQueueEmpty() throws SimulationException, VehicleException {
		
		final int queueSize = Constants.DEFAULT_MAX_QUEUE_SIZE;
		carPark = new CarPark(0, 0, 0, queueSize);
		generateNewVehicles(queueSize);
		
		assertTrue(carPark.queueEmpty());
		
		Vehicle v = null;
		for(int i = 0; i < queueSize; i++)
		{
			v = getNextVehicle();
			carPark.enterQueue(v);
			assertFalse(carPark.queueEmpty());
		}
		
		if(v != null)
			carPark.archiveQueueFailures(v.getArrivalTime() + Constants.MAXIMUM_QUEUE_TIME);
		
		assertTrue(carPark.queueEmpty());
	}


	@Test
	public void testQueueFull() throws SimulationException, VehicleException {
		
		final int queueSize = Constants.DEFAULT_MAX_QUEUE_SIZE;
		carPark = new CarPark(0, 0, 0, queueSize);
		
		for(int i = 0; i < queueSize; i++)
		{
			assertFalse(carPark.queueFull());
			carPark.enterQueue(getNextVehicle());
		}
		
		assertTrue(carPark.queueFull());
	}


	@Test
	public void testSpacesAvailable() throws VehicleException, SimulationException {
		
		final int motorCycleSpots = 2;
		final int smallCarSpotsSpots = 4;
		final int carSpotsSpots = 8;
		final int totalAllowedMotorCycles = motorCycleSpots + smallCarSpotsSpots;
		final int totalAllowedSmallCars = smallCarSpotsSpots + carSpotsSpots;
		final int totalAllowedCars= carSpotsSpots;
		
		carPark = new CarPark(carSpotsSpots, smallCarSpotsSpots, motorCycleSpots, 0);
		

		Car smallCar = new Car(getNextId(), getNextVehicleArrivalTime(), true);
		carPark.parkVehicle(smallCar, 0, Constants.MINIMUM_STAY);
		
		boolean spaceAvailable = true;
		do
		{
			MotorCycle mc = new MotorCycle(getNextId(), getNextVehicleArrivalTime());
			spaceAvailable = carPark.spacesAvailable(mc);
			assertEquals(spaceAvailable, carPark.getNumMotorCycles() !=  totalAllowedMotorCycles - carPark.getNumSmallCars() );
			if(spaceAvailable)
				carPark.parkVehicle(mc, 0, Constants.MINIMUM_STAY);
		} while(spaceAvailable);
		
		
		Car car = new Car(getNextId(), getNextVehicleArrivalTime(), false);
		carPark.parkVehicle(car, 0, Constants.MINIMUM_STAY);
		
		do
		{
			smallCar = new Car(getNextId(), getNextVehicleArrivalTime(), true);
			spaceAvailable = carPark.spacesAvailable(smallCar);
			assertEquals(spaceAvailable, carPark.getNumMotorCycles() + carPark.getNumSmallCars() - (motorCycleSpots + smallCarSpotsSpots) + carPark.getNumCars() != totalAllowedCars);
			if(spaceAvailable)
				carPark.parkVehicle(smallCar, 0, Constants.MINIMUM_STAY);
		} while(spaceAvailable);
	
	}


	@Test
	public void testTryProcessNewVehicles() throws SimulationException, VehicleException {
	
		// Modified simulator to guarantee car arrival at each cycle
		sim = new Simulator(Constants.DEFAULT_SEED, Constants.DEFAULT_INTENDED_STAY_MEAN, Constants.DEFAULT_INTENDED_STAY_SD, 1, 0, 0);
		carPark = new CarPark(1,0,0,1);

		assertTrue(carPark.carParkEmpty());
		assertTrue(carPark.queueEmpty());
		
		int time = 1;
		carPark.tryProcessNewVehicles(time++, sim);
		
		assertFalse(carPark.carParkEmpty());
		assertTrue(carPark.queueEmpty());
		
		carPark.tryProcessNewVehicles(time++, sim);
		
		assertFalse(carPark.carParkEmpty());
		assertFalse(carPark.queueEmpty());
		
		carPark.tryProcessNewVehicles(time++, sim);
		
		assertFalse(carPark.carParkEmpty());
		assertFalse(carPark.queueEmpty());
	}


	@Test
	public void testUnparkVehicle() throws SimulationException, VehicleException {
		Vehicle v = getNextVehicle();
		carPark.parkVehicle(v, 1, Constants.MINIMUM_STAY);
		assertTrue(v.isParked());
		assertFalse(carPark.carParkEmpty());
		
		carPark.unparkVehicle(v, v.getParkingTime() + Constants.MINIMUM_STAY);
		assertFalse(v.isParked());
		assertTrue(carPark.carParkEmpty());
	}
}
