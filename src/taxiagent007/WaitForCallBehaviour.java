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
    }
   
    
    @Override
    public void action() {
        ACLMessage msg = this.myAgent.receive();
        if (msg != null && !received ) {
            
            System.out.println("Received Message!");
            String passenger = msg.getContent();
            System.out.println("They asked me to go to: " + passenger);
            //ACLMessage reply = msg.createReply();
            
            
            Pattern p = Pattern.compile("\\{\\d+\\,\\d+\\}");
            Matcher m = p.matcher(passenger);
            
            m.find();
            Intersection origin = driver.city.getNearestIntersection(m.group());
            
            m.find();
            Intersection destination = driver.city.getNearestIntersection(m.group());
            
            driver.addBehaviour(new GoToLocationBehaviour( origin , driver ){
                @Override
                public int onEnd(){
                    driver.addBehaviour(new PickCostumerBehaviour( driver ){
                        @Override
                        public int onEnd(){
                            driver.addBehaviour(new GoToLocationBehaviour( destination , driver ){
                                @Override
                                public int onEnd(){
                                    driver.addBehaviour(new DropCostumerBehaviour( driver ){
                                        @Override
                                        public int onEnd(){
                                            driver.addBehaviour(new WaitForCallBehaviour( driver ) );
                                            return 0;
                                        }
                                    });
                                    return 0;
                                }
                            });
                            return 0;
                        }
                    } );
                    return 0;
                }
            });
           
            received = true;
        }
    }

    @Override
    public boolean done() {
        return received;
    }
    
}
