
package asgn2Simulators;

import asgn2CarParks.CarPark;
import asgn2Exceptions.SimulationException;
import asgn2Exceptions.VehicleException;

import java.awt.TextField;
import java.io.IOException;
import java.util.EventListener;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUISimulator extends  javax.swing.JFrame implements ChangeListener  {


    private CarPark carPark;
    private Simulator sim;
    private Log log;
    private SimulationRunner sr;
    
    
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
        
        this.carArrivalProbSlider.addChangeListener(this);
        this.smallCarArrivalProbSlider.addChangeListener(this);
        this.motorCycleArrivalProbSlider.addChangeListener(this);
        
        
        maxCarSpacesField.setText(String.valueOf(Constants.DEFAULT_MAX_CAR_SPACES));
        maxSmallCarSpacesField.setText(String.valueOf(Constants.DEFAULT_MAX_SMALL_CAR_SPACES));
        maxMotorCycleSpacesField.setText(String.valueOf(Constants.DEFAULT_MAX_MOTORCYCLE_SPACES));
        maxQueueLengthField.setText(String.valueOf(Constants.DEFAULT_MAX_QUEUE_SIZE));
       
        carArrivalProbSlider.setValue((int)(Constants.DEFAULT_CAR_PROB * 100));
        smallCarArrivalProbSlider.setValue((int)(Constants.DEFAULT_SMALL_CAR_PROB * 100));
        motorCycleArrivalProbSlider.setValue((int)(Constants.DEFAULT_MOTORCYCLE_PROB * 100));
        
        simulationSeedField.setText(String.valueOf(Constants.DEFAULT_SEED));
        meanStayDurationField.setText(String.valueOf(Constants.DEFAULT_INTENDED_STAY_MEAN));
        standardStayDurationField.setText(String.valueOf(Constants.DEFAULT_INTENDED_STAY_SD));
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
    
    private int getIntValueFromField(JTextField field) throws NumberFormatException
    {
        try {
            return Integer.parseInt(field.getText());
        } catch(NumberFormatException e){
            throw new NumberFormatException("Value for the " + field.getToolTipText() + " field must be a valid integer");
        }
    }
    
    private void checkIfFieldIntegerValueIsHigherOrEqualTo(JTextField field, int value) throws Exception
    {  
        if(getIntValueFromField(field) < value)
            throw new Exception("Value for the " + field.getToolTipText() + " field must be higher than " + value);
    }
    
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
    
    private void launchCarParkSimulation()
    {
        if(!checkUiFieldsValidity())
            return;
        
        outputToTextArea("Launching simulation...");

        try {
            carPark =  setUpCarParkFromUiFields();
            sim = setUpSimulatorFromUFields();
            log = new Log();
        } catch (IOException | SimulationException e1) {
            e1.printStackTrace();
            outputToTextArea(e1.getMessage());    
        }
		
        //Run the simulation 
        try {
            runSimulation();
        } catch (Exception e) {
            e.printStackTrace();
            outputToTextArea(e.getMessage());
        }

        
        outputToTextArea("Simulation completed !");
    }
    
    private void outputToTextArea(String str)
    {
        simulationResultsTextArea.append(str + "\n");
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
        jScrollPane1 = new javax.swing.JScrollPane();
        simulationResultsTextArea = new javax.swing.JTextArea();
        standardStayLbl = new javax.swing.JLabel();
        standardStayDurationField = new javax.swing.JTextField();
        carProbDisplay = new javax.swing.JTextField();
        motorCycleProbDisplay = new javax.swing.JTextField();
        smallCarProbDisplay = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        simulationResultsTextArea.setColumns(20);
        simulationResultsTextArea.setRows(5);
        jScrollPane1.setViewportView(simulationResultsTextArea);

        standardStayLbl.setText("Standard stay duration:");

        standardStayDurationField.setToolTipText("Standard Stay Duration");

        carProbDisplay.setEditable(false);

        motorCycleProbDisplay.setEditable(false);

        smallCarProbDisplay.setEditable(false);

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
                        .addGap(0, 264, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(motorCycleArrivalProbSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                            .addComponent(smallCarArrivalProbSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(carArrivalProbSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(launchSimBtn))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
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
                    .addComponent(carProbDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(carArrivalProbLbl))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(launchSimBtn)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 682, Short.MAX_VALUE)
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
    private javax.swing.JScrollPane jScrollPane1;
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
    private javax.swing.JTextArea simulationResultsTextArea;
    private javax.swing.JTextField simulationSeedField;
    private javax.swing.JLabel simulationSeedLbl;
    private javax.swing.JLabel smallCarArrivalProbLbl;
    private javax.swing.JSlider smallCarArrivalProbSlider;
    private javax.swing.JTextField smallCarProbDisplay;
    private javax.swing.JTextField standardStayDurationField;
    private javax.swing.JLabel standardStayLbl;
    // End of variables declaration//GEN-END:variables

    
}
