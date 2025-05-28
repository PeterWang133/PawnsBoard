package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.DeckLoader;
import cs3500.pawnsboard.controller.GameOrchestrator;
import cs3500.pawnsboard.controller.HumanGUIController;
import cs3500.pawnsboard.controller.MachineGUIController;
import cs3500.pawnsboard.controller.PlayerController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.ControlBoardStrategy;
import cs3500.pawnsboard.model.FillFirstStrategy;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.model.MinimaxStrategy;
import cs3500.pawnsboard.model.QueensBloodGame;
import cs3500.pawnsboard.model.Role;
import cs3500.pawnsboard.model.Strategy;
import cs3500.pawnsboard.model.StrategySelector;
import cs3500.pawnsboard.model.StrategySelectorImpl;
import cs3500.pawnsboard.view.QueensBloodGUIView;
import java.io.IOException;
import java.util.List;

/**
 * The entry point for the game.
 */
public class PawnsBoardGame {

  /**
   * Main method that runs the game.
   *
   * <p>Initializes the game components such as the controller, the game model, and the GUI view.
   * Then start the game and show the graphical user interface.
   *
   * @param args command-line arguments <red-deck-path> <blue-deck-path> <red-type> <blue-type>
   * @throws IOException if an I/O error occurs during the game
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      System.out.println(
          "Usage: java -jar pawnsboard.jar <red-deck> <blue-deck> <red-type> <blue-type>");
      System.exit(1);
    }

    String redDeckPath = args[0];
    String blueDeckPath = args[1];
    String redType = args[2].toLowerCase();
    String blueType = args[3].toLowerCase();

    Game<Card> game = new QueensBloodGame(3, 5, 5);

    DeckLoader redLoader = new DeckLoader(redDeckPath);
    DeckLoader blueLoader = new DeckLoader(blueDeckPath);
    List<Card> redDeck = redLoader.loadDeck(Role.RED);
    List<Card> blueDeck = blueLoader.loadDeck(Role.BLUE);

    StrategySelector selector = new StrategySelectorImpl();
    List<Strategy> strategies = List.of(
        new FillFirstStrategy(),
        new MaximizeRowScoreStrategy(),
        new ControlBoardStrategy(),
        new MinimaxStrategy()
    );

    PlayerController redCtrl;
    PlayerController blueCtrl;

    redCtrl = redType.equals("human") ?
        new HumanGUIController(game, Role.RED)
        : new MachineGUIController(game, Role.RED, strategies, selector);

    blueCtrl = blueType.equals("human") ?
        new HumanGUIController(game, Role.BLUE)
        : new MachineGUIController(game, Role.BLUE, strategies, selector);

    game.startGame(redDeck, blueDeck);

    QueensBloodGUIView redView = new QueensBloodGUIView(game, Role.RED);
    redView.setTitle("PawnsBoard - RED Player");
    redView.setLocation(100, 100);

    QueensBloodGUIView blueView = new QueensBloodGUIView(game, Role.BLUE);
    blueView.setTitle("PawnsBoard - BLUE Player");
    blueView.setLocation(950, 100);

    redCtrl.setView(redView);
    blueCtrl.setView(blueView);

    GameOrchestrator orchestrator = new GameOrchestrator(game, redCtrl, blueCtrl);

    game.addModelListener(redCtrl);
    game.addModelListener(blueCtrl);
    game.addModelListener(orchestrator);

    redView.setVisible(true);
    blueView.setVisible(true);
  }

}
