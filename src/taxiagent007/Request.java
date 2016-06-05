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
public class Request {
    public Intersection origin;
    public Intersection destiny;
    public double company_cut;
    public double price;
    public int passenger_id;
    
    public Request( Intersection origin , Intersection destiny , double price , double company_cut , int passenger_id ){
        this.origin = origin;
        this.destiny = destiny;
        this.price = price;
        this.company_cut = company_cut;
        this.passenger_id = passenger_id;
    }
}
