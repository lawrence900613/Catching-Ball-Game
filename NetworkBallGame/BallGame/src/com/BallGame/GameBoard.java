package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import java.awt.Robot;
import java.awt.AWTException;

public class GameBoard extends JPanel implements MouseInputListener {
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
    static final int MENU = 1;
    static final int GAMEPLAY = 2;

    Ball ball = new Ball();

    long startTime;

    long estimatedTime;

    public GameBoard() {
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
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.WHITE);
        startButton.setBounds(50 * 30 / 2 - 50, 50 * 20 / 2 - 25, 100, 50);
        this.add(startButton);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);

        Timer timer = new Timer(0, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startButton.addActionListener(this);
                if (e.getSource() == startButton) {
                    gameState = GAMEPLAY;
                    leaderboardPanel.setVisible(true);
                    startButton.setVisible(false);
                    startButton.setFocusable(false);
                }
                startTime = System.nanoTime();
                lockCheck();
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
    Shape startButton;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (gameState == GAMEPLAY) {
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x,
                    2.0 * ball.dim.y);
            g2d.setColor(ball.color);
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
        this.add(leaderboardPanel);
        if (gameState == MENU) {
            leaderboardPanel.setVisible(false);
        }

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
        releaseTime = System.currentTimeMillis() / 1000;

        // update player score
        dummyPlayer.score += (releaseTime - catchTime);

        // update leaderboard
        sortPlayers();
        renderScores();
    }

    public void handleBallCatched() {
        catchTime = System.currentTimeMillis() / 1000;
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
        if (theCircle.contains(e.getX(), e.getY())) {
            handleBallCatched();
            Draggingflag = true;
            ball.color = dummyPlayer.teamname;
            ball.lockStart = System.currentTimeMillis();
            startTime = System.nanoTime();
            estimatedTime = startTime + 100000 * 10000;
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

    @Override
    public void mouseDragged(MouseEvent e) {

        int x, y;
        // if (theCircle.contains(e.getX(), e.getY())) {
        if (Draggingflag) {

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
            ball.spd.x = Integer.signum(ball.spd.x) * 0;
            ball.spd.y = Integer.signum(ball.spd.y) * 0;
            ball.pos.x = x;
            ball.pos.y = y;

        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

}