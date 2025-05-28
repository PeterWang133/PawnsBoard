package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.Role;
import cs3500.pawnsboard.model.Strategy;
import cs3500.pawnsboard.model.StrategySelector;
import cs3500.pawnsboard.view.GUIView;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A controller for an AI player using the GUI interface.
 */
public class MachineGUIController extends QueensBloodGUIController implements PlayerController,
    PlayerActionListener {

  private final Game<Card> game;
  private final Role role;
  private final StrategySelector strategySelector;
  private final List<Strategy> strategyPool;
  private GUIView view;
  private boolean isTakingTurn = false;

  /**
   * Constructs a MachineGUIController object with the given name, role, strategies and selector.
   *
   * @param game the game model
   * @param role the role of the AI player
   * @param strategies a list of strategies available for decision-making
   * @param selector the strategy selector that chooses the best strategy from the pool
   */
  public MachineGUIController(Game<Card> game, Role role,
      List<Strategy> strategies, StrategySelector selector) {
    super(game);
    this.game = game;
    this.role = role;
    this.strategyPool = strategies;
    this.strategySelector = selector;
  }

  /**
   * Called when it's the AI player's turn.
   * Selects and executes a move using the best strategy.
   */
  @Override
  public void takeTurn() {
    if (game.getCurrentPlayer().getRole() != role || isTakingTurn) {
      return;
    }

    isTakingTurn = true;
    try {
      Strategy strategy = strategySelector.selectBestStrategy(strategyPool, game, role);
      Move move = strategy.makeMove(game, role);
      if (move.isPass()) {
        JOptionPane.showMessageDialog((JFrame) view, game.getCurrentPlayer()
                        + " passed turn", "Move Details", JOptionPane.INFORMATION_MESSAGE);
        game.increaseConsecutivePass();
        game.setSelectedCardIndex(-1);
        game.setSelectedCellCoordinate(-1, -1);
        game.switchCurrentPlayer();
      } else {
        game.setSelectedCardIndex(move.getCardIndex());
        game.setSelectedCellCoordinate(move.getRow(), move.getCol());
        game.placeCard(move.getRow(), move.getCol(), move.getCardIndex());
        JOptionPane.showMessageDialog((JFrame) view,
            game.getCurrentPlayer() + " placed card in row " + move.getRow() + " and col "
                + move.getCol(), "Move Details", JOptionPane.INFORMATION_MESSAGE);
        game.resetConsecutivePass();
        game.switchCurrentPlayer();
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog((JFrame) view, "Machine move error: " + e.getMessage(),
          "Invalid Move", JOptionPane.ERROR_MESSAGE);
      game.increaseConsecutivePass();
      game.switchCurrentPlayer();
    } finally {
      isTakingTurn = false;
    }
  }

  /**
   * Called when the model updates. Refreshes the view if one is set.
   */
  @Override
  public void onModelUpdate() {
    if (view != null) {
      view.refreshView();
    }
  }

  /**
   * Sets the view for this controller to interact with.
   *
   * @param view the GUI view to be controlled
   */
  @Override
  public void setView(GUIView view) {
    this.view = view;
  }

  /**
   * Called when a card is selected in the GUI.
   * Updates the model's selected card index.
   *
   * @param index the index of the selected card in the player's hand
   */
  @Override
  public void onCardSelected(int index) {
    game.setSelectedCardIndex(index);
  }

  /**
   * Called when a cell is selected in the GUI. Updates the model's selected cell coordinate.
   *
   * @param row the row of the selected cell
   * @param col the column of the selected cell
   */
  @Override
  public void onCellSelected(int row, int col) {
    game.setSelectedCellCoordinate(row, col);
  }

  /**
   * Called when the AI confirms its move via the GUI. Tries to place the card on the board.
   */
  @Override
  public void onConfirmMove() {
    int[] cell = game.getSelectedCellCoordinate();
    int cardIdx = game.getSelectedCardIndex();

    if (cardIdx != -1 && cell[0] != -1 && cell[1] != -1) {
      try {
        game.placeCard(cell[0], cell[1], cardIdx);
        game.setSelectedCardIndex(-1);
        game.setSelectedCellCoordinate(-1, -1);
        game.resetConsecutivePass();
        game.switchCurrentPlayer();
      } catch (Exception e) {
        JOptionPane.showMessageDialog((JFrame) view, "AI Move Error: " + e.getMessage(),
            "Invalid Move", JOptionPane.ERROR_MESSAGE);
        game.setSelectedCardIndex(-1);
        game.setSelectedCellCoordinate(-1, -1);
      }
    }
  }

  /**
   * Called when the AI decides to pass its turn.
   */
  @Override
  public void onPassTurn() {
    game.increaseConsecutivePass();
    game.setSelectedCardIndex(-1);
    game.setSelectedCellCoordinate(-1, -1);
    game.switchCurrentPlayer();
  }
}
