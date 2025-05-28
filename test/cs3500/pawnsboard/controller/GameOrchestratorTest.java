package cs3500.pawnsboard.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import cs3500.pawnsboard.model.Role;
import org.junit.Before;
import org.junit.Test;

public class GameOrchestratorTest {

  private MockGame game;
  private MockPlayerController redController;
  private MockPlayerController blueController;
  private GameOrchestrator orchestrator;

  @Before
  public void setUp() {
    redController = new MockPlayerController();
    blueController = new MockPlayerController();
    game = new MockGame();
    orchestrator = new GameOrchestrator(game, redController, blueController);
  }

  @Test
  public void testRedPlayerTurn() {
    game.setCurrentPlayer(new MockPlayer(Role.RED));

    orchestrator.onModelUpdate();

    assertTrue(redController.tookTurn);
    assertFalse(blueController.tookTurn);
  }

  @Test
  public void testBluePlayerTurn() {
    game.setCurrentPlayer(new MockPlayer(Role.BLUE));

    orchestrator.onModelUpdate();

    assertFalse(redController.tookTurn);
    assertTrue(blueController.tookTurn);
  }

  @Test
  public void testNullPlayer() {
    game.setCurrentPlayer(null);

    orchestrator.onModelUpdate();

    assertFalse(redController.tookTurn);
    assertFalse(blueController.tookTurn);
  }

  @Test
  public void testObserverRegistration() {
    assertTrue(game.observerAdded);
  }

  @Test
  public void testControllerThrowsException() {
    PlayerController failingCtrl = new FailingPlayerController();
    game.setCurrentPlayer(new MockPlayer(Role.RED));
    GameOrchestrator crashingOrchestrator =
        new GameOrchestrator(game, failingCtrl, blueController);

    try {
      crashingOrchestrator.onModelUpdate();
    } catch (Exception e) {
      assert false : "Orchestrator should not crash when controller fails";
    }
  }

  @Test
  public void testMissingControllerForRole() {
    game.setCurrentPlayer(new MockPlayer(Role.RED));

    // Make RED controller map entry missing
    GameOrchestrator incompleteOrchestrator =
            new GameOrchestrator(game, null, blueController);  // will put null in map

    try {
      incompleteOrchestrator.onModelUpdate();  // should not crash
    } catch (Exception e) {
      assert false : "Should gracefully handle null controller";
    }

    assertFalse(blueController.tookTurn); // no BLUE turn
  }

}
