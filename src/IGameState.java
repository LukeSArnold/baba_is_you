import edu.usu.graphics.Graphics2D;
import utils.Serializer;

public interface IGameState {
    void initialize(Graphics2D graphics, Serializer serializer);

    void initializeSession();

    void initializeSession(int level);

    GameStateEnum processInput(double elapsedTime);

    int processInputLevel(double elapsedTime);

    void update(double elapsedTime);

    void render(double elapsedTime);
}
