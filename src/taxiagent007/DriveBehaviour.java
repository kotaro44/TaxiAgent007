/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import jade.core.behaviours.Behaviour;
import java.util.LinkedList;

/**
 *
 * @author kotaro and Fabio :)
 */
public class DriveBehaviour extends Behaviour {

    Taxi taxi;
    Intersection dest;
    City city;
    int actual;
    LinkedList<Intersection> path;
    
    public DriveBehaviour( Taxi taxi , City city , Intersection dest ){
        this.taxi = taxi;
        this.dest = dest;
        this.city = city;
        this.actual = 0;
        this.path = city.getShortestPath(city.getNearestIntersection( taxi.x , taxi.y ) , dest );
    }
    

    @Override
    public void action() {
        if( !city.company.mainpanel.running || this.path == null )
            return;
        
        if( actual < this.path.size() ){
            Intersection nearestDest = this.path.get(actual);

            if( taxi.x != nearestDest.x ){
                if( taxi.x < nearestDest.x ){
                    if( taxi.x + taxi.speed >= nearestDest.x ){
                        taxi.x = nearestDest.x;
                    }else{
                        taxi.goRight();
                    }
                }else{
                    if( taxi.x - taxi.speed <= nearestDest.x ){
                        taxi.x = nearestDest.x;
                    }else{
                        taxi.goLeft();
                    }
                }
            }

            if( taxi.y != nearestDest.y ){
                if( taxi.y < nearestDest.y ){
                    if( taxi.y + taxi.speed >= nearestDest.y ){
                        taxi.y = nearestDest.y;
                    }else{
                        taxi.goDown();
                    }
                }else{
                    if( taxi.y - taxi.speed <= nearestDest.y ){
                        taxi.y = nearestDest.y;
                    }else{
                        taxi.goUp();
                    }
                }
            }

            if( taxi.y == nearestDest.y && taxi.x == nearestDest.x ){
                if( ++actual >= this.path.size() ){
                    this.actual = 0;
                    Intersection new_dest;
                    Intersection origin = city.getNearestIntersection( taxi.x , taxi.y );
                    do {
                        new_dest = city.intersections[(int)Math.floor(city.intersections.length*Math.random())];
                    } while( new_dest == origin );
                    this.path = city.getShortestPath( origin , new_dest  );
                }
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
    
    @Override
    public String toString(){
        return "behaviour from " + taxi + " to " + dest + " by " + path;
    }
}
