package com.BallGame;
import java.awt.Color;

public class Team {
    String name;
    Color color;
    long score;

    public Team(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }
}
