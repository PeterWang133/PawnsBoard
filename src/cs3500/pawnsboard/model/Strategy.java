package cs3500.pawnsboard.model;

import java.io.IOException;

/**
 * Represents a strategy for making a move in the PawnsBoard game.
 *
 * <p>A strategy defines how a player determines their next move based on current state of the
 * game.
 * Implementations of this interface encapsulate different decision-making logic for gameplay, such
 * as greedy selection, minimax evaluation, or scoring heuristics.
 */
public interface Strategy {

  /**
   * Determines the next move for the given player (role) using this strategy.
   *
   * @param game    the current game state, including board, players, and hands
   * @param forWhom the role (RED or BLUE) for which the move is being calculated
   * @return the {@link Move} the strategy decides to make; this can be a valid move or a pass move
   * if no legal moves are possible
   * @throws IOException if any I/O error occurs during move simulation or evaluation
   */
  Move makeMove(ReadonlyGame<Card> game, Role forWhom) throws IOException;
}
