/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;

/**
 *
 * @author kotaro and fabio :)
 */
public class Company extends Agent {
    boolean ready = false;
    MainPanel mainpanel;
    
    @Override
     protected void setup() {
        System.out.println("This Agent only starts the UI");
        MainPanel.jade_main(this);
    }
     
    public void onPanelReady( MainPanel mainpanel){
        this.mainpanel = mainpanel;
        System.out.println("Panel is ready and visible!");
    }
}
