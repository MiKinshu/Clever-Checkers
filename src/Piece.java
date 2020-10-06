public class Piece {
    Coordinate position;
    boolean isKing, isMax;

    public Piece(Coordinate position, boolean isKing, boolean isMax) {
        this.position = position;
        this.isKing = isKing;
        this.isMax = isMax;
    }

    public Piece(Piece piece) {
        this.position = new Coordinate(piece.getPosition());
        this.isKing = piece.isKing();
        this.isMax = piece.isMax();
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public boolean isMax() {
        return isMax;
    }

    public void setMax(boolean max) {
        isMax = max;
    }
}