/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.util.ArrayList;

/**
 *
 * @author kotaro and Fabio :)
 */
public class Intersection {
    public int x;
    public int y;
    public Passenger passenger;
    public int calls = 0;
    public int index;
    
    public Intersection( int x , int y , int index ){
        this.x = x;
        this.y = y;
        this.index = index;
    }
    
    public void receiveCall(Passenger new_passenger) {
        this.calls++;
        this.passenger = new_passenger;
    }
    
    public double distance( Intersection other ){
        return Math.sqrt( Math.pow( this.x - other.x, 2 ) + Math.pow( this.y - other.y, 2 ) );
    }
    
    public double distance( int x , int y  ){
        return Math.sqrt( Math.pow( this.x - x, 2 ) + Math.pow( this.y - y, 2 ) );
    }
    
    public double distance( double x , double y  ){
        return Math.sqrt( Math.pow( this.x - x, 2 ) + Math.pow( this.y - y, 2 ) );
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
  
   @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Intersection other = (Intersection) obj;
        return index == other.index;
    }
    
    @Override
    public String toString() { 
        return index + ": {" + this.x + "," + this.y + "}"; 
    }
}
