import java.util.Objects;

public class Coordinate {
    float xCoordinate, yCoordinate;

    public Coordinate(float xCoordinate, float yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Float.compare(that.xCoordinate, xCoordinate) == 0 &&
                Float.compare(that.yCoordinate, yCoordinate) == 0;
    }

    @Override
    public String toString() {
        return "x : " + xCoordinate + " y : " + yCoordinate + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCoordinate, yCoordinate);
    }

    public Coordinate(Coordinate c) {
        this.xCoordinate = c.getxCoordinate();
        this.yCoordinate = c.getyCoordinate();
    }

    public float getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}