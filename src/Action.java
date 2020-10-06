public class Action {
    Coordinate oldCoordinate, newCoordinate;
    int newMaxScore, newMinScore;
    boolean isKillAction;

    public boolean isKillAction() {
        return isKillAction;
    }

    public void setKillAction(boolean killAction) {
        isKillAction = killAction;
    }

    public Action(Coordinate oldCoordinate, Coordinate newCoordinate, int newMaxScore, int newMinScore, boolean isKillAction) {
        this.oldCoordinate = oldCoordinate;
        this.newCoordinate = newCoordinate;
        this.newMaxScore = newMaxScore;
        this.newMinScore = newMinScore;
        this.isKillAction = isKillAction;
    }

    public Action(Action a) {
        this.oldCoordinate = a.oldCoordinate;
        this.newCoordinate = a.newCoordinate;
        this.newMaxScore = a.newMaxScore;
        this.newMinScore = a.newMinScore;
        this.isKillAction = a.isKillAction;
    }

    public Coordinate getOldCoordinate() {
        return oldCoordinate;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "old coordinates\n";
        ret += oldCoordinate.toString();
        ret += "new coordinates\n";
        ret += newCoordinate.toString();
        return ret;
    }

    public void setOldCoordinate(Coordinate oldCoordinate) {
        this.oldCoordinate = oldCoordinate;
    }

    public Coordinate getNewCoordinate() {
        return newCoordinate;
    }

    public void setNewCoordinate(Coordinate newCoordinate) {
        this.newCoordinate = newCoordinate;
    }

    public int getNewMaxScore() {
        return newMaxScore;
    }

    public void setNewMaxScore(int newMaxScore) {
        this.newMaxScore = newMaxScore;
    }

    public int getNewMinScore() {
        return newMinScore;
    }

    public void setNewMinScore(int newMinScore) {
        this.newMinScore = newMinScore;
    }
}