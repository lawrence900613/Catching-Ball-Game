package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import java.util.ArrayList;
import java.util.Random;

public class GameBoard extends JPanel implements MouseInputListener {
    Player dummyPlayer;
    ArrayList<Player> playerList = new ArrayList<>();
    long catchTime = 0;
    long releaseTime = 0;
    JLabel catchLabel; // pops up when a player grabs the ball
    JPanel leaderboardPanel;

    int speedchangecount = 0;
    boolean Draggingflag = false; // check whether circle is holding

    int gameState = 1;
    static final int GAMEPLAY = 1;

    Ball ball = new Ball();

    public GameBoard() {
        this.dummyPlayer = new Player("Dummy Player");
        playerList.add(dummyPlayer);
        catchLabel = new JLabel();
        catchLabel.setForeground(Color.white);
        add(catchLabel);

        setUpLeaderboard();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);
        Timer timer = new Timer(5, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (speedchangecount == 0 && !Draggingflag) {
                    ball.spd.x = Integer.signum(ball.spd.x) * 5;
                    ball.spd.y = Integer.signum(ball.spd.y) * 5;
                } else {
                    speedchangecount--;
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
        if (gameState == GAMEPLAY) {
            Graphics2D g2d = (Graphics2D) g;
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x,
                    2.0 * ball.dim.y);
            g2d.setColor(Color.white);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
            // g.setColor(Color.WHITE);
            // g.fillOval(ball.pos.x, ball.pos.y, ball.dim.x, ball.dim.y); // Oval Drawing
            // DEPRECATED
        }
    }

    public void setUpLeaderboard() {
        leaderboardPanel = new JPanel();
        leaderboardPanel.setBackground(Color.black);
        leaderboardPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        
        JLabel leaderboardTitle = new JLabel("Leaderboard");
        leaderboardTitle.setForeground(Color.white);
        leaderboardPanel.add(leaderboardTitle);

        Dimension size = leaderboardPanel.getPreferredSize();
        leaderboardPanel.setBounds(1250, 50, size.width, size.height);
        add(leaderboardPanel);
    }

    public void handleBallCatched() {
        catchTime = System.currentTimeMillis();
        catchLabel.setText(dummyPlayer.username + " has grabbed the ball!");
        Dimension size = catchLabel.getPreferredSize();
        catchLabel.setBounds(650, 100, size.width, size.height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (theCircle.contains(e.getX(), e.getY())) {
            handleBallCatched();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (theCircle.contains(e.getX(), e.getY())) {
            handleBallCatched();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Draggingflag) {
            speedchangecount = 10;
            Random ran = new Random();
            int x = ran.nextInt(20 + 20) - 20;
            int y = ran.nextInt(20 + 20) - 20;
            ball.spd.x = x;
            ball.spd.y = y;

            releaseTime = System.currentTimeMillis();
            dummyPlayer.score += (releaseTime - catchTime);
            System.out.println("Score = " + dummyPlayer.score);
            catchLabel.setText("");

            Draggingflag = false;
        }
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
        if (theCircle.contains(e.getX(), e.getY())) {

            if (e.getX() < 50) {
                x = Math.max(e.getX(), 0 + ball.dim.x);
            } else {
                x = Math.min(e.getX(), (50 * 30) - ball.dim.x);
            }

            if (e.getY() < 50) {
                y = Math.max(e.getY(), 0 + ball.dim.y);
            } else {
                y = Math.min(e.getY(), (50 * 20) - ball.dim.y);
            }

            ball.spd.x = Integer.signum(ball.spd.x) * 0;
            ball.spd.y = Integer.signum(ball.spd.y) * 0;
            Draggingflag = true;

            ball.pos.x = x;
            ball.pos.y = y;

        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

}
