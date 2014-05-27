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
	 * @method testMotorCycleIncorrectArrivalTime()
	 * @param vehID
	 * @param ArrivalTime
	 * @throws VehicleException 
	 * @throws Exception if the arrival time is equal to zero or less
	 * than zero
	 */
	
	@Test(expected = VehicleException.class)
	public  void testMotorCycleIncorrectArrivalTime() throws VehicleException {
		
		MotorCycle motor = new MotorCycle(vehID, -1);
	}
	
	
	/**
	 * @method testMotorCycleIncorrectIsInQueued_enterParedkState()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException 
	 * @throws Exception if the motor is already Parked.
	 */
	@Test(expected = VehicleException.class)
	public void testMotorCycleIncorrectIsInQueued_enterParedkState() throws VehicleException {
		motor.enterParkedState(getFirstMotorParkingTime(), intendedDuration);
		motor.enterParkedState(getNextMotorParkingTime(), intendedDuration);
		
	}
	
	/**
	 * @method testMotorCycleIncorrectParkingTimeLessThanZero()
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException 
	 * @throws Exception if parkingTime is Less Than Zero
	 */
	@Test(expected = VehicleException.class)
	public void testMotorCycleIncorrectParkingTimeLessThanZero() throws VehicleException {
		motor.enterParkedState(-1,intendedDuration);
	}
	
	
	/**
	 * @param testIncorrectMotorIntendedDurationLessThanMinimumStay
	 * @param intendedDuration
	 * @throws VehicleException 
	 * @throws Exception if intendedDuration is Less Than the Minimum
	 * Stay(20)
	 */
	@Test(expected = VehicleException.class)
	public void testIncorrectMotorIntendedDurationLessThanMinimumStay() throws VehicleException {
		
		final int invalidStayDuration = Constants.MINIMUM_STAY - 1;
		assertTrue(invalidStayDuration < Constants.MINIMUM_STAY);
		motor.enterParkedState(2,invalidStayDuration);
	}
	
	
	///////////////////////////////////////////////////////////
		
	/*
	* Throws Exception if the Motor is already in a 
	* queued or parked state when the motor Enters a Queued State
	*/
	////////////////////////////////////////////////////
	
	/**
	* @throws VehicleException 
	 * @method testEnterQueueWhenAlreadyInQueuedState();
	* @throws Exception if the motor is already in queued state
	*/
	@Test(expected = VehicleException.class)
	public void testEnterQueueWhenAlreadyInQueuedState() throws VehicleException {
	
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
	 * @method testMotorCycleExitParkedStateWhenNotParked()
	 * @param departureTime
	 * @throws VehicleException 
	 * @throws Exception if Motor is Not Park or Not Queued
	 */
	@Test(expected = VehicleException.class)
	public void testMotorCycleExitParkedStateWhenNotParked() throws VehicleException {
		motor.enterQueuedState();
		motor.exitParkedState(departureTime);
	}


	/**
	 * @param testMotorCycleIncorrectDepartureTimeLesserThanParkingTime()
	 * @throws VehicleException 
	 * @throws Exception if revised departure time 
	 * is lesser than parking time
	 */
	@Test(expected = VehicleException.class)
	public void testMotorCycleIncorrectDepartureTimeLesserThanParkingTime() throws VehicleException {
		
		motor.enterParkedState(0, Constants.MINIMUM_STAY);
		motor.exitParkedState(motor.getParkingTime()-1);		
	}

	
	@Test
	public void testMotorCycleConstructor() throws VehicleException {
		
		MotorCycle motor = new MotorCycle(vehID,arrivalTime);
		assertEquals(motor.getVehID(), vehID);
		assertEquals(motor.getArrivalTime(), arrivalTime );
	}
	
	
	/**
	 * @method testMotorCycleEnterParkedState
	 * @param parkingTime
	 * @param intendedDuration
	 * @throws VehicleException 
	 * 
	 */
	@Test
	public void testEnterParkedState() throws VehicleException {
		
		assertFalse(motor.isParked());
		assertFalse(motor.isSatisfied());
		assertFalse(motor.wasParked());
		
		motor.enterParkedState(parkingTime, intendedDuration);
		
		assertTrue(motor.isParked());
		assertTrue(motor.isSatisfied());
		assertTrue(motor.wasParked());
		assertEquals(motor.getParkingTime(),parkingTime);
		assertEquals(parkingTime + intendedDuration, motor.getDepartureTime());
	}
	
	@Test
	public void testExitParkedState() throws VehicleException {
		
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertTrue(motor.isParked());
		
		motor.exitParkedState(departureTime);
		assertFalse(motor.isParked());
		assertEquals(motor.getDepartureTime(),departureTime);
		assertTrue(motor.wasParked());
	}
	
	
	@Test
	public void testEnterQueuedState() throws VehicleException {
		
		assertFalse(motor.isQueued());
		assertFalse(motor.wasQueued());
		
		motor.enterQueuedState();
		
		assertTrue(motor.wasQueued());
		assertTrue(motor.isQueued());
	}
	
	@Test
	public void testExitQueuedState() throws VehicleException {
		
		motor.enterQueuedState();		
		
		motor.exitQueuedState(motor.getArrivalTime()+1);
		assertFalse(motor.isQueued());
		assertTrue(motor.wasQueued());
	}
	
	
	@Test
	public void testGetArrivalTime() throws VehicleException {
		
		final int time = arrivalTime;
		motor = new MotorCycle(vehID, time);
		assertEquals(motor.getArrivalTime(), time);
	}
	
	
	@Test
	public void testGetDepartureTime() throws VehicleException {
		
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertEquals(firstMotorParkingTime + intendedDuration, motor.getDepartureTime());
	}
	
	@Test
	public void testMotorCycleGetParkingTime() throws VehicleException {
		
		
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertEquals(firstMotorParkingTime, motor.getParkingTime());
	}
	
	
	@Test
	public void testGetVehicleID() throws VehicleException {
		
		motor = new MotorCycle(vehID, arrivalTime);
		assertEquals(vehID, motor.getVehID());
	}
	
	@Test
	public void testIsParked() throws VehicleException {
		
		assertFalse(motor.isParked());
		
		motor.enterParkedState(firstMotorParkingTime, intendedDuration);
		assertTrue(motor.isParked());
		
		motor.exitParkedState(departureTime);
		assertFalse(motor.isParked());
	}
	
	@Test
	public void testIsQueued() throws VehicleException {
		
		assertFalse(motor.isQueued());
		motor.enterQueuedState();
		assertTrue(motor.isQueued());
	}
	
	@Test
	public void testIsSatisfied() throws VehicleException {
		
		MotorCycle mc1 = new MotorCycle(vehID, arrivalTime);
		MotorCycle mc2 = new MotorCycle(vehID, arrivalTime);
		
		assertFalse(mc1.isSatisfied());
		assertFalse(mc2.isSatisfied());
		
		mc1.enterQueuedState();
		mc2.enterParkedState(firstMotorParkingTime, intendedDuration);
		
		assertFalse(mc1.isSatisfied());
		assertTrue(mc2.isSatisfied());
		
		mc2.exitParkedState(departureTime);
		mc1.exitQueuedState(exitTime);
		
		assertFalse(mc1.isSatisfied());
		assertTrue(mc2.isSatisfied());
	}
	
	
	@Test
	public void testWasParked() throws VehicleException {
		
		MotorCycle mc1 = new MotorCycle(vehID, arrivalTime);
		MotorCycle mc2 = new MotorCycle(vehID, arrivalTime);
		
		assertFalse(mc1.wasParked());
		assertFalse(mc2.wasParked());
		
		mc1.enterQueuedState();
		mc2.enterParkedState(firstMotorParkingTime, intendedDuration);
		
		assertFalse(mc1.wasParked());
		assertTrue(mc2.wasParked());
		
		mc2.exitParkedState(departureTime);
		mc1.exitQueuedState(exitTime);
		
		assertFalse(mc1.wasParked());
		assertTrue(mc2.wasParked());
	}
	
	
	
	@Test
	public void testWasQueued() throws VehicleException{
		
		MotorCycle mc1 = new MotorCycle(vehID, arrivalTime);
		MotorCycle mc2 = new MotorCycle(vehID, arrivalTime);
		
		assertFalse(mc1.wasQueued());
		assertFalse(mc2.wasQueued());
		
		mc1.enterQueuedState();
		mc2.enterParkedState(firstMotorParkingTime, intendedDuration);
		
		assertTrue(mc1.wasQueued());
		assertFalse(mc2.wasQueued());
		
		mc2.exitParkedState(departureTime);
		mc1.exitQueuedState(exitTime);
		
		assertTrue(mc1.wasQueued());
		assertFalse(mc2.wasQueued());
		
	}


}
