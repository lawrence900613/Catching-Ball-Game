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
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

import com.BallGame.net.TestClient;
import com.BallGame.net.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import java.awt.Robot;
import java.awt.AWTException;

import java.util.Date;

public class GameBoardClient extends JPanel implements MouseInputListener {
    Player player;
    HashMap<Integer, Player> playerList = new HashMap<Integer, Player>(); // tracks players and updates scores based on
                                                                          // ID
    ArrayList<Player> scoreList = new ArrayList<>(); // stores sorted scores
    long catchTime = 0;
    long releaseTime = 0; // score = catchTime - releaseTime
    JLabel catchLabel; // pops up when a player grabs the ball
    JPanel leaderboardPanel;
    JPanel scorePanel;

    int speedchangecount = 0;
    boolean Draggingflag = false; // check whether circle is holding

    int gameState = 1;
    static final int GAMEPLAY = 1;
    static final int GAMEOVER = 2;
    Ball ball = new Ball();
    int UIDholdball = 0;

    long startTime;

    long estimatedTime;

    Timer timer;
    long elapsedTime;
    long gamestarttime;

    TestClient client = new TestClient();

    boolean holdright = true; // it only change to false when the ball is holding by others

    /*
     * Essentially listens to the server and updates the ball position based on the
     * received message
     * All action will be constantly updated in the ActionListener(),
     * lockcheck() forces player to drop the ball when the allocated time has
     * execeded
     * holdright determines if a player can grab hold of the ball or not based on
     * the message received
     * From there, basic condition checking such as window border detection and
     * colour changing will be done
     */
    public GameBoardClient() {
        player = new Player(client.getUID());
        // Player janice = new Player(2);
        // Player arthur = new Player(3);
        // janice.score = 1540;
        // arthur.score = 250;
        playerList.put(client.getUID(), player);
        scoreList.add(player);
        // playerList.add(janice);
        // playerList.add(arthur);
        catchLabel = new JLabel();
        catchLabel.setForeground(Color.white);
        add(catchLabel);

        setUpLeaderboard();
        gamestarttime = System.currentTimeMillis();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);
        timer = new Timer(0, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startTime = System.nanoTime();
                lockCheck();
                try {
                    int[] temp = client.listenForMsgs();
                    UIDholdball = temp[0];

                    ball.pos.x = temp[3];
                    ball.pos.y = temp[4];
                    ball.setcolor(temp[0]);
                    if (temp[0] == 0 || temp[0] == client.getUID()) {
                        holdright = true;
                    } else {
                        holdright = false;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                ball.wallDetection();
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
     * PaintComponent redraws the Ball object and fills Colour to the (assigned
     * colour per user playing) or when ball is free to (White)
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (gameState == GAMEPLAY) {
            theCircle = new Ellipse2D.Double(ball.pos.x - ball.dim.x, ball.pos.y - ball.dim.y, 2.0 * ball.dim.x,
                    2.0 * ball.dim.y);
            g2d.setColor(Color.white);
            g2d.drawString("Time: " + (10 - elapsedTime / 1000), 50 * 30 / 2, 25);
            g2d.setColor(ball.color);
            g2d.fill(theCircle);
            g2d.draw(theCircle);
        }
        if (gameState == GAMEOVER) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Game Over", 50 * 30 / 2, 50 * 20 / 2);
        }
    }

    /*
     * Initialize the scoreboard object on our game window
     */
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

    /*
     * draws the scoreboard window
     */
    public void renderScores() {
        scorePanel.removeAll();
        for (Player player : scoreList) {
            JLabel scoreLabel = new JLabel(player.teamname + ": " + player.score);
            scoreLabel.setForeground(Color.white);
            scorePanel.add(scoreLabel);
        }

        Dimension size = leaderboardPanel.getPreferredSize();
        leaderboardPanel.setBounds(1250, 50, size.width, size.height);

    }

    /*
     * display players from highest to lowest
     */
    public void sortPlayers() {
        // sort scores from highest to lowest
        Collections.sort(scoreList, new Comparator<Player>() {
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

    /*
     * Update the score based on how long the user has grabbed on to the ball
     */
    public void updateScore() {
        System.out.println("Player holding: " + UIDholdball);
        catchLabel.setText("");
        releaseTime = System.currentTimeMillis();

        // if it's a new player, create a new player
        if (!playerList.containsKey(UIDholdball)) {
            System.out.println("new player");
            Player newPlayer = new Player(UIDholdball);
            playerList.put(UIDholdball, newPlayer);
            scoreList.add(newPlayer);
        }
        // update player score based on uid.
        // skip if uid is the server's id
        // if (UIDholdball != 0) {
        playerList.get(UIDholdball).score += (releaseTime - catchTime);
        // }

        // player.score += (releaseTime - catchTime);

        sortPlayers();
        renderScores();
    }

    /*
     * handleBallCatched() displays a message on window saying which player has
     * grabbed the ball
     */
    public void handleBallCatched() {
        catchTime = System.currentTimeMillis();
        catchLabel.setText(player.teamname + " has grabbed the ball!");
        Dimension size = catchLabel.getPreferredSize();
        catchLabel.setBounds(650, 100, size.width, size.height);
    }

    /*
     * lockCheck() forces player to drop the ball when the allocated time has
     * execeded
     * This ensures that player cannot indefinitely hold on to the shared object
     */
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

    /*
     * Starting here , these are the mouse listener functions from
     * swing.event.MouseInputListener,
     * We use these to determine which player is currently holding on the ball and
     * when they have released it.
     * Therefore, whenever these actions are done, we will send a messsage to the
     * server letting them know which client has grabbed the ball.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /*
     * As soon as the mouse is pressed, the client will inform server of who the
     * holder is and therefore locking the lock for other clients.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (theCircle.contains(x, y) && holdright) {
            // System.out.println("pressed x : " + ball.pos.x +" y :" + ball.pos.y);
            int msg = network.encode(client.getUID(), 0, 1, 4095, 4095);
            try {
                client.sendMsg(msg);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // send msg to server saying ball grabbed and get lock + which colour grabbed
            handleBallCatched();
            Draggingflag = true;
            // ball.color = player.teamcolor;
            ball.lockStart = System.currentTimeMillis();
            startTime = System.nanoTime();
            estimatedTime = startTime + 1000000 * 10000; // s * ns
        }
    }

    /*
     * When user has released mouse, we let server know that the ball is free to be
     * held again.
     * Once ball is realeased, we also update the score based on how long the user
     * has held on to the ball
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (Draggingflag && holdright) {
            speedchangecount = 10;
            Random ran = new Random();
            int x = ran.nextInt(50 + 50) - 50;
            int y = ran.nextInt(50 + 50) - 50;
            int msg = network.encode(client.getUID(), 1, 0, ball.pos.x + x, ball.pos.y + y);
            try {
                // System.out.println("release x : " + ball.pos.x +" y :" + ball.pos.y);
                client.sendMsg(msg);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // send msg to server saying ball released and new speed + change colour to
            // neutral
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

    /*
     * When mouse is dragged, we will constantly update the server on where the
     * mouse is dragged on the client's window
     * Server will then propagate this message to every other client to mimic the
     * movement on other's screen.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        int x, y;
        // if (theCircle.contains(e.getX(), e.getY())) {
        if (Draggingflag && holdright) {
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
            // System.out.println("Dragging x : " + ball.pos.x +" y :" + ball.pos.y);

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