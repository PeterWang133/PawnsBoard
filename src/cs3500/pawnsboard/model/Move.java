package cs3500.pawnsboard.model;

/**
 * Represents a move in the Pawns Board game.
 *
 * <p>A Move consists of either:
 * <ul>
 *   <li>a normal move (selecting a card from the hand and a target cell on the board), or</li>
 *   <li>a pass move (indicating that the player cannot legally place any card).</li>
 * </ul>
 */
public final class Move {

  private final boolean isPass;
  private final int cardIndex;
  private final int row;
  private final int col;

  /**
   * Private constructor for a pass move.
   */
  private Move() {
    this.isPass = true;
    this.cardIndex = -1;
    this.row = -1;
    this.col = -1;
  }

  /**
   * Public constructor for a normal move. Creates a move that plays a card at the specified
   * position on the board.
   *
   * @param cardIndex the index of the card in the player's hand
   * @param row       the target row on the board where the card will be placed
   * @param col       the target column on the board where the card will be placed
   */
  public Move(int cardIndex, int row, int col) {
    this.cardIndex = cardIndex;
    this.row = row;
    this.col = col;
    this.isPass = false;
  }

  /**
   * Factory method for a pass move.
   *
   * @return a Move object that indicates a pass.
   */
  public static Move passMove() {
    return new Move();
  }

  /**
   * Checks if no move has been made, and passes to the opponent player.
   *
   * @return a boolean object that returns {@true} if passes to the opponent player
   */
  public boolean isPass() {
    return isPass;
  }

  /**
   * Get the card index in the Player's hand that has been chosen to place on the cell.
   *
   * @return an integer that indicates the index of the card from Player's hand
   */
  public int getCardIndex() {
    return cardIndex;
  }

  /**
   * Get the row that has been chosen to place the Card.
   *
   * @return an integer that indicates the row number to place the Card
   */
  public int getRow() {
    return row;
  }

  /**
   * Get the column that has been chosen to place the Card.
   *
   * @return an integer that indicates the column number.
   */
  public int getCol() {
    return col;
  }

  /**
   * Prints out a message about the move chosen by a specific strategy. Either: 1) pass to the
   * opponent, or 2) find a position on the cell that satisfies the strategy.
   *
   * @return A String that shows where should the Card be placed, or a pass
   */
  @Override
  public String toString() {
    if (isPass) {
      return "Pass move";
    }
    return "Move: card index " + cardIndex + " at (" + row + ", " + col + ")";
  }
}
