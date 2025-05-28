package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.QueensBloodCard;
import cs3500.pawnsboard.model.Role;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for loading a deck of cards from a configuration file.
 *
 * <p>Ensures that the deck follows the correct format and can be used in the game.
 * Each deck must contain at most two copies of any card.
 */
public class DeckLoader {

  private final String filePath;

  /**
   * Constructs a DeckLoader with the specified file path.
   *
   * @param filePath the path to the deck configuration file
   * @throws IllegalArgumentException if the file path is null or empty
   */
  public DeckLoader(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      throw new IllegalArgumentException("Invalid file path.");
    }
    this.filePath = filePath;
  }

  /**
   * Reads and constructs a deck from the file.
   *
   * @param role the player who owns the deck
   * @return a list of cards representing the player's deck
   * @throws IOException if the file is missing, incorrectly formatted, or unreadable
   */
  public List<Card> loadDeck(Role role) throws IOException {
    File configFile = new File(filePath);

    if (!configFile.exists()) {
      throw new FileNotFoundException("Deck file not found: " + configFile.getAbsolutePath());
    }

    List<Card> deck = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(" ");
        if (parts.length != 3) {
          throw new IOException("Invalid deck format: missing card details.");
        }

        String name = parts[0];
        int cost = Integer.parseInt(parts[1]);
        int value = Integer.parseInt(parts[2]);

        char[][] influenceGrid = new char[5][5];
        for (int i = 0; i < 5; i++) {
          line = reader.readLine();
          if (line == null || line.length() != 5) {
            throw new IOException("Invalid deck format: incorrect influence grid.");
          }
          influenceGrid[i] = line.toCharArray();
        }

        deck.add(new QueensBloodCard(name, role, value, cost, influenceGrid));
      }
    }
    return deck;
  }

  /**
   * Returns the file path of the deck configuration file.
   *
   * @return the file path
   */
  public String getFilePath() {
    return filePath;
  }
}
