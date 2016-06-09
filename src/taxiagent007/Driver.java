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
    
    Behaviour main_behaviour;
    
    public Taxi getTaxi(){
        return this.taxi;
    }
    
    public City getCity(){
        return this.city;
    }
    
    public void bid( ACLMessage msg , double bidValue , double maxPayOff ){
        ACLMessage reply = msg.createReply();
        //Can't Bid IF:
        // - My Bid is bigger than my max Profit
        // - I can't get profit from the task
        // - I won a bid recently 
        // - going home
        if( bidValue >= maxPayOff-1 || maxPayOff < 0 || this.cantBidIn != 0 ){
            reply.setContent(this.index + ":0");
        }else{
            reply.setContent(this.index + ":" + bidValue);
            this.last_max_bid = bidValue;
        }
        this.send(reply);
    }
    
    private void _wonbid( Request new_req  ){
        this.cantBidIn = (int)(new_req.origin.distance(new_req.destiny)/this.taxi.speed);
        this.requests.add( new_req );
    }
    
    public void wonBid( Request new_req ){
        this._wonbid(new_req);
    }
    
    public void wonBid( Intersection origin , Intersection destiny , double maxPayOff , double myBid , int passenger_id ){
        Request new_req = new Request( origin , destiny , maxPayOff , myBid , passenger_id );
        this._wonbid(new_req);
    }
    
    public void update( int elapsed_seconds ){
        
        this.cantBidIn -= elapsed_seconds;
        if( this.cantBidIn - elapsed_seconds <= 0 )
            this.cantBidIn = 0;
        
        boolean should_working = false;
        switch( this.shift ){
            case FROM_3AM_TO_1PM:
                    should_working = ( MainPanel.seconds >= 3*3600 && MainPanel.seconds <= 13*3600 );
                break;
            case FROM_6PM_TO_4AM:
                    should_working = !( MainPanel.seconds >= 4*3600 && MainPanel.seconds <= 18*3600 );
                break;
            case FROM_9AM_TO_7PM:
                    should_working = ( MainPanel.seconds >= 9*3600 && MainPanel.seconds <= 19*3600 );
                break;
        }
        
        if( should_working ){
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
        System.out.println("Driver" + index + " is ready!");
    }
        
    @Override
      protected void takeDown() {
        System.out.println( getAID().getName() + " Quits!");
    }
}
