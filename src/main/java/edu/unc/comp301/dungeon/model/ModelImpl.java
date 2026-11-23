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
  private boolean hasHighScore;
  private int level;
  private STATUS status;
  private final List<Observer> observers;

  public ModelImpl(int width, int height) {
    this(new BoardImpl(width, height));
  }

  public ModelImpl(Board board) {
    this.board = board;
    this.curScore = 0;
    this.highScore = 0;
    this.hasHighScore = false;
    this.level = 0;
    this.status = STATUS.END_GAME;
    this.observers = new ArrayList<>();
  }

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

  @Override
  public void startGame() {
    status = STATUS.IN_PROGRESS;
    curScore = 0;
    level = 1;
    try {
      board.setHardMode(hardMode);
      board.init(level + 1, TREASURES, WALLS, FREEZES);
    } catch (IllegalArgumentException e) {
      endGame(); // board too small even for level 1
      return;
    }
    notifyObservers();
  }

  @Override
  public void endGame() {
    status = STATUS.END_GAME;
    if (!hasHighScore || curScore > highScore) {
      highScore = curScore;
      hasHighScore = true;
    }
    notifyObservers();
  }

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
      return;
    }

    CollisionResult res = board.moveHero(drow, dcol);
    curScore += res.getPoints();

    if (res.getResults() == CollisionResult.Result.GAME_OVER) {
      endGame();
      return;
    }

    if (res.getResults() == CollisionResult.Result.NEXT_LEVEL) {
      level++;
      try {
        board.init(level + 1, TREASURES, WALLS, FREEZES);
      } catch (IllegalArgumentException e) {
        endGame();
        return;
      }
    }

    notifyObservers();
  }

  @Override
  public void addObserver(Observer o) {
    observers.add(o);
  }

  private void notifyObservers() {
    for (Observer o : observers) {
      o.update();
    }
  }

  private static final int FREEZES = 1;   // one crystal per room
  private boolean hardMode = false;

  @Override
  public boolean isHardMode() {
    return hardMode;
  }

  @Override
  public void toggleHardMode() {
    if (status == STATUS.IN_PROGRESS) return;   // only changeable before a game
    hardMode = !hardMode;
    board.setHardMode(hardMode);
    notifyObservers();
  }

  @Override
  public int getFrozenTurns() {
    return board.getFrozenTurns();
  }
}
