package cs3500.pawnsboard.model;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.Role;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for strategies that need to simulate moves.
 */
public class StrategyUtils {

  /**
   * DummyPlayer is used for simulation in strategies that require a mutable player. It implements
   * the Player interface minimally.
   */
  public static class DummyPlayer implements Player<Card> {

    private final Role role;
    private final List<Card> hand;

    /**
     * Constructs a dummy player with a role and a single card in hand.
     *
     * @param role the role (RED or BLUE) this dummy player represents
     * @param card the card to be used in simulation
     */
    public DummyPlayer(Role role, Card card) {
      this.role = role;
      this.hand = new ArrayList<>();
      this.hand.add(card);
    }

    @Override
    public List<Card> getHand() {
      return hand;
    }

    @Override
    public int getRemainingDeckSize() {
      return 0;
    }

    @Override
    public void initializePlayer(int boardCapacity, List<Card> deck) {
      // Not needed for simulation.
    }

    @Override
    public Role getRole() {
      return role;
    }

    @Override
    public Card drawNewCard(Board<Card> board) {
      return null;
    }

    @Override
    public Player<Card> clone() {
      return null;
    }
  }
}
