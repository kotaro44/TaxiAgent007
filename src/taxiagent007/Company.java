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
    boolean ready = false;
    MainPanel mainpanel;
    ArrayList<Taxi> taxis = new ArrayList<>();
    ArrayList<Passenger> passengers;
    Object[][] taxi_props = {{1,1,Color.ORANGE,"TaxiDriver1"}};/*,
                             {15,50,Color.BLACK,"TaxiDriver2"},
                             {100,1,Color.WHITE,"TaxiDriver3"}};*/

  
    public void onPanelReady(MainPanel mainpanel){
        this.mainpanel = mainpanel;
        this.mainpanel.city.setCompany(this);
        this.passengers = new ArrayList<>();
        hireTaxiAgents();
        this.addBehaviour(new AssignPassengerBehaviour(this));
      
        
    }
    
    public void hireTaxiAgents(){
        ContainerController cc = getContainerController();
        try {
            for( Object[] props : this.taxi_props ){
                Object[] taxi = new Object[3];
                taxi[0] = new Taxi( (int) props[0] ,(int) props[1], (Color) props[2] );
                taxi[1] = this.mainpanel.city;
                taxi[2] = (int)Math.floor( this.mainpanel.city.intersections.length*Math.random() );
                AgentController new_agent = cc.createNewAgent((String)props[3], "taxiagent007.Driver", taxi );

                new_agent.start();
                
                //delay propositado para os agents nao acessarem this.taxis ao mesmo tempo
                try {
                    Thread.sleep(1000);                 //1000 milliseconds is one second.
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
               
                //os 3 taxis estavam a accessar esse lista ao mesmo tempo, causando erro.
                this.taxis.add((Taxi)taxi[0]); 
            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void callTaxi( Passenger passenger ){
        System.out.println("Received a call from: " + passenger );
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
