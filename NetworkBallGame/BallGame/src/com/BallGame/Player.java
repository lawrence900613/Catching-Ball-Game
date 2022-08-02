package com.BallGame;

import java.awt.Color;

public class Player {

    String username;
    Color teamname;
    long score;

    public Player(String username, Color teamname) {
        this.username = username;
        this.teamname = teamname;
        this.score = 0;
    }

}
