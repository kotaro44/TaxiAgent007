/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.Agent;
import java.awt.Color;
/**
 *
 * @author kotaro and fabio :)
 */

public class Taxi extends Agent {
    public int x;
    public int y;
    public Color color;
    public int speed = 1;
    
    /*public Taxi(int x ,int y , Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }*/
    
    public boolean drive(Direction dir,City city){
        switch(dir){
            case RIGHT:
                    this.x += this.speed;
                break;
            case LEFT:
                    this.x -= this.speed;
                break;
            case DOWN:
                    this.y += this.speed;
                break;
            case UP:
                    this.y -= this.speed;
                break;
        }
        return true;
    }
    
    @Override
     protected void setup() {
        System.out.println("Hallo! Buyer-agent "+getAID().getName()+ " is ready.");
    }
}
