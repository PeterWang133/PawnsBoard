package cs3500.pawnsboard.view;

import cs3500.pawnsboard.controller.PlayerActionListener;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyGame;
import cs3500.pawnsboard.model.Role;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * The GUI implementation of the view. Displays the board, hand cards, current status, and handles
 * input events.
 */
public class QueensBloodGUIView extends JFrame implements GUIView {

  private final ReadonlyGame<Card> model;
  private final JLabel statusLabel;
  private final JPanel boardPanel;
  private final JPanel handCardPanel;
  private final Role role;
  private final JButton passTurnButton;
  private boolean gameOverShown = false;
  private PlayerActionListener listener;

  /**
   * Constructs the GUI view for the game.
   *
   * @param model the read-only model used to render the view
   */
  public QueensBloodGUIView(ReadonlyGame<Card> model, Role role) {
    super("Pawns Board Game");
    this.model = model;
    this.role = role;

    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setResizable(true);

    statusLabel = new JLabel("Welcome to PawnsBoard! Current player: RED", SwingConstants.CENTER);
    statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
    statusLabel.setForeground(new Color(255, 255, 255));
    statusLabel.setBackground(new Color(120, 126, 130));
    statusLabel.setOpaque(true);
    statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

    add(statusLabel, BorderLayout.NORTH);

    boardPanel = new JPanel();
    boardPanel.setLayout(new GridLayout(model.getBoard().getHeight(), model.getBoard().getWidth()));
    add(boardPanel, BorderLayout.CENTER);
    renderBoardGrid();

    handCardPanel = new JPanel();
    handCardPanel.setLayout(new FlowLayout());
    add(handCardPanel, BorderLayout.SOUTH);
    renderHandCards();

    passTurnButton = new JButton("Pass Turn");
    passTurnButton.addActionListener(e -> {
      if (listener != null) {
        listener.onPassTurn();
      }
    });

    add(passTurnButton, BorderLayout.EAST);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (listener != null) {
            listener.onConfirmMove();
          }
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
          listener.onPassTurn();
        }
      }
    });

    setVisible(true);
    setFocusable(true);
  }

  /**
   * Renders the current state of the board grid. Includes pawns, cards, scores and selection
   * highlights.
   */
  @Override
  public void renderBoardGrid() {
    boardPanel.removeAll();
    Board<Card> board = model.getBoard();
    int[] selectedCell = model.getSelectedCellCoordinate();

    for (int i = 0; i < board.getHeight(); i++) {
      Map<Role, Integer> rowScores = model.getBoard().getRowScores(i);
      int redScore = rowScores.get(Role.RED);
      int blueScore = rowScores.get(Role.BLUE);

      addCellForScore(Role.RED, redScore, redScore > blueScore);
      for (int j = 0; j < board.getWidth(); j++) {
        Cell<Card> cell = board.getCellAt(i, j);
        JButton button = new JButton(generateCellText(cell));

        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        if (selectedCell.length == 2 && selectedCell[0] == i && selectedCell[1] == j) {
          button.setBackground(Color.CYAN);
        } else {
          button.setBackground(generateCellColor(cell));
        }

        final int row = i;
        final int col = j;
        button.addActionListener(e -> {
          if (listener != null) {
            listener.onCellSelected(row, col);
          }
        });

        boardPanel.add(button);
      }
      addCellForScore(Role.BLUE, blueScore, blueScore > redScore);
    }

    boardPanel.revalidate();
    boardPanel.repaint();
  }

  private void addCellForScore(Role role, int score, boolean isLarger) {

    JButton button = new JButton(String.valueOf(score));
    button.setOpaque(true);
    button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    button.setBackground(Color.WHITE);

    if (isLarger) {
      button.setFont(new Font("Arial", Font.BOLD, 50));
      button.setForeground(role == Role.RED ? Color.RED : Color.BLUE);
    }

    boardPanel.add(button);

  }

  /**
   * Renders the current player's hand cards. Includes selection highlights.
   */
  @Override
  public void renderHandCards() {
    handCardPanel.removeAll();
    int selectedCardIndex = model.getSelectedCardIndex();
    Player<Card> player = this.model.getPlayer(this.role);

    for (int i = 0; i < player.getHand().size(); i++) {
      Card card = player.getHand().get(i);
      String cardContent = card.toString();

      JTextArea cardTextArea = new JTextArea(cardContent);
      cardTextArea.setEditable(false);
      cardTextArea.setOpaque(true);
      cardTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

      if (i == selectedCardIndex && model.getCurrentPlayer().getRole() == role) {
        cardTextArea.setBackground(Color.CYAN);
      } else {
        cardTextArea.setBackground(player.getRole() == Role.RED ? Color.RED : Color.BLUE);
      }

      final int cardIndex = i;
      cardTextArea.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (listener != null) {
            listener.onCardSelected(cardIndex);
          }
        }
      });
      handCardPanel.add(cardTextArea);
    }

    handCardPanel.revalidate();
    handCardPanel.repaint();
  }

  private String generateCellText(Cell<Card> cell) {
    if (cell.getCard() != null) {
      return "card " + cell.getCard().getName() + ": " + cell.getCard().getValue() + " (value)";
    } else if (cell.getPawns() > 0) {
      return cell.getPawns() + " pawns";
    } else {
      return "";
    }
  }

  private Color generateCellColor(Cell<Card> cell) {
    if (cell.getOwner() == Role.RED) {
      return Color.RED;
    } else if (cell.getOwner() == Role.BLUE) {
      return Color.BLUE;
    } else {
      return Color.lightGray;
    }
  }

  public void addPlayerActionListener(PlayerActionListener listener) {
    this.listener = listener;
  }

  /**
   * Refreshes the entire GUI view. This typically happens when the model is updated.
   */
  @Override
  public void refreshView() {
    updateStatusLabel();
    renderBoardGrid();
    renderHandCards();

    if (model.isGameOver() && !gameOverShown) {
      gameOverShown = true; // prevent further dialogs

      String winnerMsg;
      Role wonPlayer = model.getWonPlayer();
      if (wonPlayer == Role.RED) {
        winnerMsg = "Game Over! RED wins!";
      } else if (wonPlayer == Role.BLUE) {
        winnerMsg = "Game Over! BLUE wins!";
      } else {
        winnerMsg = "Game Over! It's a tie!";
      }

      Map<Role, Integer> finalScores = model.getBoard().getScores();
      int redScore = finalScores.getOrDefault(Role.RED, 0);
      int blueScore = finalScores.getOrDefault(Role.BLUE, 0);

      winnerMsg += "\nFinal Scores:\nRED: " + redScore + " | BLUE: " + blueScore;

      String finalWinnerMsg = winnerMsg;
      javax.swing.SwingUtilities.invokeLater(() ->
          javax.swing.JOptionPane.showMessageDialog(this,
              finalWinnerMsg, "Game Over",
              javax.swing.JOptionPane.INFORMATION_MESSAGE)
      );
    }
    if (model.isGameOver()) {
      passTurnButton.setEnabled(false);  // disable pass button
    }

    System.out.println("Model updated, refreshing view.");
  }


  private void updateStatusLabel() {
    Role role = model.getCurrentPlayer().getRole();
    this.statusLabel.setText("Current player: " + role);
  }
}
