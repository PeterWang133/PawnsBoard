package cs3500.pawnsboard.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A strategy that selects the move which minimizes the opponent's best possible response.
 *
 * <p>This strategy considers every legal move for the current player, simulates it, and
 * evaluates how the opponent could respond using a dummy card. The move that results in the
 * smallest worst-case score difference (opponentScore - myScore) is selected.
 *
 * <p>If multiple moves result in the same worst-case score difference, a tiebreaker is used:
 * uppermost row, then leftmost column, then lowest card index.
 *
 * <p>If no legal move is possible, a pass move is returned.
 */
public class MinimaxStrategy implements Strategy {

  /**
   * Selects a move using the minimax strategy. It simulates all possible legal placements and
   * evaluates each move by assuming the opponent will respond optimally using a simple dummy card.
   *
   * @param game    the current game state
   * @param forWhom the player (role) for whom the move is being selected
   * @return the best move that minimizes the opponent's best response, or a pass move if none
   */
  @Override
  public Move makeMove(ReadonlyGame<Card> game, Role forWhom) {
    Player<Card> currentPlayer = game.getCurrentPlayer();
    List<Card> hand = currentPlayer.getHand();
    Board<Card> board = game.getBoard();

    Move bestMove = null;
    int bestWorstOpponentScoreDiff = Integer.MAX_VALUE;

    // Try all legal placements for each card in hand
    for (int cardIdx = 0; cardIdx < hand.size(); cardIdx++) {
      Card card = hand.get(cardIdx);
      for (int row = 0; row < board.getHeight(); row++) {
        for (int col = 0; col < board.getWidth(); col++) {
          if (isLegalPlacement(board, forWhom, card, row, col)) {
            Board<Card> boardAfterMyMove = board.clone();
            StrategyUtils.DummyPlayer me = new StrategyUtils.DummyPlayer(forWhom, card);
            try {
              boardAfterMyMove.placeCard(me, 0, row, col);
            } catch (IOException | IllegalArgumentException e) {
              continue;
            }

            int worstCaseForMe = simulateOpponentBestResponse(boardAfterMyMove,
                forWhom.getOpponent());

            // Update best move if this one results in a lower worst-case score difference
            if (worstCaseForMe < bestWorstOpponentScoreDiff ||
                (worstCaseForMe == bestWorstOpponentScoreDiff &&
                    isTiebreakerBetter(bestMove, cardIdx, row, col))) {
              bestWorstOpponentScoreDiff = worstCaseForMe;
              bestMove = new Move(cardIdx, row, col);
            }
          }
        }
      }
    }
    return (bestMove != null) ? bestMove : Move.passMove();
  }

  /**
   * Determines whether a placement is legal based on ownership and pawn cost.
   *
   * @param board the game board
   * @param role  the role attempting the move
   * @param card  the card to place
   * @param row   the row to place in
   * @param col   the column to place in
   * @return true if the move is legal, false otherwise
   */
  private boolean isLegalPlacement(Board<Card> board, Role role, Card card, int row, int col) {
    Cell<Card> cell = board.getCellAt(row, col);
    return cell.getCard() == null &&
        (cell.getOwner() == null || cell.getOwner().equals(role)) &&
        cell.getPawns() >= card.getCost();
  }

  /**
   * Simulates the opponent's best move on the given board using a dummy 1-cost, 1-value card.
   *
   * @param board    the board after the current player's move
   * @param opponent the role of the opponent
   * @return the score difference (opponent - current player) after the opponent's best move
   */
  private int simulateOpponentBestResponse(Board<Card> board, Role opponent) {
    final char[][] dummyInfluence = {
        {'X', 'X', 'X', 'X', 'X'},
        {'X', 'X', 'X', 'X', 'X'},
        {'X', 'X', 'C', 'X', 'X'},
        {'X', 'X', 'X', 'X', 'X'},
        {'X', 'X', 'X', 'X', 'X'}
    };
    Card dummyOppCard = new QueensBloodCard("DummyOpp", opponent, 1, 1,
        dummyInfluence);

    int maxOpponentScoreDiff = Integer.MIN_VALUE;

    for (int row = 0; row < board.getHeight(); row++) {
      for (int col = 0; col < board.getWidth(); col++) {
        if (isLegalPlacement(board, opponent, dummyOppCard, row, col)) {
          Board<Card> cloned = board.clone();
          StrategyUtils.DummyPlayer dummy = new StrategyUtils.DummyPlayer(opponent, dummyOppCard);
          try {
            cloned.placeCard(dummy, 0, row, col);
            Map<Role, Integer> scores = cloned.getScores();
            int opponentScore = scores.getOrDefault(opponent, 0);
            int myScore = scores.getOrDefault(opponent.getOpponent(), 0);
            int scoreDiff = opponentScore - myScore;
            maxOpponentScoreDiff = Math.max(maxOpponentScoreDiff, scoreDiff);
          } catch (IOException | IllegalArgumentException e) {
            // Skip invalid simulated move.
          }
        }
      }
    }

    return (maxOpponentScoreDiff == Integer.MIN_VALUE) ? 0 : maxOpponentScoreDiff;
  }

  /**
   * Tiebreaker rule that prefers the uppermost, then leftmost, then smallest card index.
   *
   * @param bestMove   the currently selected best move
   * @param newCardIdx index of the new card
   * @param newRow     row of the new move
   * @param newCol     column of the new move
   * @return true if the new move is preferred over the bestMove, false otherwise
   */
  private boolean isTiebreakerBetter(Move bestMove, int newCardIdx, int newRow, int newCol) {
    return bestMove == null ||
        newRow < bestMove.getRow() ||
        (newRow == bestMove.getRow() && newCol < bestMove.getCol()) ||
        (newRow == bestMove.getRow() && newCol == bestMove.getCol() &&
            newCardIdx < bestMove.getCardIndex());
  }
}
