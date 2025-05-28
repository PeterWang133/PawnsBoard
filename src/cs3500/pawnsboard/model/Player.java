package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents a player in the game.
 *
 * @param <C> the type of card used by the player
 */
public interface Player<C extends Card> {

  /**
   * Initializes the player's deck and hand.
   *
   * @param boardCapacity the minimum number of cards required in the deck
   * @throws IllegalStateException if the deck file is missing, incorrectly formatted, or does not
   *                               contain enough cards
   */
  void initializePlayer(int boardCapacity, List<Card> newDeck);

  /**
   * Returns the player's current hand.
   *
   * @return a list of cards in the player's hand
   */
  List<C> getHand();

  /**
   * Returns the number of remaining cards in the player's deck.
   *
   * @return the count of cards left in the deck
   */
  int getRemainingDeckSize();

  /**
   * Returns the role of the player.
   *
   * @return the player's role
   */
  Role getRole();

  /**
   * Draws a new card from the player's deck based on the current state of the board.
   *
   * @param board the current state of the game board (must not be null)
   * @return the drawn card, or null if no cards remain in the deck
   * @throws IllegalStateException if the deck is empty and no card can be drawn
   */
  Card drawNewCard(Board<Card> board);

  Player<C> clone();
}
