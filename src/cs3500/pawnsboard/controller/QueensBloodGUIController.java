package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.view.GUIView;
import java.io.IOException;

/**
 * Controller for the GUI version of QueensBlood. Handles user input from the GUI and delegates
 * actions to the model.
 */
public class QueensBloodGUIController implements GUIController {

  private final Game game;
  private GUIView view;

  /**
   * Constructs a GUI controller with the specified game model.
   *
   * @param game the game model
   */
  public QueensBloodGUIController(Game game) {
    this.game = game;
  }

  /**
   * Handles a card click in the hand area.
   *
   * @param newCardIndex the index of the card clicked.
   */
  @Override
  public void handleCardClick(int newCardIndex) {
    int selectedCardIndex = game.getSelectedCardIndex();

    if (selectedCardIndex == newCardIndex) {
      // deselect
      game.setSelectedCardIndex(-1);
      System.out.println(
          "Deselected card: " + newCardIndex + " (Player: " + game.getCurrentPlayer().getRole()
              + ")");
    } else {
      game.setSelectedCardIndex(newCardIndex);
      System.out.println(
          "Selected card: " + newCardIndex + " (Player: " + game.getCurrentPlayer().getRole()
              + ")");
    }

  }

  /**
   * Handles a board cell click.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  @Override
  public void handleCellClick(int row, int col) {
    int[] selectedCell = game.getSelectedCellCoordinate();

    if (selectedCell[0] == row && selectedCell[1] == col) {
      // deselect
      game.setSelectedCellCoordinate(-1, -1);
      System.out.println("DeSelected cell: (" + row + ", " + col + ")");
    } else {
      game.setSelectedCellCoordinate(row, col);
      System.out.println("Selected cell: (" + row + ", " + col + ")");
    }

  }

  /**
   * Handles the user choosing to pass their turn.
   */
  @Override
  public void handlePassTurn() {
    game.increaseConsecutivePass();

    System.out.println("Turn passed");
    game.switchCurrentPlayer();

    game.setSelectedCellCoordinate(-1, -1);
    game.setSelectedCardIndex(-1);
  }

  /**
   * Confirms the move currently selected by the user.
   */
  @Override
  public void confirmMove() {
    game.resetConsecutivePass();

    int[] selectedCell = game.getSelectedCellCoordinate();
    int selectedCellRow = selectedCell[0];
    int selectedCellCol = selectedCell[1];
    int selectedCardIndex = game.getSelectedCardIndex();

    if (selectedCardIndex != -1 && selectedCellRow != -1 && selectedCellCol != -1) {
      try {
        game.placeCard(selectedCellRow, selectedCellCol, selectedCardIndex);
        System.out.println(
            "Confirmed move: card " + selectedCardIndex + " at (" + selectedCellRow + ", "
                + selectedCellCol + ")");
        game.setSelectedCellCoordinate(-1, -1);
        game.setSelectedCardIndex(-1);
        game.switchCurrentPlayer();
      } catch (IOException e) {
        System.out.println("IO error occurs while placing card");
        game.setSelectedCellCoordinate(-1, -1);
        game.setSelectedCardIndex(-1);
      } catch (IllegalArgumentException | IllegalStateException e) {
        System.out.println(e.getMessage());
        game.setSelectedCellCoordinate(-1, -1);
        game.setSelectedCardIndex(-1);
      }

    } else {
      System.out.println(
          "Invalid move: card " + selectedCardIndex + " at (" + selectedCellRow + ", "
              + selectedCellCol + ")");
    }
  }

  /**
   * Called when the model has been updated and the observer should refresh its state/view.
   */
  @Override
  public void onModelUpdate() {
    this.view.refreshView();
  }

  /**
   * Sets the view for the controller to communicate with.
   *
   * @param view the GUI view to be controlled
   */
  public void setView(GUIView view) {
    this.view = view;
  }
}
