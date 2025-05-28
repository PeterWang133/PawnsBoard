package cs3500.pawnsboard.model;

import cs3500.pawnsboard.controller.ModelListener;

/**
 * Represents a subject in the Observer design pattern.
 * Allows listeners to subscribe, unsubscribe, and receive updates when the model changes.
 */
public interface ObservableModel {

  /**
   * Registers a listener to be notified of model updates.
   *
   * @param modelListener the listener to add
   */
  void addModelListener(ModelListener modelListener);

  /**
   * Unregisters a listener so it no longer receives model updates.
   *
   * @param modelListener the listener to remove
   */
  void removeModelListener(ModelListener modelListener);

  /**
   * Notifies all registered observers that the model has changed.
   */
  void notifyModelListeners();
}
