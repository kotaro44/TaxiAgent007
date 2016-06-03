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

/**
 *
 * @author kotaro and Fabio :)
 */
public class AssignPassengerBehaviour extends Behaviour {
    
    public Company company;
    public boolean waiting_response = false;
    public Passenger attendee;

    public AssignPassengerBehaviour(Company company){
        this.company = company;
    }
    
    public void processPassenger(){
        waiting_response = true;
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID( company.driverName + company.askingTo , AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Take-Passenger");
        msg.setContent(this.attendee.toString());
        company.send(msg);
    }
    
    @Override
    public void action() {
        if( waiting_response ){
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
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
