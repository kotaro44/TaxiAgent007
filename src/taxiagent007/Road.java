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
public class Road {
    private final int id; 
    private final Intersection source;
    private final Intersection destination;
    private final double weight;
   
    
    public Road(int id, Intersection source , Intersection target, double weight){ 
        this.id = id;
        this.destination = target; 
        this.source = source; 
        this.weight = weight; 
    }
    
    @Override
    public String toString(){
        return " -> " + this.destination;
    }

    public Object getSource() {
        return this.source;
    }

    public Intersection getDestination() {
        return this.destination;
    }

    public double getWeight() {
        return this.weight;
    }

}
