package com.BallGame;

import javax.swing.JFrame;

/*
 * Starts the game window for client 
 */
public class GameWindowClient {

    public static void initWindow() throws Exception {
        JFrame window = new JFrame("Ball Game Client");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameBoardClient board = new GameBoardClient();
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