import java.util.*;

public class State {
    int maxScore, minScore;
    boolean maxChance;
    List<Piece> maxPieceList, minPieceList;
    Map<Coordinate, Piece> board;
    Map<Coordinate, ArrayList<Action>> stateActions;
    int[][] ActionsX = {{-1, -1, 1, 1}, {1, 1, -1, -1}};
    int[][] ActionsY = {{-1, 1, -1, 1}, {-1, 1, -1, 1}};

    public State(int maxScore, int minScore, boolean maxChance, List<Piece> maxPieceList, List<Piece> minPieceList, Map<Coordinate, Piece> board) {
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.maxChance = maxChance;
        this.maxPieceList = maxPieceList;
        this.minPieceList = minPieceList;
        this.board = board;

        //This inversion of maxChance is required because the getActions() functions is modelled such that it gives the set of actions for the next player, because he will be the one new state formed.
        this.maxChance = !this.maxChance;
        this.stateActions = getActions();
        this.maxChance = !this.maxChance;
    }

    public State(State s) {
        this.maxScore = s.getMaxScore();
        this.minScore = s.getMinScore();
        this.maxChance = s.isMaxChance();
        this.maxPieceList = new ArrayList<>();
        this.minPieceList = new ArrayList<>();
        this.board = new HashMap<>();
        for(Piece p : s.getMaxPieceList()) {
            this.maxPieceList.add(new Piece(p));
        }
        for(Piece p : s.getMinPieceList()) {
            this.minPieceList.add(new Piece(p));
        }
        for(Map.Entry<Coordinate, Piece> e : s.board.entrySet()){
            this.board.put(new Coordinate(e.getKey()), new Piece(e.getValue()));
        }
        //No inversion is done here, unlike above, as the getActions() function is not called.
        stateActions = new HashMap<>();
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public boolean isMaxChance() {
        return maxChance;
    }

    public void setMaxChance(boolean maxChance) {
        this.maxChance = maxChance;
    }

    public List<Piece> getMaxPieceList() {
        return maxPieceList;
    }

    public void setMaxPieceList(List<Piece> maxPieceList) {
        this.maxPieceList = maxPieceList;
    }

    public List<Piece> getMinPieceList() {
        return minPieceList;
    }

    public void setMinPieceList(List<Piece> minPieceList) {
        this.minPieceList = minPieceList;
    }

    public Map<Coordinate, Piece> getBoard() {
        return board;
    }

    public void setBoard(Map<Coordinate, Piece> board) {
        this.board = board;
    }

    public Map<Coordinate, ArrayList<Action>> getStateActions() {
        return stateActions;
    }

    public void setStateActions(Map<Coordinate, ArrayList<Action>> stateActions) {
        this.stateActions = stateActions;
    }

    Coordinate getPositionAfterScoring(Coordinate oldCoordinate, Coordinate newCoordinate) {
        float dx = newCoordinate.getxCoordinate() - oldCoordinate.getxCoordinate();
        float dy = newCoordinate.getyCoordinate() - oldCoordinate.getyCoordinate();
        return new Coordinate(newCoordinate.getxCoordinate() + dx, newCoordinate.getyCoordinate() + dy);
    }

    ArrayList<Action> getPieceActions(Piece piece) {
        ArrayList<Action> actions = new ArrayList<>();
        int k = piece.isKing() ? 2 : 0;
        Action action = null;
        int player = piece.isMax() ? 0 : 1;
        float oldX = piece.getPosition().getxCoordinate(), oldY = piece.getPosition().getyCoordinate();
        for(int i = 0; i < 2 + k; i++) {
            float newX = oldX + ActionsX[player][i], newY = oldY + ActionsY[player][i];
            if(board.containsKey(new Coordinate(newX, newY))) {
                Piece piecePresentOnNewLocation = board.get(new Coordinate(newX, newY));
                if((piece.isMax() && !piecePresentOnNewLocation.isMax()) || (!piece.isMax() && piecePresentOnNewLocation.isMax())) {
                    Coordinate positionAfterScoring = getPositionAfterScoring(piece.getPosition(), new Coordinate(newX, newY));
                    if((!board.containsKey(positionAfterScoring)) && positionAfterScoring.getxCoordinate() < 8 && positionAfterScoring.getyCoordinate() < 8 &&
                            positionAfterScoring.getxCoordinate() > -1 && positionAfterScoring.getyCoordinate() > -1) {
                        int newMaxScore = piece.isMax() ? maxScore + 1 : maxScore;
                        int newMinScore = piece.isMax() ? minScore : minScore + 1;
                        action = new Action(piece.getPosition(), positionAfterScoring, newMaxScore, newMinScore, true);
                        actions.add(action);
                    }
                }
            }
            else {
                Coordinate newCoordinate = new Coordinate(newX, newY);
                if ((!board.containsKey(newCoordinate)) && newCoordinate.getxCoordinate() > -1 && newCoordinate.getyCoordinate() > -1 &&
                        newCoordinate.getyCoordinate() < 8 && newCoordinate.getxCoordinate() < 8) {
                    action = new Action(piece.getPosition(), newCoordinate, maxScore, minScore, false);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    Map<Coordinate, ArrayList<Action>> getActions() {
        Map<Coordinate, ArrayList<Action>> actions = new HashMap<>();
        List<Piece> PieceList = (!maxChance) ? maxPieceList : minPieceList;

        for(Piece piece : PieceList) {
            ArrayList<Action> pieceActions = getPieceActions(piece);
            if (!pieceActions.isEmpty()) actions.put(piece.getPosition(), pieceActions);
        }
        boolean killActionFound = false;
        for(Map.Entry<Coordinate, ArrayList<Action>> e : actions.entrySet()) {
            if (killActionFound) break;
            for (Action a : e.getValue()) {
                if (a.isKillAction()) {
                    killActionFound = true;
                    break;
                }
            }
        }
        if (killActionFound) {
            Map<Coordinate, ArrayList<Action>> modifiedActions = new HashMap<>();
            for(Map.Entry<Coordinate, ArrayList<Action>> e : actions.entrySet()) {
                boolean containsKillAction = false;
                for (Action a : e.getValue()) {
                    if (a.isKillAction()) {
                        containsKillAction = true;
                        break;
                    }
                }
                if (containsKillAction) {
                    ArrayList<Action> killActionsForThisPiece = new ArrayList<>();
                    for (Action a : e.getValue()) {
                        if (a.isKillAction()) {
                            killActionsForThisPiece.add(a);
                        }
                    }
                    modifiedActions.put(e.getKey(), killActionsForThisPiece);
                }
            }
            actions = modifiedActions;
        }
        return actions;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += ("Printing maxPieces\n");
        for (Piece piece : maxPieceList) {
            ret += (piece.getPosition());
            ret += piece.isKing() + "\n";
        }
        ret += ("Printing minPieces\n");
        for (Piece piece : minPieceList) {
            ret += (piece.getPosition());
            ret += piece.isKing() + "\n";
        }
        return ret;
    }

    int getGuessUtility() {
        int kingsMax = 0, kingsMin = 0, normalMax = 0, normalMin = 0;
        for (Piece piece : maxPieceList) {
            if (piece.isKing()) kingsMax++;
            else normalMax++;
        }
        for (Piece piece : minPieceList) {
            if (piece.isKing()) kingsMin++;
            else normalMin++;
        }
        return (int)(normalMax - normalMin + 2 * kingsMax - 2 * kingsMin);
    }

    State getNextState(Action a) {
        Coordinate oldPosition = a.getOldCoordinate(), newPosition = a.getNewCoordinate();
        State newState = new State(this);

        Piece piece = newState.getBoard().get(oldPosition);
        newState.setMaxScore(a.getNewMaxScore());
        newState.setMinScore(a.getNewMinScore());

        if (piece.isMax()) {
            for (Piece p : newState.getMaxPieceList()) {
                if (p.getPosition().equals(oldPosition)) {
                    p.setPosition(newPosition);
                    if (p.getPosition().getxCoordinate() == 0) p.setKing(true);
                    break;
                }
            }
        }
        else {
            for (Piece p : newState.getMinPieceList()) {
                if (p.getPosition().equals(oldPosition)) {
                    p.setPosition(newPosition);
                    if (p.getPosition().getxCoordinate() == 7) p.setKing(true);
                    break;
                }
            }
        }
        piece.setPosition(newPosition);

        if (piece.isMax() && piece.getPosition().getxCoordinate() == 0) {
            piece.setKing(true);
        }
        if ((!piece.isMax()) && piece.getPosition().getxCoordinate() == 7) piece.setKing(true);

        newState.getBoard().remove(oldPosition);
        newState.getBoard().put(newPosition, piece);

        if (a.newMaxScore != maxScore || a.newMinScore != minScore) {
            Coordinate removeCoordinate = new Coordinate((a.newCoordinate.getxCoordinate() + a.oldCoordinate.getxCoordinate()) / 2,
                    (a.newCoordinate.getyCoordinate() + a.oldCoordinate.getyCoordinate()) / 2);
            if (piece.isMax()) {
                newState.getBoard().remove(removeCoordinate);
                int i = 0;
                for(;i < newState.getMinPieceList().size(); i++) {
                    if (newState.getMinPieceList().get(i).getPosition().equals(removeCoordinate)) break;
                }
                if(i != newState.getMinPieceList().size()) newState.getMinPieceList().remove(i);
            }
            else{
                newState.getBoard().remove(removeCoordinate);
                int i = 0;
                for(;i < newState.getMaxPieceList().size(); i++) {
                    if (newState.getMaxPieceList().get(i).getPosition().equals(removeCoordinate)) break;
                }
                if(i != newState.getMaxPieceList().size()) newState.getMaxPieceList().remove(i);
            }
        }
        newState.setStateActions(newState.getActions());
        newState.setMaxChance(!newState.isMaxChance());
        return newState;
    }
}