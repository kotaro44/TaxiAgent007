/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

/**
 *
 * @author kotaro and Fabio :)
 */
public class Intersection {
    public int x;
    public int y;
    public Passenger passenger;
    public int calls = 0;
    
    public Intersection( int x , int y ){
        this.x = x;
        this.y = y;
    }
    
    public void receiveCall(Passenger new_passenger) {
        this.calls++;
        this.passenger = new_passenger;
    }
    
    public double distance( Intersection other ){
        return Math.sqrt( Math.pow( this.x - other.x, 2 ) + Math.pow( this.y - other.y, 2 ) );
    }
    
}
