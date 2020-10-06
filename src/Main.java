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
    List<Circle> circles = new ArrayList<>(); //contains circles that are visible on the screen.
    boolean oneHuman = true, twoHuman = true; //modes oneHuman = true ==> player one is human
    int depth = 6; //the difficulty in AI vs Human mode.

    public static void main(String[] args) {
        launch(args);
    }

    //This function updates the javaFX scene. Additionally it also sets up on click listeners on the circles to implement the human players.
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

            final Alert[] alert = new Alert[1];
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
                            else {
                                alert[0] = new Alert(Alert.AlertType.INFORMATION);
                                alert[0].setTitle("Information Dialog");
                                alert[0].setHeaderText(null);
                                alert[0].setContentText("This piece cannot make any valid moves. \nEither:\n1. This piece is blocked. OR\n2. Another piece is in killing position");
                                alert[0].showAndWait();
                            }

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
                                            alert.setContentText("Red is Winner!");
                                            alert.showAndWait();
                                            Platform.exit();
                                            System.exit(0);
                                        }

                                        game.setState(s);
                                        clearScene(root);
                                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                                        if (!game.getState().isContinuedState()) game.getState().setMaxChance(true);
                                        else {
                                            alert[0] = new Alert(Alert.AlertType.INFORMATION);
                                            alert[0].setTitle("Information Dialog");
                                            alert[0].setHeaderText(null);
                                            alert[0].setContentText("Please again make a move. The previous killer is again in killing position");
                                            alert[0].showAndWait();
                                        }
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

            final Alert[] alert = new Alert[1];
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

                            List<Action> actions = new ArrayList<>();
                            if(state.getStateActions().containsKey(p.getPosition())) actions = state.getStateActions().get(p.getPosition());
                            else {
                                alert[0] = new Alert(Alert.AlertType.INFORMATION);
                                alert[0].setTitle("Information Dialog");
                                alert[0].setHeaderText(null);
                                alert[0].setContentText("This piece cannot make any valid moves. \nEither:\n1. This piece is blocked. OR\n2. Another piece is in killing position");
                                alert[0].showAndWait();
                            }

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
                                            alert.setContentText("Black is Winner!");
                                            alert.showAndWait();
                                            Platform.exit();
                                            System.exit(0);
                                        }

                                        clearScene(root);
                                        updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
                                        System.out.println("max's turn over");
                                        if (!game.getState().isContinuedState()) game.getState().setMaxChance(false);
                                        else {
                                            alert[0] = new Alert(Alert.AlertType.INFORMATION);
                                            alert[0].setTitle("Information Dialog");
                                            alert[0].setHeaderText(null);
                                            alert[0].setContentText("Please again make a move. The previous killer is again in killing position");
                                            alert[0].showAndWait();
                                        }
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

    //This function removes all the circles from the JavaFx window.
    void clearScene(Group root) {
        for (Circle circle : circles) {
            root.getChildren().remove(circle);
        }
        circles.clear();
    }

    //Opens a popup with the various difficulties and return them.
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
                return 3;
            }
            case "novice" -> {
                return 4;
            }
            case "expert" -> {
                return 5;
            }
            default -> {
                return 2;
            }
        }
    }

    //Opens a popup with the various game modes and returns them.
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

    //This is the first function that starts.
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas();
        Group root = new Group(canvas);
        Scene scene= new Scene(root, 900, 900);

        //Making the board. Stacking the squares.
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
        //Following are the various labels that are shown at the bottom of the screen.
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

        //This detects the clicks on the scene. This function is responsible making the AI play the next move.
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!game.hasFinished()) {
                    game.playNextMove(oneHuman, twoHuman, depth);
                    clearScene(root);
                    updateScene(root, game, chanceLabel, maxScoreLabel, minScoreLabel);
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