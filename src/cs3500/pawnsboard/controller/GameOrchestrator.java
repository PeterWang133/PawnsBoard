package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Role;

import java.util.HashMap;
import java.util.Map;

/**
 * Coordinates game progression by delegating turns to the appropriate player controller.
 */
public class GameOrchestrator implements ModelListener {

  private final Game<Card> game;
  private final Map<Role, PlayerController> controllers;

  /**
   * Constructs a GameOrchestrator object for the given name and player controllers.
   *
   * @param game the game model to observe and orchestrate
   * @param redCtrl the controller for the RED player
   * @param blueCtrl the controller for the BLUE player
   */
  public GameOrchestrator(Game<Card> game, PlayerController redCtrl, PlayerController blueCtrl) {
    this.game = game;
    this.controllers = new HashMap<>();
    if (redCtrl != null) {
      this.controllers.put(Role.RED, redCtrl);
    }
    if (blueCtrl != null) {
      this.controllers.put(Role.BLUE, blueCtrl);
    }

    game.addModelListener(this);
  }

  /**
   * Called when the model is updated. If the game is in a valid state,
   * it delegates control to the current player's controller to perform their turn.
   */
  @Override
  public void onModelUpdate() {
    if (game.getCurrentPlayer() == null) {
      return; // Game might be over or not yet started
    }

    PlayerController controller = controllers.get(game.getCurrentPlayer().getRole());

    if (controller != null) {
      try {
        controller.takeTurn();
      } catch (Exception e) {
        System.err.println("Controller failed to take turn: " + e.getMessage());
        e.printStackTrace(); // optional for debugging
      }
    }
  }
}

