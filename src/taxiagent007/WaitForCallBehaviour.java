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
    public Driver driver;
    
    public WaitForCallBehaviour(Driver driver){
        this.driver = driver;
        this.driver.state = State.WAITING_FOR_COMPANY;
    }
   
    @Override
    public void action() {
        switch(this.driver.state){
            case WAITING_FOR_COMPANY:
                ACLMessage msg = this.myAgent.receive();
                if (msg != null && !received ) {
                   
                    String passenger = msg.getContent();
                    System.out.println("Driver " + driver.index +  ": They asked me to go to: " + passenger);
                    
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
                    
                    driver.addBehaviour(new BidBehaviour( origin ,destination , this.driver , msg  , passenger_id ));
                }
                break;
            case WON_BID_RESTING:
                    this.driver.actual_request = this.driver.requests.remove(0);
                    this.driver.state = State.GOING_FOR_PASSENGER;
                    driver.addBehaviour(new ProcessRequestBehaviour( this.driver , this.driver.actual_request ));
                break;
        }

    }

    @Override
    public boolean done() {
        return received;
    }
    
}
