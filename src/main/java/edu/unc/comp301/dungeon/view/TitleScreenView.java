package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TitleScreenView implements FXComponent {

  private final Controller controller;
  private final Model model;
  private final Theme theme;
  private final Runnable onToggleTheme;

  public TitleScreenView(Controller controller, Model model, Theme theme, Runnable onToggleTheme) {
    this.controller = controller;
    this.model = model;
    this.theme = theme;
    this.onToggleTheme = onToggleTheme;
  }

  @Override
  public Parent render() {
    Label title = new Label("DUNGEON CRAWLER");
    title.getStyleClass().add("title");

    Label high = new Label("High Score: " + model.getHighScore());
    Label last = new Label("Last Score: " + model.getCurScore());

    Button start = new Button("START GAME");
    start.getStyleClass().add("start-button");
    start.setOnAction(e -> controller.startGame());

    // Theme switch
    ToggleButton themeToggle = new ToggleButton("Theme: " + theme.getLabel());
    themeToggle.setSelected(theme == Theme.LIGHT);
    themeToggle.getStyleClass().add("switch");
    themeToggle.setOnAction(e -> onToggleTheme.run());

    // Hard mode switch
    ToggleButton hardToggle =
            new ToggleButton(model.isHardMode() ? "Hard Mode: ON" : "Hard Mode: OFF");
    hardToggle.setSelected(model.isHardMode());
    hardToggle.getStyleClass().add("switch");
    hardToggle.setOnAction(e -> controller.toggleHardMode());

    HBox toggles = new HBox(16, themeToggle, hardToggle);
    toggles.setAlignment(Pos.CENTER);

    Label by = new Label("By Maleea");
    by.getStyleClass().add("byline");

    VBox box = new VBox(22, title, high, last, start, toggles, by);
    box.setAlignment(Pos.CENTER);

    StackPane root = new StackPane(box);
    root.getStyleClass().addAll("title-root", theme.getCssClass());
    return root;
  }
}