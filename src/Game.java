import javafx.scene.control.Alert;

import java.util.*;


public class Game{
    private static int MAXDEPTH = 6;
    boolean gameComplete;
    State state;
    Action nextAction;
    String winner;

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    public static int getMAXDEPTH() {
        return MAXDEPTH;
    }

    public boolean isGameComplete() {
        return gameComplete;
    }

    public void setGameComplete(boolean gameComplete) {
        this.gameComplete = gameComplete;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Action getNextAction() {
        return nextAction;
    }

    public void setNextAction(Action nextAction) {
        this.nextAction = nextAction;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Game() {
        gameComplete = false;
        List<Piece> maxPieceList = new ArrayList<>(), minPieceList = new ArrayList<>();
        Map<Coordinate, Piece> board = new HashMap<>();

        int [] minCoordinatesX = {1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0, 2}, maxCoordinatesX = {5, 7, 6, 5, 7, 6, 5, 7, 6, 5, 7, 6};
        int [] minCoordinatesY = {0, 1, 1, 2, 3, 3, 4, 5, 5, 6, 7, 7}, maxCoordinatesY = {0, 0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7};

        for(int i = 0; i < 12; i++) {
            Coordinate maxC = new Coordinate(maxCoordinatesX[i], maxCoordinatesY[i]);
            Piece maxP = new Piece(maxC, false, true);
            maxPieceList.add(maxP);
            board.put(maxC, maxP);
        }

        for(int i = 0; i < 12; i++) {
            Coordinate minC = new Coordinate(minCoordinatesX[i], minCoordinatesY[i]);
            Piece minP = new Piece(minC, false, false);
            minPieceList.add(minP);
            board.put(minC, minP);
        }
        state = new State(0, 0, true, maxPieceList, minPieceList, board);
    }

    boolean hasFinished() {
        return gameComplete;
    }

    int maxValue(int depth, State s) {
        if(depth == MAXDEPTH) {
            return s.getGuessUtility();
        }
        int utility = Integer.MIN_VALUE;
        Map<Coordinate, ArrayList<Action>> actions = s.getStateActions();
        if (actions.isEmpty()) {
            return s.getGuessUtility();
        }
        for(Map.Entry<Coordinate, ArrayList<Action>> e : actions.entrySet()) {
            for (Action action : e.getValue()) {
                State newState = s.getNextState(action);
                int utilityNext = minValue(depth + 1, newState);
                if (utilityNext > utility) {
                    if (depth == 0) {
                        nextAction = action;
                    }
                    utility = utilityNext;
                }
            }
        }
        return utility;
    }

    int minValue(int depth, State s) {
        if(depth == MAXDEPTH) {
            return s.getGuessUtility();
        }
        int utility = Integer.MAX_VALUE;
        Map<Coordinate, ArrayList<Action>> actions = s.getStateActions();
        if (actions.isEmpty()) {
            return s.getGuessUtility();
        }
        for(Map.Entry<Coordinate, ArrayList<Action>> e : actions.entrySet()) {
            for (Action action : e.getValue()) {
                State newState = s.getNextState(action);
                int utilityNext = maxValue(depth + 1, newState);
                if (utilityNext < utility) {
                    if (depth == 0) {
                        nextAction = action;
                    }
                    utility = utilityNext;
                }
            }
        }
        return utility;
    }

    void playNextMove(boolean oneHuman, boolean twoHuman, int depth) {
        if (state.isMaxChance() && !oneHuman) {
            maxValue(0, state);
            if(!gameComplete) {
                state = state.getNextState(nextAction);
                if (state.getStateActions().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Max is winner!");
                    alert.showAndWait();
                }
            }
            state.setMaxChance(false);
        }
        else if(!state.isMaxChance() && !twoHuman) {
            MAXDEPTH = depth;
            minValue(0,state);
            if(!gameComplete) {
                state = state.getNextState(nextAction);
                if (state.getStateActions().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Min is winner!");
                    alert.showAndWait();
                }
            }
            state.setMaxChance(true);
        }
    }
}