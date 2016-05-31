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
public class Passenger {
    
    public Intersection destination;
    public Intersection origin;

    public Passenger(Intersection origin, Intersection[] intersections ){
        this.origin = origin;
        this.destination = origin;
        
        int dest = 0;
        while( this.destination == this.origin ){
            this.destination = intersections[ dest ];
            if( ++dest >= intersections.length ){
                dest = 0;
            }
            double rnd = Math.random();
            double p = this.probability( this.origin.distance( this.destination ) );

            if( rnd >  p ){
                this.destination = this.origin;
            }
        }
    }
    
    public double probability( double d ){
        double sigma = 1.5*7;
        double m = 2*7;
        return (1/(sigma*Math.sqrt(2*Math.PI)))*Math.pow(Math.E, -1*Math.pow(d - m, 2)/2 )*sigma;
    }
}
