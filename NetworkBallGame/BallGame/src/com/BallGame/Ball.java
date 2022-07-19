package com.BallGame;
import java.awt.*;
import java.awt.event.*;
import java.util.Formatter;
import javax.swing.*;
import java.util.Date;

public class Ball extends JPanel {
   private static final int BOX_WIDTH = 1000;
   private static final int BOX_HEIGHT = 800;
   private float ballRadius = 30; 
   private float ballX = ballRadius + 20; 
   private float ballY = ballRadius + 20; 
   private float ballSpeedX = 6;  
   private float ballSpeedY = 10;

   private static final int UPDATE_RATE = 30;
   long startTime = System.currentTimeMillis();
   long elapsedTime = 0L;

   public Ball() {
      this.setPreferredSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
      Thread gameThread = new Thread() {
         public void run() {
            while (elapsedTime < 1*60*1000) {
               ballX += ballSpeedX;
               ballY += ballSpeedY;
               if (ballX - ballRadius < 0) {
                  ballSpeedX = -ballSpeedX; 
                  ballX = ballRadius; 
               } else if (ballX + ballRadius > BOX_WIDTH) {
                  ballSpeedX = -ballSpeedX;
                  ballX = BOX_WIDTH - ballRadius;
               }
               if (ballY - ballRadius < 0) {
                  ballSpeedY = -ballSpeedY;
                  ballY = ballRadius;
               } else if (ballY + ballRadius > BOX_HEIGHT) {
                  ballSpeedY = -ballSpeedY;
                  ballY = BOX_HEIGHT - ballRadius;
               }
               repaint();
               try {
                  Thread.sleep(1000 / UPDATE_RATE);
               } catch (InterruptedException ex) { }
               elapsedTime = (new Date()).getTime() - startTime;
            }
         }
      };
      gameThread.start(); 
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);    // Paint background
  
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, BOX_WIDTH, BOX_HEIGHT);
  
      g.setColor(Color.BLUE);
      g.fillOval((int) (ballX - ballRadius), (int) (ballY - ballRadius),
            (int)(2 * ballRadius), (int)(2 * ballRadius));
  
      g.setColor(Color.WHITE);
      g.setFont(new Font("Courier New", Font.PLAIN, 12));
      StringBuilder sb = new StringBuilder();
      Formatter formatter = new Formatter(sb);
      formatter.format("Ball @(%3.0f,%3.0f) Speed=(%2.0f,%2.0f)", ballX, ballY,
            ballSpeedX, ballSpeedY);
      g.drawString(sb.toString(), 20, 30);
   }
   public static void main(String[] args) {
      // Run GUI in the Event Dispatcher Thread (EDT) instead of main thread.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            JFrame frame = new JFrame("A Bouncing Ball");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new Ball());
            frame.pack();
            frame.setVisible(true);
         }
      });
   }
}
