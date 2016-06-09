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
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<Passenger> passengers = new ArrayList<>();
    public Company company;
    ArrayList<Road> edges;
    DijkstraAlgorithm dijkstra;
    Graph graph;
    
    public int totalCalls = 0;
    public int callsHour = 0;
    public int last_callHour = 0;
    
    public Intersection[] intersections; //ver aqui
    
    int last = 0;
    int total_minute = 0;
    int total_hour = 0;
    
    //behaviour variables
    int lambda = 1;
    int k = 4;

    
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
    
    private int[][] intersections_init = {
        //1st horizontal line
        {1,1,1,6},//0
        {15,1,0,2,7},//1
        {43,1,1,3,8},//2
        {57,1,2,4,9},//3
        {86,1,3,5,10},//4
        {100,1,4,11},//5
        //2nd horizontal line
        {1,15,0,7,12},//6
        {15,15,1,6,8,13},//7
        {43,15,2,7,39,14},//8
        {57,15,3,8,10,15},//9
        {86,15,4,9,11,16},//10
        {100,15,5,10,17},//11
        //3rd horizontal line
        {1,22,6,13,18},//12
        {15,22,7,12,14,19},//13
        {43,22,8,13,40,20},//14
        {57,22,9,40,16,21},//15
        {86,22,10,15,17,22},//16
        {100,22,11,16,23},//17
        //4th horizontal line
        {1,29,12,19,24},//18
        {15,29,13,18,36,25},//19
        {43,29,14,36,41,26},//20
        {57,29,15,41,22,27},//21
        {86,29,16,21,23,28},//22
        {100,29,17,22,29},//23
        //5th horizontal line
        {1,36,18,25,30},//24
        {15,36,19,24,37,31},//25
        {43,36,20,37,27,32},//26
        {57,36,21,26,28,33},//27
        {86,36,22,27,29,34},//28
        {100,36,23,28,35},//29
        //6th horizontal line
        {1,50,24,31},//30
        {15,50,25,30,38},//31
        {43,50,26,38,33},//32
        {57,50,27,32,34},//33
        {86,50,28,33,35},//34
        {100,50,29,34},//35
        //1st vertocal line
        {29,29,19,20,37},//36
        {29,36,36,25,26,38},//37
        {29,50,37,31,32},//38
        //2nd vertical line
        {50,15,8,9,40},//39
        {50,22,39,14,15,41},//40
        {50,29,40,20,21}//41
    };
    
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
        this.intersections = new Intersection[this.intersections_init.length];
        //create all intersections
        for( int i = 0 ; i <  this.intersections_init.length ; i++ ){
            this.intersections[i] = new Intersection(this.intersections_init[i][0],this.intersections_init[i][1],i);
        }
        
        //create all adjacencies
        int id = 0;
        this.edges = new ArrayList<>();
        for( int i = 0 ; i <  this.intersections_init.length ; i++ ){
            for (int j = 2; j < this.intersections_init[i].length; j++) {
                Intersection dest = this.intersections[this.intersections_init[i][j]];
                Road r = new Road( id++ , this.intersections[i] , dest , this.intersections[i].distance(dest) );
                this.edges.add(  r );
            }
        }
        
        this.graph = new Graph(this.intersections, this.edges);
        this.dijkstra = new DijkstraAlgorithm(this.graph);
      
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
    
    public void setCompany( Company company ){
        this.company = company;
    }
    
    @Override
    public void paint(Graphics g){
        if( company == null )
            return;
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
            
            //draw Passenger
            
          
            if( intersection.passenger != null ){
                
                g.drawLine(scaleX(intersection.x), scaleY(intersection.y), 
                    scaleX(intersection.passenger.destination.x), scaleY(intersection.passenger.destination.y));
    
                g.fillOval(this.scaleX(intersection.passenger.destination.x) - r/2, 
                    scaleY(intersection.passenger.destination.y) - r/2, r, r);
                
                g.drawString("P" + intersection.passenger.id, scaleX(intersection.x) + 5, scaleY(intersection.y) + 5 );  
                
                g.setColor(Color.yellow);
            }else{
                g.setColor(Color.blue);
            }
            
            g.fillOval(scaleX(intersection.x) - r/2,scaleY(intersection.y) - r/2, r, r);
            
            r = 1*intersection.drops.size();
            g.setColor(Color.white);
            g.fillOval(scaleX(intersection.x) - r/2,scaleY(intersection.y) - r/2, r, r);
        }
        
        //draw Taxi Center
        g.setColor(Color.DARK_GRAY); //taxi center
        g.fillRect( 22 + scaleX(this.taxi_center[0]) ,  scaleY(this.taxi_center[1]) - 35, 30 , 30 ); 
        
        //draw Taxis
        int tr = 10;
        for ( int i = 0 ; i < this.company.taxis.size() ; i++ ) {
            Taxi taxi = this.company.taxis.get(i);
            
            if( taxi != null ){
                if( taxi.driver.state != State.OUT_OF_SERVICE ){
                    if( taxi.passenger != null ){
                        g.setColor(Color.WHITE);
                        tr += 5;
                        g.fillOval( scaleX(taxi.x) - tr/2, scaleY(taxi.y)- tr/2, tr, tr);
                        tr -= 5;
                        g.setColor(taxi.color);
                        g.drawString("X", scaleX(taxi.passenger.destination.x) - 5, scaleY(taxi.passenger.destination.y) + 5 ); 
                        
                    }
                    g.setColor(taxi.color);
                    g.fillOval( scaleX(taxi.x) - tr/2, scaleY(taxi.y)- tr/2, tr, tr);
                }else{
                    g.setColor(taxi.color);
                    g.fillOval( 62 + scaleX(this.taxi_center[0]) - tr/2 + tr*(i%4) , 
                            scaleY(this.taxi_center[1]) - tr/2 - 35 + tr*(i/4) , tr, tr);
                }
            }
        }

    }
    
    public int scaleX( int x ){
        return this.margin_x + this.w*x/100;
    }
    
    public int scaleX( double x ){
        return (int)(this.margin_x + this.w*x/100);
    }
    
    public int scaleY( int y ){
        return this.margin_y + this.h*y/100;
    }
    
    public int scaleY( double y ){
        return (int)(this.margin_y + this.h*y/100);
    }
    
    public boolean isThereAnyAvailableIntersection(){
        for( Intersection intersection : this.intersections ){
            if( intersection.passenger == null )
                return true;
        }
        return false;
    }
    
    public void updateCity(){
        int actual = MainPanel.seconds;
        int elapsed = actual - last ;
        
        if( elapsed >= 0 ){
            total_minute += elapsed;
            total_hour += elapsed;
        }
        
        for( Taxi taxi : this.company.taxis ){
            taxi.driver.update( elapsed );
        }
       
        if( total_minute >= 60*2 ){//Every 2 minute
            total_minute = 0;
            if( Math.random() < this.poisson(this.k) ){
                this.makeCall();
            }
        }
        
        if( total_hour >= 60*60 ){//every hour
            total_hour = 0;
            this.callsHour = this.totalCalls - this.last_callHour;
            this.last_callHour = this.totalCalls;
        }
        
        last = actual;
       
    }
    
    public void makeCall(){
        if( !isThereAnyAvailableIntersection() )
            return;
        
        Intersection intersection = null;
        do{ 
            intersection = this.intersections[ (int)Math.floor(Math.random()*this.intersections.length) ];
        }while( intersection.passenger != null );
        Passenger new_passenger = new Passenger( intersection , this.intersections , this.totalCalls++ );
        this.passengers.add(new_passenger);
        intersection.receiveCall( new_passenger );
        this.company.callTaxi(new_passenger);
    }

    public void resolveAll() {
        for( Intersection intersection : this.intersections ){
            intersection.passenger = null;
        }
    }
    
    //This function throws errors of concurrent modification
    public LinkedList<Intersection> getShortestPath( Intersection from, Intersection to){
        LinkedList<Intersection> result;
        if( from == to ){
            result = new LinkedList<>();
            result.add(to);
        }else{
            dijkstra.execute(from);
            result = dijkstra.getPath(to);
        }
        if( result == null ){
            System.out.println(">>>>>>>>>>>>" + from + " -> " + to + "  gives null?");
        }
        
        return result;
    }
    
    public double getTotalDistance(LinkedList<Intersection> path){
        double result = 0;
        for( int i = 1 ; i < path.size() ; i++  ){
            result += path.get(i-1).distance(path.get(i));
        }
        return result;
    }
    
    public Intersection[] getIntersections() //just in case we need a copy of intersections[]
    {
        return intersections.clone();
    }

    public Intersection getNearestIntersection(double x, double y) {
        Intersection nearest = this.intersections[0];
        for (int i = 1; i < this.intersections.length; i++) {
            if( nearest.distance(x,y) > this.intersections[i].distance(x, y) ){
                nearest = this.intersections[i];
            }
        }
        return nearest;
    }
    
    public Intersection getNearestIntersection(String coordinate) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(coordinate);
        int[] coord = {0,0};
        m.find();
        coord[0] = Integer.parseInt(m.group());
        m.find();
        coord[1] = Integer.parseInt(m.group());
        Intersection result = this.getNearestIntersection(coord[0], coord[1]);
        return result;
        
    }
}