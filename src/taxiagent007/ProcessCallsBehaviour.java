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

    public ProcessCallsBehaviour(Company company){
        this.company = company;
        this.company.state = State.WAITING_FOR_CALLS;
        this.answers = new String[this.company.taxi_props.length];
        
        this.company.state = State.WAITING_FOR_CALLS;
    }
    
    public void processPassenger(){
        this.company.state = State.WAITING_FOR_BIDS;
        System.out.println("--------------------------------------------------");
        System.out.println("Company: Bididng Passenger " + this.attendee.id + "!!" );
        
        //send message to all Taxis that are in service'
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            if( isOutOfService(i-1) ){
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Take-Passenger");
                msg.setContent(this.attendee.toString());
                company.send(msg);
                this.answers[i-1] = null;
            }else{
                this.answers[i-1] = "0";
            }
        }
    }
    
    public boolean gotAllBids(){
        for( Object answer : this.answers ){
            if( answer == null ){
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
        
        for( int i = 1 ; i < answers.length ; i++ ){
            int bid = Integer.parseInt(this.answers[i]);
            if(bid != 0 &&  bid < max ){
                max = Integer.parseInt(this.answers[i]);
                index = i;
            }
        }
        return index;
    }
    
    public boolean noMoreBids(){
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
                        if( this.noMoreBids() ){
                            //Finish Auction
                            System.out.println("Driver " + (this.maxBidder+1) + " bids " + this.answers[this.maxBidder] + "!");
                            System.out.println("Give request to: " + (this.maxBidder+1) );
                            System.out.println("--------------------------------------------------");
                            
                            this.attendee.taxiId = this.maxBidder;
                            //notify the rest of bidders
                            for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
                                if( isOutOfService(i-1) ){
                                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                                    msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                                    msg.setLanguage("English");
                                    msg.setOntology("Decision");

                                    if( this.maxBidder+1 != i ){
                                        msg.setContent( "Sorry" );
                                    }else{
                                        msg.setContent( "GO" );
                                    }

                                    company.send(msg);
                                    this.answers[i-1] = null;
                                }else{
                                    this.answers[i-1] = "0";
                                }
                            }
                            
                            this.company.state = State.WAITING_FOR_CALLS;
                            
                        }else{
                            //if nobody can take the passenger
                            if( this.nobodyCan() ){
                                System.out.println("Company: I'm so sorry but nobody can take passenger P" + this.attendee.id);
                                this.attendee.origin.passenger = null;
                                this.attendee = null;
                                
                                for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
                                    if( isOutOfService(i-1) ){
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
                            }else{
                                //Notify Auctioners about biggest Bid
                                int amount = Integer.parseInt(this.answers[this.maxBidder]);

                                //notify the rest of bidders
                                for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
                                    if( this.maxBidder+1 != i ){
                                        if( isOutOfService(i-1) ){
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
                        }
                        
                    }else{
                        //Received a BID
                        ACLMessage msg = this.myAgent.receive();
                        if ( msg != null ){
                            Pattern p = Pattern.compile("\\d+");
                            Matcher m = p.matcher(msg.getContent());

                            m.find();
                            Integer taxiIndex = Integer.parseInt(m.group());
                            m.find();
                            Integer bid = Integer.parseInt(m.group());
                            
                            if( bid != 0 ){
                                System.out.println("Driver " + taxiIndex + " bids " + bid);
                            }
                            
                            this.answers[taxiIndex-1] = ""+bid;
                        }
                    }
                break;
                
        }
    }

    public boolean isOutOfService( int i ){
        Driver driver = this.company.taxis.get(i).driver; 
        return driver.state != State.OUT_OF_SERVICE && driver.state != State.GOING_HOME;
    }
    
    @Override
    public boolean done() {
        return false;
    }
    
}
