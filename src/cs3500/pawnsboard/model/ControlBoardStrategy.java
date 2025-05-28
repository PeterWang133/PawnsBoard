package cs3500.pawnsboard.model;

import java.io.IOException;
import java.util.List;

/**
 * A strategy that chooses the move that yields control of the most cells.
 *
 * <p>For every legal move (cell is empty, has enough pawns for the card’s cost, and is either
 * unowned or owned by the current player), the strategy clones the board, simulates placing the
 * card (using a DummyPlayer), and counts the number of cells owned by the current player. In the
 * event of a tie, the move with the uppermost-leftmost cell is chosen; if still tied, the leftmost
 * card is selected.
 */
public class ControlBoardStrategy implements Strategy {

  @Override
  public Move makeMove(ReadonlyGame<Card> game, Role forWhom) {
    Player<Card> currentPlayer = game.getCurrentPlayer();
    List<Card> hand = currentPlayer.getHand();
    Board<Card> board = game.getBoard();

    Move bestMove = null;
    int bestCount = -1;

    // Iterate over each card in the player's hand.
    for (int cardIdx = 0; cardIdx < hand.size(); cardIdx++) {
      Card card = hand.get(cardIdx);
      for (int row = 0; row < board.getHeight(); row++) {
        for (int col = 0; col < board.getWidth(); col++) {
          // Check if the move is legal.
          // Legal if:
          //   No card is already placed.
          //   The cell is either unowned or owned by the current player.
          //  　The cell has enough pawns to cover the card's cost.
          if (board.getCellAt(row, col).getCard() == null
              && (board.getCellAt(row, col).getOwner() == null
              || board.getCellAt(row, col).getOwner().equals(forWhom))
              && board.getCellAt(row, col).getPawns() >= card.getCost()) {
            int count = simulateMoveOwnership(board, card, row, col, forWhom);
            // Tie-breaking: higher count wins; if equal, choose move with lower row,
            // then lower col, then lower card index.
            if (count > bestCount || (count == bestCount && bestMove != null
                && (row < bestMove.getRow()
                || (row == bestMove.getRow() && col < bestMove.getCol())
                || (row == bestMove.getRow() && col == bestMove.getCol()
                && cardIdx < bestMove.getCardIndex())))) {
              bestCount = count;
              bestMove = new Move(cardIdx, row, col);
            }
          }
        }
      }
    }

    return (bestMove != null) ? bestMove : Move.passMove();
  }

  /**
   * Simulates placing the given card at (row, col) on a cloned board and returns the number of
   * cells that become owned by the given role.
   *
   * @param board the current board
   * @param card  the card to place
   * @param row   the target row
   * @param col   the target column
   * @param role  the role (player) for whom the move is simulated
   * @return the count of cells owned by the role after the move, or -1 if simulation fails
   */
  private int simulateMoveOwnership(Board<Card> board, Card card, int row, int col, Role role) {
    Board<Card> boardClone = board.clone();
    // Use a dummy player that has the card at index 0.
    StrategyUtils.DummyPlayer dummy = new StrategyUtils.DummyPlayer(role, card);
    try {
      boardClone.placeCard(dummy, 0, row, col);
    } catch (IOException | IllegalArgumentException e) {
      return -1;
    }
    // Count cells owned by the role.
    int count = 0;
    for (int r = 0; r < boardClone.getHeight(); r++) {
      for (int c = 0; c < boardClone.getWidth(); c++) {
        Cell<Card> cell = boardClone.getCellAt(r, c);
        if (cell.getOwner() != null && cell.getOwner().equals(role)) {
          count++;
        }
      }
    }
    return count;
  }
}
