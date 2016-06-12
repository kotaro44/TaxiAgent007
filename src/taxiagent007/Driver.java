/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public Shift shift;
    public ArrayList<Request> requests;
    public Request actual_request;
    public double last_max_bid = 0;
    public double last_profit = 0;
    public boolean working = false;
    public int cantBidIn = 0;
    public int bidIncrease = 2;
    public boolean silent_biding = false;
    
    Behaviour main_behaviour;
    
    public Taxi getTaxi(){
        return this.taxi;
    }
    
    public City getCity(){
        return this.city;
    }
    
    
    public double distance_to_complete_request(Intersection location , Request request ){
        double requests_dist = 0;
        requests_dist += city.getTotalDistance( location , this.actual_request.origin );
        requests_dist += city.getTotalDistance( this.actual_request.origin , this.actual_request.destiny );
        return requests_dist;
    }
    
    public void bid( ACLMessage msg , double bidValue , double maxPayOff , Intersection origin , Intersection destiny ){
        Request tmp_request = new Request(origin,destiny,0,0,0,-1);
        this.bid(msg, bidValue, maxPayOff, tmp_request);
    }
    
    public int totalRequestTime( Request new_request ){
        //calculate total time of completing my job
        double total_requests_time = 0;
        Request last_request = null;
        Intersection last_intersection = null;
        if( actual_request != null ){
            last_request = actual_request;
        }else if( !this.requests.isEmpty() ){
                last_request = this.requests.get(0);
        }
            
        if( last_request != null ){ 
            //First Request time calculation
            Intersection nearest_intersection = city.getNearestIntersection(this.taxi.x, this.taxi.y);
            if( this.taxi.passenger != null ){
                total_requests_time += city.getTotalDistance( nearest_intersection , last_request.destiny );
                total_requests_time += nearest_intersection.distance(this.taxi.x, this.taxi.y);
            }else{
                total_requests_time += distance_to_complete_request(nearest_intersection,last_request);
            }
            
            //Rest request time calculation
            for( int i = 1 ; i < this.requests.size() ; i++ ){
                Request req = this.requests.get(i);
                total_requests_time += city.getTotalDistance( last_request.destiny , req.origin );
                total_requests_time += city.getTotalDistance( req.origin , req.destiny );
            }
            
            if( this.requests.isEmpty() )
                last_intersection = actual_request.destiny;
            else
                last_intersection = this.requests.get( this.requests.size() - 1).destiny;
           
        }else {
            last_intersection = city.getNearestIntersection(this.taxi.x, this.taxi.y);
            total_requests_time += last_intersection.distance(this.taxi.x, this.taxi.y);
        }
        
        //NEW request time calculation
        total_requests_time += city.getTotalDistance( last_intersection , new_request.origin );
        total_requests_time += city.getTotalDistance( new_request.origin , new_request.destiny );
        
        
        return (int)(total_requests_time/this.taxi.speed);
    }
    
    
    public void bid( ACLMessage msg , double bidValue , double maxPayOff , Request new_request ) {
        /*try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        this.last_profit = maxPayOff;
        ACLMessage reply = msg.createReply();
 
        //Can't Bid IF:  
        if( !this.timeOfDuty( MainPanel.seconds + this.totalRequestTime(new_request) ) ){
            // - I'll be out of duty taking that passanger
            System.out.println("Driver " + this.index + ": I don't have time for that");
            reply.setContent(this.index + ":0");
        } else if ( this.cantBidIn != 0 ){
            // - I won a bid recently 
            System.out.println("Driver " + this.index + ": I won a bid recently");
            reply.setContent(this.index + ":0");
        } else {
            
            if( bidValue >= maxPayOff-1 ){
                // - My Bid is bigger than my max Profit
                System.out.println("Driver " + this.index + ": the bid is too low for me");
                reply.setContent(this.index + ":0");
            } else if ( maxPayOff <= 0 ){
                // - I can't get profit from the task
                System.out.println("Driver " + this.index + ": I can't get any profit from that request");
                reply.setContent(this.index + ":0");
            } else{
                if( Company.Vickrey ){
                    reply.setContent(this.index + ":" + bidValue );
                    this.last_max_bid = bidValue;
                }else{
                    reply.setContent(this.index + ":" + (maxPayOff - bidValue) );
                    this.last_max_bid = (maxPayOff - bidValue);
                }
            }
        }
        
        this.send(reply);
    }
    
    private void _wonbid( Request new_req  ){
        this.cantBidIn = (int)(this.city.getTotalDistance(new_req.origin, new_req.destiny)/this.taxi.speed);
        this.requests.add( new_req );
    }
    
    public void wonBid( Request new_req ){
        this._wonbid(new_req);
    }
    
    public void wonBid( Intersection origin , Intersection destiny , double profit , double company_cut, double myBid , int passenger_id ){
        Request new_req = new Request( origin , destiny , profit , company_cut , myBid , passenger_id );
        this._wonbid(new_req);
    }
    
    public boolean timeOfDuty( int seconds ){
        seconds = (seconds%(60*60*24));
        boolean should_working = false;
        
        switch( this.shift ){
            case FROM_3AM_TO_1PM:
                    should_working = ( seconds >= 3*3600 && seconds <= 13*3600 );
                break;
            case FROM_6PM_TO_4AM:
                    should_working = !( seconds >= 4*3600 && seconds <= 18*3600 );
                break;
            case FROM_9AM_TO_7PM:
                    should_working = ( seconds >= 9*3600 && seconds <= 19*3600 );
                break;
        }
        
        return should_working;
    }
    
    public void update( int elapsed_seconds ){
        
        this.cantBidIn -= elapsed_seconds;
        if( this.cantBidIn - elapsed_seconds <= 0 )
            this.cantBidIn = 0;
        
        
        
        if( timeOfDuty( MainPanel.seconds ) ){
            //should be working
            if( !working ){
                working = true;
                this.main_behaviour = new WaitForCallBehaviour(this){
                    @Override
                    public int onEnd(){
                        return 0;
                    }
                };
                this.addBehaviour(this.main_behaviour);  
            }
        }else{
            //should be off
            if( working ){
                Driver self = this;
                if( this.state == State.WAITING_FOR_COMPANY ){
                    working = false;
                    this.state = State.GOING_HOME;
                    this.removeBehaviour(main_behaviour);
                    this.addBehaviour(new GoToLocationBehaviour( this.city.intersections[21] , this ){
                        @Override
                        public int onEnd(){
                            self.state = State.OUT_OF_SERVICE;
                            return 0;
                        }
                    });
                }
            }
        }
        
    }
    
    @Override
     protected void setup() {
        Object[] args = getArguments();
        this.taxi = (Taxi)args[0];
        this.city = (City)args[1];
        this.index = (Integer)args[2];
        this.shift = (Shift)args[3];
        this.taxi.driver = this;
        this.requests = new ArrayList<>();
        this.state = State.OUT_OF_SERVICE;
        bidIncrease = (int)Math.floor(  Math.random()*5 )+1;
        System.out.println("Driver" + index + " is ready!");
    }
        
    @Override
      protected void takeDown() {
        System.out.println( getAID().getName() + " Quits!");
    }
}
