package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Role;
import cs3500.pawnsboard.view.GUIView;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Controller for the human player using the GUI interface.
 */
public class HumanGUIController implements PlayerController, PlayerActionListener {

  private final Game<Card> game;
  private final Role role;
  private GUIView view;

  /**
   * Constructs a HumanGUIController for the given name and player role.
   *
   * @param game the game model
   * @param role the role of the player controlled by this controller
   */
  public HumanGUIController(Game<Card> game, Role role) {
    this.game = game;
    this.role = role;
  }

  /**
   * Called when it's the human player's turn. Waits for input via GUI interaction.
   */
  @Override
  public void takeTurn() {
    // Human input is already event-driven through the GUI
    System.out.println("Waiting for human input: " + role);
  }

  /**
   * Called when the model updates. Refreshes the GUI view if available.
   */
  @Override
  public void onModelUpdate() {
    if (view != null) {
      view.refreshView();
    }
  }

  /**
   * Sets the GUI view for the controller and registers this controller as a listener to user actions.
   *
   * @param view the GUI view
   */
  @Override
  public void setView(GUIView view) {
    this.view = view;
    view.addPlayerActionListener(this);
  }

  /**
   * Handles a card being clicked by the user in the hand panel.
   *
   * @param cardIdx the index of the clicked card
   */
  public void handleCardClick(int cardIdx) {
    if (game.getCurrentPlayer().getRole() != role) {
      return;
    }
    int current = game.getSelectedCardIndex();
    game.setSelectedCardIndex(current == cardIdx ? -1 : cardIdx);
  }

  /**
   * Handles a board cell click by the user.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  public void handleCellClick(int row, int col) {
    if (game.getCurrentPlayer().getRole() != role) {
      return;
    }
    int[] selected = game.getSelectedCellCoordinate();
    game.setSelectedCellCoordinate((selected[0] == row && selected[1] == col) ? -1 : row,
        (selected[0] == row && selected[1] == col) ? -1 : col);
  }

  /**
   * Handles the user choosing to pass their turn.
   *
   */
  public void handlePassTurn() {
    if (game.getCurrentPlayer().getRole() != role) {
      return;
    }
    JOptionPane.showMessageDialog((JFrame) view, game.getCurrentPlayer() + " passed turn",
        "Move Details", JOptionPane.INFORMATION_MESSAGE);
    game.increaseConsecutivePass();
    game.setSelectedCardIndex(-1);
    game.setSelectedCellCoordinate(-1, -1);
    game.switchCurrentPlayer();
  }

  /**
   * Confirms the user's move if both a card and a board cell have been selected.
   */
  public void confirmMove() {
    if (game.getCurrentPlayer().getRole() != role) {
      return;
    }

    int[] cell = game.getSelectedCellCoordinate();
    int cardIdx = game.getSelectedCardIndex();
    if (cardIdx != -1 && cell[0] != -1 && cell[1] != -1) {
      try {
        game.placeCard(cell[0], cell[1], cardIdx);
        JOptionPane.showMessageDialog((JFrame) view,
            game.getCurrentPlayer() + " placed card in row " + cell[0] + " and col "
                    + cell[1], "Move Details", JOptionPane.INFORMATION_MESSAGE);
        game.setSelectedCardIndex(-1);
        game.setSelectedCellCoordinate(-1, -1);
        game.resetConsecutivePass();
        game.switchCurrentPlayer();
      } catch (IOException | IllegalArgumentException | IllegalStateException e) {
        JOptionPane.showMessageDialog((JFrame) view, e.getMessage(), "Invalid Move",
            JOptionPane.ERROR_MESSAGE);
        game.setSelectedCardIndex(-1);
        game.setSelectedCellCoordinate(-1, -1);
      }
    } else {
      System.out.println("Invalid move. Selection incomplete.");
    }
  }

  /**
   * Called when the user selects a card in the GUI.
   *
   * @param index the index of the selected card in the player's hand
   */
  @Override
  public void onCardSelected(int index) {
    this.handleCardClick(index);
  }

  /**
   * Called when the user selects a board cell in the GUI.
   *
   * @param row the row of the selected cell
   * @param col the column of the selected cell
   */
  @Override
  public void onCellSelected(int row, int col) {
    handleCellClick(row, col);
  }

  /**
   * Called when the user confirms their move.
   *
   */
  @Override
  public void onConfirmMove() {
    confirmMove();
  }

  /**
   * Called when the user passes their turn.
   */
  @Override
  public void onPassTurn() {
    handlePassTurn();
  }
}
