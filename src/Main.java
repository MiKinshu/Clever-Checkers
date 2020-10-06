import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    List<Circle> circles = new ArrayList<>();
    boolean oneHuman = true, twoHuman = true;
    int depth = 6;

    public static void main(String[] args) {
        launch(args);
    }

    void updateScene(Group root, Game game, Label chanceLabel, Label maxScoreLabel, Label minScoreLabel) {
        State state = game.getState();
        for (int i = 0; i < state.getMinPieceList().size(); i++) {
            Circle circle = new Circle();
            circle.setCenterY(140 + state.getMinPieceList().get(i).getPosition().getxCoordinate() * 80);
            circle.setCenterX(140 + state.getMinPieceList().get(i).getPosition().getyCoordinate() * 80);
            if(state.getMinPieceList().get(i).isKing()) circle.setFill(Color.DARKRED);
            else circle.setFill(Color.RED);
            circle.setRadius(30);
            circle.setFocusTraversable(true);

            if (twoHuman) {
                circle.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        System.out.println("Click detected on min");
                        clearScene(root);
                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                        if (!game.getState().isMaxChance()){
                            double dragBaseX = circle.getCenterX();
                            double dragBaseY = circle.getCenterY();
                            int y = (int) (dragBaseX - 140) / 80;
                            int x = (int) (dragBaseY - 140) / 80;

                            Piece p = state.getBoard().get(new Coordinate(x, y));
                            System.out.println(p.getPosition());

                            if (state.getStateActions().isEmpty()) System.out.println("Max is winner!");

                            List<Action> actions = new ArrayList<>();
                            if(state.getStateActions().containsKey(p.getPosition())) actions = state.getStateActions().get(p.getPosition());
                            else System.out.println("No Action found!");

                            for (Action action : actions) {
                                Circle c = new Circle();
                                c.setCenterY(140 + action.getNewCoordinate().getxCoordinate() * 80);
                                c.setCenterX(140 + action.getNewCoordinate().getyCoordinate() * 80);
                                c.setFill(Color.PINK);
                                c.setRadius(30);
                                c.setFocusTraversable(true);
                                circles.add(c);

                                c.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        State s = state.getNextState(action);

                                        if(s.getStateActions().isEmpty()) {
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Information Dialog");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Min winner!");
                                            alert.showAndWait();
                                        }

                                        game.setState(s);
                                        clearScene(root);
                                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                                        game.getState().setMaxChance(true);
                                    }
                                });
                                root.getChildren().add(c);
                            }
                        }
                        else System.out.println("It's Max's turn");
                        mouseEvent.consume();
                    }
                });
            }
            circles.add(circle);
            root.getChildren().add(circle);
        }
        for (int i = 0; i < state.getMaxPieceList().size(); i++){
            Circle circle = new Circle();
            circle.setCenterY(140 + state.getMaxPieceList().get(i).getPosition().getxCoordinate() * 80);
            circle.setCenterX(140 + state.getMaxPieceList().get(i).getPosition().getyCoordinate() * 80);
            if (state.getMaxPieceList().get(i).isKing()) circle.setFill(Color.BLACK);
            else circle.setFill(Color.GREY);
            circle.setRadius(30);
            circle.setFocusTraversable(true);

            if (oneHuman) {
                circle.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        clearScene(root);
                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                        System.out.println("Click detected on Max");
                        if (game.getState().isMaxChance()) {
                            double dragBaseX = circle.getCenterX();
                            double dragBaseY = circle.getCenterY();
                            int y = (int) (dragBaseX - 140) / 80;
                            int x = (int) (dragBaseY - 140) / 80;

                            Piece p = state.getBoard().get(new Coordinate(x, y));

                            if (p.isKing()) System.out.println("KINGGG");

                            if (state.getStateActions().isEmpty()) System.out.println("Min is winner!");

                            List<Action> actions = new ArrayList<>();
                            if(state.getStateActions().containsKey(p.getPosition())) actions = state.getStateActions().get(p.getPosition());
                            else System.out.println("Piece not found");

                            for (Action action : actions) {
                                Circle c = new Circle();
                                c.setCenterY(140 + action.getNewCoordinate().getxCoordinate() * 80);
                                c.setCenterX(140 + action.getNewCoordinate().getyCoordinate() * 80);
                                c.setFill(Color.PINK);
                                c.setRadius(30);
                                c.setFocusTraversable(true);
                                circles.add(c);

                                c.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        State s = state.getNextState(action);
                                        game.setState(s);

                                        if(s.getStateActions().isEmpty()) {
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Information Dialog");
                                            alert.setHeaderText(null);
                                            alert.setContentText("Max winner!");
                                            alert.showAndWait();
                                        }

                                        clearScene(root);
                                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                                        System.out.println("max's turn over");
                                        game.getState().setMaxChance(false);
                                        System.out.println("Printing turn " + game.getState().isMaxChance());
                                    }
                                });
                                root.getChildren().add(c);
                            }
                        }
                        else System.out.println("It's Min's turn");
                        mouseEvent.consume();
                    }
                });
            }
            circles.add(circle);
            root.getChildren().add(circle);
        }
        String chanceLabelText = state.isMaxChance() ? "Black's turn." : "Red's turn.";
        chanceLabel.setText(chanceLabelText);
        maxScoreLabel.setText("Max's Score : " + state.getMaxScore());
        minScoreLabel.setText("Min's Score : " + state.getMinScore());
    }

    void clearScene(Group root) {
        for (Circle circle : circles) {
            root.getChildren().remove(circle);
        }
        circles.clear();
    }

    int chooseDifficulty() {
        String[] arrayData = new String[]{"easy", "normal", "hard", "novice", "expert"};
        List<String> dialogData = Arrays.asList(arrayData);

        ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
        dialog.setTitle("Difficulty");
        dialog.setHeaderText("Choose game difficulty");

        Optional<String> result = dialog.showAndWait();
        String selected = "cancelled.";
        if (result.isPresent()) {
            selected = result.get();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Cannot work until you choose difficulty.\nExiting!");
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        }
        System.out.println(selected);

        switch (selected) {
            case "easy" -> {
                return 1;
            }
            case "hard" -> {
                return 4;
            }
            case "novice" -> {
                return 5;
            }
            case "expert" -> {
                return 6;
            }
            default -> {
                return 3;
            }
        }
    }

    String chooseGameMode() {
        final String [] arrayData = {"AI vs AI", "Human vs AI", "Human vs Human"};
        List<String> dialogData = Arrays.asList(arrayData);
        ChoiceDialog dialog = new ChoiceDialog(dialogData.get(0), dialogData);
        dialog.setTitle("Choose Mode");
        dialog.setHeaderText("Choose game mode");
        Optional<String> result = dialog.showAndWait();
        String selected = "cancelled.";
        if (result.isPresent()) {
            selected = result.get();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Exiting");
            alert.setHeaderText(null);
            alert.setContentText("Cannot work until you choose mode\nExiting.");
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        }
        return selected;
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas();
        Group root = new Group(canvas);
        Scene scene= new Scene(root, 900, 900);

        boolean red = true;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle rectangle = new Rectangle(100 + 80 * i, 100 + 80 * j, 80, 80);
                if (red) {
                    rectangle.setFill(Color.GREENYELLOW);
                }
                else {
                    rectangle.setFill(Color.DARKGREEN);
                }
                red = !red;
                root.getChildren().add(rectangle);
            }
            red = !red;
        }
        Label chanceLabel = new Label("Black's Turn");
        chanceLabel.setFont(Font.font(15));
        chanceLabel.setLayoutX(660);
        chanceLabel.setLayoutY(740);
        root.getChildren().add(chanceLabel);

        Label maxScoreLabel = new Label("Max's Score : 0");
        maxScoreLabel.setFont(Font.font(15));
        maxScoreLabel.setLayoutX(100);
        maxScoreLabel.setLayoutY(740);
        root.getChildren().add(maxScoreLabel);

        Label minScoreLabel = new Label("Min's Score : 0");
        minScoreLabel.setFont(Font.font(15));
        minScoreLabel.setLayoutX(260);
        minScoreLabel.setLayoutY(740);
        root.getChildren().add(minScoreLabel);

        Game game = new Game();
        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
        primaryStage.setTitle("Assignment");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (game.getState().isMaxChance()) System.out.println("max's turn");
                else System.out.println("Min's turn");
                if (!game.hasFinished()) {
                    game.playNextMove(oneHuman, twoHuman, depth);
                    clearScene(root);
                    updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                    if (game.getState().getStateActions().isEmpty()) System.out.println("Game Over!");
                }
            }
        });

        String selected = chooseGameMode();

        switch (selected) {
            case "AI vs AI" -> {
                oneHuman = false;
                twoHuman = false;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Please click the screen EVERYTIME to make AI play the next move.");
                alert.showAndWait();
            }
            case "Human vs AI" -> {
                oneHuman = true;
                twoHuman = false;
                depth = chooseDifficulty();
            }
            case "Human vs Human" -> {
                oneHuman = true;
                twoHuman = true;
            }
        }
    }
}