
package asgn2Simulators;

import asgn2CarParks.CarPark;
import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;

import java.awt.Dimension;
import java.awt.TextArea;


import java.io.IOException;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GUISimulator extends  javax.swing.JFrame implements ChangeListener  {


    private CarPark carPark;
    private Simulator sim;
    private Log log;

    
    private int simulationCounter = 1;
    
    private TextArea simulationResultsTextArea = new TextArea();
    private ChartPanel simulationResusltsGraphPanel;
    
    public GUISimulator(String maxCarSpacesFieldValue,
                        String maxSmallCarSpacesFieldValue,
                        String maxMotorCycleSpacesFieldValue,
                        String maxQueueLengthFieldValue,
                        String simulationSeedFieldValue,
                        int carArrivalProbSliderValue,
                        int smallCarArrivalProbSliderValue,
                        int motorCycleArrivalProbSliderValue,
                        String meanStayDurationFieldValue,
                        String standardStayDurationFieldValue)
    {
        
    	
    	
        initComponents();
        
        this.setMinimumSize(new Dimension(800, 400));
        
        simulationResusltsGraphPanel = new ChartPanel();
        
        resultsTabPane.addTab("Text", simulationResultsTextArea);
        resultsTabPane.addTab("Graph", simulationResusltsGraphPanel);
        
        this.motorCycleArrivalProbSlider.addChangeListener(this);
        this.carArrivalProbSlider.addChangeListener(this);
        this.smallCarArrivalProbSlider.addChangeListener(this);
        
        maxCarSpacesField.setText(maxCarSpacesFieldValue);
        maxSmallCarSpacesField.setText(maxSmallCarSpacesFieldValue);
        maxMotorCycleSpacesField.setText(maxMotorCycleSpacesFieldValue);
        maxQueueLengthField.setText(maxQueueLengthFieldValue);
       
        carArrivalProbSlider.setValue(carArrivalProbSliderValue);
        smallCarArrivalProbSlider.setValue(smallCarArrivalProbSliderValue);
        motorCycleArrivalProbSlider.setValue(motorCycleArrivalProbSliderValue);
        
        simulationSeedField.setText(simulationSeedFieldValue);
        meanStayDurationField.setText(meanStayDurationFieldValue);
        standardStayDurationField.setText(standardStayDurationFieldValue);
        
        outputToTextAreaWithNewLine("Press the button to launch the simulation");
        
    }
    
    public GUISimulator() 
    {
        this(String.valueOf(Constants.DEFAULT_MAX_CAR_SPACES),
             String.valueOf(Constants.DEFAULT_MAX_SMALL_CAR_SPACES),
             String.valueOf(Constants.DEFAULT_MAX_MOTORCYCLE_SPACES),
             String.valueOf(Constants.DEFAULT_MAX_QUEUE_SIZE),
             String.valueOf(Constants.DEFAULT_SEED),
             (int)(Constants.DEFAULT_CAR_PROB * 100),
             (int)(Constants.DEFAULT_SMALL_CAR_PROB * 100),
             (int)(Constants.DEFAULT_MOTORCYCLE_PROB * 100),
             String.valueOf(Constants.DEFAULT_INTENDED_STAY_MEAN),
             String.valueOf(Constants.DEFAULT_INTENDED_STAY_SD));
    }
   
    public GUISimulator(String[] args) 
    {
    	// ** Careful to order the args correctly
        this(args[0],									// maxCarSpaces
             args[1],									// maxSmallCarSpaces
             args[2],									// maxMotorCycleSpaces
             args[3],									// maxQueueSize
             args[4],									// seed
             (int)(Double.parseDouble(args[5]) * 100),	// carProb
             (int)(Double.parseDouble(args[6]) * 100),	// smallCarProb
             (int)(Double.parseDouble(args[7]) * 100),	// mcProb
             args[8],									// meanStay
             args[9]);									// sdStay
    }
    
    //Sets up a CarPark with the given arguments
    private CarPark setUpCarParkFromUiFields()
    {
            final int maxCarSpaces = Integer.parseInt(this.maxCarSpacesField.getText());
            final int maxSmallCarSpaces = Integer.parseInt(this.maxSmallCarSpacesField.getText());
            final int maxMotorCycleSpaces = Integer.parseInt(this.maxMotorCycleSpacesField.getText());
            final int maxQueueSize = Integer.parseInt(this.maxQueueLengthField.getText());
        
            return new CarPark(maxCarSpaces, maxSmallCarSpaces, maxMotorCycleSpaces, maxQueueSize);
    }

    // Sets up a CarPark with the given arguments
    private Simulator setUpSimulatorFromUFields() throws SimulationException
    {
            final int seed = Integer.parseInt(this.simulationSeedField.getText());
            final double meanStay = Double.parseDouble(this.meanStayDurationField.getText());
            final double sdStay = Double.parseDouble(this.standardStayDurationField.getText());
            
            final double carProb = this.carArrivalProbSlider.getValue() / 100.0f;
            final double smallCarProb = this.smallCarArrivalProbSlider.getValue() / 100.0f;
            final double motorCycleProb = this.motorCycleArrivalProbSlider.getValue() / 100.0f;

            return new Simulator(seed, meanStay, sdStay, carProb, smallCarProb, motorCycleProb);
    }
    
    // Returns an integer value from the provided JTextField, or throws an exception if no conversion exists
    private int getIntValueFromField(JTextField field) throws NumberFormatException
    {
        try {
            return Integer.parseInt(field.getText());
        } catch(NumberFormatException e){
            throw new NumberFormatException("Value for the " + field.getToolTipText() + " field must be a valid integer");
        }
    }
    // Check if the integer value extracted from the provided JTextField is higher or equal to another integer, throws an exception otherwise
    private void checkIfFieldIntegerValueIsHigherOrEqualTo(JTextField field, int value) throws Exception
    {  
        if(getIntValueFromField(field) < value)
            throw new Exception("Value for the " + field.getToolTipText() + " field must be higher than " + value);
    }
    
    // Check if the double value extracted from the provided JTextField is higher or equal to another double, throws an exception otherwise
    private void checkIfFieldDoubleValueIsHigherOrEqualTo(JTextField field, double value) throws Exception
    {  
        double fieldValue;
        
        try {
            fieldValue = Double.parseDouble(field.getText());
        } catch(NumberFormatException e){
            throw new Exception("Value for the " + field.getToolTipText() + " field must be a valid double");
        }
        
        if(fieldValue < value)
            throw new Exception("Value for the " + field.getToolTipText() + " field must be higher than " + value);
    }
    
    //Checks if every single UI field contribution to the simulation parameters have acceptable values
    private boolean checkUiFieldsValidity()
    {
        try {
            checkIfFieldIntegerValueIsHigherOrEqualTo(maxCarSpacesField, 0);
            checkIfFieldIntegerValueIsHigherOrEqualTo(maxSmallCarSpacesField, 0);
            checkIfFieldIntegerValueIsHigherOrEqualTo(maxMotorCycleSpacesField, 0);
            checkIfFieldIntegerValueIsHigherOrEqualTo(maxQueueLengthField, 0);
            
            checkIfFieldIntegerValueIsHigherOrEqualTo(maxCarSpacesField, getIntValueFromField(maxSmallCarSpacesField));
            
            checkIfFieldDoubleValueIsHigherOrEqualTo(simulationSeedField, 0.0f);
            checkIfFieldDoubleValueIsHigherOrEqualTo(meanStayDurationField, 0.0f);
            checkIfFieldDoubleValueIsHigherOrEqualTo(standardStayDurationField, 0.0f);

        } catch(Exception e) {
            outputToTextArea(e.getMessage());
            return false;
        }
        
        return true;
    }
   
    // Event handler for the 3 sliders' state change
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider)e.getSource();
        if(slider != null)
        {
            if(slider.equals(carArrivalProbSlider))
                carProbDisplay.setText(String.valueOf(slider.getValue()) + "%");
            
            if(slider.equals(smallCarArrivalProbSlider))
                smallCarProbDisplay.setText(String.valueOf(slider.getValue())+ "%");
            
            if(slider.equals(motorCycleArrivalProbSlider))
                motorCycleProbDisplay.setText(String.valueOf(slider.getValue())+ "%");
        }
    }
    
    // Tries to launch a simulation instance according to the current parameters
    private void launchCarParkSimulation()
    {
        clearTextArea();
        
        if(!checkUiFieldsValidity())
            return;
        
        outputToTextAreaWithNewLine("Launching simulation #" + (simulationCounter++) +"...");
        deactivateAllInputs();
        
        try {
            carPark =  setUpCarParkFromUiFields();
            sim = setUpSimulatorFromUFields();
            log = new Log();
        } catch (IOException | SimulationException e1) {
            e1.printStackTrace();
            outputToTextAreaWithNewLine(e1.getMessage());
            activateAllInputs();
        }
        
		
        //Run the simulation 
        try {
            runSimulation();
        } catch (Exception e) {
            e.printStackTrace();
            outputToTextAreaWithNewLine(e.getMessage());
        }
        finally{
            activateAllInputs();
        }

        
        outputToTextAreaWithNewLine("Simulation completed !");
    }
    
    // Outputs the string to the text area
    private void outputToTextArea(String str)
    {
        simulationResultsTextArea.append(str);
    }
    
    private void outputToTextAreaWithNewLine(String str)
    {
    	outputToTextArea(str + "\n");
    }
    
    // Clears the text area
    private void clearTextArea()
    {
        simulationResultsTextArea.setText("");
    }
    
    // Deactivate all the UI inputs 
    private void deactivateAllInputs()
    {
        setAllInputsActivationState(false);
    }
    
    // Reactivate all the UI inputs
    private void activateAllInputs()
    {
        setAllInputsActivationState(true);
    }
    
    // Set all the input fields' enabled setting to a certain value
    private void setAllInputsActivationState(boolean isEnabled)
    {
        carArrivalProbSlider.setEnabled(isEnabled);
        launchSimBtn.setEnabled(isEnabled);
        maxCarSpacesField.setEnabled(isEnabled);
        maxMotorCycleSpacesField.setEnabled(isEnabled);
        maxSmallCarSpacesField.setEnabled(isEnabled);
        maxQueueLengthField.setEnabled(isEnabled);
        meanStayDurationField.setEnabled(isEnabled);
        motorCycleArrivalProbSlider.setEnabled(isEnabled);
        simulationSeedField.setEnabled(isEnabled);
        smallCarArrivalProbSlider.setEnabled(isEnabled);
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
		this.simulationResusltsGraphPanel.resetSimulationData();
                
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
			this.outputToTextArea(this.carPark.getStatus(time));
            this.simulationResusltsGraphPanel.addDataForGivenTimePoint
            (
                time, 
                carPark.getNumCars(), 
                carPark.getNumSmallCars(), 
                carPark.getNumMotorCycles(), 
                carPark.getNumCars() + carPark.getNumSmallCars() + carPark.getNumMotorCycles()
            );
		}
		this.log.finalise(this.carPark);
                this.simulationResusltsGraphPanel.generateFinalChartFromData();
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        maxCarSpacesLbl = new javax.swing.JLabel();
        maxSmallCarSpacesLbl = new javax.swing.JLabel();
        maxMotorCycleSpacesLbl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        maxQueueLengthLbl = new javax.swing.JLabel();
        maxCarSpacesField = new javax.swing.JTextField();
        maxSmallCarSpacesField = new javax.swing.JTextField();
        maxMotorCycleSpacesField = new javax.swing.JTextField();
        maxQueueLengthField = new javax.swing.JTextField();
        simulationSeedLbl = new javax.swing.JLabel();
        carArrivalProbLbl = new javax.swing.JLabel();
        smallCarArrivalProbLbl = new javax.swing.JLabel();
        motorCyleArrivalProbLbl = new javax.swing.JLabel();
        meanStayLbl = new javax.swing.JLabel();
        carArrivalProbSlider = new javax.swing.JSlider();
        smallCarArrivalProbSlider = new javax.swing.JSlider();
        motorCycleArrivalProbSlider = new javax.swing.JSlider();
        simulationSeedField = new javax.swing.JTextField();
        meanStayDurationField = new javax.swing.JTextField();
        launchSimBtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        standardStayLbl = new javax.swing.JLabel();
        standardStayDurationField = new javax.swing.JTextField();
        motorCycleProbDisplay = new javax.swing.JTextField();
        smallCarProbDisplay = new javax.swing.JTextField();
        resultsTabPane = new javax.swing.JTabbedPane();
        carProbDisplay = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 750));
        setMinimumSize(new java.awt.Dimension(800, 0));
        setPreferredSize(new java.awt.Dimension(800, 750));
        setResizable(false);

        maxCarSpacesLbl.setText("Maximum car spaces:");

        maxSmallCarSpacesLbl.setText("Maximum small car spaces:");

        maxMotorCycleSpacesLbl.setText("Maximum motorcycle spaces:");

        maxQueueLengthLbl.setText("Maximum queue length:");

        maxCarSpacesField.setToolTipText("Max Car Spaces");

        maxSmallCarSpacesField.setToolTipText("Max Small Car Spaces");

        maxMotorCycleSpacesField.setToolTipText("Max Motorcyle Spaces");

        maxQueueLengthField.setToolTipText("Max Queue Length");

        simulationSeedLbl.setText("Simulation seed:");

        carArrivalProbLbl.setText("Car arrival probability:");

        smallCarArrivalProbLbl.setText("Small car arrival probability:");

        motorCyleArrivalProbLbl.setText("Motorcycle arrival probability:");

        meanStayLbl.setText("Mean stay duration:");

        carArrivalProbSlider.setPaintLabels(true);
        carArrivalProbSlider.setPaintTicks(true);

        smallCarArrivalProbSlider.setPaintLabels(true);
        smallCarArrivalProbSlider.setPaintTicks(true);
        smallCarArrivalProbSlider.setToolTipText("");

        motorCycleArrivalProbSlider.setPaintTicks(true);

        simulationSeedField.setToolTipText("Simulation Seed");

        meanStayDurationField.setToolTipText("Mean Stay Duration");

        launchSimBtn.setText("Launch Simulation");
        launchSimBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchSimBtnActionPerformed(evt);
            }
        });

        standardStayLbl.setText("Standard stay duration:");

        standardStayDurationField.setToolTipText("Standard Stay Duration");

        motorCycleProbDisplay.setEditable(false);

        smallCarProbDisplay.setEditable(false);

        carProbDisplay.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(carArrivalProbLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(carProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(smallCarArrivalProbLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(smallCarProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(standardStayLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(meanStayDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(standardStayDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(resultsTabPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(motorCycleArrivalProbSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                            .addComponent(smallCarArrivalProbSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(carArrivalProbSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(launchSimBtn))
                            .addComponent(jSeparator2)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(maxMotorCycleSpacesLbl)
                                            .addComponent(maxCarSpacesLbl)
                                            .addComponent(maxSmallCarSpacesLbl)
                                            .addComponent(maxQueueLengthLbl))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(maxCarSpacesField, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                            .addComponent(maxQueueLengthField)
                                            .addComponent(maxMotorCycleSpacesField)
                                            .addComponent(maxSmallCarSpacesField)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(motorCyleArrivalProbLbl)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(motorCycleProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(meanStayLbl)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(simulationSeedLbl)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(simulationSeedField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxCarSpacesLbl)
                    .addComponent(maxCarSpacesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxSmallCarSpacesLbl)
                    .addComponent(maxSmallCarSpacesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxMotorCycleSpacesLbl)
                    .addComponent(maxMotorCycleSpacesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxQueueLengthLbl)
                    .addComponent(maxQueueLengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simulationSeedLbl)
                    .addComponent(simulationSeedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(carArrivalProbLbl)
                    .addComponent(carProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(carArrivalProbSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(smallCarArrivalProbLbl)
                    .addComponent(smallCarProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(smallCarArrivalProbSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(motorCyleArrivalProbLbl))
                    .addComponent(motorCycleProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(motorCycleArrivalProbSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(meanStayLbl)
                    .addComponent(meanStayDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(standardStayLbl)
                    .addComponent(standardStayDurationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(resultsTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(launchSimBtn)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 711, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void launchSimBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchSimBtnActionPerformed
        launchCarParkSimulation();
    }//GEN-LAST:event_launchSimBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel carArrivalProbLbl;
    private javax.swing.JSlider carArrivalProbSlider;
    private javax.swing.JTextField carProbDisplay;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton launchSimBtn;
    private javax.swing.JTextField maxCarSpacesField;
    private javax.swing.JLabel maxCarSpacesLbl;
    private javax.swing.JTextField maxMotorCycleSpacesField;
    private javax.swing.JLabel maxMotorCycleSpacesLbl;
    private javax.swing.JTextField maxQueueLengthField;
    private javax.swing.JLabel maxQueueLengthLbl;
    private javax.swing.JTextField maxSmallCarSpacesField;
    private javax.swing.JLabel maxSmallCarSpacesLbl;
    private javax.swing.JTextField meanStayDurationField;
    private javax.swing.JLabel meanStayLbl;
    private javax.swing.JSlider motorCycleArrivalProbSlider;
    private javax.swing.JTextField motorCycleProbDisplay;
    private javax.swing.JLabel motorCyleArrivalProbLbl;
    private javax.swing.JTabbedPane resultsTabPane;
    private javax.swing.JTextField simulationSeedField;
    private javax.swing.JLabel simulationSeedLbl;
    private javax.swing.JLabel smallCarArrivalProbLbl;
    private javax.swing.JSlider smallCarArrivalProbSlider;
    private javax.swing.JTextField smallCarProbDisplay;
    private javax.swing.JTextField standardStayDurationField;
    private javax.swing.JLabel standardStayLbl;
    // End of variables declaration//GEN-END:variables

    
}
