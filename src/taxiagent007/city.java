/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author kotaro and fabio :)
 */
public class city extends Canvas {
    
    
    private int x;
    private int y;
    
    public city(){
        x = 100;
        y = 100;
    }
    
    @Override
    public void paint(Graphics g){
        
        g.setColor(Color.BLACK);
        g.drawLine(20, 20, 700, 20); //draw the rectangle
        g.drawLine(20, 20, 20, 361);
        g.drawLine(20, 361, 700, 361);
        g.drawLine(700, 361, 700, 20);
        
        g.drawLine(117, 20, 117, 361); //draw the vertical lines
        g.drawLine(311, 20, 311, 361);
        g.drawLine(408, 20, 408, 361);
        g.drawLine(583, 20, 583, 361);

        g.drawLine(20, 117, 700, 117); //draw the horizontal lines
        g.drawLine(20, 166, 700, 166);
        g.drawLine(20, 215, 700, 215);
        g.drawLine(20, 264, 700, 264);
        
        g.drawLine(216, 215, 216, 361); //draw short vertical lines in the middle
        g.drawLine(360, 117, 360, 215);
        
        g.setColor(Color.RED); //taxi center
        g.drawOval(395, 203, 25, 25); 
        //g.drawOval(388, 194, 20, 20);
        
        g.setColor(Color.blue);
        //g.drawLine(x, y, x, y);
        g.drawOval(x, y, 20, 20);
    }
    
    public void updateCity(){
        x+=5;
        this.repaint();
    }
    
}
