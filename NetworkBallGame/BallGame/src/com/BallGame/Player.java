package com.BallGame;

import java.awt.Color;

public class Player {

    String teamname;
    Color teamcolor;
    long score;

    public Player(int uid) {
        setcolor(uid);
        this.score = 0;
    }

    private void setcolor(int in){
        switch(in) {
        case 1:
           teamcolor = Color.blue;
           teamname = "BLUE";
           break;
        case 2:
            teamcolor = Color.cyan;
            teamname = "CYAN";
           break;
        case 3:
            teamcolor = Color.green;
            teamname = "GREEN";
           break;
        case 4:
           teamcolor = Color.magenta;
           teamname = "MAGENTA";
           break;
        case 5:
           teamcolor = Color.orange;
           teamname = "ORANGE";
           break;
        case 6:
           teamcolor = Color.pink;
           teamname = "PINK";
           break;
        case 7:
           teamcolor = Color.red;
           teamname = "RED";
           break;
        case 8:
           teamcolor = Color.yellow;
           teamname = "YELLOW";
           break;
        case 9:
           teamcolor = Color.gray;
           teamname = "GRAY";
           break;
        case 0:
           teamcolor = Color.white;
           teamname = "WHITE";
           break;
        }
  }

}
