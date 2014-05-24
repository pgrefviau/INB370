/**
 * 
 * This file is part of the CarParkSimulator Project, written as 
 * part of the assessment for INB370, semester 1, 2014. 
 *
 * CarParkSimulator
 * asgn2Simulators 
 * 23/04/2014
 * 
 */
package asgn2Simulators;

import java.io.IOException;

import asgn2CarParks.CarPark;
import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;

/**
 * Class to operate the simulation, taking parameters and utility methods from the Simulator
 * to control the CarPark, and using Log to provide a record of operation. 
 * @author hogan
 *
 */
public class SimulationRunner {
	
    
        private CarPark carPark;
        private Simulator sim;
        private Log log;
        private SimulationRunner sr;
	
        private static GUISimulator guiSimulator;
        
	private static enum ArgTypes {INTEGER, DOUBLE};
	private static final ArgTypes[] validArgTypesMapping = 
        {
            ArgTypes.INTEGER,
            ArgTypes.INTEGER,
            ArgTypes.INTEGER,
            ArgTypes.INTEGER,
            ArgTypes.INTEGER,
            ArgTypes.DOUBLE,
            ArgTypes.DOUBLE,
            ArgTypes.DOUBLE,
            ArgTypes.DOUBLE,
            ArgTypes.DOUBLE
        };

	
	/**
	 * Constructor just does initialisation 
	 * @param carPark CarPark currently used 
	 * @param sim Simulator containing simulation parameters
	 * @param log Log to provide logging services 
	 */
	public SimulationRunner(CarPark carPark, Simulator sim,Log log) {
		this.carPark = carPark;
		this.sim = sim;
		this.log = log;
	}
	
	/**
	 * Method to run the simulation from start to finish. Exceptions are propagated upwards from Vehicle,
	 * Simulation and Log objects as necessary 
	 * @throws VehicleException if Vehicle creation or operation constraints violated 
	 * @throws SimulationException if Simulation constraints are violated 
	 * @throws IOException on logging failures
	 */
	public void runSimulation() throws VehicleException, SimulationException, IOException {
		this.log.initialEntry(this.carPark,this.sim);
		for (int time=0; time<=Constants.CLOSING_TIME; time++) {
			//queue elements exceed max waiting time
			if (!this.carPark.queueEmpty()) {
				this.carPark.archiveQueueFailures(time);
			}
			//vehicles whose time has expired
			if (!this.carPark.carParkEmpty()) {
				//force exit at closing time, otherwise normal
				boolean force = (time == Constants.CLOSING_TIME);
				this.carPark.archiveDepartingVehicles(time, force);
			}
			//attempt to clear the queue 
			if (!this.carPark.carParkFull()) {
				this.carPark.processQueue(time,this.sim);
			}
			// new vehicles from minute 1 until the last hour
			if (newVehiclesAllowed(time)) { 
				this.carPark.tryProcessNewVehicles(time,this.sim);
			}
			//Log progress 
			this.log.logEntry(time,this.carPark);
		}
		this.log.finalise(this.carPark);
	}

	/**
	 * Helper method to determine if new vehicles are permitted
	 * @param time int holding current simulation time
	 * @return true if new vehicles permitted, false if not allowed due to simulation constraints. 
	 */
	private boolean newVehiclesAllowed(int time) {
		boolean allowed = (time >=1);
		return allowed && (time <= (Constants.CLOSING_TIME - 60));
	}
	
	private static boolean areArgumentsValid(String[] args)
	{
            if(args.length != validArgTypesMapping.length)
                    return false;

            try{
                for(int i = 0 ; i < validArgTypesMapping.length; i++)
                {
                    switch(validArgTypesMapping[i])
                    {
                        case INTEGER: Integer.parseInt(args[i]);
                                break;
                        case DOUBLE: Double.parseDouble(args[i]);
                                break;
                    }
                }
            }
            catch(NumberFormatException e)
            {
                return false;
            }

            return true;
	}
        
	/**
	 * Main program for the simulation 
	 * @param args Arguments to the simulation 
	 */
	public static void main(String[] args) {
			
            if(args.length == 0)
                guiSimulator = new GUISimulator();
            else if(areArgumentsValid(args))
                guiSimulator = new GUISimulator(args);
            else
                System.exit(-1);
            
            guiSimulator.setVisible(true);
	} 



}
