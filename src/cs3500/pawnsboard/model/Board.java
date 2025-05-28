package cs3500.pawnsboard.model;

import java.io.IOException;
import java.util.Map;

/**
 * Represent a game board. It manages the grid of cells, allows placement of cards, and tracks game
 * progress.
 *
 * @param <C> the type of card that can be placed on the board
 */
public interface Board<C extends Card> {

  /**
   * Initializes the board by placing an empty cell in each position and setting up the initial
   * pawns for both players on the left and right sides.
   *
   * @param playerRed  the red player
   * @param playerBlue the blue player
   * @throws IllegalArgumentException if either player is null
   */
  void initializeBoard(Player<Card> playerRed, Player<Card> playerBlue);

  /**
   * Places a card on the board at the specified position and applies its influence.
   *
   * @param player  the player placing the card
   * @param cardIdx the index of the card in the player's hand
   * @param row     the row where the card is placed
   * @param col     the column where the card is placed
   * @throws IOException              if an IO error occurs
   * @throws IllegalArgumentException if the player is null, if the position is out of bounds, or if
   *                                  the card index is invalid
   */
  void placeCard(Player<C> player, int cardIdx, int row, int col) throws IOException;

  /**
   * Get the cell at the specified position on the board.
   *
   * @param row the row index
   * @param col the column index
   * @return the cell at the specified position
   * @throws IllegalArgumentException if the row or column is out of bounds
   */
  Cell<C> getCellAt(int row, int col);

  /**
   * Computes the total scores for both players. Only the player with the highest row score in each
   * row gains points.
   *
   * @return a map containing the total scores for both players
   */
  Map<Role, Integer> getScores();

  /**
   * Calculates the scores for each player based on the cards placed in a given row.
   *
   * @param row the row index for which the scores are being calculated
   * @return a map of players and their respective scores for the row
   * @throws IllegalArgumentException if the row index is out of bounds
   */
  Map<Role, Integer> getRowScores(int row);

  /**
   * Calculates the number of remaining cells on the board that do not contain cards.
   *
   * @return the number of empty cells on the board
   */
  int remainingCell();

  /**
   * Returns the total number of cells on the board.
   *
   * @return the total size of the board in terms of cells
   */
  int getBoardSize();

  /**
   * Returns the number of columns on the board.
   *
   * @return the width of the board
   */
  int getWidth();

  /**
   * Returns the number of rows on the board.
   *
   * @return the height of the board
   */
  int getHeight();

  /**
   * Returns the grid of cells on the board.
   *
   * @return the 2D array representing the grid of cells
   */
  Cell<C>[][] getGrid();

  /**
   * Copies this Board object into a new object. Mutation is restricted on this method.
   *
   * @return A new Board object that contains the same character as this Board object
   */
  Board clone();

  boolean isLegalMove(Player<Card> player, int cardIdx, int row, int col);
}
