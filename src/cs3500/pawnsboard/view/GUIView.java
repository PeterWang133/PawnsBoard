package cs3500.pawnsboard.view;

import cs3500.pawnsboard.controller.PlayerActionListener;

/**
 * Represents the view component in the GUI for the game. Exposes method that allows the controller
 * to trigger rendering updates.
 */
public interface GUIView {

  /**
   * Renders the current state of the board grid. Includes pawns, cards, scores and selection
   * highlights.
   */
  void renderBoardGrid();

  /**
   * Renders the current player's hand cards. Includes selection highlights.
   */
  void renderHandCards();

  /**
   * Refreshes the entire GUI view. This typically happens when the model is updated.
   */
  void refreshView();

  void addPlayerActionListener(PlayerActionListener listener);

}
