/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;

/**
 *
 * @author kotaro and fabio :)
 */
public class Company extends Agent {
    boolean ready = false;
    MainPanel mainpanel;
    ArrayList<Taxi> taxis = new ArrayList<>();
    Object[][] taxi_props = {{1,1,Color.ORANGE,"TaxiDriver1"}};//,
                             //{15,50,Color.BLACK,"TaxiDriver2"},
                             //{100,1,Color.WHITE,"TaxiDriver3"}};
    
    public void onPanelReady(MainPanel mainpanel){
        this.mainpanel = mainpanel;
        this.mainpanel.city.setCompany(this);
        hireTaxiAgents();
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
                this.taxis.add((Taxi)taxi[0]);
            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
        }
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
