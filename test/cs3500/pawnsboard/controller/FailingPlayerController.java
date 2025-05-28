package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.view.GUIView;

public class FailingPlayerController implements PlayerController {

  @Override
  public void takeTurn() {
    throw new RuntimeException("Simulated failure");
  }

  @Override
  public void handleCardClick(int newCardIndex) {

  }

  @Override
  public void handleCellClick(int row, int col) {

  }

  @Override
  public void handlePassTurn() {

  }

  @Override
  public void confirmMove() {

  }

  @Override
  public void setView(GUIView view) {

  }

  @Override
  public void onModelUpdate() {

  }
}
