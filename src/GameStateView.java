import edu.usu.graphics.Graphics2D;

public abstract class GameStateView implements IGameState {
    protected Graphics2D graphics;

    @Override
    public void initialize(Graphics2D graphics) {
        this.graphics = graphics;
    }

    @Override
    public void initializeSession() {};

    public abstract void initializeSession(int level);

    @Override
    public abstract GameStateEnum processInput(double elapsedTime);

    public abstract int processInputLevel(double elapsedTime);

    @Override
    public abstract void update(double elapsedTime);

    @Override
    public abstract void render(double elapsedTime);
}
