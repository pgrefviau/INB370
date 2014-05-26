package asgn2Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import asgn2Exceptions.VehicleException;
import asgn2Simulators.Constants;
import asgn2Vehicles.Car;
import asgn2Vehicles.MotorCycle;

public class MotorCycleTests {
	
	private MotorCycle motor ;
	
	private String vehID = "Sheng008";
	private int arrivalTime = 60;
	private int firstMotorParkingTime = 10;
	
	private int parkingTime = firstMotorParkingTime;
	private int intendedDuration = Constants.MINIMUM_STAY + (int)(Math.random());
	private int departureTime = parkingTime +intendedDuration ;
	private int exitTime = 60; 
	@Before
	public void creatAMotorObject() throws Exception {
		motor = new MotorCycle(vehID,arrivalTime);
	}
	////////////////////////////////////////////////////
	
	private int getFirstMotorParkingTime(){
		firstMotorParkingTime = 10;
		return firstMotorParkingTime;
	}
	
	private int getNextMotorParkingTime(){
		Random random = new Random();
		firstMotorParkingTime +=  random.nextInt(50);
		return firstMotorParkingTime;
	}
	/////////////////////////////////////////////////
	
	// Boundary condition testing for Motor
	
	
	/////////////////////////////////////////////////
	
	/**
	 * @method testMotorIncorrectArrivalTime()
	 * @param vehID
	 * @param ArrivalTime
	 * @throws Exception if the arrival time is equal to zero or less
	 * than zero
	 */
	
	@Test(expected = Exception.class)
	public  void testMotorIncorrectArrivalTime() throws Exception{
		
		MotorCycle motor = new MotorCycle(vehID, -1);
		MotorCycle motor2 = new MotorCycle(vehID,0);
	}
	
	
	/**
	 * @method testMotorIncorrectIsInQueued_enterParedkState()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws Exception if the motor is already Parked.
	 */
	@Test(expected = Exception.class)
	public void testMotorIncorrectIsInQueued_enterParedkState() throws Exception{
		motor.enterParkedState(getFirstMotorParkingTime(), intendedDuration);
		motor.enterParkedState(getNextMotorParkingTime(), intendedDuration);
		
	}
	
	/**
	 * @method testMotorIncorrectParkingTimeLessThanZero()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws Exception if parkingTime is Less Than Zero
	 */
	@Test(expected = Exception.class)
	public void testMotorIncorrectParkingTimeLessThanZero() throws Exception{
		motor.enterParkedState(-1,intendedDuration);
	}
	
	
	/**
	 * @param testIncorrectMotorIntendedDurationLessThanMinimumStay
	 * @param intendedDuration
	 * @throws Exception if intendedDuration is Less Than the Minimum
	 * Stay(20)
	 */
	@Test(expected = Exception.class)
	public void testIncorrectMotorIntendedDurationLessThanMinimumStay() throws Exception{
		motor.enterParkedState(2,intendedDuration/2-(int)(Math.random()));
	}
	
	
	///////////////////////////////////////////////////////////
		
	/*
	* Throws Exception if the Motor is already in a 
	* queued or parked state when the motor Enters a Queued State
	*/
	////////////////////////////////////////////////////
	
	/**
	* @method TestIncorrectMotorIsParked_enterQueuedState();
	* @param parkingTime
	* @param intendedDuration
	* @throws Exception if the motor is Parked
	*/
	@Test(expected = Exception.class)
	public void TestIncorrectMotorIsParked_enterQueuedState() throws Exception{
	
	motor.enterParkedState(getFirstMotorParkingTime(), intendedDuration);
	motor.enterQueuedState();
	}
	
	
	/**
	 * @method testIncorrectMotorIsAlreadyInQueued();
	 * @throws Exception if the car is Queued
	 */
	@Test(expected = Exception.class)
	public void testIncorrectMotorIsAlreadyInQueued() throws Exception{
		motor.enterQueuedState();
		motor.enterQueuedState();
	}
	
	
	//////////////////////////////////////////////////
	/*
	 * Exception - if the Motor is not in a parked state,
	 *  is in a queued state or if the revised 
	 *  departureTime < parkingTime
	 */
	
	////////////////////////////////////////////////
	/**
	 * @method testMotorIncorrectISNOTParked()
	 * @param departureTime
	 * @throws Exception if Motor is Not Park or Not Queued
	 */
	@Test(expected = VehicleException.class)
	public void testMotorIncorrectISNOTParked() throws Exception{
		motor.exitParkedState(departureTime);
	}
	
	

/**
 * @method testMotorIncorrectIsInAQueued() 
 * @param departureTime
 * @throws Exception if car is in a Queued
 */

@Test(expected = Exception.class)
public void testMotorIncorrectIsInAQueued() 
		throws Exception{
	motor.enterQueuedState();
	motor.exitParkedState(departureTime);

}

	/**
	 * @param testMotorIncorrectDepartureTimeLesserThanParkingTime()
	 * @throws Exception if revised departure time 
	 * is lesser than parking time
	 */
	@Test(expected = VehicleException.class)
	public void testMotorIncorrectDepartureTimeLesserThanParkingTime() 
			throws VehicleException{
		getFirstMotorParkingTime();
		getNextMotorParkingTime();
		motor.exitParkedState(motor.getParkingTime()-1);
		
	}
		
	//////////////////////////////////////////////////////////////////////

	
	////////////////////////////////////////////////////


	
	@Test
	public void testMotorVehicle() throws Exception{
		
		MotorCycle motor = new MotorCycle(vehID,arrivalTime);
		assertEquals(motor.getVehID(),(String)"Sheng008");
	}
	
	
	/**
	 * @method testMotorEnterParkedState
	 * @param parkingTime
	 * @param intendedDuration
	 * 
	 */
	@Test
	public void testMotorEnterParkedState() throws Exception{
		
		motor.enterParkedState(parkingTime, intendedDuration);
		assertEquals(motor.getParkingTime(),parkingTime,intendedDuration);
	}
	
	@Test
	public void testExitMotorParkedStateTest() throws Exception{
		
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		motor.exitParkedState(departureTime);
		assertEquals(motor.getDepartureTime(),departureTime);
	}
	
	
	@Test
	public void testMotorEnterQueuedState() throws Exception{
		
		assertFalse(motor.isQueued());
		motor.enterQueuedState();
		assertTrue(motor.isQueued());
	}
	
	@Test
	public void testMotorExitQueuedState() throws Exception{
		assertFalse(motor.isQueued());
		motor.enterQueuedState();
		assertTrue(motor.isQueued());
		motor.exitQueuedState(motor.getArrivalTime()+1);
		assertFalse(motor.isQueued());
	}
	
	
	@Test
	public void testMotorGetArrivalTime() throws Exception{
		assertEquals(motor.getArrivalTime(), 60);
	}
	
	
	@Test
	public void testMotorGetDepartureTime() throws Exception{
		assertEquals(0,motor.getDepartureTime());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertEquals(motor.getDepartureTime(),parkingTime +intendedDuration);
	}
	
	@Test
	public void testMotorGetParkingTime() throws Exception{
		assertEquals(0,motor.getParkingTime());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertEquals(motor.getParkingTime(),parkingTime);
	}
	
	
	@Test
	public void testMotorGetVehcalID()throws Exception{
		assertEquals("Sheng008",motor.getVehID());
	}
	
	@Test
	public void testMotorIsParked() throws Exception{
		
		assertFalse(motor.isParked());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertTrue(motor.isParked());
	}
	
	@Test
	public void testMotorIsNotParked() throws Exception{
		
		assertFalse(motor.isParked());
		motor.enterQueuedState();
		motor.exitQueuedState(60);
		assertFalse(motor.isParked());
	}
	
	@Test
	public void testMotorIsQueued() throws Exception{
		
		assertFalse(motor.isQueued());
		motor.enterQueuedState();
		assertTrue(motor.isQueued());
	}
	
	@Test
	public void testMotorIsNotQueued() throws Exception{
		
		assertFalse(motor.isQueued());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertFalse(motor.isQueued());
	}
	
	
	@Test
	public void testMotorIsSatisfied() throws Exception{
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertTrue(motor.isSatisfied());
	}
	
	
	
	@Test
	public void testMotorIsDissatisfied() throws Exception{
		

		
		motor.enterQueuedState();
		assertFalse(motor.isSatisfied());
		
		motor.exitQueuedState(60);
		assertFalse(motor.isSatisfied());
		
		Car car2 = new Car("345", 67, false);
		car2.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertFalse(motor.isSatisfied());
		
		car2.exitParkedState(departureTime);
		assertFalse(motor.isSatisfied());
	}
	
	@Test
	public void testCarWasParked() throws Exception{
		assertFalse(motor.wasParked());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertTrue(motor.wasParked());
		motor.exitParkedState(departureTime);
		assertTrue(motor.wasParked());
	}
	
	
	
	@Test
	public void testCarWasNotParked() throws Exception{
		
		assertFalse(motor.wasParked());
		motor.enterQueuedState();
		motor.exitQueuedState(exitTime);
		assertFalse(motor.wasParked());
	}
	
	
	
	
	@Test
	public void testMotorWasQueued() throws VehicleException{
		assertFalse(motor.wasQueued());
		
		
		motor.enterQueuedState();
		assertTrue(motor.wasQueued());
		motor.exitQueuedState(exitTime);
		assertTrue(motor.wasQueued());
		
	}
	
	@Test
	public void testMotorWasNotQueued() throws VehicleException{
		
		assertFalse(motor.wasQueued());
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		
		assertFalse(motor.wasQueued());
	}
	
	@Test
	public void testMotorToString() {
		String result = "Vehicle ID: Sheng008\nArrival time: 60\n";
		assertEquals(result, motor.toString());
	}
	
//	@After
//	public void tearDown() throws Exception {
//	}
//
//
//	@Test
//	public void testMotorCycle() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testVehicle() {
//		
//	}
//
//
//	@Test
//	public void testGetVehID() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testGetArrivalTime() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testEnterQueuedState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testExitQueuedState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testEnterParkedState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testExitParkedStateInt() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testExitParkedState() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testIsParked() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testIsQueued() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testGetParkingTime() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testGetDepartureTime() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testWasQueued() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testWasParked() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testIsSatisfied() {
//		fail("Not yet implemented"); // TODO
//	}
//
//
//	@Test
//	public void testToString() {
//		fail("Not yet implemented"); // TODO
//	}

}
