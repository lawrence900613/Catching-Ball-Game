package com.BallGame;

import javax.swing.JFrame;

public class GameWindow {

    public static void initWindow() {
        JFrame window = new JFrame("Ball Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GameBoard board = new GameBoard();

        window.add(board);
        window.addMouseMotionListener(board);
        window.addMouseListener(board);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setFocusable(true);
        window.setVisible(true);

    }

    public static void main(String[] args) {
        initWindow();
    }

}
