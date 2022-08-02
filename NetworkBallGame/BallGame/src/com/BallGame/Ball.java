package com.BallGame;

import java.awt.Point;
import java.awt.Color;

public class Ball {

   Point pos;
   Point dim;
   Point spd;
   Color color;
   double lockDuration = 5000;
   double lockStart = -lockDuration;

   public Ball() {
      this.pos = new Point((50 * 30) / 2, (50 * 20) / 2);
      this.dim = new Point(50, 50);
      this.spd = new Point(0, 0);
      this.color = Color.WHITE;
   }

   public void move() {
      this.pos.x += spd.x;
      this.pos.y += spd.y;
   }

   public void wallDetection() {

      if (this.pos.x <= 0 + this.dim.x || this.pos.x >= (50 * 30) - this.dim.x) {
         spd.x *= -1;
      }
      if (this.pos.y <= 0 + this.dim.y || this.pos.y >= (50 * 20) - this.dim.y) {
         spd.y *= -1;
      }
   }

   public int getX() {
      return this.pos.x;
   }

   public int getY() {
      return this.pos.y;
   }

}
