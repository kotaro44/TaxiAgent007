/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kotaro and Fabio :)
 */
public class SilentBidBehaviour extends Behaviour {

    Driver driver;
    public boolean silent_biding = false;
    public boolean stop = false;
    public Request possible_request;
    public double maxPayOff;
    public double myBid;
    
    public SilentBidBehaviour(Driver driver){
        this.driver = driver;
        this.myBid = this.driver.bidIncrease;
    }
    
    public void stop(){
        this.stop = true;
    }
    
    public void finish(){
        if( this.driver.requests.size() > 0 )
            this.driver.state = State.WON_BID_RESTING;
        else
            this.driver.state = State.WAITING_FOR_COMPANY;
        
        driver.actual_request = null;
        driver.taxi.passenger = null;
        this.driver.removeBehaviour(this);
    }
    
    
    
    @Override
    public void action() {
        if( !silent_biding ){
            ACLMessage msg = this.myAgent.receive();
            if( msg != null ){
                String passenger = msg.getContent();
                System.out.println("Driver " + driver.index +  ": I have a passenger but They asked me to go to: " + passenger);

                Pattern p = Pattern.compile("\\{\\d+\\,\\d+\\}");
                Matcher m = p.matcher(passenger);

                m.find();
                Intersection origin = driver.city.getNearestIntersection(m.group());

                m.find();
                Intersection destination = driver.city.getNearestIntersection(m.group());
                
                p = Pattern.compile("P\\d+");
                m = p.matcher(passenger);

                m.find();
                String pid = m.group();
                int passenger_id = Integer.parseInt( pid.substring(1, pid.length()) );
               
                double chargeable_dist = this.driver.city.getTotalDistance(origin, destination);
                double total_dist =  this.driver.city.getTotalDistance(this.driver.taxi.x , this.driver.taxi.y , this.driver.actual_request.destiny ) + chargeable_dist;
                double company_cut = 0.3*chargeable_dist*(this.driver.city.company.charge_rate_km - this.driver.city.company.gas_cost_km);
                this.maxPayOff = chargeable_dist*this.driver.city.company.charge_rate_km - total_dist*this.driver.city.company.gas_cost_km - company_cut;
                possible_request = new Request( origin , destination , this.maxPayOff , company_cut , 0 , passenger_id );
                
                this.driver.bid(msg, myBid, maxPayOff,this.possible_request);
          
                silent_biding = true;
            }else{
                if( this.stop )
                        this.finish();
            }
        }else{
            ACLMessage msg = this.myAgent.receive();
            if ( msg != null  ) {
                String company_decision = msg.getContent();
                if( company_decision.compareTo("GO") == 0 ){
                    //WE WON
                    this.driver.wonBid( possible_request );
                    silent_biding = false;
                    this.myBid = 1;
                    if( this.stop )
                        this.finish();
                } else if( company_decision.compareTo("Sorry") == 0 ){
                    //WE LOOSE
                    possible_request = null;
                    silent_biding = false;
                    this.myBid = this.driver.bidIncrease;
                    if( this.stop )
                        this.finish();
                } else{
                    //CAN CONTINUE BIDDING
                    do{
                        this.myBid += this.driver.bidIncrease;
                    } while( (maxPayOff - this.myBid) >= Integer.parseInt(company_decision));
                    this.possible_request.company_bid = this.myBid;
                    this.driver.bid(msg,myBid, maxPayOff,this.possible_request);
                }
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
