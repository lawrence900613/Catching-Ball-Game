package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.InputStream;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import com.BallGame.net.TestClient;
import com.BallGame.net.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import java.awt.Robot;
import java.awt.AWTException;

public class GameBoardClient extends JPanel implements MouseInputListener {
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

    TestClient client = new TestClient();

    boolean holdright = true; // it only change to false when the ball is holding by others 
    public GameBoardClient() {
        this.dummyPlayer = new Player("Dummy Player", Color.RED);
        Player janice = new Player("Janice", Color.BLUE);
        Player arthur = new Player("Arthur", Color.GREEN);
        janice.score = 1540;
        arthur.score = 250;
        playerList.add(dummyPlayer);
        playerList.add(janice);
        playerList.add(arthur);
        catchLabel = new JLabel();
        catchLabel.setForeground(Color.white);
        add(catchLabel);

        setUpLeaderboard();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);
        Timer timer = new Timer(0, new ActionListener() {
            int i = 0;
            public void actionPerformed(ActionEvent e) {
                startTime = System.nanoTime();
                lockCheck();
                // if (speedchangecount == 0 && !Draggingflag) {
                //     ball.spd.x = Integer.signum(ball.spd.x) * 5;
                //     ball.spd.y = Integer.signum(ball.spd.y) * 5;
                // } else {
                //     speedchangecount--;
                // }
                //receive position coords to move from server
                try {
                    int[] temp = client.listenForMsgs();
                    // InputStream is = client.socket.getInputStream();
                    // byte[] p = new byte[4];
                    // is.read(p, 0, 4);
                    // int[] message = network.decode(network.byteArrToInt(p));
                    //int color = temp[0]; //UID determines color 
                    
                    ball.pos.x = temp[3];
                    ball.pos.y = temp[4];
                    ball.setcolor(temp[0]);
                    if(temp[0] == 0 || temp[0] == client.getUID()){
                        holdright = true;
                    }else{
                        holdright = false;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                ball.wallDetection();
                repaint();
                i++;
            }

        });
        timer.start();
    }

    Shape theCircle;

    

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == GAMEPLAY) {
            Graphics2D g2d = (Graphics2D) g;
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x, 2.0 * ball.dim.y);
            
            g2d.setColor(ball.color);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
        }
    }

    public void setUpLeaderboard() {
        leaderboardPanel = new JPanel();
        leaderboardPanel.setBackground(Color.black);
        leaderboardPanel.setBorder(BorderFactory.createLineBorder(Color.white));
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));

        JLabel leaderboardTitle = new JLabel("Leaderboard");
        leaderboardTitle.setForeground(Color.white);
        leaderboardPanel.add(leaderboardTitle);

        // nested panel for scores
        scorePanel = new JPanel();
        scorePanel.setBackground(Color.black);
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

        sortPlayers();
        renderScores();
        leaderboardPanel.add(scorePanel);

        Dimension size = leaderboardPanel.getPreferredSize();
        leaderboardPanel.setBounds(1250, 50, size.width, size.height);
        add(leaderboardPanel);

    }

    public void renderScores() {
        scorePanel.removeAll();
        for (Player player : playerList) {
            JLabel scoreLabel = new JLabel(player.username + ": " + player.score);
            scoreLabel.setForeground(Color.white);
            scorePanel.add(scoreLabel);
        }

        Dimension size = leaderboardPanel.getPreferredSize();
        leaderboardPanel.setBounds(1250, 50, size.width, size.height);

    }

    public void sortPlayers() {
        // sort scores from highest to lowest
        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (p1.score == p2.score)
                    return 0;
                else if (p1.score > p2.score)
                    return -1;
                else
                    return 1;
            }
        });
    }

    public void updateScore() {
        catchLabel.setText("");
        releaseTime = System.currentTimeMillis();

        // update player score
        dummyPlayer.score += (releaseTime - catchTime);

        // update leaderboard
        sortPlayers();
        renderScores();
    }

    public void handleBallCatched() {
        catchTime = System.currentTimeMillis();
        catchLabel.setText(dummyPlayer.username + " has grabbed the ball!");
        Dimension size = catchLabel.getPreferredSize();
        catchLabel.setBounds(650, 100, size.width, size.height);
    }

    public void lockCheck() {
        if (Draggingflag && (startTime > estimatedTime)) {
            try {
                Robot bot = new Robot();
                bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } catch (AWTException e) {
                System.out.println("Error");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // if (theCircle.contains(e.getX(), e.getY())) {
        // handleBallCatched();
        // }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (theCircle.contains(x, y)&&holdright) {
            System.out.println("pressed x : " + ball.pos.x +" y :" + ball.pos.y);
            int msg = network.encode(client.getUID(), 0, 1, 4095, 4095);
            try {
                client.sendMsg(msg);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //send msg to server saying ball grabbed and get lock + which colour grabbed
            handleBallCatched();
            Draggingflag = true;
            //ball.color = dummyPlayer.teamname;
            ball.lockStart = System.currentTimeMillis();
            startTime = System.nanoTime();
            estimatedTime = startTime + 1000000 * 10000; //s * ns
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Draggingflag&&holdright) {
            speedchangecount = 10;
            Random ran = new Random();
            int x = ran.nextInt(50 + 50) - 50;
            int y = ran.nextInt(50 + 50) - 50;
            int msg = network.encode(client.getUID(), 1, 0,ball.pos.x + x, ball.pos.y + y);
            try {
                System.out.println("release x : " + ball.pos.x  +" y :" + ball.pos.y);
                client.sendMsg(msg);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //send msg to server saying ball released and new speed + change colour to neutral 
            updateScore();
            Draggingflag = false;
            ball.lockStart = -ball.lockDuration;
            ball.color = Color.WHITE;
        } else if (theCircle.contains(e.getX(), e.getY())) {
            updateScore();
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

    //int flag = 1;

    @Override
    public void mouseDragged(MouseEvent e) {

        int x, y;
        // if (theCircle.contains(e.getX(), e.getY())) {
        if (Draggingflag&&holdright) {
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
            startTime = System.nanoTime();
            System.out.println("Dragging x : " + ball.pos.x +" y :" + ball.pos.y);
                
            int msg = network.encode(client.getUID(), 0, 1, x, y);
            try {
                client.sendMsg(msg);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

}