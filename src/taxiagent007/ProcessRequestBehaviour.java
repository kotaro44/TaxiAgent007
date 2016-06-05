/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.behaviours.Behaviour;

/**
 *
 * @author kotaro and Fabio :)
 */
public class ProcessRequestBehaviour extends Behaviour {
    
    Driver driver;
    Request request;
    boolean processing = false;
    
    public ProcessRequestBehaviour(Driver driver, Request request ){
        this.driver = driver;
        this.request = request;
    }

    @Override
    public void action() {
        if( !processing ){
            ProcessRequestBehaviour self = this;
            processing = true;
            SilentBidBehaviour silent_bid = new SilentBidBehaviour( driver );
            driver.addBehaviour( silent_bid );
            this.driver.addBehaviour(new GoToLocationBehaviour( request.origin , driver ){
                @Override
                public int onEnd(){
                    this.driver.state = State.PICKING_PASSENGER;
                    driver.addBehaviour(new PickCostumerBehaviour( driver ){
                        @Override
                        public int onEnd(){
                            this.driver.state = State.TAKING_PASSENGER;
                            driver.addBehaviour(new GoToLocationBehaviour( request.destiny , driver ){
                                @Override
                                public int onEnd(){
                                    this.driver.state = State.DROPING_PASSENGER;
                                    driver.addBehaviour(new DropCostumerBehaviour( driver ){
                                        @Override
                                        public int onEnd(){
                                            silent_bid.stop();
                                            driver.removeBehaviour(self);
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
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
}
