package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;


import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.OutputStream;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.awt.Graphics2D;
import javax.swing.Timer;


import java.util.ArrayList;
import java.util.List;



import com.BallGame.net.Handler;
import com.BallGame.net.network;

public class GameBoardServer extends JPanel{
    Player dummyPlayer;
    ArrayList<Player> playerList = new ArrayList<>();
    long catchTime = 0;
    long releaseTime = 0;
    JLabel catchLabel; // pops up when a player grabs the ball
    JPanel leaderboardPanel;
    JPanel scorePanel;

    int speedchangecount = 0;
    boolean Draggingflag = false; // check whether circle is holding

    int gameState = 1;
    static final int GAMEPLAY = 1;

    Ball ball = new Ball();

    long startTime;

    long estimatedTime;

    int UIDholdball = 0;
    ArrayList<Socket> csockets = network.connectAsServer(3000);
    List<Integer> pipe = new ArrayList<Integer>();
    Handler Handle  = new Handler(csockets, pipe);
    public GameBoardServer() throws Exception {         
        Handle.startListen();

        this.dummyPlayer = new Player("Dummy Player", Color.RED);
        Player janice = new Player("Janice", Color.BLUE);
        Player arthur = new Player("Arthur", Color.GREEN);
        catchLabel = new JLabel();
        catchLabel.setForeground(Color.white);
        add(catchLabel);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);

        Timer timer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while(!pipe.isEmpty()){
                    int temp = pipe.get(0);
                    pipe.remove(0);
                    int[] change = network.decode(temp);
                    UIDholdball = change[0];
                    if(change[1] == 1 ||change[2] == 0 ){ //no one is dragging
                        Draggingflag = false;
                    }
                    if(change[2] == 1 || change[1] == 0){ //some1 is dragging
                        Draggingflag = true;
                    }
                    if((change[3] != 4095)){ // it is for mouse press, it shouldn't change the location
                        ball.pos.x = change[3];
                        ball.pos.y = change[4];
                        System.out.println("Receive drag x : " + change[3] +" y :" + change[4]);
                    }
                    /// it change the position(via dragging) and who is holding the ball
                }
                if(!Draggingflag){ 
                    ball.move();
                    UIDholdball = 0; 
                }
                ball.wallDetection();
                for(int i = 0 ; i<csockets.size(); i++){
                    try {
                        OutputStream os = csockets.get(i).getOutputStream();
                        os.write(ByteBuffer.allocate(4).putInt(network.encode(UIDholdball,!Draggingflag,Draggingflag ,ball.getX(),ball.getY())).array());
                    } catch (IOException x) {
                        //System.out.print(x);
                    }
                }
                // it keep sending the ball posistion to every client
                repaint();
            }

        });
        timer.start();
    }

    Shape theCircle;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == GAMEPLAY) {
            Graphics2D g2d = (Graphics2D) g;
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x,
                    2.0 * ball.dim.y);
            g2d.setColor(ball.color);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
        }
    }
}