package cs3500.pawnsboard.model;

import java.util.List;
import java.util.Map;

/**
 * A strategy that attempts to improve the player's row score.
 *
 * <p>For each row (examined from top to bottom), the strategy checks whether the current player's
 * row score is less than or equal to the opponent's. In such a row, it scans every cell,
 * considering only cells that are empty and that are either unowned or owned by the current player.
 * For each such cell, it tries each card in the player's hand. If placing a card would raise the
 * player's row score by adding the card's value to at least the opponent's score, then that move is
 * chosen. If no move in any such row meets this criterion, the strategy falls back to the
 * FillFirstStrategy.
 */
public class MaximizeRowScoreStrategy implements Strategy {

  /**
   * Determines the best move for the given player using the MaximizeRowScore strategy.
   *
   * @param game    the game state including the board and current player
   * @param forWhom the role of the player for whom the move is being chosen
   * @return a {@link Move} that attempts to improve row score, or a move determined by {@link
   * FillFirstStrategy} if no improving move is found
   */
  @Override
  public Move makeMove(ReadonlyGame<Card> game, Role forWhom) {
    Board<Card> board = game.getBoard(); // a copy of the board, not the actual board from the game
    Player<Card> currentPlayer = game.getCurrentPlayer().clone();
    List<Card> hand = currentPlayer.getHand();

    // Examine each row from top (row 0) to bottom.
    for (int row = 0; row < board.getHeight(); row++) {
      Map<Role, Integer> rowScores = board.getRowScores(row);
      int currentScore = rowScores.getOrDefault(forWhom, 0);
      int opponentScore = rowScores.getOrDefault(forWhom.getOpponent(), 0);

      // Consider rows where the current player is not already winning.
      if (currentScore <= opponentScore) {
        // For every cell in the row...
        for (int col = 0; col < board.getWidth(); col++) {
          if (board.getCellAt(row, col).getCard() == null &&
              (board.getCellAt(row, col).getOwner() == null ||
                  board.getCellAt(row, col).getOwner().equals(forWhom))) {
            // Try every card in the player's hand.
            for (int cardIdx = 0; cardIdx < hand.size(); cardIdx++) {
              Card card = hand.get(cardIdx);
              // Check if this cell has enough pawns to cover the card's cost.
              if (board.getCellAt(row, col).getPawns() >= card.getCost()) {
                // Calculate the potential new score for this row if this card is placed.
                int potentialScore = currentScore + card.getValue();
                if (potentialScore >= opponentScore) {
                  return new Move(cardIdx, row, col);
                }
              }
            }
          }
        }
      }
    }
    // Fallback: if no move improves a row score, use the FillFirstStrategy.
    return new FillFirstStrategy().makeMove(game, forWhom);
  }
}
