package cs3500.pawnsboard.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import cs3500.pawnsboard.model.Role;
import org.junit.Before;
import org.junit.Test;


public class HumanGUIControllerTest {

  private MockGameforHumanGUI game;
  private HumanGUIController controller;
  private MockViewforHumanGUI view;

  @Before
  public void setup() {
    game = new MockGameforHumanGUI();
    controller = new HumanGUIController(game, Role.RED);
    view = new MockViewforHumanGUI();
    controller.setView(view);
  }

  @Test
  public void testHandleCardClick() {
    game.selectedCard = -1;
    controller.handleCardClick(2);
    assertEquals(2, game.selectedCard);

    // clicking again should deselect
    controller.handleCardClick(2);
    assertEquals(-1, game.selectedCard);
  }

  @Test
  public void testHandleCellClick() {
    game.setSelectedCellCoordinate(-1, -1);
    controller.handleCellClick(1, 3);
    assertArrayEquals(new int[]{1, 3}, game.getSelectedCellCoordinate());

    // clicking same cell again should deselect
    controller.handleCellClick(1, 3);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testPassTurn() {
    controller.handlePassTurn();
    assertTrue(game.passed);
    assertTrue(game.switched);
    assertEquals(-1, game.selectedCard);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testConfirmMove() {
    game.setSelectedCardIndex(0);
    game.setSelectedCellCoordinate(1, 1);
    controller.confirmMove();

    assertTrue(game.cardPlaced);
    assertTrue(game.reset);
    assertTrue(game.switched);
    assertEquals(-1, game.selectedCard);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testConfirmMoveFailsIfIncomplete() {
    game.setSelectedCardIndex(-1);
    game.setSelectedCellCoordinate(1, 1);
    controller.confirmMove();

    assertFalse(game.cardPlaced); // should not place card
  }

  @Test
  public void testModelUpdatedRefreshesView() {
    controller.onModelUpdate();
    assertTrue(view.refreshed);
  }

  @Test
  public void testViewRegistersListener() {
    assertNotNull(view.listener);
  }

  @Test
  public void testHandleCardClickWhenNotPlayersTurn() {
    game.role = Role.BLUE; // Controller is RED
    game.selectedCard = -1;

    controller.handleCardClick(1);
    assertEquals(-1, game.selectedCard); // Should not update
  }

  @Test
  public void testHandleCellClickWhenNotPlayersTurn() {
    game.role = Role.BLUE; // Controller is RED
    game.setSelectedCellCoordinate(-1, -1);

    controller.handleCellClick(2, 2);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testHandlePassTurnWhenNotPlayersTurn() {
    game.role = Role.BLUE; // Not RED's turn
    controller.handlePassTurn();

    assertFalse(game.passed);
    assertFalse(game.switched);
  }

  @Test
  public void testConfirmMoveThrowsIllegalMoveException() {
    game.throwOnPlaceCard = true; // simulate an exception
    game.setSelectedCardIndex(0);
    game.setSelectedCellCoordinate(1, 1);

    controller.confirmMove();

    assertFalse(game.switched); // should not proceed
    assertEquals(-1, game.selectedCard);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testRapidCardAndCellSelection() {
    controller.handleCardClick(0);
    controller.handleCardClick(1);
    controller.handleCellClick(0, 0);
    controller.handleCellClick(0, 1);

    assertEquals(1, game.selectedCard);
    assertArrayEquals(new int[]{0, 1}, game.getSelectedCellCoordinate());
  }

  @Test
  public void testDoubleConfirmMoveCall() {
    game.setSelectedCardIndex(0);
    game.setSelectedCellCoordinate(1, 1);
    controller.confirmMove(); // first call: should proceed
    controller.confirmMove(); // second call: nothing should happen

    assertTrue(game.cardPlaced); // only first triggered
    assertEquals(-1, game.selectedCard);
    assertArrayEquals(new int[]{-1, -1}, game.getSelectedCellCoordinate());
  }

}
