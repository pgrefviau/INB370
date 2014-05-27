package asgn2Tests;


import java.util.Random;


import org.junit.Test;
import org.junit.Before;


import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Vehicles.Car;
import static org.junit.Assert.*;

public class CarTests {
	
	private Car car ;
	
	private String vehID = "Sheng007";
	private int arrivalTime = 60;
	private int firstCarParkingTime = 10;
	
	private int parkingTime = firstCarParkingTime;
	private int intendedDuration = Constants.MINIMUM_STAY + (int)(Math.random());
	private int departureTime = parkingTime +intendedDuration ;
	private int exitTime = 60; 
	
	@Before
	public void creatACarObject() throws VehicleException{
		car = new Car(vehID,arrivalTime, false);
	}
	////////////////////////////////////////////////////
	

	private int getFirstCarParkingTime(){
		firstCarParkingTime = 10;
		return firstCarParkingTime;
	}
	
	private int getNextCarParkingTime(){
		Random random = new Random();
		firstCarParkingTime +=  random.nextInt(50);
		return firstCarParkingTime;
	}
	

//////////////////////////////////////////////////
	/*
	 * Boundary Conditions Testings
	 */
	

	
	///////////////////////////////////////////////
	
	/*
	 * Throw exception if arrivalTime is < 0;
	 */
	
	@Test(expected = VehicleException.class)
	public  void testAsrrivalTimeLesserZero() throws VehicleException{
		
		Car carTest1 = new Car(vehID, -1, false);
		Car carTest2 = new Car(vehID, 0, false);
	}
	
	
	/*
	 * Throws VehicleException if the Car is already in a 
	 * queued or parked state when the car Enters a Parked
	 *  State or Parking Time is lesser than zero or intendedDuration
	 *  is lesser than the minimum stay required
	 */
	
	
	
	/**
	 * @method testCarIncorrectIsInQueued_enterParedkState()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is already Parked.
	 */
	@Test(expected = VehicleException.class)
	public void testCarIncorrectIsInQueued_enterParedkState() throws VehicleException{
		car.enterParkedState(getFirstCarParkingTime(), intendedDuration);
		car.enterParkedState(getNextCarParkingTime(), intendedDuration);
		
	}
	
	/**
	 * @method testCarIncorrectParkingTimeLessThanZero()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if parkingTime is Less Than Zero
	 */
	@Test(expected = VehicleException.class)
	public void testCarIncorrectParkingTimeLessThanZero() throws VehicleException{
		car.enterParkedState(-1,intendedDuration);
	}
	/**
	 * @param testIncorrectCarIntendedDurationLessThanMinimumStay
	 * @param intendedDuration
	 * @throws VehicleException if intendedDuration is Less Than the Minimum
	 * Stay(20)
	 */
	@Test(expected = VehicleException.class)
	public void testIncorrectCarIntendedDurationLessThanMinimumStay() throws VehicleException{
		car.enterParkedState(2,intendedDuration/2-(int)(Math.random()));
	}
///////////////////////////////////////////////////////////
	
	/*
	 * Throws VehicleException if the Car is already in a 
	 * queued or parked state when the car Enters a Queued State
	 */
	////////////////////////////////////////////////////
	
	/**
	 * @method TestIncorrectCarIsParked_enterQueuedState();
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is Parked
	 */
	@Test(expected = VehicleException.class)
	public void testIncorrectCarIsParked_enterQueuedState() throws VehicleException{
		
		car.enterParkedState(getFirstCarParkingTime(), intendedDuration);
		car.enterQueuedState();
	}
	
	
	/**
	 * @method testIncorrectCarIsAlreadyInQueued();
	 * @throws VehicleException if the car is Queued
	 */
	@Test(expected = VehicleException.class)
	public void testIncorrectCarIsAlreadyInQueue() throws VehicleException{
		car.enterQueuedState();
		car.enterQueuedState();
	}
	
	//////////////////////////////////////////////////
	/*
	 * VehicleException - if the vehicle is not in a parked state,
	 *  is in a queued state or if the revised 
	 *  departureTime < parkingTime
	 */
	
	////////////////////////////////////////////////
	/**
	 * @method testCarIncorrectISNOTParked()
	 * @param departureTime
	 * @throws VehicleException if Car is Not Park or Not Queued
	 */
	@Test(expected = VehicleException.class)
	public void testCarIncorrectISNOTParked() throws VehicleException{
		car.exitParkedState(departureTime);
	
	}
	/**
	 * @method testCarIncorrectIsInAQueued() 
	 * @param departureTime
	 * @throws VehicleException if car is in a Queued
	 */
	
	@Test(expected = VehicleException.class)
	public void testCarIncorrectIsInAQueued() 
			throws VehicleException{
		car.enterQueuedState();
		car.exitParkedState(departureTime);
	
	}
	
	/**
	 * @param testCarIncorrectDepartureTimeLesserThanParkingTime()
	 * @throws VehicleException if revised departure time 
	 * is lesser than parking time
	 */
	@Test(expected = VehicleException.class)
	public void testCarIncorrectDepartureTimeLesserThanParkingTime() 
			throws VehicleException{
		getFirstCarParkingTime();
		getNextCarParkingTime();
		car.exitParkedState(car.getParkingTime()-1);
		
	}
	
	
	//////////////////////////////////////////////////////////////////////

	
	////////////////////////////////////////////////////


	
	@Test
	public void testCarVehicle() throws VehicleException{
		
		Car car = new Car(vehID,arrivalTime,true);
		assertEquals(car.getVehID(),(String)"Sheng007");
	}
	
	
	
	
	
	/**
	 * @method testCarEnterParkedState
	 * @param parkingTime
	 * @param intendedDuration
	 * 
	 */
	
	@Test
	public void testCarEnterParkedState() throws VehicleException{
		
		car.enterParkedState(parkingTime, intendedDuration);
		assertEquals(car.getParkingTime(),parkingTime,intendedDuration);
	}



	@Test
	public void testExitCarParkedStateTest() throws VehicleException{
		
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		car.exitParkedState(departureTime);
		assertEquals(car.getDepartureTime(),departureTime);
	}

	
	@Test
	public void testCarEnterQueuedState() throws VehicleException{
		
		assertFalse(car.isQueued());
		car.enterQueuedState();
		assertTrue(car.isQueued());
	}
	

	
	@Test
	public void testCarExitQueuedState() throws VehicleException{
		assertFalse(car.isQueued());
		car.enterQueuedState();
		assertTrue(car.isQueued());
		car.exitQueuedState(car.getArrivalTime()+1);
		assertFalse(car.isQueued());
	}
	

	
	@Test
	public void testCarGetArrivalTime() throws VehicleException{
		assertEquals(car.getArrivalTime(), 60);
	}
	

	@Test
	public void testCarGetDepartureTime() throws VehicleException{
		assertEquals(0,car.getDepartureTime());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertEquals(car.getDepartureTime(),parkingTime +intendedDuration);
	}
	
	@Test
	public void testCarGetParkingTime() throws VehicleException{
		assertEquals(0,car.getParkingTime());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertEquals(car.getParkingTime(),parkingTime);
	}
	
	
	@Test
	public void testCarGetVehcleID()throws VehicleException{
		assertEquals("Sheng007",car.getVehID());
	}
	
	@Test
	public void testCarIsParked() throws VehicleException{
		
		assertFalse(car.isParked());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertTrue(car.isParked());
	}

	@Test
	public void testCarIsNotParked() throws VehicleException{
		
		assertFalse(car.isParked());
		car.enterQueuedState();
		car.exitQueuedState(60);
		assertFalse(car.isParked());
	}
	
	@Test
	public void testCarIsQueued() throws VehicleException{
		
		assertFalse(car.isQueued());
		car.enterQueuedState();
		assertTrue(car.isQueued());
	}
	
	@Test
	public void testCarIsNotQueued() throws VehicleException{
		
		assertFalse(car.isQueued());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertFalse(car.isQueued());
	}
	
	
	@Test
	public void testCarIsSatisfied() throws VehicleException{
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertTrue(car.isSatisfied());
	}
	
	
	@Test
	public void testCarIsDissatisfied() throws VehicleException{
		

		
		car.enterQueuedState();
		assertFalse(car.isSatisfied());
		
		car.exitQueuedState(60);
		assertFalse(car.isSatisfied());
		
		Car car2 = new Car("345", 67, false);
		car2.enterParkedState(firstCarParkingTime, intendedDuration);
		assertFalse(car.isSatisfied());
		
		car2.exitParkedState(departureTime);
		assertFalse(car.isSatisfied());
	}
	
	
	
	@Test
	public void testCarWasParked() throws VehicleException{
		assertFalse(car.wasParked());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		assertTrue(car.wasParked());
		car.exitParkedState(departureTime);
		assertTrue(car.wasParked());
	}
	
	
	
	@Test
	public void testCarWasNotParked() throws VehicleException{
		
		assertFalse(car.wasParked());
		car.enterQueuedState();
		car.exitQueuedState(exitTime);
		assertFalse(car.wasParked());
	}
	
	
	@Test
	public void testCarWasQueued() throws VehicleException{
		assertFalse(car.wasQueued());
		
		
		car.enterQueuedState();
		assertTrue(car.wasQueued());
		car.exitQueuedState(exitTime);
		assertTrue(car.wasQueued());
		
	}
	
	@Test
	public void testCarWasNotQueued() throws VehicleException{
		
		assertFalse(car.wasQueued());
		car.enterParkedState(firstCarParkingTime, intendedDuration);
		
		assertFalse(car.wasQueued());
	}


	
}
