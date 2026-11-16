package edu.unc.comp301.dungeon.model.pieces;

import edu.unc.comp301.dungeon.model.board.Posn;

public interface Piece {
  String getName();


  Posn getPosn();

  void setPosn(Posn posn);

  String getResourcePath();
}
