package com.BallGame;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GameBoard extends JPanel {

    int gameState = 1;
    static final int GAMEPLAY = 1;

    Ball ball = new Ball();

    public GameBoard() {
        setPreferredSize(new Dimension(50 * 30, 50 * 20));
        setLayout(null);
        setBackground(Color.BLACK);
        Timer timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ball.move();
                ball.wallDetection();
                repaint();
            }
        });
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == GAMEPLAY) {
            g.setColor(Color.WHITE);
            g.fillOval(ball.pos.x, ball.pos.y, ball.dim.x, ball.dim.y); // Oval Drawing
        }
    }

}
