package asgn2Tests;


import java.util.Random;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Vehicles.Car;
import static org.junit.Assert.*;

public class CarTests {
	
	private Car car ;
	
	
	private String vehID = "Sheng007";
	private int arrivalTime = 60;
	private int firstCarParkingTime;
	
	private int parkingTime = firstCarParkingTime;
	private int intendedDuration = 70;
	private int departureTime = parkingTime + intendedDuration;
	
	@Before
	public void creatACarObject() throws VehicleException{
		car = new Car(vehID,arrivalTime, true);
		
	}
	////////////////////////////////////////////////////
	
	/*
	 * Boundary Conditions Testings
	 */
	
	
	/*
	 * Throw exception if arrivalTime is < 0;
	 */
	
	@Test(expected = VehicleException.class)
	public  void arrivalTimeLesserZero() throws VehicleException{
		
		Car carTest1 = new Car(vehID, -1, false);
	}
	
	
	/*
	 * Throw exception if arrivalTime = 0;
	 */
	
	@Test(expected = VehicleException.class)
	public  void arrivalTimeEqualsZero() throws VehicleException{
	
		Car carTest2 = new Car(vehID, 0, false);
	}
	
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
	 * Throws VehicleException if the Car is already in a 
	 * queued or parked state when the car Enters a Parked
	 *  State or Parking Time is lesser than zero or intendedDuration
	 *  is lesser than the minimum stay required
	 */
	
	/**
	 * @method enterParkedState()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is already Queued.
	 */
	@Test(expected = VehicleException.class)
	public void carIsInParked_enterParedkState() throws VehicleException{
		car.enterQueuedState();
		car.enterParkedState(getFirstCarParkingTime(), intendedDuration);
		
	}
	
	
	/**
	 * @method enterParkedState()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is already Parked.
	 */
	@Test(expected = VehicleException.class)
	public void carIsInQueued_enterParedkState() throws VehicleException{
		car.enterParkedState(getFirstCarParkingTime(), intendedDuration);
		car.enterParkedState(getNextCarParkingTime(), intendedDuration);
		
	}
	
	/**
	 * @method 
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if parkingTime is Less Than Zero
	 */
	@Test(expected = VehicleException.class)
	public void parkingTimeLessThanZero() throws VehicleException{
		car.enterParkedState(-1,Constants.MINIMUM_STAY);
	}
	/**
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if intendedDuration is Less Than the Minimum
	 * Stay(20)
	 */
	@Test(expected = VehicleException.class)
	public void intendedDurationLessThanMinimumStay() throws VehicleException{
		car.enterParkedState(2,Constants.MINIMUM_STAY/2);
	}
///////////////////////////////////////////////////////////
	
	/*
	 * Throws VehicleException if the Car is already in a 
	 * queued or parked state when the car Enters a Queued State
	 */
	////////////////////////////////////////////////////
	
	/**
	 * @method enterQueuedState();
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is Queued
	 */
	@Test(expected = VehicleException.class)
	public void carIsParked_enterQueuedState() throws VehicleException{
		
		car.enterParkedState(getFirstCarParkingTime(), intendedDuration);
		car.enterQueuedState();
	}
	
	
	/**
	 * @method enterQueuedState();
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException if the car is Queued
	 */
	@Test(expected = VehicleException.class)
	public void carIsInQueued_enterQueuedState() throws VehicleException{
		car.enterQueuedState();
		car.enterQueuedState();
	}
	
	/////////////////////////////////////////////////////////////
	
	
	

	

	

	
	
	
	
	/**
	 * @param departureTime
	 * @throws VehicleException if Car is Not Park or Not Queued
	 */
	@Test(expected = VehicleException.class)
	public void carISNOTParked() throws VehicleException{
		car.exitParkedState(departureTime);
	
	}
	/**
	 * @param departureTime
	 * @throws VehicleException if car is in a Queued
	 */
	
	@Test(expected = VehicleException.class)
	public void carIsInAQueued() 
			throws VehicleException{
		car.enterQueuedState();
		car.exitParkedState(departureTime);
	
	}
	
	/**
	 * @param departureTime
	 * @throws VehicleException if revised departure time 
	 * is lesser than parking time
	 */
	@Test(expected = VehicleException.class)
	public void departureTimeLesserThanParkingTime() 
			throws VehicleException{
		getFirstCarParkingTime();
		getNextCarParkingTime();
		if(parkingTime > departureTime)
		car.exitParkedState(departureTime);
		
	}
	
	
	//////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////

	/**
	 * @param vehID
	 * @param arrivalTime
	 * @Boolean 
	 * 
	 * To test correct Car ID with a valve arrival time
	 */
	
	@Test
	public void getCorrectCarID()throws VehicleException{
	
		Car car = new Car(vehID,arrivalTime,true);
		assertEquals(car.getVehID(),(String)"Sheng007");
	}
	
	
	
	

	
	
	
	////////////////////////////////////////////////////
	

	

	


	
	
	
	@Test
	public void getMoreCarIDifferentArrivalTime()throws VehicleException{
		
		Car carTest3 = new Car(vehID,arrivalTime,true);
		Car carTest4 = new Car("Diablo001",12,true);
		Car carTest5 = new Car("Diablo002",22,true);
		Car carTest6 = new Car("Diablo003",10,true);
		Car carTest7 = new Car("Diablo004",21,true);
		assertEquals(carTest3.getArrivalTime(),(int) arrivalTime);
		assertEquals(carTest4.getArrivalTime(),(int) 700);
		assertEquals(carTest5.getArrivalTime(),(int) 22);
		assertEquals(carTest6.getArrivalTime(),(int) 700);
		assertEquals(carTest7.getArrivalTime(),(int) 21);
	}
	
	

	
	
	
	
//	
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	/**
//	 * Test method for {@link asgn2Vehicles.Car#toString()}.
//	 */
//	@Test
//	public void testToString() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link asgn2Vehicles.Car#Car(java.lang.String, int, boolean)}.
//	 */
//	@Test
//	public void testCar() {
//		fail("Not yet implemented"); // TODO
//	}
//
//	/**
//	 * Test method for {@link asgn2Vehicles.Car#isSmall()}.
//	 */
//	@Test
//	public void testIsSmall() {
//		fail("Not yet implemented"); // TODO
//	}

	
	
	
}
