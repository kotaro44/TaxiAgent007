/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.awt.Color;
/**
 *
 * @author kotaro and fabio :)
 */

public class Taxi  {
    public double x;
    public double y;
    public Color color;
    public Passenger passenger;
    public Driver driver;
    public double speed = (((30.0*7.0)/60)/60); //30km/h   scale=7 transformed to seconds
    
    private int actual = 0;
    private int last = 0;
    private int elapsed = 0;
    private int total = 0;
    
    private boolean moving = false;
    
    public Taxi( int x , int y , Color color ){
        this.x = x;
        this.y = y;
        this.color = color;
    }
    
    private double move(){
        actual = (int) MainPanel.total_seconds;
        elapsed = actual - last;
        
        if( !moving ){
            moving = true;
            total = 0;
            return 0;
        }else{
            total += elapsed;
            if( total >= MainPanel.frame ){
                total = 0;
                return this.speed*MainPanel.frame;
            }
            return 0;
        }
    }
    
    public void goRight(){
        this.x += this.move();
        last = actual;
    }
    
    public void goLeft(){
        this.x -= this.move();
        last = actual;
    }
    
    public void goUp(){
        this.y -= this.move();
        last = actual;
    }
    
    public void goDown(){
        this.y += this.move();
        last = actual;
    }
    
    @Override
    public String toString(){
        return "{" + this.x + "," + this.y + "}";
    }
}
