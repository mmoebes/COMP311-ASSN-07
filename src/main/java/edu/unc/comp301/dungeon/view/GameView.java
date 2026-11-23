package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.board.Posn;
import edu.unc.comp301.dungeon.model.pieces.Piece;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameView implements FXComponent {

  private static final int TILE = 48;
  private static final Map<String, Image> IMAGES = new HashMap<>();

  private final Controller controller;
  private final Model model;
  private final Theme theme;
  private final Runnable onToggleTheme;

  public GameView(Controller controller, Model model, Theme theme, Runnable onToggleTheme) {
    this.controller = controller;
    this.model = model;
    this.theme = theme;
    this.onToggleTheme = onToggleTheme;
  }

  @Override
  public Parent render() {
    BorderPane root = new BorderPane();
    root.getStyleClass().addAll("game-root", theme.getCssClass());
    root.setPadding(new Insets(16));

    HBox hud = new HBox(32);
    hud.getChildren().addAll(
            new Label("SCORE " + model.getCurScore()),
            new Label("LEVEL " + model.getLevel()),
            new Label("HIGH " + model.getHighScore()));
    if (model.isHardMode()) {
      Label hard = new Label("HARD");
      hard.getStyleClass().add("warn");
      hud.getChildren().add(hard);
    }
    if (model.getFrozenTurns() > 0) {
      Label frozen = new Label("FROZEN " + model.getFrozenTurns());
      frozen.getStyleClass().add("frozen");
      hud.getChildren().add(frozen);
    }
    hud.setAlignment(Pos.CENTER);
    hud.getStyleClass().add("hud");
    hud.setPadding(new Insets(0, 0, 12, 0));
    root.setTop(hud);

    GridPane grid = new GridPane();
    grid.getStyleClass().add("board");
    grid.setHgap(0);
    grid.setVgap(0);
    for (int r = 0; r < model.getHeight(); r++) {
      for (int c = 0; c < model.getWidth(); c++) {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("tile");
        cell.setMinSize(TILE, TILE);
        cell.setPrefSize(TILE, TILE);
        cell.setMaxSize(TILE, TILE);

        Piece p = model.get(new Posn(r, c));
        if (p != null) cell.getChildren().add(pieceNode(p));

        grid.add(cell, c, r);
      }
    }
    grid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    root.setCenter(new StackPane(grid));

    GridPane pad = new GridPane();
    pad.setHgap(6);
    pad.setVgap(6);
    pad.add(padButton("▲", controller::moveUp), 1, 0);
    pad.add(padButton("◀", controller::moveLeft), 0, 1);
    pad.add(padButton("▶", controller::moveRight), 2, 1);
    pad.add(padButton("▼", controller::moveDown), 1, 2);
    pad.setAlignment(Pos.CENTER);

    Label hint = new Label("Arrows / WASD");
    hint.getStyleClass().add("hint");

    Text mechanic = new Text(
            "FREEZE CRYSTAL\n"
                    + "One crystal appears in every room. Step on it to freeze "
                    + "every enemy for 3 turns. Enemies can't touch it, so "
                    + "plan your route!");
    mechanic.setWrappingWidth(200);
    mechanic.getStyleClass().add("info");

    ToggleButton themeToggle = new ToggleButton("Theme: " + theme.getLabel());
    themeToggle.setSelected(theme == Theme.LIGHT);
    themeToggle.getStyleClass().add("switch");
    themeToggle.setFocusTraversable(false);
    themeToggle.setOnAction(e -> onToggleTheme.run());

    VBox side = new VBox(18, pad, hint, mechanic, themeToggle);
    side.setAlignment(Pos.CENTER);
    side.setPadding(new Insets(0, 0, 0, 16));
    root.setRight(side);

    return root;
  }

  private Button padButton(String text, Runnable action) {
    Button b = new Button(text);
    b.getStyleClass().add("pad-button");
    b.setFocusTraversable(false);
    b.setOnAction(e -> action.run());
    return b;
  }

  private Node pieceNode(Piece p) {
    String res = p.getResourcePath();
    String file = res.substring(res.lastIndexOf('/') + 1);
    String path = "images/" + theme.getFolder() + "/" + file;

    Image img = IMAGES.computeIfAbsent(path, GameView::loadImage);
    if (img == null) {
      Label fallback = new Label(p.getName().substring(0, 1));
      fallback.getStyleClass().add("fallback");
      return fallback;
    }
    ImageView iv = new ImageView(img);
    iv.setFitWidth(TILE);
    iv.setFitHeight(TILE);
    iv.setPreserveRatio(true);
    iv.setSmooth(false);
    return iv;
  }

  private static Image loadImage(String path) {
    java.net.URL url = GameView.class.getResource("/" + path);
    if (url == null) {
      System.out.println("Missing image: " + path);
      return null;
    }
    Image img = new Image(url.toExternalForm());
    return img.isError() ? null : img;
  }
}