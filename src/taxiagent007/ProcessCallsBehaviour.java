/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kotaro and Fabio :)
 */
public class ProcessCallsBehaviour extends Behaviour {
    public Company company;
    public boolean waiting_response = false;
    public Passenger attendee;
    public int maxBidder = -1;
    
    public String[] answers;
    public boolean[] participating;
    public int lastBid = 0;
    public ArrayList<Integer> bid_stack = new ArrayList<>();

    public ProcessCallsBehaviour(Company company){
        this.company = company;
        this.company.state = State.WAITING_FOR_CALLS;
        this.answers = new String[this.company.taxi_props.length];
        this.participating = new boolean[this.company.taxi_props.length];
        
        this.company.state = State.WAITING_FOR_CALLS;
    }
    
    public void processPassenger(){
        this.company.state = State.WAITING_FOR_BIDS;
        System.out.println("--------------------------------------------------");
        System.out.println("Company: Bididng Passenger " + this.attendee.id + "!!" );
        
        this.lastBid = 0;
        while( !bid_stack.isEmpty() )
            bid_stack.remove(bid_stack.size()-1);
        
        //send message to all Taxis that are in service'
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){ 
            if( isWaitingForCall(i-1) ){
                this.participating[i-1] = true;
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Take-Passenger");
                msg.setContent(this.attendee.toString());
                company.send(msg);
                this.answers[i-1] = null;
            }else{
                this.participating[i-1] = false;
                this.answers[i-1] = "0";
            }
        }
    }
    
    public boolean gotAllBids(){
        /*for( int i = 0 ; i <  this.answers.length ; i++ ){
            if( this.answers[i] == null && isOutOfService( i , true ) ){
                this.answers[i] = "0";
            }
        }*/
        
        for( int i = 0 ; i <  this.answers.length ; i++ ){
            if( this.answers[i] == null ){
                return false;
            }
        }
        
        return true;
    }
    
    public int getMaxBidder(){
        int max = 0;
        int index = 0;
        
        for( int i = 0 ; i < this.answers.length ; i++ ){
            max = Integer.parseInt(this.answers[i]);
            if( max != 0 ){
                index = i;
                i = this.answers.length;
            }
        }
        
        for( int i = 0 ; i < answers.length ; i++ ){
            int bid = Integer.parseInt(this.answers[i]);
            
            if( Company.Vickrey ){
                if(bid != 0 &&  bid > max ){
                    max = Integer.parseInt(this.answers[i]);
                    index = i;
                }
            }else{
                if(bid != 0 &&  bid < max ){
                    max = Integer.parseInt(this.answers[i]);
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getSecondLowestBid(){
        int max_index = this.getMaxBidder();
        int winning_bid = Integer.parseInt( this.answers[ max_index ] );
        
        int max = 0;
        int index = -1;
        
        for( int i = 0 ; i < this.answers.length ; i++ ){
            max = Integer.parseInt(this.answers[i]);
            if( max != 0 && i != max_index && max != winning_bid ){
                index = i;
                i = this.answers.length;
            }
        }
        
        for( int i = 0 ; i < answers.length ; i++ ){
            int bid = Integer.parseInt(this.answers[i]);
            if(bid != 0 &&  bid > max && i != max_index && bid != winning_bid ){
                max = Integer.parseInt(this.answers[i]);
                index = i;
            }
        }
        
        if( index != -1 )
            winning_bid = Integer.parseInt( this.answers[ index ] );
                
        return winning_bid;
    }
    
    public boolean thereIsWinner(){
        int amount_bidders = 0;
        for ( String answer : answers ) {
            if (  Integer.parseInt(answer) != 0 ) {
                amount_bidders++;
            }
        }
        return amount_bidders==1;
    }
    
    public boolean nobodyCan(){
        for ( String answer : answers ) {
            if (  Integer.parseInt(answer) != 0 ) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void action() {
        
        switch( this.company.state ){
            case WAITING_FOR_CALLS:
                    if( this.company.passengers.size() > 0  ){
                        this.attendee = this.company.passengers.remove(0);
                        this.processPassenger();
                    } else {
                        this.company.state = State.WAITING_FOR_CALLS;
                    }
                break;
                
            case WAITING_FOR_BIDS:
                    if( gotAllBids() ){
                        //Got all responses
                        this.maxBidder = this.getMaxBidder();
                        if( Company.Vickrey ){
                            //*******VICKREY AUCTION**********
                            
                            //if nobody can take the passenger
                            /*if( this.nobodyCan() ){
                                this.rejectCostumer();
                            }else{
                                this.notifyVickreyBiggestBid();
                            }*/
                            
                            if( this.thereIsWinner() ){
                                //Finish Auction   
                                this.notifyVickreyBiggestBid();

                            }else{
                                //if nobody can take the passenger
                                if( this.nobodyCan() ){
                                    this.rejectCostumer();
                                }else{
                                    this.notifyBiggestBid();
                                }
                            }
                            
                            
                        }else{
                            //******LOWEST BID AUCTION********

                            if( this.thereIsWinner() ){
                                //Finish Auction   
                                this.notifyWinner();

                            }else{
                                //if nobody can take the passenger
                                if( this.nobodyCan() ){
                                    this.rejectCostumer();
                                }else{
                                    this.notifyBiggestBid();
                                }
                            }
                        }
                        
                    }else{
                        //Received a BID
                        this.checkForBid();
                    }
                break;
                
        }
    }
    public void notifyWinner(){
        System.out.println("Give request to: " + (this.maxBidder+1) );
        System.out.println("--------------------------------------------------");
        
        this.attendee.taxiId = this.maxBidder;
        //notify the rest of bidders
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            if( isWatingForDecisionOrBid(i-1) ){
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Decision");

                if( this.maxBidder+1 != i ){
                    msg.setContent( "Sorry" );
                }else{
                    msg.setContent( "GO");
                }

                company.send(msg);
                this.answers[i-1] = null;
            }else{
                this.answers[i-1] = "0";
            }
        }

        this.company.state = State.WAITING_FOR_CALLS;
    }
    
    public void notifyVickreyBiggestBid(){
        System.out.println("Give request to: " + (this.maxBidder+1) );
        System.out.println("--------------------------------------------------");
        
        Collections.sort(bid_stack);
        int amount = bid_stack.remove(bid_stack.size()-1);
        int possible = amount;
        while( !bid_stack.isEmpty() && possible == amount ){
            possible = bid_stack.remove(bid_stack.size()-1);
        }
        if( possible != amount )
            amount = possible;


        this.attendee.taxiId = this.maxBidder;
        //notify the rest of bidders
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            if( isWatingForDecisionOrBid(i-1) ){
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Decision");

                if( this.maxBidder+1 != i ){
                    msg.setContent( "Sorry" );
                }else{
                    msg.setContent( "GO-" + amount);
                }

                company.send(msg);
                this.answers[i-1] = null;
            }else{
                this.answers[i-1] = "0";
            }
        }

        this.company.state = State.WAITING_FOR_CALLS;
    }
    
   
    public void rejectCostumer(){
        System.out.println("Company: I'm so sorry but nobody can take passenger P" + this.attendee.id);
        this.attendee.origin.passenger = null;
        this.attendee = null;

        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            if( isWatingForDecisionOrBid(i-1) ){
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Decision");
                msg.setContent( "Sorry" );
                company.send(msg);
                this.answers[i-1] = null;
            }else{
                this.answers[i-1] = "0";
            }
        }

        this.company.state = State.WAITING_FOR_CALLS;
    }
    
    public void notifyBiggestBid(){
        //Notify Auctioners about biggest Bid
        int amount = Integer.parseInt(this.answers[this.maxBidder]);
        this.lastBid = amount;

        //notify the rest of bidders
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            if( this.maxBidder+1 != i ){
                if( isWatingForDecisionOrBid(i-1) ){
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                    msg.setLanguage("English");
                    msg.setOntology("Bigger-Bid");
                    msg.setContent( amount + "" );
                    company.send(msg);
                    this.answers[i-1] = null;
                }else{
                    this.answers[i-1] = "0";
                }
            }
        }
    }
    
    public void checkForBid(){
        ACLMessage msg = this.myAgent.receive();
        if ( msg != null ){
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(msg.getContent());

            m.find();
            Integer taxiIndex = Integer.parseInt(m.group());
            m.find();
            Integer bid = Integer.parseInt(m.group());

            if( bid != 0 ){
                bid_stack.add(bid);
                System.out.println("Driver " + taxiIndex + " bids " + bid);
            }

            this.answers[taxiIndex-1] = ""+bid;
        }
    }
    
    public boolean isOutOfService( Driver driver ){
        return driver.state == State.OUT_OF_SERVICE || driver.state == State.GOING_HOME;
    }
    
    public boolean isWaitingForCall( int i ){
        Driver driver = this.company.taxis.get(i).driver;
        if( driver.state == State.WAITING_FOR_COMPANY  )
            return true;
        if( !isOutOfService(driver) && !driver.silent_biding )
            return true;
        return false;
    }
    
    public boolean isWatingForDecisionOrBid( int i ){//true
        Driver driver = this.company.taxis.get(i).driver;
      
        if( driver.silent_biding )
            return true;
        if( driver.state == State.WAITING_FOR_COMPANY  )
            return false;
        if( isOutOfService(driver) )
            return false;
        
        return this.participating[i];
    }
    
    @Override
    public boolean done() {
        return false;
    }
    
}
