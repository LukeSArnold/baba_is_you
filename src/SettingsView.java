import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import utils.KeyBoardConfig;
import utils.Serializer;

import static org.lwjgl.glfw.GLFW.*;

public class SettingsView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.Settings;
    private Font font;
    private KeyBoardConfig config;
    private Serializer serializer;

    private enum MenuState {
        Up,
        Left,
        Right,
        Down,
        Undo,
        Restart;

        public SettingsView.MenuState next() {
            int nextOrdinal = (this.ordinal() + 1) % SettingsView.MenuState.values().length;
            return SettingsView.MenuState.values()[nextOrdinal];
        }

        public SettingsView.MenuState previous() {
            int previousOrdinal = (this.ordinal() - 1) % SettingsView.MenuState.values().length;
            if (previousOrdinal < 0) {
                previousOrdinal = Restart.ordinal();
            }
            return SettingsView.MenuState.values()[previousOrdinal];
        }
    }

    private SettingsView.MenuState currentSelection = SettingsView.MenuState.Up;
    private Font fontMenu;
    private Font fontSelected;

    @Override
    public void initialize(Graphics2D graphics, Serializer serializer) {
        super.initialize(graphics, serializer);

        this.serializer = serializer;

        fontMenu = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        fontSelected = new Font("resources/fonts/Roboto-Bold.ttf", 48, false);


        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });

        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            currentSelection = currentSelection.previous();
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            currentSelection = currentSelection.next();
        });
    }

    @Override
    public void initializeSession() {
        this.config = new KeyBoardConfig();
        this.nextGameState = GameStateEnum.Settings;
        serializer.loadKeyboardConfig(this.config);
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

        final float HEIGHT_MENU_ITEM = 0.075f;
        float top = -0.25f;

        if (this.config.initialized) {

            top = renderMenuItem(
                    currentSelection == SettingsView.MenuState.Up ? fontSelected : fontMenu,
                    "Up",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Up ? Color.YELLOW : Color.BLUE);

            top = renderMenuItem(currentSelection == SettingsView.MenuState.Up ? fontSelected : fontMenu,
                    "" + (char) this.config.up,
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Up ? Color.YELLOW : Color.BLUE);
        }

//        renderMenuItem(currentSelection == SettingsView.MenuState.Left ? fontSelected : fontMenu, "Left", top, HEIGHT_MENU_ITEM, true, currentSelection == SettingsView.MenuState.Left ? Color.YELLOW : Color.BLUE);
//        top = renderMenuItem(currentSelection == SettingsView.MenuState.Right ? fontSelected : fontMenu, "Right", top, HEIGHT_MENU_ITEM, false, currentSelection == SettingsView.MenuState.Right ? Color.YELLOW : Color.BLUE);
//        top = renderMenuItem(currentSelection == SettingsView.MenuState.Down ? fontSelected : fontMenu, "Down", top, HEIGHT_MENU_ITEM, currentSelection == SettingsView.MenuState.Down ? Color.YELLOW : Color.BLUE);
//        top = renderMenuItem(currentSelection == SettingsView.MenuState.Undo ? fontSelected : fontMenu, "Undo", top, HEIGHT_MENU_ITEM, currentSelection == SettingsView.MenuState.Undo ? Color.YELLOW : Color.BLUE);
//        renderMenuItem(currentSelection == MenuState.Restart ? fontSelected : fontMenu, "Restart", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Restart ? Color.YELLOW : Color.BLUE);
    }


    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, color);

        return top + height;
    }

    private float renderMenuItem(Font font, String text, float top, float height, boolean left, Color color) {
        float width = font.measureTextWidth(text, height);

        if (left) {
            graphics.drawTextByHeight(font, text, (0.0f - width / 2) - width, top, height, color);
        } else {
            graphics.drawTextByHeight(font, text, (0.0f - width / 2) + width, top, height, color);
        }


        return top + height;
    }
}
