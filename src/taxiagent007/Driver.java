/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.awt.Color;

/**
 *
 * @author kotaro
 */
public class Driver extends Agent {
 
    public Taxi taxi;
    public City city;
    
    @Override
     protected void setup() {
        Object[] args = getArguments();
        this.taxi = (Taxi)args[0];
        this.city = (City)args[1];
        System.out.println("Driver is ready!");
        this.AddBehaviour();
    }
     
    public void AddBehaviour(){
        Driver self = this;
        Behaviour b;
        b = new DriveBehaviour( taxi , city  ){
            @Override
            public int onEnd(){
                self.AddBehaviour();
                return 0;
            }
        };
        this.addBehaviour( b );
    }
        
    @Override
      protected void takeDown() {
        System.out.println( getAID().getName() + " Quits!");
    }
}
