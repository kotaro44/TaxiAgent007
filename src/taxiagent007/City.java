/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author kotaro and fabio :)
 */
public class City extends Canvas {    
    private int height = 570;
    private int width = 780;
    private int margin_x = 40;
    private int margin_y = 40;
    private Image road_texture;
    private Taxi taxis[];
    
    private int[] taxi_center = {56,28};
    private int[][] roads = {{1, 1, 100, 1}, 
                            {1, 15, 100, 15},
                            {1, 22, 100, 22},
                            {1, 29, 100, 29},
                            {1, 36, 100, 36},
                            {1, 50, 100, 50},
                            {1, 1, 1, 50},
                            {15, 1, 15, 50},
                            {43, 1, 43, 50}, 
                            {57, 1, 57, 50},
                            {86, 1, 86, 50},
                            {100, 1, 100, 50},
                            {50, 15, 50, 29}, 
                            {29, 29, 29, 50}};
    
    
    public City(){
      
        try {
            File pathToFile = new File("road.jpg");
            road_texture = ImageIO.read(pathToFile);
            this.generateCity();
        } catch (IOException ex) {
            
        }
    }
    
    private void generateCity(){
        this.taxis = new Taxi[1];
        this.taxis[0] = new Taxi(this.taxi_center[0],this.taxi_center[1],Color.ORANGE);
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
        
        int h = (this.height - this.margin_y*2)*2;
        int w = this.width - this.margin_x*2;
        g.setColor(Color.GRAY);
        
        //draw roads
        for (int[] road : roads) {
            this.drawRoad(this.margin_x + w*road[0]/100, this.margin_y + (h)*road[1]/100, this.margin_x + w*road[2]/100, this.margin_y + (h)*road[3]/100, g);
        }
        
        //draw Taxi Center
        g.setColor(Color.RED); //taxi center
        g.fillRect( 22 + this.margin_x + w*this.taxi_center[0]/100 ,  this.margin_y + h*this.taxi_center[1]/100 - 35, 30 , 30 ); 
        
        //draw Taxis
        for (Taxi taxi : this.taxis) {
            g.setColor(taxi.color);
            g.fillOval(this.margin_x + w*taxi.x/100 , this.margin_y + h*taxi.y/100, 10, 10);
        }

    }
    
    public void updateCity(){
       for (Taxi taxi : this.taxis) {
           while( !taxi.drive(Direction.values()[(int) Math.floor( Math.random()*4 )] , this ) ){
               
           }
       } 
     
    }
    
}
