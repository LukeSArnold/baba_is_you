import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.Graphics2D;
import utils.Serializer;

import static org.lwjgl.glfw.GLFW.*;

public class GamePlayView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.GamePlay;
    private GameModel gameModel;
    private Serializer serializer;
    private SoundManager audio;
    private Sound polkaMusic;

    @Override
    public void initialize(Graphics2D graphics, Serializer serializer) {
        super.initialize(graphics, serializer);

        this.serializer = serializer;
        audio = new SoundManager();
        polkaMusic = audio.load("background", "resources/audio/Polka.ogg", true);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
            polkaMusic.stop();
        });
    }

    @Override
    public void initializeSession() {

        polkaMusic.play();
        gameModel = new GameModel(serializer);
        gameModel.initialize(graphics, 0, audio);
        nextGameState = GameStateEnum.GamePlay;
    }

    @Override
    public void initializeSession(int level) {
        gameModel = new GameModel(serializer);
        gameModel.initialize(graphics, level, audio);
        nextGameState = GameStateEnum.GamePlay;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public int processInputLevel(double elapsedTime) {
        return 0;
    }

    @Override
    public void update(double elapsedTime) {
        gameModel.update(elapsedTime);
    }

    @Override
    public void render(double elapsedTime) {
        // Nothing to do because the render now occurs in the update of the game model
    }
}