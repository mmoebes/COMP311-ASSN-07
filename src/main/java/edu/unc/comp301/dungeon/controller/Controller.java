package edu.unc.comp301.dungeon.controller;

public interface Controller {
  void moveUp();

  void moveDown();

  void moveLeft();

  void moveRight();

  void startGame();

  default void toggleHardMode() {}
}
