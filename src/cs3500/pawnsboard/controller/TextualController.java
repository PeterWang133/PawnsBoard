package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.view.TextualView;
import java.io.IOException;

/**
 * Interface representing a game controller that manages the interaction between user and game.
 */
public interface TextualController {

  /**
   * Starts and controls the flow of the game. It repeatedly accepts user input, processes the
   * moves, and updates the view accordingly.
   *
   * @param game the game being played
   * @param view the view responsible for rendering the game board
   * @param <C>  the type of card used in the game
   * @throws IOException if an IO error occurs during the interaction
   */
  <C extends Card> void playGame(Game<C> game, TextualView view) throws IOException;
}
