/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asgn2Simulators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Phil
 */
public class ChartPanel extends org.jfree.chart.ChartPanel {
    private final String TITLE = "Car Park Simulation Results";
    
    private TimeSeries vehTotal;
    private TimeSeries carTotal; 
    private TimeSeries mcTotal;
    private TimeSeries scTotal;
    private Calendar cal = GregorianCalendar.getInstance();
    
    public ChartPanel() {
        super(new JFreeChart(new XYPlot()));    
    }

    public final void resetSimulationData()
    {
        setUpSeries();
    }
    
    private void setUpSeries()
    {
        vehTotal = new TimeSeries("Total Vehicles");
        carTotal = new TimeSeries("Total Cars");
        scTotal = new TimeSeries("Total Small Cars");
        mcTotal = new TimeSeries("MotorCycles");
    }
    
    public void addDataForGivenTimePoint( int time, int nbCars, int nbSmallCars, int nbMotorCycles, int total)
    {
        cal.set(2014,0,1,6, time);
        Date timePoint = cal.getTime();
        
        scTotal.add(new Minute(timePoint), nbSmallCars);
        mcTotal.add(new Minute(timePoint), nbMotorCycles);
	carTotal.add(new Minute(timePoint),nbCars);
	vehTotal.add(new Minute(timePoint), total);        
    }
    
    public void generateFinalChartFromData()
    {
        TimeSeriesCollection finalCollection = getFinalCollection();
        JFreeChart chart = createChart(finalCollection);
        setChart(chart);
    }
    
    private TimeSeriesCollection getFinalCollection()
    {   
        TimeSeriesCollection tsc = new TimeSeriesCollection(); 
        tsc.addSeries(vehTotal);
        tsc.addSeries(carTotal);
        tsc.addSeries(scTotal);
        tsc.addSeries(mcTotal);
       
        return tsc; 
    }
    
        /**
     * Helper method to deliver the Chart - currently uses default colours and auto range 
     * @param dataset TimeSeriesCollection for plotting 
     * @returns chart to be added to panel 
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            TITLE, "hh:mm:ss", "Vehicles", dataset, true, true, false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        range.setAutoRange(true);
        return result;
    }
}
