package cs3500.pawnsboard.model;

import cs3500.pawnsboard.controller.ModelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the game implementation, which defines the specific rules and mechanics. It includes
 * player roles, board config, and the logic for determining how cards are drawn and played.
 *
 * <p>Invariants:
 * - The board must have an odd number of columns greater than 1. - The game must be started
 * (`startGame()`) before any moves can be made. - Players' starting hand size cannot be more than
 * one-third of their deck size. - A cell on the board can either have a card or pawns, but not
 * both.
 */
public class QueensBloodGame implements Game<Card> {

  private final Board<Card> board;
  private final Player<Card> playerRed;
  private final Player<Card> playerBlue;
  private final int handSize;
  private final List<ModelListener> modelListeners;
  private final int[] selectedCell;
  boolean isGameStarted;
  private Player<Card> currentPlayer;
  private int selectedCardIndex;
  private int consecutivePass = 0;

  /**
   * Constructs a new QueensBloodGame with the specified board dimension and number of players.
   *
   * @param rows     the number of rows in the board
   * @param columns  the number of columns in the board
   * @param handSize the maximum number of cards a player can hold initially
   * @throws IllegalArgumentException if the number of rows or columns is illegal
   */
  public QueensBloodGame(int rows, int columns, int handSize) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Number of rows must be positive!");
    }
    if (columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException(
          "Number of cols must not only be greater than 1, but they must be odd!");
    }
    if (handSize <= 0) {
      throw new IllegalArgumentException("Size of hand must be positive!");
    }
    this.playerRed = new QueensBloodPlayer(Role.RED, handSize);
    this.playerBlue = new QueensBloodPlayer(Role.BLUE, handSize);
    this.board = new QueensBloodBoard(rows, columns);
    this.handSize = handSize;
    this.currentPlayer = playerRed;
    this.isGameStarted = false;
    this.modelListeners = new ArrayList<>();
    this.selectedCardIndex = -1;
    this.selectedCell = new int[2];
    this.selectedCell[0] = -1;
    this.selectedCell[1] = -1;
    this.consecutivePass = 0;
  }

  /**
   * Protected constructor to create a game with a pre-filled board. Used primarily for testing or
   * resuming saved game states.
   *
   * @param board      the board with pre-placed cards
   * @param playerRed  the red player
   * @param playerBlue the blue player
   * @param handSize   the maximum hand size allowed
   * @throws IllegalArgumentException if board, players, or handSize are invalid
   */
  protected QueensBloodGame(Board<Card> board, Player<Card> playerRed, Player<Card> playerBlue,
      int handSize) {
    if (board == null || playerRed == null || playerBlue == null) {
      throw new IllegalArgumentException("Board and Players cannot be null.");
    }
    if (handSize <= 0) {
      throw new IllegalArgumentException("Size of hand must be positive.");
    }

    this.board = board;
    this.playerRed = playerRed;
    this.playerBlue = playerBlue;
    this.handSize = handSize;
    this.currentPlayer = playerRed;

    // Start game automatically if board is already filled
    this.isGameStarted = board.remainingCell() < board.getBoardSize();

    this.modelListeners = new ArrayList<>();
    this.selectedCardIndex = -1;
    this.selectedCell = new int[2];
    this.selectedCell[0] = -1;
    this.selectedCell[1] = -1;
  }


  /**
   * Initializes the game, setting up the board and players.
   */
  @Override
  public void startGame(List<Card> redDeck, List<Card> blueDeck) {
    if (this.isGameStarted) {
      throw new IllegalStateException("Game has already started");
    }
    int boardCapacity = board.getBoardSize();
    this.playerBlue.initializePlayer(boardCapacity, blueDeck);
    this.playerRed.initializePlayer(boardCapacity, redDeck);
    this.board.initializeBoard(this.playerRed, this.playerBlue);
    this.isGameStarted = true;
    this.drawNewCardForCurrentPlayer();
  }

  /**
   * Determine if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  @Override
  public boolean isGameOver() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }

    if (consecutivePass >= 2) {
      return true;
    }

    boolean noRemainingCells = this.board.remainingCell() == 0;
    boolean redOutOfMoves = this.playerRed.getHand().isEmpty()
        && this.playerRed.getRemainingDeckSize() == 0;
    boolean blueOutOfMoves = this.playerBlue.getHand().isEmpty()
        && this.playerBlue.getRemainingDeckSize() == 0;

    return noRemainingCells || (redOutOfMoves && blueOutOfMoves);
  }

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
  @Override
  public void placeCard(int row, int col, int cardIndex) throws IOException {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }
    if (row < 0 || row >= board.getHeight() || col < 0 || col >= board.getWidth()) {
      throw new IllegalArgumentException("Invalid row or column: " + row + ", " + col);
    }
    if (cardIndex < 0 || cardIndex >= currentPlayer.getHand().size()) {
      throw new IllegalArgumentException("Invalid card index: " + cardIndex);
    }

    Card card = currentPlayer.getHand().get(cardIndex);
    if (board.getCellAt(row, col).getCard() != null) {
      throw new IllegalStateException("Cell is already occupied by another card.");
    }
    if (board.getCellAt(row, col).getPawns() < card.getCost()) {
      throw new IllegalStateException("Not enough pawns to cover the cost of this card.");
    }

    this.board.placeCard(currentPlayer, cardIndex, row, col);
    this.selectedCardIndex = -1;
    notifyModelListeners();
  }

  /**
   * Switches the turn to the other player.
   */
  @Override
  public void switchCurrentPlayer() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }
    currentPlayer = currentPlayer == playerRed ? playerBlue : playerRed;
    this.drawNewCardForCurrentPlayer();
    notifyModelListeners();
  }

  /**
   * Draws a new card for the current player.
   *
   * @return the drawn card or null if the deck is empty
   * @throws IllegalStateException if the game has not started
   */
  @Override
  public Card drawNewCardForCurrentPlayer() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }
    Card card = currentPlayer.drawNewCard(board);

    notifyModelListeners();

    return card;
  }

  /**
   * Returns the player whose turn it currently is.
   *
   * @return the current player
   */
  @Override
  public Player<Card> getCurrentPlayer() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started. Thus, there is no current player.");
    }
    return currentPlayer;
  }

  /**
   * Returns the copy of the game board.
   *
   * @return the copy of the game board
   */
  @Override
  public Board<Card> getBoard() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started. The board thus "
          + "has not been initialized.");
    }
    return this.board.clone();
  }

  /**
   * Get the hand size from the current player.
   *
   * @return an integer that indicates the size of the hand
   */
  @Override
  public int getHandSize() {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }
    return this.handSize;
  }

  /**
   * Adds an observer to the notified of model updates.
   *
   * @param modelListener the observer to add
   */
  @Override
  public void addModelListener(ModelListener modelListener) {
    this.modelListeners.add(modelListener);
  }

  /**
   * Removes an observer from the list of subscribed observers.
   *
   * @param modelListener the observer to remove
   */
  @Override
  public void removeModelListener(ModelListener modelListener) {
    this.modelListeners.remove(modelListener);
  }

  /**
   * Notifies all registered observers that the model has changed.
   */
  @Override
  public void notifyModelListeners() {
    for (ModelListener modelListener : this.modelListeners) {
      modelListener.onModelUpdate();
    }
  }

  /**
   * Returns the index of the currently selected card in the hand, or -1 if none selected.
   *
   * @return the selected card index
   */
  @Override
  public int getSelectedCardIndex() {
    return selectedCardIndex;
  }

  /**
   * Set the index of the currently selected card.
   *
   * @param selectedCardIndex the index to set, or -1 to deselect
   */
  @Override
  public void setSelectedCardIndex(int selectedCardIndex) {
    this.selectedCardIndex = selectedCardIndex;
    notifyModelListeners();
  }

  /**
   * Returns the coordinates of the currently selected board cell.
   *
   * @return an int array of size 2: [row, col]
   */
  @Override
  public int[] getSelectedCellCoordinate() {
    return selectedCell;
  }

  /**
   * Sets the coordinates of the selected board cell.
   *
   * @param selectedRow the row index
   * @param selectedCol the column index
   */
  @Override
  public void setSelectedCellCoordinate(int selectedRow, int selectedCol) {
    this.selectedCell[0] = selectedRow;
    this.selectedCell[1] = selectedCol;
    notifyModelListeners();
  }

  /**
   * Resets the count of consecutive passes to 0.
   */
  @Override
  public void resetConsecutivePass() {
    this.consecutivePass = 0;
  }

  /**
   * Increments the number of consecutive passes by 1.
   */
  @Override
  public void increaseConsecutivePass() {
    this.consecutivePass += 1;
  }

  /**
   * Returns the role of the player who won the game. Returns null if the game is not over.
   *
   * @return the winning player's role, or null if game not over
   */
  @Override
  public Role getWonPlayer() {
    if (!isGameOver()) {
      return null;
    } else {
      Map<Role, Integer> scores = board.getScores();
      if (scores.get(Role.RED) > scores.get(Role.BLUE)) {
        return Role.RED;
      } else if (scores.get(Role.BLUE) > scores.get(Role.RED)) {
        return Role.BLUE;
      } else {
        return null;
      }
    }
  }

  @Override
  public Player<Card> getPlayer(Role role) {
    if (!this.isGameStarted) {
      throw new IllegalStateException("Game has not started");
    }
    if (role == Role.RED) {
      return this.playerRed;
    } else {
      return this.playerBlue;
    }
  }

}