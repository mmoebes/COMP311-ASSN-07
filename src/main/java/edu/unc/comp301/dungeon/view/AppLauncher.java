package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.controller.ControllerImpl;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.ModelImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

  private static final int WIDTH = 820;
  private static final int HEIGHT = 620;

  @Override
  public void start(Stage stage) {
    stage.setTitle("Maleea's Dungeon Crawler");

    // MVC setup
    Model model = new ModelImpl(10, 10);
    Controller pc = new ControllerImpl(model);
    View view = new View(pc, model, stage, WIDTH, HEIGHT);
    model.addObserver(view);

    // Initial scene
    Scene scene = new Scene(view.render(), WIDTH, HEIGHT);
    scene.getStylesheets().add(AppLauncher.class.getResource("/dungeon.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }
}
