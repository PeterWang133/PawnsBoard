package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.view.GUIView;

/**
 * Represents a controller responsible for managing a player's turn in the game.
 */
public interface PlayerController extends ModelListener, GUIController {

  /**
   * Executes logic when it's this player's turn.
   */
  void takeTurn();

  /**
   * Sets the GUI view for this controller, allowing it to communicate with the interface.
   *
   * @param view the GUI view to be controlled
   */
  void setView(GUIView view);
}
