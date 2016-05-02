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
 * @author kotaro
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
        g.setColor(Color.blue);
        g.drawOval(x, y, 20, 20);
    }
    
    public void updateCity(){
        x++;
        this.repaint();
    }
    
}
