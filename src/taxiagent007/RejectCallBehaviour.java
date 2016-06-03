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
public class RejectCallBehaviour extends Behaviour {

    Driver driver;
    
    public RejectCallBehaviour(Driver driver){
        this.driver = driver;
    }
    
    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive();
        if( msg != null ){
            String passenger = msg.getContent();
            ACLMessage reply = msg.createReply();
            reply.setContent("No");
            this.driver.send(reply);
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
