package com.BallGame;

import javax.swing.JFrame;

/*
 * Starts the game window for server to mimic where the ball is moving
 */
public class GameWindowServer {

    public static void initWindow() throws Exception {
        JFrame window = new JFrame("Ball Game Server");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameBoardServer board = new GameBoardServer();
        window.add(board);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setFocusable(true);
        window.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        initWindow();
    }
}
