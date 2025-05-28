package cs3500.pawnsboard.model;

import java.util.List;

/**
 * A strategy implementation that selects the first legal move found while scanning the board from
 * top-left to bottom-right.
 */
public class FillFirstStrategy implements Strategy {

  /**
   * Computes the next move for the specified player using the Fill-First strategy. The strategy
   * places the first card it can legally play in the first valid cell encountered in a row-major
   * order scan (top-to-bottom, left-to-right).
   *
   * @param game    the current game state, including board and players
   * @param forWhom the role of the player for whom the move is being computed
   * @return a {@link Move} representing the chosen move, or a {@link Move#passMove()} if no legal
   * move exists
   */
  @Override
  public Move makeMove(ReadonlyGame<Card> game, Role forWhom) {

    // Get current player and board from the game model.
    Board<Card> board = game.getBoard(); // a copy of the board, not the actual board from the game
    Player<Card> currentPlayer = game.getCurrentPlayer().clone();
    List<Card> hand = currentPlayer.getHand();

    // Iterate over each card in the player's hand.
    for (int cardIdx = 0; cardIdx < hand.size(); cardIdx++) {
      Card card = hand.get(cardIdx);
      // Iterate over the board cells row-by-row (top down) and left-to-right.
      for (int row = 0; row < board.getHeight(); row++) {
        for (int col = 0; col < board.getWidth(); col++) {
          Cell<Card> cell = board.getCellAt(row, col);

          // Legal move: cell is empty and has enough pawns to cover the card's cost,
          // and the cell is either unowned or owned by the player.
          boolean emptyOwner = cell.getOwner() == null;
          boolean sameOwner = false;
          if (!emptyOwner) {
            sameOwner = cell.getOwner().equals(currentPlayer.getRole());
          }
          boolean canPlace = sameOwner || emptyOwner;

          if (cell.getCard() == null && cell.getPawns() >= card.getCost() && canPlace) {
            return new Move(cardIdx, row, col);
          }
        }
      }
    }

    // If no legal move is found, return a pass move, which passes the turn to the opponent.
    return Move.passMove();
  }
}
