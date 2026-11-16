package edu.unc.comp301.dungeon.model.pieces;

public class Hero extends APiece implements MovablePiece {
  public Hero() {
    super("Hero", "Photos/hero.png");
  }

  public CollisionResult collide(Piece other) {
    if (other == null) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    if (other instanceof Exit) {
      return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
    }

    if (other instanceof Treasure) {
      return new CollisionResult(100, CollisionResult.Result.CONTINUE);
    }

    if (other instanceof Enemy) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }

    return new CollisionResult(0, CollisionResult.Result.CONTINUE);
  }
}
