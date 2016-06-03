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
public class GoToLocationBehaviour extends Behaviour {
    
    public Driver driver;
    public Intersection origin;
    public Intersection dest;
    public LinkedList<Intersection> path;
    public int actual;
    
    public GoToLocationBehaviour(Intersection dest , Driver driver ){
        System.out.println("Driver " + driver.index + ": going to " + dest );
        this.driver = driver;
        this.origin = this.driver.city.getNearestIntersection( this.driver.taxi.x , this.driver.taxi.y );
        this.dest = dest;
        this.path = this.driver.city.getShortestPath( this.origin , this.dest );
        this.actual = 0;
    }

    @Override
    public void action() {
        if( !this.driver.city.company.mainpanel.running || this.path == null )
            return;
        
        if( actual < this.path.size() ){
            Intersection nearestDest = this.path.get(actual);

            if( this.driver.taxi.x != nearestDest.x ){
                if( this.driver.taxi.x < nearestDest.x ){
                    if( this.driver.taxi.x + this.driver.taxi.speed*MainPanel.frame >= nearestDest.x ){
                        this.driver.taxi.x = nearestDest.x;
                    }else{
                        this.driver.taxi.goRight();
                    }
                }else{
                    if( this.driver.taxi.x - this.driver.taxi.speed*MainPanel.frame <= nearestDest.x ){
                        this.driver.taxi.x = nearestDest.x;
                    }else{
                        this.driver.taxi.goLeft();
                    }
                }
            }

            if( this.driver.taxi.y != nearestDest.y ){
                if( this.driver.taxi.y < nearestDest.y ){
                    if( this.driver.taxi.y + this.driver.taxi.speed*MainPanel.frame >= nearestDest.y ){
                        this.driver.taxi.y = nearestDest.y;
                    }else{
                        this.driver.taxi.goDown();
                    }
                }else{
                    if( this.driver.taxi.y - this.driver.taxi.speed*MainPanel.frame <= nearestDest.y ){
                        this.driver.taxi.y = nearestDest.y;
                    }else{
                        this.driver.taxi.goUp();
                    }
                }
            }
            
            if( this.driver.taxi.y == nearestDest.y && this.driver.taxi.x == nearestDest.x ){
                actual++;
            }
        }
    }

    @Override
    public boolean done() {
        if( this.dest.x == this.driver.taxi.x && this.dest.y == this.driver.taxi.y ){
            System.out.println("Driver " + driver.index + ": Arrived at " + this.driver.taxi );
            return true;
        }
        return false;
    }
    
}
