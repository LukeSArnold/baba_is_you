import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import utils.Serializer;

import static org.lwjgl.glfw.GLFW.*;

public class AboutView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.About;
    private Font font;

    @Override
    public void initialize(Graphics2D graphics, Serializer serializer) {
        super.initialize(graphics, serializer);

        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.About;
    }

    @Override
    public void initializeSession(int level) {

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
    }

    @Override
    public void render(double elapsedTime) {
        final String message = "This game was ported to Java by Luke Arnold";
        final String message2 = "(+ a little bit of chatGPT and starter code)";
        final float height = 0.075f;
        final float width = font.measureTextWidth(message, height);

        graphics.drawTextByHeight(font, message, 0.0f - width / 2, 0 - height / 2, height, Color.YELLOW);
        graphics.drawTextByHeight(font, message2, 0.0f - width / 2, height - height / 2, height, Color.YELLOW);

    }
}
