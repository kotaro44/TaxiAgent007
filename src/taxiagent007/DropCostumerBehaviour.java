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
            
            
            System.out.println( "cut: " + this.driver.actual_request.company_cut );
            System.out.println( "bid: " + this.driver.actual_request.company_bid );
            System.out.println( "price: " + this.driver.actual_request.price );
            
            driver.city.company.profit += Math.ceil( this.driver.actual_request.company_cut + this.driver.actual_request.company_bid );
            driver.profit += Math.floor( this.driver.actual_request.price-this.driver.actual_request.company_bid );
            
            actual.dropPassenger(driver.taxi.passenger);
        }
    }

    @Override
    public boolean done() {
        return droped;
    }
    
}
