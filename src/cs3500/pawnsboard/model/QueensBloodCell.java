package cs3500.pawnsboard.model;

/**
 * Represents a cell on the board. The cell can contain a card, a specific number of pawns, and an
 * owner. The cell may be occupied by a card or pawns. The class handles the logic for adding cards,
 * pawns, and managing ownership.
 *
 * <p>Invariants:
 * - If a cell contains a card, its pawn count must be 0. - If a cell contains pawns, then `pawns`
 * must be between 0 and 3 (inclusive). - A cell's owner is either null or the owner of its contents
 * (card or pawns). - If a card is placed in a cell, the owner of the cell must match the owner of
 * the card.
 */
public class QueensBloodCell implements Cell<Card> {

  private Role owner;
  private Card card;
  private int pawns;


  /**
   * Constructs a new cell with the specified owner, card, and number of pawns.
   *
   * @param owner the player who owns this cell
   * @param card  the card placed on the cell
   * @param pawns the number of pawns in this cell
   * @throws IllegalArgumentException if the number of pawns is negative or exceed 3
   * @throws IllegalArgumentException if the owner does not match with the owner of the card
   * @throws IllegalArgumentException if the pawns is non-zero while a card has been added
   */
  public QueensBloodCell(Role owner, Card card, int pawns) {
    if (pawns < 0 || pawns > 3) {
      throw new IllegalArgumentException("pawns cannot be negative");
    }
    if (card != null && owner == null) {
      this.owner = card.getOwner();
    }
    if (card != null && owner != null && !owner.equals(card.getOwner())) {
      throw new IllegalArgumentException("Cell has a different owner than the card.");
    }
    if (card != null && pawns > 0) {
      throw new IllegalArgumentException("Card already added. Pawns thus should be 0.");
    }
    this.owner = owner;
    this.card = card;
    this.pawns = pawns;
  }

  /**
   * Constructs a deep copy of the given {@code Cell}.
   *
   * @param other the cell to copy
   * @throws IllegalArgumentException if the number of pawns is invalid, or if the ownership and
   *                                  card data conflict
   */
  public QueensBloodCell(Cell other) {
    if (other.getPawns() < 0 || other.getPawns() > 3) {
      throw new IllegalArgumentException("pawns cannot be negative");
    }
    if (other.getCard() != null && other.getOwner() == null) {
      this.owner = other.getCard().getOwner();
    }
    if (other.getCard() != null && other.getOwner() != null && !other.getOwner()
        .equals(other.getCard().getOwner())) {
      throw new IllegalArgumentException("Cell has a different owner than the card.");
    }
    if (other.getCard() != null && other.getPawns() > 0) {
      throw new IllegalArgumentException("Card already added. Pawns thus should be 0.");
    }
    this.owner = other.getOwner();
    this.card = other.getCard();
    this.pawns = other.getPawns();
  }

  /**
   * Constructs a new empty cell with no owner, card, or pawns.
   */
  public QueensBloodCell() {
    this(null, null, 0);
  }

  /**
   * Adds a card to the cell if conditions are met. This includes checking if the cell is already
   * occupied by a card, ensuring the player is allowed to place the card, and verifying that there
   * are enough pawns to cover the card's cost.
   *
   * @param card   the card to place on the cell
   * @param player the player playing the card
   * @throws IllegalArgumentException if the card is already placed in the cell, if the cell is
   *                                  under the opponent's influence, if the owner of the card does
   *                                  not match the current player,or if there is no enough pawns
   */
  @Override
  public void addCard(Card card, Role player) {
    if (this.card != null) {
      throw new IllegalArgumentException("Card already added");
    }
    if (this.owner != null && !this.owner.equals(player)) {
      throw new IllegalArgumentException("Cell is under opponent influence, cannot place card.");
    }
    if (!card.getOwner().equals(player)) {
      throw new IllegalArgumentException("Owner of the card does not match the current player.");
    }
    if (this.pawns < card.getCost()) {
      throw new IllegalArgumentException("No enough pawns to cover the cost of the card.");
    }
    this.card = card;
    this.owner = player;
    this.pawns = 0;
  }

  /**
   * Gets the player who owns this cell.
   *
   * @return the owner of the cell, or null if the cell has no owner
   */
  @Override
  public Role getOwner() {
    if (this.owner != null) {
      return this.owner;
    }
    return (this.card != null) ? this.card.getOwner() : null;
  }

  /**
   * Gets the number of pawns in this cell.
   *
   * @return the number of pawns in this cell
   */
  @Override
  public int getPawns() {
    return this.pawns;
  }

  /**
   * Adds pawns to the cell, ensuring the total number of pawns does not exceed 3. If the cell
   * already contains a card, no pawns can be added.
   *
   * @param player the player adding the pawns
   * @param pawns  the number of pawns to add
   */
  @Override
  public void addPawn(Role player, int pawns) {
    if (this.card != null) {
      return;
    }
    if (this.owner == null) {
      this.owner = player;
    }
    if (this.owner.equals(player)) {
      this.pawns = Math.min(this.pawns + pawns, 3);
    } else {
      this.owner = player;
      this.pawns = Math.min(pawns, 3);
    }
  }

  /**
   * Gets the card placed in the cell, or null if no card is present.
   *
   * @return the card in the cell or null if no card is placed
   */
  @Override
  public Card getCard() {
    if (this.card != null) {
      return this.card;
    }
    return null;
  }

  @Override
  public Cell clone() {
    return new QueensBloodCell(this);
  }
}
