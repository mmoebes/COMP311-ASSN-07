package edu.unc.comp301.dungeon.model.pieces;

public class Enemy extends APiece implements MovablePiece {

  public Enemy() {
    super("Enemy", "Photos/enemy.jpg");
  }

  public CollisionResult collide(Piece other) {
    if (other == null) {
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    if( other instanceof Treasure){
      return new CollisionResult(0, CollisionResult.Result.CONTINUE);
    }

    if(other instanceof Hero){
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }

    return new CollisionResult(0, CollisionResult.Result.CONTINUE);
  }
}
