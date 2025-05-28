package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Game;
import cs3500.pawnsboard.model.Role;
import cs3500.pawnsboard.view.TextualView;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A controller for handling the interaction between user and game in a textual format. It handles
 * user input and controls the flow of the game.
 */
public class QueensBloodTextualController implements TextualController {

  private final Appendable appendable;
  private final Scanner scanner;

  /**
   * Constructs a TextualController with the provided input and output streams.
   *
   * @param rd the input stream used for reading user input
   * @param ap the output stream used for displaying game information
   * @throws IllegalArgumentException if the input or output stream is null
   */
  public QueensBloodTextualController(Readable rd, Appendable ap) {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("Readable and Appendable cannot be null");
    }
    this.appendable = ap;
    this.scanner = new Scanner(rd);
  }

  /**
   * Starts and controls the flow of the game. It repeatedly accepts user input, processes the
   * moves, and updates the view accordingly.
   *
   * @param game the game being played
   * @param view the view responsible for rendering the game board
   * @throws IOException              if an IO error occurs during the interaction
   * @throws IllegalArgumentException if the game or view object is null
   */
  public <C extends Card> void playGame(Game<C> game, TextualView view) throws IOException {
    if (game == null || view == null) {
      throw new IllegalArgumentException("Game and View objects cannot be null");
    }

    try {
      DeckLoader blueReader = new DeckLoader("docs" + File.separator + "deck.config");
      DeckLoader redReader = new DeckLoader("docs" + File.separator + "deck.config");
      List<Card> blueDeck = blueReader.loadDeck(Role.BLUE);
      List<Card> redDeck = redReader.loadDeck(Role.RED);
      game.startGame(blueDeck, redDeck);
      runGameLoop(game, view);
    } catch (IOException e) {
      throw new IOException("Unable to receive input or transmit output: " + e);
    }
  }

  private <C extends Card> void drawNewCard(Game<C> game) throws IOException {
    Card newDrewCard = game.drawNewCardForCurrentPlayer();
    if (newDrewCard == null) {
      appendable.append("No new card has been drawn.\n");
    } else {
      appendable.append("\nThe new card drawn from the deck for you: " + newDrewCard + "\n");
    }
  }

  private void handlePlaceCardError(Exception e) throws IOException {
    String errorMsg = e.getMessage();

    // Special handling for "Not enough pawns" error
    if (errorMsg.contains("Not enough pawns to play this card.")) {
      appendable.append("\nError: ").append(errorMsg).append("\n");
      appendable.append("You need more pawns to play this card. Please choose another.\n");
    } else {
      appendable.append("\nInvalid move: ").append(errorMsg).append("\n");
      appendable.append("Please enter a valid move.\n");
    }
  }

  private <C extends Card> void runGameLoop(Game<C> game, TextualView view) throws IOException {
    int consecutivePass = 0;

    while (!game.isGameOver()) {
      appendable.append("\nCurrent Board:\n");
      view.render(this.appendable);
      appendable.append(game.getCurrentPlayer() + "'s turn!\n");

      drawNewCard(game);
      appendable.append("\nYour hand: \n\n");
      for (Card card : game.getCurrentPlayer().getHand()) {
        appendable.append(card.toString()).append("\n");
      }

      boolean validMove = false;
      while (!validMove) {
        appendable.append("\nEnter row and column to place a card (or -1 to pass), "
            + "with index starting from 0:\n");
        int row = scanner.nextInt();
        if (row == -1) {
          consecutivePass++;
          validMove = true;
          continue;
        }

        int col = scanner.nextInt();
        appendable.append("\nChoose card index from hand (index starting from 0): \n");
        int cardIndex = scanner.nextInt();

        try {
          game.placeCard(row, col, cardIndex);
          validMove = true; // If no exception, mark move as valid
          consecutivePass = 0; // Reset pass count since a move was made
        } catch (IllegalArgumentException | IllegalStateException e) {
          handlePlaceCardError(e);
        }
      }
      if (consecutivePass == 2) {
        appendable.append("\nBoth players have chosen to pass. Game ends early!\n");
        break;
      }
      game.switchCurrentPlayer();
    }
    handleGameOver(game, view);
  }


  private <C extends Card> void handleGameOver(Game<C> game, TextualView view) throws IOException {
    // Game Over
    appendable.append("\nGame Over!\nFinal Game State:\n");
    view.render(this.appendable);

    Map<Role, Integer> finalScores = game.getBoard().getScores();
    int redScore = 0;
    int blueScore = 0;

    for (Map.Entry<Role, Integer> entry : finalScores.entrySet()) {
      if (entry.getKey() == Role.RED) {
        redScore = entry.getValue();
      } else if (entry.getKey() == Role.BLUE) {
        blueScore = entry.getValue();
      }
    }

    appendable.append("\nFinal Scores:\n");
    appendable.append("Red Player: ").append(String.valueOf(redScore)).append("\n");
    appendable.append("Blue Player: ").append(String.valueOf(blueScore)).append("\n");

    if (redScore > blueScore) {
      appendable.append("\nRed Player wins!\n");
    } else if (blueScore > redScore) {
      appendable.append("\nBlue Player wins!\n");
    } else {
      appendable.append("\nIt's a tie!\n");
    }
  }

}
