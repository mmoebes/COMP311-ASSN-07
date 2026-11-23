package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.Observer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class View implements FXComponent, Observer {

  private final Controller controller;
  private final Model model;
  private final Stage stage;
  private final int width;
  private final int height;
  private Theme theme = Theme.DARK;

  public View(Controller controller, Model model, Stage stage, int width, int height) {
    this.controller = controller;
    this.model = model;
    this.stage = stage;
    this.width = width;
    this.height = height;

    stage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (model.getStatus() != Model.STATUS.IN_PROGRESS) return;
      switch (e.getCode()) {
        case UP, W -> controller.moveUp();
        case DOWN, S -> controller.moveDown();
        case LEFT, A -> controller.moveLeft();
        case RIGHT, D -> controller.moveRight();
        default -> { return; }
      }
      e.consume();
    });
  }

  @Override
  public Parent render() {
    if (model.getStatus() == Model.STATUS.IN_PROGRESS) {
      return new GameView(controller, model, theme, this::toggleTheme).render();
    }
    return new TitleScreenView(controller, model, theme, this::toggleTheme).render();
  }

  @Override
  public void update() {
    Scene scene = new Scene(render(), width, height);
    scene.getStylesheets().add(View.class.getResource("/dungeon.css").toExternalForm());
    stage.setScene(scene);
  }

  private void toggleTheme() {
    theme = theme.next();
    update();
  }
}