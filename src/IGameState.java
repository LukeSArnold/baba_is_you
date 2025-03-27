import edu.usu.graphics.Graphics2D;

public interface IGameState {
    void initialize(Graphics2D graphics);

    void initializeSession();

    void initializeSession(int level);

    GameStateEnum processInput(double elapsedTime);

    int processInputLevel(double elapsedTime);

    void update(double elapsedTime);

    void render(double elapsedTime);
}
