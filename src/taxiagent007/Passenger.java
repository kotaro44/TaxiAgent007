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
    
    public int id;
    public Intersection destination;
    public Intersection origin;
    public int taxiId = -1;
    public int payment = 0;

    public Passenger(Intersection origin, Intersection[] intersections , int id ){
        this.origin = origin;
        this.destination = origin;
        this.id = id;
        
        int dest = 0;
        while( this.destination == this.origin ){
            this.destination = intersections[ dest ];
            if( ++dest >= intersections.length ){
                dest = 0;
            }
            double rnd = Math.random();
            double p = this.dest_probability( MainPanel.st_company.mainpanel.city.getTotalDistance(origin, destination) );

            if( rnd >  p ){
                this.destination = this.origin;
            }
        }
        
        this.payment = (int)(MainPanel.st_company.mainpanel.city.getTotalDistance(origin, destination)*MainPanel.st_company.charge_rate_km);
    }
    
    public double dest_probability( double d ){
        double sigma = 1.5*7;
        double m = 2*7;
        return (1/(sigma*Math.sqrt(2*Math.PI)))*Math.pow(Math.E, -1*Math.pow(d - m, 2)/2 )*sigma;
    }
    
    @Override
    public String toString(){
        return "P" + this.id + " in " + this.origin + " wants to go to " + this.destination;
    }
}
