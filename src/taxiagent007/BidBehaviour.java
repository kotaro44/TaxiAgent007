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
    
    public double myBid;
    public double maxPayOff;
    
    public int passenger_id;
    
    public Request possible_request;
    
    public BidBehaviour( Intersection origin, Intersection destiny , Driver driver , ACLMessage msg , int passenger_id ){
        this.origin = origin;
        this.destiny = destiny;
        this.driver = driver;
        this.msg = msg;
        this.driver.state = State.BIDDING_FOR_PASSENGER;
        this.passenger_id = passenger_id;
        this.myBid = this.driver.bidIncrease;
    }

    @Override
    public void action() {
        switch(this.driver.state){
            case BIDDING_FOR_PASSENGER:
                    double chargeable_dist = this.driver.city.getTotalDistance(origin, destiny);
                    double total_dist =  this.driver.city.getTotalDistance(this.driver.taxi.x , this.driver.taxi.y , origin ) + chargeable_dist;
                    double company_cut = 0.3*chargeable_dist*(this.driver.city.company.charge_rate_km - this.driver.city.company.gas_cost_km);
                    this.maxPayOff = chargeable_dist*this.driver.city.company.charge_rate_km - total_dist*this.driver.city.company.gas_cost_km - company_cut;
                    possible_request = new Request( origin , destiny , this.maxPayOff , company_cut , this.myBid , passenger_id );
                
                    this.driver.bid(msg, myBid, maxPayOff,this.possible_request);
                    
                    this.driver.state = State.WAITING_FOR_COMPANY_DECISION;
                break;
            case WAITING_FOR_COMPANY_DECISION:
                ACLMessage new_msg = this.myAgent.receive();
                if ( new_msg != null  ) {
                    this.msg = new_msg;
                    String company_decision = this.msg.getContent();
        
                    if( company_decision.compareTo("GO") == 0 ){
                        this.driver.state = State.WON_BID_RESTING;
                        this.driver.wonBid( this.possible_request );
                        this.driver.removeBehaviour(this);
                    } else if( company_decision.compareTo("Sorry") == 0 ){
                        possible_request = null;
                        this.driver.state = State.WAITING_FOR_COMPANY;
                        this.driver.removeBehaviour(this);
                    } else{
                        do{
                            this.myBid += this.driver.bidIncrease;
                        } while( (maxPayOff - this.myBid) >= Integer.parseInt(company_decision));
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
