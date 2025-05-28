package cs3500.pawnsboard.model;

/**
 * Represents the role of a player.
 */
public enum Role {
  BLUE, RED;

  /**
   * Returns the opponent's role.
   *
   * @return the role of the opposing player
   */
  public Role getOpponent() {
    return (this == RED) ? BLUE : RED;
  }

  /**
   * Returns a human-readable name for the role.
   *
   * @return "Red" for red player or "Blue" for blue player
   */
  public String getDisplayName() {
    return (this == RED) ? "Red" : "Blue";
  }
}
