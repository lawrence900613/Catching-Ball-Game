package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;
import java.awt.event.*;
import java.util.Random;
public class GameBoard extends JPanel implements MouseInputListener{

    int speedchangecount = 0;
    boolean Draggingflag = false; //check whether circle is holding

    int gameState = 1;
    static final int GAMEPLAY = 1;

    Ball ball = new Ball();

    public GameBoard() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);
        Timer timer = new Timer(5, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                if(speedchangecount == 0 && !Draggingflag){
                    ball.spd.x = Integer.signum(ball.spd.x)*5;
                    ball.spd.y = Integer.signum(ball.spd.y)*5;
                }else{
                    speedchangecount --;
                }
                ball.move();
                ball.wallDetection();
                repaint();
            }

        });
        timer.start();
    }

    Shape theCircle;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
             }
      
            @Override
            public void mousePressed(MouseEvent e) {
            if(theCircle.contains(e.getX(), e.getY())){
                ball.pos.x = e.getX();
                ball.pos.y = e.getY();
            }
        }
      
            @Override
            public void mouseReleased(MouseEvent e) {
                if(Draggingflag){
                    speedchangecount = 10;
                    Random ran = new Random();
                    int x = ran.nextInt(20 + 20) - 20;
                    int y = ran.nextInt(20 + 20) - 20;
                    ball.spd.x = x;
                    ball.spd.y = y;
                    Draggingflag = false;
                }
            }
      
            @Override
            public void mouseEntered(MouseEvent e) { }
      
            @Override
            public void mouseExited(MouseEvent e) { }
      
          });
          addMouseMotionListener(new MouseMotionListener() {
      
            @Override
            public void mouseDragged(MouseEvent e) {
            if(theCircle.contains(e.getX(), e.getY())){
                ball.pos.x = e.getX();
                ball.pos.y = e.getY();
                }
                ball.spd.x = Integer.signum(ball.spd.x)*0;
                ball.spd.y = Integer.signum(ball.spd.y)*0;
                Draggingflag = true;
            }
      
            @Override
            public void mouseMoved(MouseEvent e) { }
      
          });
        if (gameState == GAMEPLAY) {
            Graphics2D g2d = (Graphics2D) g;
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x, 2.0 * ball.dim.y);
            g2d.setColor(Color.white);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
            // g.setColor(Color.WHITE);
            // g.fillOval(ball.pos.x, ball.pos.y, ball.dim.x, ball.dim.y); // Oval Drawing
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        int x, y;
        if(theCircle.contains(e.getX(), e.getY())){

            if(e.getX() < 50){
                x = Math.max(e.getX(), 0 + ball.dim.x);
            }
            else{
                x = Math.min(e.getX(), (50 * 30) - ball.dim.x);
            }

            if(e.getY() < 50){
                y = Math.max(e.getY(), 0 + ball.dim.y);
            }
            else{
                y = Math.min(e.getY(), (50 * 20) - ball.dim.y);
            }
        
            ball.spd.x = 0;
            ball.spd.y = 0;

            ball.pos.x = x;
            ball.pos.y = y;

        }
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

}
