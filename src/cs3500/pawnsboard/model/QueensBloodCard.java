package cs3500.pawnsboard.model;

/**
 * Represents a card in the game. A card has a name, a value, a cost, and an influence grid that
 * affects the board. The card also has an owner, which is a player in the game. The influence grid
 * defines the range and type of influence a card has when placed on the board.
 *
 * <p>Invariants:
 * - The `cost` of the card must be 1, 2, or 3. - The `value` of the card must be a non-negative
 * integer. - The `influence` grid must be a 5x5 matrix. - The center of the influence grid
 * (`influence[2][2]`) must always be 'C'. - The `owner` of the card must be non-null and must not
 * change after instantiation.
 */
public class QueensBloodCard implements Card {

  private final String name;
  private final Role owner;
  private final int value;
  private final int cost;
  private final char[][] influence;

  /**
   * Constructs a new QueensBloodCard with the specified parameters.
   *
   * @param name      the name of the card
   * @param owner     the player who owns the card
   * @param value     the value of the card
   * @param cost      the cost of the card in terms of pawns
   * @param influence a 5x5 character matrix representing the card's influence grid
   * @throws IllegalArgumentException if the name or owner is null, if the value or cost is
   *                                  negative, or if the influence grid is invalid
   */
  public QueensBloodCard(String name, Role owner, int value, int cost, char[][] influence) {
    if (name == null || owner == null) {
      throw new IllegalArgumentException("name and owner cannot be null!");
    }
    if (value < 0 || cost < 0) {
      throw new IllegalArgumentException("value and cost must be grater or equal to 0.");
    }
    validateInfluenceGrid(influence);
    this.name = name;
    this.owner = owner;
    this.value = value;
    this.cost = cost;
    this.influence = influence;

    if (this.owner.equals(Role.BLUE)) {
      mirrorInfluence();
    }

  }

  /**
   * Constructs a deep copy of another {@code Card}. Used to create an exact duplicate of an
   * existing card, preserving all properties.
   *
   * @param other the card to copy (must have valid name, owner, cost, value, and influence)
   * @throws IllegalArgumentException if {@code other} is {@code null}, or has invalid fields
   */
  public QueensBloodCard(Card other) {
    if (other.getName() == null || other.getOwner() == null) {
      throw new IllegalArgumentException("name and owner cannot be null!");
    }
    if (other.getValue() < 0 || other.getCost() < 0) {
      throw new IllegalArgumentException("value and cost must be grater or equal to 0.");
    }
    validateInfluenceGrid(other.getInfluence());
    this.name = other.getName();
    this.owner = other.getOwner();
    this.value = other.getValue();
    this.cost = other.getCost();
    this.influence = deepCopyInfluence(other.getInfluence());

    if (this.owner.equals(Role.BLUE)) {
      mirrorInfluence();
    }
  }

  private char[][] deepCopyInfluence(char[][] original) {
    if (original == null) {
      return null;
    }

    char[][] copy = new char[original.length][];
    for (int row = 0; row < original.length; row++) {
      copy[row] = original[row].clone();
    }

    return copy;
  }

  /**
   * Returns the name of the card.
   *
   * @return the name of the card
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Returns the value of the card.
   *
   * @return the value of the card
   */
  @Override
  public int getValue() {
    return this.value;
  }

  /**
   * Returns the cost of the card, which represents the number of pawns required to place it.
   *
   * @return the cost of the card
   */
  @Override
  public int getCost() {
    return this.cost;
  }

  /**
   * Returns the owner of the card.
   *
   * @return the player who owns the card
   */
  @Override
  public Role getOwner() {
    return this.owner;
  }

  @Override
  public char[][] getInfluence() {
    return influence;
  }

  /**
   * Returns the influence grid of the card as a 5x5 boolean array. Each 'I' is coverted to true,
   * and 'X' or 'C' are converted to false.
   *
   * @return the influence grid of the card as a boolean matrix
   */
  @Override
  public boolean[][] getInfluenceGrid() {
    boolean[][] grid = new boolean[5][5];

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = (influence[i][j] == 'I');
      }
    }

    return grid;
  }

  /**
   * Returns a string representation of the card, including its name, value, cost and influence
   * grid.
   *
   * @return a String representation of the card
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(this.name + "\n");
    builder.append("value: " + this.value + "\n");
    builder.append("cost: " + this.cost + "\n");

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        builder.append(influence[i][j] + " ");
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  private void mirrorInfluence() {
    for (int i = 0; i < influence.length; i++) {
      for (int j = 0; j < 2; j++) {
        char temp = influence[i][j];
        influence[i][j] = influence[i][4 - j];
        influence[i][4 - j] = temp;
      }
    }
  }

  private void validateInfluenceGrid(char[][] influence) {
    if (influence.length != 5 || influence[0].length != 5) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 matrix.");
    }
    if (influence[2][2] != 'C') {
      throw new IllegalArgumentException("The center of influence grid must be C");
    }
    for (char[] chars : influence) {
      for (int j = 0; j < influence[0].length; j++) {
        if (chars[j] != 'I' && chars[j] != 'X' && chars[j] != 'C') {
          throw new IllegalArgumentException("Char in influence grid must be I, X or C");
        }
      }
    }
  }

  @Override
  public Card clone() {
    return new QueensBloodCard(this);
  }
}
