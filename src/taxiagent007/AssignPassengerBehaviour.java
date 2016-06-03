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

    public AssignPassengerBehaviour(Company company){
        this.company = company;
    }
    
    public void processPassenger(Passenger passenger){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("TaxiDriver1", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Take-Passenger");
        msg.setContent(passenger.toString());
        company.send(msg);
    }
    
    @Override
    public void action() {
        if( this.company.passengers.size() > 0 ){
            Passenger next = this.company.passengers.remove(0);
            this.processPassenger(next);
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
