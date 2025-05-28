package cs3500.pawnsboard.controller;

/**
 * Represents the actions a player can take during their turn, which the view will publish and the
 * controller will listen for.
 */
public interface PlayerActionListener {

  /**
   * Called when the player selects a card to play.
   *
   * @param index the index of the selected card in the player's hand
   */
  void onCardSelected(int index);

  /**
   * Called when the player selects a cell on the board.
   *
   * @param row the row of the selected cell
   * @param col the column of the selected cell
   */
  void onCellSelected(int row, int col);

  /**
   * Called when the player confirms their move.
   */
  void onConfirmMove();

  /**
   * Called when the player passes their turn.
   */
  void onPassTurn();
}

