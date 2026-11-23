package edu.unc.comp301.dungeon.model.pieces;

public class Treasure extends APiece {

  public Treasure() {
    super("Treasure", "images/treasure.png");
  }

  public int getValue() {
    return -1;
  }
}
