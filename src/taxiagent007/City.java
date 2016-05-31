/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author kotaro and fabio :)
 */
public class City extends JPanel {    
    private int height = 570;
    private int width = 780;
    private int margin_x = 40;
    private int margin_y = 40;
    private int h;
    private int w;
    private Image road_texture;
    private Taxi taxis[];
    private ArrayList<Passenger> passengers = new ArrayList<>();
    
    public int totalCalls = 0;

    
    private int[] taxi_center = {56,28};
                            //horizontal
    private int[][] roads = {{1, 1, 100, 1}, 
                            {1, 15, 100, 15},
                            {1, 22, 100, 22},
                            {1, 29, 100, 29},
                            {1, 36, 100, 36},
                            {1, 50, 100, 50},
                            {1, 1, 1, 50},
                            //vertical
                            {15, 1, 15, 50},
                            {43, 1, 43, 50}, 
                            {57, 1, 57, 50},
                            {86, 1, 86, 50},
                            {100, 1, 100, 50},
                            //small lines
                            {50, 15, 50, 29}, 
                            {29, 29, 29, 50}};
    
    private int[][] intersections_init = {{1,1},{15,1},{43,1},{57,1},{86,1},{100,1},
                                     {1,15},{15,15},{43,15},{57,15},{86,15},{100,15},
                                     {1,22},{15,22},{43,22},{57,22},{86,22},{100,22},
                                     {1,29},{15,29},{43,29},{57,29},{86,29},{100,29},
                                     {1,36},{15,36},{43,36},{57,36},{86,36},{100,36},
                                     {1,50},{15,50},{43,50},{57,50},{86,50},{100,50},
                                     {50,15},{50,22},{50,29},
                                     {29,29},{29,36},{29,50}};
    private Intersection[] intersections;
    
    
    //behaviour variables
    int lambda = 1;
    int k = 5;
    
    public City(){
        try {
            File pathToFile = new File("road.jpg");
            road_texture = ImageIO.read(pathToFile);
            this.generateCity();
        } catch (IOException ex) {
            
        }
        
        this.h = (this.height - this.margin_y*2)*2;
        this.w = this.width - this.margin_x*2;
    }
    
    public double poisson( double k ){
        return (Math.pow( this.lambda , k)*Math.pow(Math.E, -this.lambda))/fact( k );
    }
    
    public double fact( double x ){
        if( x <= 1 )
            return 1;
        return x*fact( x -1 );
    }
    
    private void generateCity(){
        this.taxis = new Taxi[1];
        /*this.taxis[0] = new Taxi(this.taxi_center[0],this.taxi_center[1],Color.ORANGE);*/
        
        this.intersections = new Intersection[this.intersections_init.length];
        for( int i = 0 ; i <  this.intersections_init.length ; i++ ){
            this.intersections[i] = new Intersection(this.intersections_init[i][0],this.intersections_init[i][1]);
        }
    }
    
    private void drawRoad( int x1 , int y1 , int x2 , int y2 , Graphics g ){
        int img_size = 30;
        int m2 = img_size/2;
        if( x1 != x2 ){
            int m = ((y2-y1)/(x2-x1));
            for( int i = x1 ; i <= x2 ; i+= img_size ){
                g.fillRect( i - m2 , 
                    (m*(i-m2)+(y1-m*x1))-m2,img_size,img_size);
            }
        }else{
            for( int i = y1 ; i <= y2 ; i+= img_size ){
                g.fillRect( x1- m2 , i - m2,img_size,img_size);
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        g.clearRect(0, 0, width, height);
        
        g.setColor(Color.GRAY);
        
        //draw roads
        for (int[] road : roads) {
            this.drawRoad(scaleX(road[0]), scaleY(road[1]), scaleX(road[2]), scaleY(road[3]), g);
        }
        
        //draw intersections
        int r = 4;
        for (Intersection intersection : intersections ) {
            
            r = 1*intersection.calls;
            g.setColor(Color.red);
            if( intersection.passenger != null ){
                g.drawLine(scaleX(intersection.x), scaleY(intersection.y), 
                    scaleX(intersection.passenger.destination.x), scaleY(intersection.passenger.destination.y));
    
                g.fillOval(this.scaleX(intersection.passenger.destination.x) - r/2, 
                    scaleY(intersection.passenger.destination.y) - r/2, r, r);
                
                g.setColor(Color.yellow);
            }else{
                g.setColor(Color.blue);
            }
            
            g.fillOval(scaleX(intersection.x) - r/2,scaleY(intersection.y) - r/2, r, r);
        }
        
        //draw Taxi Center
        g.setColor(Color.RED); //taxi center
        g.fillRect( 22 + scaleX(this.taxi_center[0]) ,  scaleY(this.taxi_center[1]) - 35, 30 , 30 ); 
        
        //draw Taxis
        for (Taxi taxi : this.taxis) {
            if( taxi != null ){
                g.setColor(taxi.color);
                g.fillOval( scaleX(taxi.x) , scaleY(taxi.x), 10, 10);
            }
        }

    }
    
    public int scaleX( int x ){
        return this.margin_x + this.w*x/100;
    }
    
    public int scaleY( int y ){
        return this.margin_y + this.h*y/100;
    }
    
    public void updateCity(){
       /*for (Taxi taxi : this.taxis) {
           while( !taxi.drive(Direction.values()[(int) Math.floor( Math.random()*4 )] , this ) ){
               
           }
       } */
     
       for( Intersection intersection : this.intersections ){
           if( Math.random() < this.poisson(this.k) ){
               Passenger new_passenger = new Passenger( intersection , this.intersections );
               this.passengers.add(new_passenger);
               intersection.receiveCall( new_passenger );
               this.totalCalls++;
           }
       }
       
    }

    public void resolveAll() {
        for( Intersection intersection : this.intersections ){
            intersection.passenger = null;
        }
    }
    
}
