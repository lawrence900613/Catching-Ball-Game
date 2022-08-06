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
import java.util.Date;
import java.util.List;

import com.BallGame.net.Handler;
import com.BallGame.net.network;

public class GameBoardServer extends JPanel {
    Player dummyPlayer;
    ArrayList<Player> playerList = new ArrayList<>();
    long catchTime = 0;
    long releaseTime = 0;
    JLabel catchLabel; // pops up when a player grabs the ball
    JPanel leaderboardPanel;
    JPanel scorePanel;

    int speedchangecount = 0;
    boolean Draggingflag = false; // check whether circle is holding

    int gameState = 1; // game is starting
    static final int GAMEPLAY = 1;
    static final int GAMEOVER = 2;

    Ball ball = new Ball(); // create ball object
    long startTime;
    long estimatedTime;
    int UIDholdball = 0; // The ID of client who is holding the ball. Default ID is 0 which represent no
                         // one is holding
    ArrayList<Socket> csockets = network.connectAsServer(3000);
    List<Integer> pipe = new ArrayList<Integer>();
    Handler Handle = new Handler(csockets, pipe); // create multithread to listen all the client message at the same
                                                  // time
    Timer timer;
    long gamestarttime; // the start time of the game
    long elapsedTime;
    /*
     * The GameBoardServer() is the server function which makes ball moving and
     * broadcast the new ball position to client.
     * It also see the client message in the pipe to change the holding Client
     * ID(who is holding the ball)and new ball position.
     * The function will stop running and sending packet to every client after one
     * minute and the game should be end at that time.
     */

    public GameBoardServer() throws Exception {
        Handle.startListen();

        catchLabel = new JLabel();
        catchLabel.setForeground(Color.white);
        add(catchLabel);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);

        gamestarttime = System.currentTimeMillis();

        timer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                while (!pipe.isEmpty()) {
                    int message = pipe.get(0); // message from the client
                    pipe.remove(0);
                    int[] change = network.decode(message);
                    UIDholdball = change[0];
                    if (change[1] == 1 || change[2] == 0) { // no one is dragging
                        Draggingflag = false;
                    }
                    if (change[2] == 1 || change[1] == 0) { // some1 is dragging
                        Draggingflag = true;
                    }
                    if ((change[3] != 4095)) { // it change the position(via dragging)
                        ball.pos.x = change[3]; // 4095 is the coord from mouse pressing and it shouldn't change the
                                                // ball postition in that case
                        ball.pos.y = change[4];
                        System.out.println("Receive drag x : " + change[3] +" y :" + change[4]);
                    }
                }
                if (!Draggingflag) { // if no one is dragging, ball move with constant speed
                    ball.move();
                    UIDholdball = 0;
                }
                ball.wallDetection();
                for (int i = 0; i < csockets.size(); i++) { // broadcast the ball new position and who is holding ball
                                                            // to client
                    try {
                        OutputStream os = csockets.get(i).getOutputStream();
                        os.write(ByteBuffer.allocate(4).putInt(
                                network.encode(UIDholdball, !Draggingflag, Draggingflag, ball.getX(), ball.getY()))
                                .array());
                    } catch (IOException x) {
                        System.out.print(x);
                    }
                }
                repaint();
                elapsedTime = (new Date()).getTime() - gamestarttime; // how long since we start the game
                if (elapsedTime >= 1 * 10 * 1000) { // if the game start over one minutes, stop the game
                    timer.stop();
                    gameState = GAMEOVER;
                }
            }

        });
        timer.start();
    }

    Shape theCircle;
    /*
     * paintComponet drawing the grpah in the server with the ball position
     */

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (gameState == GAMEPLAY) {
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x,
                    2.0 * ball.dim.y);
            g2d.setColor(ball.color);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
        }
        if (gameState == GAMEOVER) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Game Over", 50 * 30 / 2, 50 * 20 / 2);
        }
    }
}