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
        
        //send message to all Taxis'
        for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
            msg.setLanguage("English");
            msg.setOntology("Take-Passenger");
            msg.setContent(this.attendee.toString());
            company.send(msg);
            this.answers[i-1] = null;
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
        int max = Integer.parseInt(this.answers[0]);
        int index = 0;
        for( int i = 1 ; i < answers.length ; i++ ){
            if( Integer.parseInt(this.answers[i]) > max ){
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
                        
                        this.maxBidder = this.getMaxBidder();
                        if( this.noMoreBids() ){
                            System.out.println("should we give the request to: " + this.maxBidder );
                            
                            //notify the rest of bidders
                            for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
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
                            }
                            
                            this.company.state = State.WAITING_FOR_CALLS;
                            
                        }else{
                            
                            System.out.println("Max bidder until now: " + this.maxBidder );
                            int amount = Integer.parseInt(this.answers[this.maxBidder]);
                            
                            //notify the rest of bidders
                            for( int i = 1 ; i <= this.company.taxi_props.length ; i++ ){
                                if( this.maxBidder+1 != i ){
                                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                                    msg.addReceiver(new AID( company.driverName + i, AID.ISLOCALNAME));
                                    msg.setLanguage("English");
                                    msg.setOntology("Bigger-Bid");
                                    msg.setContent( amount + "" );
                                    company.send(msg);
                                    this.answers[i-1] = null;
                                }
                            }
                        }
                        
                    }else{
                        ACLMessage msg = this.myAgent.receive();
                        if ( msg != null ){
                            Pattern p = Pattern.compile("\\d+");
                            Matcher m = p.matcher(msg.getContent());

                            m.find();
                            Integer taxiIndex = Integer.parseInt(m.group());
                            m.find();
                            Integer bid = Integer.parseInt(m.group());
                            
                            System.out.println("Driver " + taxiIndex + " bids " + bid);
                            this.answers[taxiIndex-1] = ""+bid;
                        }
                    }
                break;
                
        }
       
        
        
        /*if( waiting_response ){
            ACLMessage msg = this.myAgent.receive();
            if ( msg != null ){
                waiting_response = false;
                if( msg.getContent().compareTo("Yes") == 0 ){
                    this.attendee = null;
                }else{
                    if( ++company.askingTo > company.taxi_props.length )
                        company.askingTo = 1;
                }
            }
        }else{
            if( this.attendee != null ){
                this.processPassenger();
            }else if( this.company.passengers.size() > 0  ){
                this.attendee = this.company.passengers.remove(0);
                this.processPassenger();
            } else {
                this.company.state = State.WAITING_FOR_CALLS;
            }
        }*/
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
