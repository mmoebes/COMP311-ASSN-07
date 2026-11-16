package edu.unc.comp301.dungeon.model;

import edu.unc.comp301.dungeon.model.board.Board;
import edu.unc.comp301.dungeon.model.board.BoardImpl;
import edu.unc.comp301.dungeon.model.board.Posn;
import edu.unc.comp301.dungeon.model.pieces.CollisionResult;
import edu.unc.comp301.dungeon.model.pieces.Piece;
import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {

  private static final int TREASURES = 3; // must be at least 2
  private static final int WALLS = 3; // must be at least 2

  private final Board board;
  private int curScore;
  private int highScore;
  private int level;
  private STATUS status;
  private final List<Observer> observers;

  // ---------- Constructors ----------

  public ModelImpl(int width, int height) {
    this(new BoardImpl(width, height));
  }

  public ModelImpl(Board board) {
    this.board = board;
    this.curScore = 0;
    this.highScore = 0;
    this.level = 0;
    this.status = STATUS.END_GAME;
    this.observers = new ArrayList<>();
  }

  // ---------- Getters ----------

  @Override
  public int getWidth() {
    return board.getWidth();
  }

  @Override
  public int getHeight() {
    return board.getHeight();
  }

  @Override
  public Piece get(Posn p) {
    return board.get(p);
  }

  @Override
  public int getCurScore() {
    return curScore;
  }

  @Override
  public int getHighScore() {
    return highScore;
  }

  @Override
  public int getLevel() {
    return level;
  }

  @Override
  public STATUS getStatus() {
    return status;
  }

  // ---------- Status methods ----------

  @Override
  public void startGame() {
    status = STATUS.IN_PROGRESS;
    curScore = 0;
    level = 1;
    try {
      board.init(level + 1, TREASURES, WALLS);
    } catch (IllegalArgumentException e) {
      endGame(); // board too small even for level 1
      return;
    }
    notifyObservers();
  }

  @Override
  public void endGame() {
    status = STATUS.END_GAME;
    if (curScore > highScore) {
      highScore = curScore;
    }
    notifyObservers();
  }

  // ---------- Movement ----------

  @Override
  public void moveUp() {
    move(-1, 0);
  }

  @Override
  public void moveDown() {
    move(1, 0);
  }

  @Override
  public void moveLeft() {
    move(0, -1);
  }

  @Override
  public void moveRight() {
    move(0, 1);
  }

  private void move(int drow, int dcol) {
    if (status != STATUS.IN_PROGRESS) {
      return; // ignore input when no game is running
    }

    CollisionResult res = board.moveHero(drow, dcol);
    curScore += res.getPoints();

    if (res.getResults() == CollisionResult.Result.GAME_OVER) {
      endGame(); // endGame() notifies observers
      return;
    }

    if (res.getResults() == CollisionResult.Result.NEXT_LEVEL) {
      level++;
      try {
        board.init(level + 1, TREASURES, WALLS);
      } catch (IllegalArgumentException e) {
        endGame(); // board is full: even a perfect game ends here
        return;
      }
    }

    notifyObservers();
  }

  // ---------- Observer pattern ----------

  @Override
  public void addObserver(Observer o) {
    observers.add(o);
  }

  private void notifyObservers() {
    for (Observer o : observers) {
      o.update();
    }
  }
}
