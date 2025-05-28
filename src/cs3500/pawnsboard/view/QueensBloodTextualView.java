package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Role;
import java.io.IOException;
import java.util.Map;

/**
 * A textual implementation of the BoardView interface that displays the game board in text format.
 */
public class QueensBloodTextualView implements TextualView {

  private final Game<Card> game;

  /**
   * Constructs a BoardTextualView for the specified game.
   *
   * @param game the game model to be displayed
   * @throws IllegalArgumentException if the game is null
   */
  public QueensBloodTextualView(Game<Card> game) {
    if (game == null) {
      throw new IllegalArgumentException("Game cannot be null!");
    }
    this.game = game;
  }

  /**
   * Renders the current game state in textual format to the specified output stream. The method
   * renders the game board row by row. For each row, it displays the score for both players and
   * contents of each cell.
   *
   * @param out the Appendable object to which the game state will be rendered
   * @throws IOException              if an IO error occurs
   * @throws IllegalArgumentException if the out parameter is null
   */
  @Override
  public void render(Appendable out) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("Appendable object cannot be null!");
    }

    if (!game.isGameOver() && game.getBoard() == null) {
      throw new IllegalStateException("Game has not started or board is not initialized.");
    }

    Board<Card> board = game.getBoard();
    Cell<Card>[][] grid = board.getGrid();

    for (int row = 0; row < grid.length; row++) {

      Map<Role, Integer> rowScores = board.getRowScores(row);
      int redScore = 0;
      int blueScore = 0;
      for (Map.Entry<Role, Integer> entry : rowScores.entrySet()) {
        if (entry.getKey() == Role.RED) {
          redScore = entry.getValue();
        } else if (entry.getKey() == Role.BLUE) {
          blueScore = entry.getValue();
        }
      }

      StringBuilder rowContent = new StringBuilder();
      for (int col = 0; col < grid[row].length; col++) {
        Cell<Card> cell = this.game.getBoard().getCellAt(row, col);
        if (cell.getPawns() > 0) {
          rowContent.append(cell.getPawns());
        } else if (cell.getCard() != null) {
          rowContent.append(cell.getOwner().equals(Role.RED) ? "R" : "B");
        } else {
          rowContent.append("_");
        }
      }
      out.append(redScore + " " + rowContent + " " + blueScore + "\n");
    }
    out.append("\n");
  }
}

