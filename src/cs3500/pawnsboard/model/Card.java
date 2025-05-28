package cs3500.pawnsboard.model;

/**
 * Represents a card interface in the game. Cards are drawn and placed by players during the game,
 * and their influences is used to control the board.
 */
public interface Card {

  /**
   * Returns the name of the card.
   *
   * @return the name of the card
   */
  String getName();

  /**
   * Returns the value of the card.
   *
   * @return the value of the card
   */
  int getValue();

  /**
   * Returns the cost of the card, which represents the number of pawns required to place it.
   *
   * @return the cost of the card
   */
  int getCost();

  /**
   * Returns the owner of the card.
   *
   * @return the player who owns the card
   */
  Role getOwner();

  char[][] getInfluence();

  /**
   * Returns the influence grid of the card as a 5x5 boolean array. Each 'I' is coverted to true,
   * and 'X' or 'C' are converted to false.
   *
   * @return the influence grid of the card as a boolean matrix
   */
  boolean[][] getInfluenceGrid();

  /**
   * Copies this Card object into a new object. Mutation is restricted on this method.
   *
   * @return A new Card object that contains the same character as this Card object
   */
  Card clone();
}
