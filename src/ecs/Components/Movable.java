package ecs.Components;

public class Movable extends Component {

    public enum Direction {
        Stopped,
        Up,
        Down,
        Left,
        Right
    }

    public Direction input;
    public int segmentsToAdd = 0;
    public double moveInterval; // seconds
    public double elapsedInterval;

    public Movable(Direction input, double moveInterval) {
        this.input = input;
        this.moveInterval =moveInterval;
    }
}
