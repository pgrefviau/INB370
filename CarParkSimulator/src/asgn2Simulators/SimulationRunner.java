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
	
        private static GUISimulator guiSimulator;
        
	private static enum ArgTypes {INTEGER, DOUBLE};
	private static final ArgTypes[] validArgTypesMapping = 
        {
            ArgTypes.INTEGER,	
            ArgTypes.INTEGER,		
            ArgTypes.INTEGER,	
            ArgTypes.INTEGER,	
            ArgTypes.INTEGER,	// seed
            ArgTypes.DOUBLE,	// carProb
            ArgTypes.DOUBLE,	// smallCarProb
            ArgTypes.DOUBLE, 	// mcProb
            ArgTypes.DOUBLE, 	// meanStay
            ArgTypes.DOUBLE 	// sdStay
        };

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
