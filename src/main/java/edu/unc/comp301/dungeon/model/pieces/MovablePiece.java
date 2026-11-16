package edu.unc.comp301.dungeon.model.pieces;

public interface MovablePiece extends Piece {
  CollisionResult collide(Piece other);
}
