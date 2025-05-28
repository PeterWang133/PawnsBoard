# Overview

PawnsBoard is a two-player strategy game where cards and pawns are used to compete for influence 
across a board. This project implements the full game logic and provides both textual and graphical 
user interfaces (GUI). The system follows the Model-View-Controller (MVC) design pattern, with 
support for Strategy design pattern, cloning, modelListener pattern, and multiple AI player strategies.

## High Level Assumptions
- The game is turn-based and strictly alternates turns between two players: RED and BLUE.
- Each player begins the game with a deck of cards, from which they draw a starting hand.
- Both red player and blue player has a deck a cards, from which they draw and place during 
- their turn.
- The board must have an odd number of columns greater than one.
- The system follows a MVC architecture for modularity.
- Card placement is based on legal placement rules, including ownership, pawn requirements, and 
  influence patterns.
- Game ends when either:
  - Both players pass consecutively
  - All cells on the board are filled with either pawns or cards.

# Class Invariants Breakdown

## 1. QueensBloodGame (Game Logic)
- The board must have at least one row and an odd number of columns greater than 1.
- The game must be initialized before any moves can be made.
- The game ends when both players pass consecutively or all board cells are filled.
- Players’ starting hand size cannot exceed one-third of their deck size.

## 2. QueensBloodBoard (Board Representation)
- The board is a fixed 2D grid with exactly (rows × cols) cells.
- Each cell must contain either pawns or a card, but not both.
- If a cell contains a card, its pawn count must be 0.
- If a cell contains pawns, it must contain between 1 and 3 pawns (inclusive).

## 3. QueensBloodPlayer (Player Attributes)
- Each player has exactly one role (RED or BLUE) that does not change.
- A player’s hand cannot exceed the designated maximum hand size.
- A player can only place cards they own and must have enough pawns to cover the card’s cost.
- A player's deck cannot contain more than two copies of the same card.

## 4. QueensBloodCard (Card Properties)
- The cost of a card must be 1, 2, or 3.
- The value of a card must be a non-negative integer.
- The influence grid must always be a 5×5 matrix.
- The center of the influence grid (influence[2][2]) must always be 'C'.
- The owner of the card cannot be null and must not change after instantiation.

## 5. QueensBloodCell (Board Cell Behavior)
- A cell can either contain pawns or a card, but not both.
- If a cell contains pawns, the number of pawns must be between 0 and 3 (inclusive).
- If a cell contains a card, the owner of the cell must match the owner of the card.
- A cell’s owner must either be null or match the owner of its contents (card or pawns).


# Quick Start

Below is a basic example of initializing and playing a game. Specifically, it initializes 
a QueensBloodGame with 3x5 board, and each player is dealt 3 cards initially. Then the game is 
started in textual mode.
```  
public static void main(String[] args) throws IOException {  
  Controller controller = new TextualController(new InputStreamReader(System.in), System.out);  
  Game<Card> game = new QueensBloodGame(3, 5, 3);  
  BoardView view = new BoardTextualView(game);   
  controller.playGame(game, view);  
}  
```
Below is an example of initializing and playing the same game but started in GUI mode.
```  
public static void main(String[] args) throws IOException {
Game<Card> game = new QueensBloodGame(3, 5, 3);
GUIView view = new QueensBloodGUIView();
GUIController controller = new QueensBloodGUIController(game, view);
controller.startGame();
}
```  

# Key Components

## 1. Controller (Driver)
- The **QueensBloodTextualController** manages user interaction and game progression.
- The **QueensBloodGUIController** handles user interaction in the GUI version using button clicks 
  and game events.
- Both controllers coordinate the flow of data between the model and the view.
- They validate user commands and handle game loop logic, including turn switching and move 
  validation.

## 2. Model (Driven)
- Implements the complete game logic, including board state, player actions, turn flow, 
  card placement, and scoring.
- Subcomponents:
	- **QueensBloodGame**: Manages game flow and player turns
	- **QueensBloodBoard**: Represents the grid where cards are placed and influence is applied
	- **QueensBloodCard**: Defines card properties, including value, cost and influence
	- **QueensBloodPlayer**: Represents a player, including their deck, hand, and actions
	- **QueensBloodCell**: Represents a single cell on the board, which contains pawns or a card
- Strategy Support:
  - FillFirstStrategy: Selects the first legal cell (top-left to bottom-right) for card placement.
  - ControlBoardStrategy: Chooses a move that maximizes total number of cells owned after placement.
  - MaximizeRowScoreStrategy: Prioritizes overtaking rows where the opponent currently leads in 
    score.
  - MinimaxStrategy: Uses a one-move lookahead to minimize the opponent's best possible scoring 
    response.

## 3. View (Driven)
- Responsible for rendering the game state.
- The **QueensBloodBoardTextualView** displays the board, player actions, and scores in a 
  textual format.
- The **QueensBloodBoardGUIView** displays the board, player actions, and scores in a graphical
  user interface.

  
# Source Organization
```
cs3500/pawnsboard/
│── controller/
│   ├── Controller.java
│   ├── TextualController.java
│   ├── GUIController.java
│   ├── QueensBloodGUIController.java
│
│── model/
│   ├── strategy/
│   │   ├── Strategy.java
│   │   ├── FillFirstStrategy.java
│   │   ├── ControlBoardStrategy.java
│   │   ├── MaximizeRowScoreStrategy.java
│   │   ├── MinimaxStrategy.java
│   │   ├── StrategyUtils.java
│   ├── Game.java
│   ├── ReadonlyGame.java
│   ├── QueensBloodGame.java
│   ├── Board.java
│   ├── QueensBloodBoard.java
│   ├── Cell.java
│   ├── QueensBloodCell.java
│   ├── Card.java
│   ├── QueensBloodCard.java
│   ├── Player.java
│   ├── QueensBloodPlayer.java
│   ├── Role.java
│   ├── Subject.java
│   ├── Observer.java
│
│── view/
│   ├── BoardView.java
│   ├── BoardTextualView.java
│   ├── GUIView.java
│   ├── QueensBloodGUIView.java
│
│── PawnsBoard.java    
```

This structure follows MVC principles. It separates game logic from input handling and rendering. 
The model directory contains game mechanics, controller directory handles interaction and view 
directory formats and renders the output.



# Changes for Part 2

## 1. Support copy of the board
To support creating a copy of the board, we add a new method clone in the Board interface and its 
subclass, the Cell interface and its subclass, the Card interface and its subclass.

## 2. Change owner type in QueensBloodCell and QueensBloodCard
To avoid storing the same player objects in multiple locations, we replace the type of owner from 
Player to Role in class QueensBloodCell and class QueensBloodCard.

## 3. Add read-only game interface
We add interface ReadonlyGame, which consists of the methods for getting the state of the game, 
like isGameOver and getBoard.

## 4. Add view related interface and class
We add interface GUIView and class QueensBloodGUIView for visualizing the game in a graphical 
interface. It has public method for renderBoardGrid, renderHandCards and refreshView.

## 5. Add controller
We add interface GUIController and class QueensBloodGUIController. It will respond to user 
interactions by calling model's methods to change model's state, for example, handleCardClick and 
handleCellClick.

## 6. Implement modelListener pattern
In the modelListener pattern, game (model) implements Subject interface, and controller implements 
Observer interface. Whenever game's state changes, it will notify controller and controller will 
then refresh the view.

## 7. Support for computer players
The game can be played not only by two human, but also a computer-based system and a human player.
The computer player will utilize different combinations of strategies as documented in Part 2 of
Key Component section.

# Changes for Part 3

## 1. Support for dual screen mode
The game now has two windows upon launching. Each window is designated to display the relevant 
infromation of one player, including that player's hand, the board, and their controls. The opponent
will not be able to see this player's hand in his or her own window. If the player wishes to pass 
turn, a "Pass Turn" button has been designed to pass the turn by clicking.

## 2. Human GUI Controler
The HumanGUIController serves as the controller for a human player in the graphical user interface. 
It listens for user actions through the PlayerActionListener interface and responds by updating 
the game model accordingly. It handles card selection, cell selection, move confirmation, and pass 
actions, ensuring that the player's interactions are valid and reflect the current turn.

## 3. Design of a machine player
The strategy design implemented in Part 2 is now integrated into a machine player that will select
the most optimal strategy for winning the game. To achieve this, Strategy and StrategySelector 
interfaces are being used to compute moves based on the current game state. 

## 4. Use of Listeners to communicate between Controller and Model
The PlayerActionListener interface was introduced to allow the GUI view to notify the
controller when the user interacts with the interface—such as selecting a card, clicking a cell, 
confirming a move, or passing a turn.

## 5. Turn observed by GameOrchestor class
The GameOrchestrator coordinates the turn-based flow of the game by observing the model as a 
ModelListener and delegating control to the appropriate PlayerController based on whose turn it is. 
Upon receiving a model update, it checks the current player and triggers that player's takeTurn() 
method. This design cleanly separates the responsibility of managing turn progression from 
individual controllers and ensures that only the correct player acts at the correct time. 