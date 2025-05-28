package cs3500.pawnsboard.view;

import java.io.IOException;

/**
 * Represents a view for displaying the game board. This interface defines the method for rendering
 * the current game state.
 */
public interface TextualView {

  /**
   * Renders the current state of the game board to the given output stream.
   *
   * @param out the Appendable object to which the game state will be rendered (must not be null)
   * @throws IOException              if an I/O error occurs while writing to the output stream
   * @throws IllegalArgumentException if the provided Appendable is null
   */
  void render(Appendable out) throws IOException;
}
