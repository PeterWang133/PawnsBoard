package cs3500.pawnsboard.model;

/**
 * Represents a read-only version of the game model. Exposes only methods that allow viewing the
 * current state of the game, not modifying it.
 *
 * @param <C> the type of card used in the game
 */
public interface ReadonlyGame<C extends Card> extends ObservableModel {

  /**
   * Determine if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Returns the player whose turn it currently is.
   *
   * @return the current player
   */
  Player<C> getCurrentPlayer();

  /**
   * Returns the game board.
   *
   * @return the game board
   */
  Board<C> getBoard();

  /**
   * Get the hand size from the current player.
   *
   * @return an integer that indicates the size of the hand
   */
  int getHandSize();

  /**
   * Returns the index of the currently selected card in the hand, or -1 if none selected.
   *
   * @return the selected card index
   */
  int getSelectedCardIndex();

  /**
   * Set the index of the currently selected card.
   *
   * @param selectedCardIndex the index to set, or -1 to deselect
   */
  void setSelectedCardIndex(int selectedCardIndex);

  /**
   * Returns the coordinates of the currently selected board cell.
   *
   * @return an int array of size 2: [row, col]
   */
  int[] getSelectedCellCoordinate();

  /**
   * Sets the coordinates of the selected board cell.
   *
   * @param selectedRow the row index
   * @param selectedCol the column index
   */
  void setSelectedCellCoordinate(int selectedRow, int selectedCol);

  /**
   * Returns the role of the player who won the game. Returns null if the game is not over.
   *
   * @return the winning player's role, or null if game not over
   */
  Role getWonPlayer();

  Player<C> getPlayer(Role role);
}
