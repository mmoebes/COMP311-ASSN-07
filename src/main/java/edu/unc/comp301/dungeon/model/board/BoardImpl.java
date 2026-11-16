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
    int needed = enemyCount + treasures + walls + 2; // +2 for hero and exit
    if (needed > getWidth() * getHeight()) {
      throw new IllegalArgumentException("Too many pieces for the board");
    }

    // Clear the board and tracking fields
    board = new Piece[getHeight()][getWidth()];
    enemies = new ArrayList<>();
    hero = null;

    // Build a list of every piece that needs to go on the board
    List<Piece> pieces = new ArrayList<>();
    pieces.add(new Hero());
    pieces.add(new Exit());
    for (int i = 0; i < enemyCount; i++) pieces.add(new Enemy());
    for (int i = 0; i < treasures; i++) pieces.add(new Treasure());
    for (int i = 0; i < walls; i++) pieces.add(new Wall());

    // Place each piece on a random empty spot
    for (Piece p : pieces) {
      int r, c;
      do {
        r = (int) (Math.random() * getHeight());
        c = (int) (Math.random() * getWidth());
      } while (board[r][c] != null);
      set(p, new Posn(r, c)); // set() also updates hero and enemies
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

    if (newRow < 0
        || newRow >= getHeight()
        || newCol < 0
        || newCol >= getWidth()
        || board[newRow][newCol] instanceof Wall) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    CollisionResult heroResult = hero.collide(board[newRow][newCol]);
    set(hero, new Posn(newRow, newCol));

    if (heroResult.getResults() != CollisionResult.Result.CONTINUE) {
      return heroResult;
    }

    int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (Enemy e : new ArrayList<>(enemies)) {

      List<int[]> options = new ArrayList<>();
      for (int[] d : dirs) {
        int r = e.getPosn().getRow() + d[0];
        int c = e.getPosn().getCol() + d[1];
        if (r >= 0 && r < getHeight() && c >= 0 && c < getWidth()) {
          Piece target = board[r][c];
          if (!(target instanceof Wall)
              && !(target instanceof Exit)
              && !(target instanceof Enemy)) {
            options.add(new int[] {r, c});
          }
        }
      }

      if (options.isEmpty()) continue;

      int[] choice = options.get((int) (Math.random() * options.size()));
      CollisionResult res = e.collide(board[choice[0]][choice[1]]);
      set(e, new Posn(choice[0], choice[1]));

      if (res.getResults() == CollisionResult.Result.GAME_OVER) {
        return new CollisionResult(heroResult.getPoints(), CollisionResult.Result.GAME_OVER);
      }
    }

    return new CollisionResult(heroResult.getPoints(), CollisionResult.Result.CONTINUE);
  }
}
