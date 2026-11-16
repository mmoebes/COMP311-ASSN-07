package edu.unc.comp301.dungeon.model.pieces;

public class Hero extends APiece implements MovablePiece {
  public Hero() {
    super("Hero", "Photos/hero.png");
  }

  @Override
  public CollisionResult collide(Piece other) {
    if (other == null) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }
    if (other instanceof Treasure) {
      return new CollisionResult(((Treasure) other).getValue(), CollisionResult.Result.CONTINUE);
    }
    if (other instanceof Enemy) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }
    if (other instanceof Exit) {
      return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
    }
    // Wall or any unknown piece
    throw new IllegalArgumentException("Hero cannot collide with " + other.getName());
  }
}
