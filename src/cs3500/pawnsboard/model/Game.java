package cs3500.pawnsboard.model;

import java.io.IOException;
import java.util.List;

/**
 * Represents a generic game interface.
 *
 * @param <C> the type of card used in the game
 */
public interface Game<C extends Card> extends ReadonlyGame<C> {

  /**
   * Initializes the game, setting up the board and players.
   */
  void startGame(List<Card> redDeck, List<Card> blueDeck);


  /**
   * Switches the turn to the other player.
   */
  void switchCurrentPlayer();

  /**
   * Places a card from the current player's hand onto the board at the specified position.
   *
   * @param row       the row of the position
   * @param col       the column of the position
   * @param cardIndex the index of the card in the player's hand to be placed
   * @throws IOException              if an IO error occurs
   * @throws IllegalStateException    if the game has not started or the cell is already occupied or
   *                                  there is no enough pawns to cover the cost of this card
   * @throws IllegalArgumentException if the provided row or column is out of bounds or card index
   *                                  in the hand is out of bounds
   */
  void placeCard(int row, int col, int cardIndex) throws IOException;


  /**
   * Draws a new card for the current player.
   *
   * @return the drawn card or null if the deck is empty
   */
  Card drawNewCardForCurrentPlayer();

  /**
   * Resets the count of consecutive passes to 0.
   */
  void resetConsecutivePass();

  /**
   * Increments the number of consecutive passes by 1.
   */
  void increaseConsecutivePass();

}
