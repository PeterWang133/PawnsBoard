package cs3500.pawnsboard.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the game board in the game. The board consists of a grid of cells where players place
 * cards and pwns to control the game. It manages card placements, influences, scoring and board
 * dimensions.
 *
 * <p>Invariants:
 * - The board must have a positive number of rows (`rows > 0`). - The board must have an odd number
 * of columns greater than 1. - The board's 2D grid (`grid`) must always contain exactly `rows *
 * cols` cells. - Each cell must either contain pawns or a card, but not both. - If a cell contains
 * a card, its pawn count must be exactly 0. - If a cell contains pawns, there can be no card in
 * that cell.
 */
public class QueensBloodBoard implements Board<Card> {

  private final int rows;
  private final int cols;
  private final Cell<Card>[][] grid;

  /**
   * Constructs a new QueensBloodBoard with the specified dimensions.
   *
   * @param rows the number of rows in the board
   * @param cols the number of columns in the board
   * @throws IllegalArgumentException if the rows is less than or equal to 0, or if the columns is
   *                                  not an odd number greater than 1
   */
  public QueensBloodBoard(int rows, int cols) {

    if (rows <= 0) {
      throw new IllegalArgumentException("Number of rows must be greater than 0.");
    }
    if (cols <= 1 || cols % 2 == 0) {
      throw new IllegalArgumentException("Number of columns must be odd and greater than 1.");
    }

    this.rows = rows;
    this.cols = cols;
    this.grid = new QueensBloodCell[rows][cols];
  }

  /**
   * Constructs a deep copy of an existing {@code QueensBloodBoard}. The new board will be
   * structurally identical but completely independent of the original.
   *
   * @param other the board to copy
   * @throws IllegalArgumentException if the dimensions of {@code other} are invalid
   */
  public QueensBloodBoard(QueensBloodBoard other) {

    if (other.rows <= 0) {
      throw new IllegalArgumentException("Number of rows must be greater than 0.");
    }
    if (other.cols <= 1 || other.cols % 2 == 0) {
      throw new IllegalArgumentException("Number of columns must be odd and greater than 1.");
    }

    this.rows = other.rows;
    this.cols = other.cols;
    this.grid = deepCopyGrid(other.grid);
  }

  /**
   * Protected constructor to create a board with a predefined grid. Ensures deep copy to prevent
   * external modification.
   *
   * @param rows the number of rows in the board
   * @param cols the number of columns in the board
   * @param grid the predefined grid of cells
   * @throws IllegalArgumentException if grid dimensions do not match
   */
  protected QueensBloodBoard(int rows, int cols, Cell<Card>[][] grid) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Number of rows and columns must be greater than 0.");
    }
    if (grid == null || grid.length != rows) {
      throw new IllegalArgumentException("Grid does not match specified row count.");
    }
    for (int r = 0; r < rows; r++) {
      if (grid[r] == null || grid[r].length != cols) {
        throw new IllegalArgumentException("Grid row " + r + " does not match column count.");
      }
    }

    this.rows = rows;
    this.cols = cols;

    // Deep copy to prevent external modification
    this.grid = new QueensBloodCell[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Cell<Card> cell = grid[r][c];
        this.grid[r][c] = new QueensBloodCell(cell.getOwner(), cell.getCard(), cell.getPawns());
      }
    }
  }

  private Cell<Card>[][] deepCopyGrid(Cell<Card>[][] grid) {
    Cell<Card>[][] copy = new QueensBloodCell[grid.length][grid[0].length];

    for (int row = 0; row < grid.length; row++) {
      for (int col = 0; col < grid[0].length; col++) {
        copy[row][col] = grid[row][col].clone();
      }
    }

    return copy;
  }

  /**
   * Initializes the board by placing an empty cell in each position and setting up the initial
   * pawns for both players on the left and right sides.
   *
   * @param playerRed  the red player
   * @param playerBlue the blue player
   * @throws IllegalArgumentException if either player is null
   */
  @Override
  public void initializeBoard(Player<Card> playerRed, Player<Card> playerBlue) {
    if (playerRed == null || playerBlue == null) {
      throw new IllegalArgumentException("The players cannot be null");
    }

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        this.grid[i][j] = new QueensBloodCell();
      }
    }
    for (int i = 0; i < rows; i++) {
      grid[i][0].addPawn(playerRed.getRole(), 1);
      grid[i][cols - 1].addPawn(playerBlue.getRole(), 1);
    }
  }

  /**
   * Places a card on the board at the specified position and applies its influence. In this
   * coordinate system, the index of row and col start from 0.
   *
   * @param player  the player placing the card
   * @param cardIdx the index of the card in the player's hand
   * @param row     the row where the card is placed
   * @param col     the column where the card is placed
   * @throws IOException              if an IO error occurs
   * @throws IllegalArgumentException if the player is null, if the position is out of bounds, or if
   *                                  the card index is invalid
   */
  @Override
  public void placeCard(Player<Card> player, int cardIdx, int row, int col) throws IOException {
    if (player == null) {
      throw new IllegalArgumentException("A null player cannot place card!");
    }

    if (row < 0 || row >= this.rows || col < 0 || col >= this.cols) {
      throw new IllegalArgumentException(row + "," + col + " is not a valid position to place!");
    }

    List<Card> hand = player.getHand();

    if (cardIdx < 0 || cardIdx >= hand.size()) {
      throw new IllegalArgumentException("Illegal card index: " + cardIdx);
    }
    Card card = hand.get(cardIdx);
    this.grid[row][col].addCard(card, player.getRole());
    hand.remove(cardIdx);
    applyInfluence(player, row, col);
  }

  @Override
  public boolean isLegalMove(Player<Card> player, int cardIdx, int row, int col) {
    if (player == null) {
      return false;
    }

    if (row < 0 || row >= this.rows || col < 0 || col >= this.cols) {
      return false;
    }

    List<Card> hand = player.getHand();

    return cardIdx >= 0 && cardIdx < hand.size();
  }

  /**
   * Get the cell at the specified position on the board.
   *
   * @param row the row index
   * @param col the column index
   * @return the cell at the specified position
   * @throws IllegalArgumentException if the row or column is out of bounds
   */
  @Override
  public Cell<Card> getCellAt(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IllegalArgumentException("Illegal row or column index: " + row + ", " + col);
    }
    return this.grid[row][col];
  }


  private void applyInfluence(Player<Card> player, int row, int col) {
    Card card = this.grid[row][col].getCard();
    if (card == null) {
      throw new IllegalArgumentException("No card for applying influence");
    }
    boolean[][] influenceGrid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {

        int effectiveJ;
        int colOffset;
        if (player.getRole() == Role.BLUE) {
          effectiveJ = 4 - j;
          colOffset = effectiveJ - 2;
        } else {
          effectiveJ = j;
          colOffset = j - 2;
        }

        int targetRow = row + (i - 2);
        int targetCol = col + colOffset;
        if (targetRow < 0 || targetRow >= rows || targetCol < 0 || targetCol >= cols) {
          continue;
        }
        if (influenceGrid[i][effectiveJ]) {
          Cell<Card> cell = this.grid[targetRow][targetCol];
          if (cell.getCard() != null) {
            continue;
          }
          if (cell.getOwner() == null) {
            cell.addPawn(player.getRole(), 1);
          } else if (cell.getOwner().equals(player)) {
            cell.addPawn(player.getRole(), 1);
          } else {
            cell.addPawn(player.getRole(), cell.getPawns());
          }
        }
      }
    }
  }

  /**
   * Computes the total scores for both players. Only the player with the highest row score in each
   * row gains points.
   *
   * @return a map containing the total scores for both players
   */
  @Override
  public Map<Role, Integer> getScores() {
    int totalRed = 0;
    int totalBlue = 0;

    for (int r = 0; r < rows; r++) {
      Map<Role, Integer> rowScores = getRowScores(r);
      int redRowScore = rowScores.getOrDefault(Role.RED, 0);
      int blueRowScore = rowScores.getOrDefault(Role.BLUE, 0);

      // Only the winner gets the score
      if (redRowScore > blueRowScore) {
        totalRed += redRowScore;
      } else if (blueRowScore > redRowScore) {
        totalBlue += blueRowScore;
      }
    }

    return Map.of(Objects.requireNonNull(Role.RED), totalRed,
        Objects.requireNonNull(Role.BLUE), totalBlue);
  }


  /**
   * Calculates the scores for each player based on the cards placed in a given row.
   *
   * @param row the row index for which the scores are being calculated
   * @return a map of players and their respective scores for the row
   * @throws IllegalArgumentException if the row index is out of bounds
   */
  @Override
  public Map<Role, Integer> getRowScores(int row) {
    if (row < 0 || row >= this.rows) {
      throw new IllegalArgumentException("Cannot obtain a score for invalid row: " + row);
    }
    Map<Role, Integer> rowSums = new HashMap<>();
    rowSums.put(Role.RED, 0);
    rowSums.put(Role.BLUE, 0);

    for (int col = 0; col < this.cols; col++) {
      Card card = grid[row][col].getCard();
      if (card != null) {
        Role owner = card.getOwner();
        rowSums.put(owner, rowSums.getOrDefault(owner, 0) + card.getValue());
      }
    }

    return rowSums;
  }

  /**
   * Calculates the number of remaining cells on the board that do not contain cards.
   *
   * @return the number of empty cells on the board
   */
  @Override
  public int remainingCell() {
    int hasCard = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell<Card> cell = this.grid[i][j];
        if (cell.getCard() != null) {
          hasCard++;
        }
      }
    }

    return this.rows * this.cols - hasCard;
  }

  /**
   * Returns the total number of cells on the board.
   *
   * @return the total size of the board in terms of cells
   */
  @Override
  public int getBoardSize() {
    return this.getHeight() * this.getWidth();
  }

  /**
   * Returns the number of columns on the board.
   *
   * @return the width of the board
   */
  @Override
  public int getWidth() {
    return this.cols;
  }

  /**
   * Returns the number of rows on the board.
   *
   * @return the height of the board
   */
  @Override
  public int getHeight() {
    return this.rows;
  }

  /**
   * Returns the grid of cells on the board.
   *
   * @return the 2D array representing the grid of cells
   */
  @Override
  public Cell<Card>[][] getGrid() {
    return grid;
  }

  @Override
  public Board clone() {
    return new QueensBloodBoard(this);
  }
}
