/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.behaviours.Behaviour;

/**
 *
 * @author kotaro and Fabio :)
 */
public class PickCostumerBehaviour extends Behaviour {

    public Intersection origin;
    public boolean picked = false;
    public Driver driver;
    
    public PickCostumerBehaviour( Driver driver ){
        this.driver = driver;
    }
    
    @Override
    public void action() {
        if( !picked ){
            System.out.println("Picked costumer!");
            picked = true;
            Intersection actual = driver.city.getNearestIntersection( driver.taxi.x , driver.taxi.y );
            driver.taxi.passenger = actual.pickPassenger();
        }
    }

    @Override
    public boolean done() {
        return picked;
    }
    
}
