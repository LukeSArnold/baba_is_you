package utils;

public class KeyBoardConfig {

    public KeyBoardConfig() {
        initialized = false;
    }

    public KeyBoardConfig(int up, int left, int right, int down, int restart, int undo) {
        this.up = up;
        this.left = left;
        this.right = right;
        this.down = down;
        this.restart = restart;
        this.undo = undo;

    }

    public boolean initialized;
    public int up;
    public int left;
    public int right;
    public int down;
    public int restart;
    public int undo;
}
