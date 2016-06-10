/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;


/**
 *
 * @author kotaro and Fabio :)
 */
public class BidBehaviour extends Behaviour {
    
    public Intersection origin;
    public Intersection destiny;
    public Driver driver;
    public ACLMessage msg;
    
    public double myBid = 1;
    public double maxPayOff;
    
    public int passenger_id;
    
    public BidBehaviour( Intersection origin, Intersection destiny , Driver driver , ACLMessage msg , int passenger_id ){
        this.origin = origin;
        this.destiny = destiny;
        this.driver = driver;
        this.msg = msg;
        this.driver.state = State.BIDDING_FOR_PASSENGER;
        this.passenger_id = passenger_id;
    }

    @Override
    public void action() {
        switch(this.driver.state){
            case BIDDING_FOR_PASSENGER:
                    double chargeable_dist = this.origin.distance(this.destiny);
                    double total_dist =  this.origin.distance( this.driver.taxi.x , this.driver.taxi.y ) + chargeable_dist;
                    
                    this.maxPayOff = chargeable_dist*this.driver.city.company.charge_rate_km - total_dist*this.driver.city.company.gas_cost_km;
                    this.driver.last_profit = this.maxPayOff;
                    
                    this.driver.bid( msg , this.myBid , this.maxPayOff , this.origin , this.destiny );
                    this.driver.state = State.WAITING_FOR_COMPANY_DECISION;
                break;
            case WAITING_FOR_COMPANY_DECISION:
                ACLMessage new_msg = this.myAgent.receive();
                if ( new_msg != null  ) {
                    this.msg = new_msg;
                    String company_decision = this.msg.getContent();
        
                    if( company_decision.compareTo("GO") == 0 ){
                        this.driver.state = State.WON_BID_RESTING;
                        this.driver.wonBid( origin , destiny , maxPayOff , myBid , passenger_id);
                        this.driver.removeBehaviour(this);
                    } else if( company_decision.compareTo("Sorry") == 0 ){
                        this.driver.state = State.WAITING_FOR_COMPANY;
                        this.driver.removeBehaviour(this);
                    } else{
                        this.myBid = Integer.parseInt(company_decision) + 1;
                        this.driver.state = State.BIDDING_FOR_PASSENGER;
                    }
                }
                    
                break;
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
