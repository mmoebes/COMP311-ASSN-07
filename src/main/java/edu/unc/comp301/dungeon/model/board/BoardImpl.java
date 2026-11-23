package edu.unc.comp301.dungeon.model.board;

import edu.unc.comp301.dungeon.model.pieces.*;
import java.util.ArrayList;
import java.util.List;

public class BoardImpl implements Board {
  private Piece[][] board;
  private Hero hero;
  private List<Enemy> enemies;

  public BoardImpl(int width, int height) {
    this.board = new Piece[height][width]; // rows = height, cols = width
    this.enemies = new ArrayList<>();
    this.hero = null;
  }

  public BoardImpl(Piece[][] board) {
    this.board = board;
    this.enemies = new ArrayList<>();
    // Find the hero and enemies already on the given board
    for (int r = 0; r < board.length; r++) {
      for (int c = 0; c < board[r].length; c++) {
        Piece p = board[r][c];
        if (p != null) {
          p.setPosn(new Posn(r, c));
          if (p instanceof Hero) hero = (Hero) p;
          if (p instanceof Enemy) enemies.add((Enemy) p);
        }
      }
    }
  }

  @Override
  public void init(int enemyCount, int treasures, int walls) {
    init(enemyCount, treasures, walls, 0);
  }

  @Override
  public void init(int enemyCount, int treasures, int walls, int freezes) {
    int needed = enemyCount + treasures + walls + freezes + 2;
    if (needed > getWidth() * getHeight()) {
      throw new IllegalArgumentException("Too many pieces for the board");
    }

    board = new Piece[getHeight()][getWidth()];
    enemies = new ArrayList<>();
    hero = null;
    frozenTurns = 0;

    List<Piece> pieces = new ArrayList<>();
    pieces.add(new Hero());
    pieces.add(new Exit());
    for (int i = 0; i < enemyCount; i++) pieces.add(new Enemy());
    for (int i = 0; i < treasures; i++) pieces.add(new Treasure());
    for (int i = 0; i < walls; i++) pieces.add(new Wall());
    for (int i = 0; i < freezes; i++) pieces.add(new Freeze());

    for (Piece p : pieces) {
      int r, c;
      do {
        r = (int) (Math.random() * getHeight());
        c = (int) (Math.random() * getWidth());
      } while (board[r][c] != null);
      set(p, new Posn(r, c));
    }
  }
  @Override
  public int getWidth() {
    return board[0].length;
  }

  @Override
  public int getHeight() {
    return board.length;
  }

  @Override
  public Piece get(Posn posn) {
    return board[posn.getRow()][posn.getCol()];
  }

  @Override
  public void set(Piece p, Posn newPos) {
    int r = newPos.getRow();
    int c = newPos.getCol();

    // If something is being overwritten, stop tracking it
    Piece old = board[r][c];
    if (old instanceof Enemy) enemies.remove(old);
    if (old == hero) hero = null;

    // If p is already on the board somewhere else, remove that old reference
    if (p != null && p.getPosn() != null) {
      Posn prev = p.getPosn();
      if (board[prev.getRow()][prev.getCol()] == p) {
        board[prev.getRow()][prev.getCol()] = null;
      }
    }

    board[r][c] = p;

    if (p != null) {
      p.setPosn(newPos);
      if (p instanceof Hero) hero = (Hero) p;
      if (p instanceof Enemy && !enemies.contains(p)) enemies.add((Enemy) p);
    }
  }

  @Override
  public CollisionResult moveHero(int drow, int dcol) {
    if (hero == null) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }

    int newRow = hero.getPosn().getRow() + drow;
    int newCol = hero.getPosn().getCol() + dcol;

    // Illegal move: off the board or into a wall. Nothing moves.
    if (newRow < 0 || newRow >= getHeight() || newCol < 0 || newCol >= getWidth()
            || board[newRow][newCol] instanceof Wall) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    Piece target = board[newRow][newCol];
    boolean grabbedFreeze = target instanceof Freeze;

    CollisionResult heroResult = hero.collide(target);
    set(hero, new Posn(newRow, newCol));

    if (grabbedFreeze) {
      frozenTurns = FREEZE_TURNS;
    }

    // Exit or death ends the turn immediately. Enemies don't move.
    if (heroResult.getResults() != CollisionResult.Result.CONTINUE) {
      return heroResult;
    }

    // Enemies are frozen: they skip this turn
    if (frozenTurns > 0) {
      frozenTurns--;
      return new CollisionResult(heroResult.getPoints(), CollisionResult.Result.CONTINUE);
    }

    int[][] dirs = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

    for (Enemy e : new ArrayList<>(enemies)) {
      List<int[]> options = new ArrayList<>();
      for (int[] d : dirs) {
        int r = e.getPosn().getRow() + d[0];
        int c = e.getPosn().getCol() + d[1];
        if (r >= 0 && r < getHeight() && c >= 0 && c < getWidth()) {
          Piece t = board[r][c];
          if (!(t instanceof Wall)
                  && !(t instanceof Exit)
                  && !(t instanceof Enemy)
                  && !(t instanceof Freeze)) {
            options.add(new int[] {r, c});
          }
        }
      }

      if (options.isEmpty()) continue;   // trapped: skip turn

      int[] choice;
      if (hardMode) {
        // Pick the move that gets closest to the hero (random among ties)
        int hr = hero.getPosn().getRow();
        int hc = hero.getPosn().getCol();
        List<int[]> best = new ArrayList<>();
        int bestDist = Integer.MAX_VALUE;
        for (int[] o : options) {
          int dist = Math.abs(o[0] - hr) + Math.abs(o[1] - hc);
          if (dist < bestDist) {
            bestDist = dist;
            best.clear();
            best.add(o);
          } else if (dist == bestDist) {
            best.add(o);
          }
        }
        choice = best.get((int) (Math.random() * best.size()));
      } else {
        choice = options.get((int) (Math.random() * options.size()));
      }

      CollisionResult res = e.collide(board[choice[0]][choice[1]]);
      set(e, new Posn(choice[0], choice[1]));

      if (res.getResults() == CollisionResult.Result.GAME_OVER) {
        return new CollisionResult(heroResult.getPoints(), CollisionResult.Result.GAME_OVER);
      }
    }

    return new CollisionResult(heroResult.getPoints(), CollisionResult.Result.CONTINUE);
  }

  private static final int FREEZE_TURNS = 3;
  private boolean hardMode = false;
  private int frozenTurns = 0;

  @Override
  public void setHardMode(boolean hard) {
    this.hardMode = hard;
  }

  @Override
  public int getFrozenTurns() {
    return frozenTurns;
  }

}
