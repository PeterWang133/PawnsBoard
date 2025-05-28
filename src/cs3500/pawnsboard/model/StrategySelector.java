package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Role;
import cs3500.pawnsboard.model.Strategy;
import java.io.IOException;
import java.util.List;

/**
 * Represents a strategy selection mechanism used by an AI player.
 */
public interface StrategySelector {

  /**
   * Selects the optimal strategy to use from a list of available strategies.
   *
   * @param strategies the list of strategies to consider
   * @param game       the current game state
   * @param forWhom    the role of the player
   * @return the chosen strategy
   */
  Strategy selectBestStrategy(List<Strategy> strategies, Game<Card> game, Role forWhom)
      throws IOException;
}
