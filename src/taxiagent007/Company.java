/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 *
 * @author kotaro and fabio :)
 */
public class Company extends Agent {
    public State state = State.PREPARING;
    
    boolean ready = false;
    MainPanel mainpanel;
    ArrayList<Taxi> taxis = new ArrayList<>();
    ArrayList<Passenger> passengers;
    public int askingTo = 1;
    public String driverName = "TaxiDriver";
    public int profit = 0;
    public Object[][] taxi_props = 
                             {{57,29,Color.ORANGE,Shift.FROM_3AM_TO_1PM},
                             {57,29,Color.BLACK,Shift.FROM_3AM_TO_1PM},
                             {57,29,Color.BLUE,Shift.FROM_3AM_TO_1PM},
                             {57,29,Color.GREEN,Shift.FROM_3AM_TO_1PM},
                             
                             {57,29,Color.MAGENTA,Shift.FROM_6PM_TO_4AM},
                             {57,29,Color.DARK_GRAY,Shift.FROM_6PM_TO_4AM},
                             {57,29,Color.YELLOW,Shift.FROM_6PM_TO_4AM},
                             {57,29,Color.RED,Shift.FROM_6PM_TO_4AM},
                             
                             {57,29,Color.orange,Shift.FROM_9AM_TO_7PM},
                             {57,29,Color.PINK,Shift.FROM_9AM_TO_7PM},
                             {57,29,Color.LIGHT_GRAY,Shift.FROM_9AM_TO_7PM},
                             {57,29,Color.cyan,Shift.FROM_9AM_TO_7PM}};
    
    public double charge_rate_km = 40.0/7.0;
    public double gas_cost_km = 6.0/7.0;

    public void onPanelReady(MainPanel mainpanel){
        this.mainpanel = mainpanel;
        this.mainpanel.city.setCompany(this);
        this.passengers = new ArrayList<>();
        hireTaxiAgents();
        this.mainpanel.setTaxiLabels();
        
        this.addBehaviour(new ProcessCallsBehaviour(this));
    }
    
    public void hireTaxiAgents(){
        ContainerController cc = getContainerController();
        try {
            int i = 1;
            for( Object[] props : this.taxi_props ){
                Object[] taxi_params = { 
                    new Taxi( (int) props[0] ,(int) props[1], (Color) props[2] ),
                    this.mainpanel.city,
                    i,
                    (Shift) props[3]
                };
                
                AgentController new_agent = cc.createNewAgent(driverName + i++, "taxiagent007.Driver", taxi_params );

                new_agent.start();
                
                //delay propositado para os agents nao acessarem this.taxis ao mesmo tempo
                try {
                    Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
               
                //os 3 taxis estavam a accessar esse lista ao mesmo tempo, causando erro.
                this.taxis.add((Taxi)taxi_params[0]); 
            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void callTaxi( Passenger passenger ){
        System.out.println("Company: Received a call from P" + passenger.id + ": " + passenger );
        passengers.add(passenger);
    }
    
    @Override
     protected void setup() {
        System.out.println("Company Agent started!");
        MainPanel.jade_main(this);
    }
     
    @Override
     protected void takeDown() {
        System.out.println( getAID().getName() + " Close Business!");
    }
}
