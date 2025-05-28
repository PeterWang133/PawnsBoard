package cs3500.pawnsboard.controller;

/**
 * Represents an observer in the Observer design pattern. Implementing classes should define how to
 * respond when the model is updated.
 */
public interface ModelListener {

  /**
   * Called when the model has been updated and the listener should refresh its state/view.
   */
  void onModelUpdate();
}
