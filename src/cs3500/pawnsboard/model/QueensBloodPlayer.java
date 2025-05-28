package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a player in the game PawnsBoard.
 *
 * <p>Invariants:
 * - Each player must have exactly one role (`Role.RED` or `Role.BLUE`). - The player's deck must be
 * non-null and contain enough cards for the game. - The player's hand size must never exceed the
 * designated maximum hand size. - The player’s deck must contain no more than two copies of any
 * card. - The player can only play cards they own and must have enough pawns to cover the card's
 * cost.
 */
public class QueensBloodPlayer implements Player<Card> {

  private final int handSize;
  private final Role role;
  private final List<Card> hand;
  private List<Card> deck;
  private int originalDeckSize = 0;

  /**
   * Constructs a QueensBloodPlayer with the specified role and hand size.
   *
   * @param role     the role of the player
   * @param handSize the maximum number of cards a player can hold initially
   */
  public QueensBloodPlayer(Role role, int handSize) {
    if (role == null || handSize <= 0) {
      throw new IllegalArgumentException("Invalid Input.");
    }
    this.role = role;
    this.deck = new ArrayList<>();
    this.hand = new ArrayList<>();
    this.handSize = handSize;
  }

  /**
   * Copy constructor that creates a deep copy of another QueensBloodPlayer.
   *
   * @param other the player to copy
   */
  public QueensBloodPlayer(QueensBloodPlayer other) {
    this.role = other.role;
    this.handSize = other.handSize;
    this.hand = deepCopyCardList(other.hand);
    this.deck = deepCopyCardList(other.deck);
    this.originalDeckSize = other.originalDeckSize;
  }



  /**
   * Initializes the player's deck and hand.
   *
   * @param boardCapacity the minimum number of cards required in the deck
   * @throws IllegalStateException if the deck file is missing, incorrectly formatted, or does not
   *                               contain enough cards
   */
  @Override
  public void initializePlayer(int boardCapacity, List<Card> newDeck) {
    this.deck = newDeck;

    this.originalDeckSize = this.deck.size();

    if (deck.size() < boardCapacity) {
      throw new IllegalStateException("Not enough cards in the deck to fill the board.");
    }

    if (this.handSize > deck.size() / 3) {
      throw new IllegalStateException(
          "Starting hand size cannot be greater than a third of the deck size.");
    }

    this.hand.clear();
    List<Card> costOneCards = new ArrayList<>();

    for (Card card : deck) {
      if (card.getCost() == 1) {
        costOneCards.add(card);
      }
    }

    if (!costOneCards.isEmpty()) {
      Card selectedCostOneCard = getRandomCardFromDeck(costOneCards);
      this.hand.add(selectedCostOneCard);
      deck.remove(selectedCostOneCard);
    } else {
      // Enforced to have a card with cost of 1 at the very beginning of the game to make sure
      // game can continue smoothly.
      throw new IllegalStateException("Game cannot continue due to missing card with cost 1.");
    }

    while (this.hand.size() < handSize && !deck.isEmpty()) {
      Card selectedCard = getRandomCardFromDeck(deck);
      this.hand.add(selectedCard);
    }

    Random random = new Random();
    int otherIndex = random.nextInt(hand.size() - 1) + 1;
    Card temp = this.hand.get(0);
    this.hand.set(0, this.hand.get(otherIndex));
    this.hand.set(otherIndex, temp);

  }

  /**
   * Returns the player's current hand.
   *
   * @return a list of cards in the player's hand
   */
  @Override
  public List<Card> getHand() {
    return this.hand;
  }

  /**
   * Returns the number of remaining cards in the player's deck.
   *
   * @return the count of cards left in the deck
   */
  @Override
  public int getRemainingDeckSize() {
    return this.deck.size();
  }

  private Card getRandomCardFromDeck(List<Card> sourceDeck) {
    if (sourceDeck.isEmpty()) {
      throw new IllegalStateException("The deck is empty. Cannot draw a card.");
    }
    Random random = new Random();
    return sourceDeck.remove(random.nextInt(sourceDeck.size()));
  }

  /**
   * Draws an adaptive card based on the current board state. The method prioritizes selecting a
   * card that the player can play based on pawn availability. If all owned cells have only one
   * pawn, it prioritizes drawing low-cost cards. If the highest pawn count is exactly 2, it reduces
   * the chance of drawing a cost-3 card. Otherwise, it follows a normal drawing probability.
   *
   * @param board The current state of the game board.
   * @return The drawn card from the deck, or null if the deck is empty.
   */
  public Card drawNewCard(Board<Card> board) {
    if (deck.isEmpty()) {
      return null;
    }

    List<Card> lowCostCards = new ArrayList<>();
    List<Card> midCostCards = new ArrayList<>();
    List<Card> highCostCards = new ArrayList<>();

    categorizeCardsByCost(lowCostCards, midCostCards, highCostCards);

    int highestPawnCount = getHighestPawnCount(board);
    boolean onlyPawnOneCells = areAllPawnsOne(board);

    Random rand = new Random();
    Card selectedCard;

    // If the current deck size is 3/5 of the original deck size, the drawing mode will be
    // completely random, without preferring a category of card with a specific cost.
    if (deck.size() <= (originalDeckSize * 3) / 5) {
      Card randomCardFromDeck = this.getRandomCardFromDeck(this.deck);
      this.hand.add(randomCardFromDeck);
      return randomCardFromDeck;
    }

    if (onlyPawnOneCells) {
      selectedCard = drawCardWithPriority(lowCostCards, midCostCards, highCostCards);
    } else if (highestPawnCount == 2) {
      selectedCard = drawCardWithReducedCost3Probability(lowCostCards, midCostCards, highCostCards,
          rand);
    } else {
      selectedCard = drawCardNormally(lowCostCards, midCostCards, highCostCards, rand);
    }

    deck.remove(selectedCard);
    this.hand.add(selectedCard);

    return selectedCard;
  }

  /**
   * Categorizes the available cards in the deck into three lists based on their cost.
   *
   * @param lowCost  The list to store cards with cost 1.
   * @param midCost  The list to store cards with cost 2.
   * @param highCost The list to store cards with cost 3.
   */
  private void categorizeCardsByCost(List<Card> lowCost, List<Card> midCost, List<Card> highCost) {
    for (Card card : deck) {
      if (card.getCost() == 1) {
        lowCost.add(card);
      } else if (card.getCost() == 2) {
        midCost.add(card);
      } else {
        highCost.add(card);
      }
    }
  }

  /**
   * Finds the highest number of pawns among the player's owned cells.
   *
   * @param board The current state of the game board.
   * @return The highest number of pawns in any cell owned by the player.
   */
  private int getHighestPawnCount(Board<Card> board) {
    int highest = 0;
    for (Cell<Card>[] row : board.getGrid()) {
      for (Cell<Card> cell : row) {
        if (cell.getOwner() == this.getRole()) {
          highest = Math.max(highest, cell.getPawns());
        }
      }
    }
    return highest;
  }

  /**
   * Checks if all cells owned by the player contain only 1 pawn. This is used to determine if
   * drawing a low-cost card should be prioritized.
   *
   * @param board The current state of the game board.
   * @return True if all owned cells contain only 1 pawn, false otherwise.
   */
  private boolean areAllPawnsOne(Board<Card> board) {
    for (Cell<Card>[] row : board.getGrid()) {
      for (Cell<Card> cell : row) {
        if (cell.getOwner() == this.getRole() && cell.getPawns() > 1) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Selects a card with priority given to low-cost and mid-cost cards. If no low-cost cards are
   * available, mid-cost cards are preferred, and high-cost cards are the last option.
   *
   * @param lowCost  The list of low-cost (cost 1) cards.
   * @param midCost  The list of mid-cost (cost 2) cards.
   * @param highCost The list of high-cost (cost 3) cards.
   * @return The selected card from the prioritized cost group.
   */
  private Card drawCardWithPriority(List<Card> lowCost, List<Card> midCost, List<Card> highCost) {
    if (!lowCost.isEmpty()) {
      return getRandomCardFromDeck(lowCost);
    } else if (!midCost.isEmpty()) {
      return getRandomCardFromDeck(midCost);
    } else {
      return getRandomCardFromDeck(highCost);
    }
  }

  /**
   * Selects a card while reducing the probability of drawing a cost-3 card when the highest pawn
   * count is 2. 60% chance to pick a cost-1 card (if available). 30% chance to pick a cost-2 card
   * (if available). 10% chance to pick a cost-3 card (if available).
   *
   * @param lowCost  The list of low-cost (cost 1) cards.
   * @param midCost  The list of mid-cost (cost 2) cards.
   * @param highCost The list of high-cost (cost 3) cards.
   * @param rand     The random generator to determine selection probability.
   * @return The selected card based on the probability distribution.
   */
  private Card drawCardWithReducedCost3Probability(List<Card> lowCost, List<Card> midCost,
      List<Card> highCost, Random rand) {
    int choice = rand.nextInt(100);

    if (choice < 60 && !lowCost.isEmpty()) {
      return getRandomCardFromDeck(lowCost);
    } else if (choice < 90 && !midCost.isEmpty()) {
      return getRandomCardFromDeck(midCost);
    } else if (!highCost.isEmpty()) {
      return getRandomCardFromDeck(highCost);
    } else {
      return getRandomCardFromDeck(deck);
    }
  }

  /**
   * Selects a card with a normal distribution: 50% chance to pick a cost-1 card 30% chance to pick
   * a cost-2 card 20% chance to pick a cost-3 card.
   *
   * @param lowCost  The list of low-cost (cost 1) cards.
   * @param midCost  The list of mid-cost (cost 2) cards.
   * @param highCost The list of high-cost (cost 3) cards.
   * @param rand     The random generator to determine selection probability.
   * @return The selected card based on the normal probability distribution.
   */
  private Card drawCardNormally(List<Card> lowCost, List<Card> midCost, List<Card> highCost,
      Random rand) {
    int choice = rand.nextInt(100);

    if (choice < 50 && !lowCost.isEmpty()) {
      return getRandomCardFromDeck(lowCost);
    } else if (choice < 80 && !midCost.isEmpty()) {
      return getRandomCardFromDeck(midCost);
    } else if (!highCost.isEmpty()) {
      return getRandomCardFromDeck(highCost);
    } else {
      return getRandomCardFromDeck(deck);
    }
  }

  /**
   * Returns the role of the player.
   *
   * @return the player's role
   */
  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    QueensBloodPlayer other = (QueensBloodPlayer) obj;
    return this.role == other.role;
  }

  @Override
  public int hashCode() {
    return this.role.hashCode();
  }

  @Override
  public String toString() {
    return role.toString();
  }

  @Override
  public Player<Card> clone() {
    return new QueensBloodPlayer(this);
  }

  /**
   * Deep copies a list of cards. Assumes cards are immutable or clone-safe.
   *
   * @param original the original list to copy
   * @return a new list with copied references
   */
  private List<Card> deepCopyCardList(List<Card> original) {
    return new ArrayList<>(original);
  }

}
