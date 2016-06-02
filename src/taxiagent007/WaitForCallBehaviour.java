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
public class WaitForCallBehaviour extends Behaviour {

    boolean received = false;
    
    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive();
        if (msg != null) {
            System.out.println("Received Message!");
            String passenger = msg.getContent();
            System.out.println("They asked me to go to: " + passenger);
            //ACLMessage reply = msg.createReply();
            
            
            /***FABIO: read where the company wants this taxi to go from passenger variable*****/
            /*Pattern p = Pattern.compile("{\\d+,\\d+}");
            Matcher m = p.matcher("aaaaab");
            boolean b = m.matches();*/
        }
    }

    @Override
    public boolean done() {
        return received;
    }
    
}
