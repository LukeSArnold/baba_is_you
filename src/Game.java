import edu.usu.graphics.*;
import utils.Serializer;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private HashMap<GameStateEnum, IGameState> states;
    private IGameState currentState;
    GameStateEnum nextStateEnum = GameStateEnum.MainMenu;
    GameStateEnum prevStateEnum = GameStateEnum.MainMenu;
    private int nextLevel;

    private Serializer serializer;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.serializer = new Serializer();
    }

    public void initialize() {
        states = new HashMap<>() {
            {
                put(GameStateEnum.MainMenu, new MainMenuView());
                put(GameStateEnum.LevelSelect, new LevelSelectMenu());
                put(GameStateEnum.GamePlay, new GamePlayView());
                put(GameStateEnum.Settings, new SettingsView());
                put(GameStateEnum.About, new AboutView());
            }
        };

        // Give all game states a chance to initialize, other than the constructor
        for (var state : states.values()) {
            state.initialize(graphics, serializer);
        }

        currentState = states.get(GameStateEnum.MainMenu);
        currentState.initializeSession();
    }

    public void shutdown() {
        serializer.shutdown();
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();
        if (currentState.getClass() == LevelSelectMenu.class){
            nextLevel = currentState.processInputLevel(elapsedTime);
        }
        nextStateEnum = currentState.processInput(elapsedTime);
    }

    private void update(double elapsedTime) {
        // Special case for exiting the game
        if (nextStateEnum == GameStateEnum.Quit) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        } else {
            if (nextStateEnum == prevStateEnum) {
                currentState.update(elapsedTime);
            } else {
                currentState = states.get(nextStateEnum);

                if (currentState.getClass() == GamePlayView.class) {
                    currentState.initializeSession(nextLevel);
                } else {
                    currentState.initializeSession();
                }
                prevStateEnum = nextStateEnum;
            }
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();

        currentState.render(elapsedTime);

        graphics.end();
    }
}
