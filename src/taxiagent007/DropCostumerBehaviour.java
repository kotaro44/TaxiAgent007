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
public class DropCostumerBehaviour extends Behaviour {

    public Intersection dest;
    public boolean droped = false;
    public Driver driver;
    
    public DropCostumerBehaviour( Driver driver){
        this.driver = driver;
    }
    
    @Override
    public void action() {
        if( !droped ){
            System.out.println("Driver " + driver.index + ": Droped costumer!");
            droped = true;
            Intersection actual = driver.city.getNearestIntersection( driver.taxi.x , driver.taxi.y );
            driver.city.company.profit += this.driver.actual_request.company_cut;
            driver.profit += (this.driver.actual_request.price-this.driver.actual_request.company_cut);
            actual.dropPassenger(driver.taxi.passenger);
        }
    }

    @Override
    public boolean done() {
        return droped;
    }
    
}
