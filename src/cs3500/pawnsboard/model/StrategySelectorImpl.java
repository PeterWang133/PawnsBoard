package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.StrategyUtils.DummyPlayer;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A concrete implementation of StrategySelector that selects the best strategy based on simulated
 * game outcomes.
 */
public class StrategySelectorImpl implements StrategySelector {

  /**
   * Selects the most favorable strategy from the provided list by simulating each possible move
   * and evaluating its impact on the board score.
   *
   * @param strategies the list of strategies to consider
   * @param game       the current game state
   * @param forWhom    the role of the player for whom the strategy is being chosen
   * @return the strategy estimated to provide the highest advantage
   * @throws IOException if an error occurs while evaluating strategies
   */
  @Override
  public Strategy selectBestStrategy(List<Strategy> strategies, Game<Card> game, Role forWhom)
      throws IOException {
    int bestScore = Integer.MIN_VALUE;
    Strategy bestStrategy = strategies.get(0); // fallback

    for (Strategy strategy : strategies) {
      Move move = strategy.makeMove(game, forWhom);

      if (!move.isPass()) {
        try {
          Board<Card> simulatedBoard = game.getBoard().clone();
          DummyPlayer dummy = new DummyPlayer(forWhom,
              game.getCurrentPlayer().getHand().get(move.getCardIndex()));
          simulatedBoard.placeCard(dummy, 0, move.getRow(), move.getCol());

          Map<Role, Integer> scoreMap = simulatedBoard.getScores();
          int score =
              scoreMap.getOrDefault(forWhom, 0) - scoreMap.getOrDefault(forWhom.getOpponent(), 0);

          if (score > bestScore) {
            bestScore = score;
            bestStrategy = strategy;
          }

        } catch (Exception ignored) {
        }
      }
    }

    return bestStrategy;
  }
}
