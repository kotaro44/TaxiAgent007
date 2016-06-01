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
    
    public boolean drive(Direction dir,City city){
        switch(dir){
            case RIGHT:
                    this.taxi.x += this.taxi.speed;
                break;
            case LEFT:
                    this.taxi.x -= this.taxi.speed;
                break;
            case DOWN:
                    this.taxi.y += this.taxi.speed;
                break;
            case UP:
                    this.taxi.y -= this.taxi.speed;
                break;
        }
        return true;
    }
    
    @Override
     protected void setup() {
        Object[] args = getArguments();
        this.taxi = (Taxi)args[0];
        this.city = (City)args[1];
        System.out.println("Driver is ready!");
        
        Intersection dest = city.intersections[(int)args[2]];
        
        Behaviour b = new DriveBehaviour( taxi , city , dest );
        System.out.println(b);
        this.addBehaviour( b );
    }
        
    @Override
      protected void takeDown() {
        System.out.println( getAID().getName() + " Quits!");
    }
}
