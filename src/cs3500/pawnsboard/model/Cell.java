package cs3500.pawnsboard.model;

/**
 * Represents a cell interface in the game.
 *
 * @param <C> the type of the card that can be placed in the cell
 */
public interface Cell<C extends Card> {

  /**
   * Adds a card to the cell if conditions are met. This includes checking if the cell is already
   * occupied by a card, ensuring the player is allowed to place the card, and verifying that there
   * are enough pawns to cover the card's cost.
   *
   * @param card   the card to place on the cell
   * @param player the player playing the card
   * @throws IllegalArgumentException if the card is already placed in the cell, if the cell is
   *                                  under the opponent's influence, or if there is no enough
   *                                  pawns
   */
  void addCard(Card card, Role player);

  /**
   * Gets the player who owns this cell.
   *
   * @return the owner of the cell, or null if the cell has no owner
   */
  Role getOwner();

  /**
   * Gets the number of pawns in this cell.
   *
   * @return the number of pawns in this cell
   */
  int getPawns();

  /**
   * Adds pawns to the cell, ensuring the total number of pawns does not exceed 3. If the cell
   * already contains a card, no pawns can be added.
   *
   * @param player the player adding the pawns
   * @param pawns  the number of pawns to add
   */
  void addPawn(Role player, int pawns);

  /**
   * Gets the card placed in the cell, or null if no card is present.
   *
   * @return the card in the cell or null if no card is placed
   */
  Card getCard();

  /**
   * Copies a Cell object into a new Cell object. Mutation is restricted on this method.
   *
   * @return A new Cell obejct that contains the same information and character of the Cell object
   * it inherited from.
   */
  Cell clone();
}
