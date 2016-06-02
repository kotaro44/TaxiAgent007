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
    public double speed = (30.0*7.0)/60; //30km/h   scale=7
    public double frame = 1;
    
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
    
    private boolean move(){
        actual = (int) MainPanel.total_minutes;
        elapsed = actual - last;
        
        if( !moving ){
            moving = true;
            total = 0;
            return false;
        }else{
            total += elapsed;
            if( total >= this.frame ){
                total = 0;
                return true;
            }
            return false;
        }
    }
    
    public void goRight(){
        if( this.move() )
            this.x += this.speed;
        last = actual;
    }
    
    public void goLeft(){
        if( this.move() )
            this.x -= this.speed;
        last = actual;
    }
    
    public void goUp(){
        if( this.move() )
            this.y -= this.speed;
        last = actual;
    }
    
    public void goDown(){
        if( this.move() )
            this.y += this.speed;
        last = actual;
    }
    
    @Override
    public String toString(){
        return "{" + this.x + "," + this.y + "}";
    }
}
