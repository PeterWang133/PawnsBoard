package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.view.GUIView;

/**
 * Represents a controller for the GUI version of the game. Handles user interaction events and
 * communicates them to the model.
 */
public interface GUIController extends ModelListener {

  /**
   * Handles a card click in the hand area.
   *
   * @param newCardIndex the index of the card clicked.
   */
  void handleCardClick(int newCardIndex);

  /**
   * Handles a board cell click.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  void handleCellClick(int row, int col);

  /**
   * Handles the user choosing to pass their turn.
   */
  void handlePassTurn();

  /**
   * Confirms the move currently selected by the user.
   */
  void confirmMove();

  /**
   * Sets the view for the controller to communicate with.
   *
   * @param view the GUI view to be controlled
   */
  void setView(GUIView view);
}
