/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 *
 * @author kotaro and Fabio :)
 */
public class Driver extends Agent {
 
    public Taxi taxi;
    public City city;
    public int index;
    public int profit;
    public State state;
    
    public Taxi getTaxi(){
        return this.taxi;
    }
    
    public City getCity(){
        return this.city;
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
     protected void setup() {
        Object[] args = getArguments();
        this.taxi = (Taxi)args[0];
        this.city = (City)args[1];
        this.index = (Integer)args[2];
        this.taxi.driver = this;
        System.out.println("Driver" + index + " is ready!");
        //this.AddBehaviour();
 
        this.addBehaviour(new WaitForCallBehaviour(this){
            @Override
            public int onEnd(){
                return 0;
            }
        });   
    }
        
    @Override
      protected void takeDown() {
        System.out.println( getAID().getName() + " Quits!");
    }
}
