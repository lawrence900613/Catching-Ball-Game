package com.BallGame;

import java.awt.Color;

public class Player {

    String teamname;
    Color teamcolor;
    long score;

    public Player(String teamname, Color teamcolor) {
        this.teamname = teamname;
        this.teamcolor = teamcolor;
        this.score = 0;
    }

}
