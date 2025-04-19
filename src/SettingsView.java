import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import utils.KeyBoardConfig;
import utils.Serializer;

import java.util.Currency;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.*;

public class SettingsView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.Settings;
    private Font font;
    private KeyBoardConfig config;
    private Serializer serializer;

    private boolean waitingForKeyInput = false;
    private boolean inputEligible = false;

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
    }

    @Override
    public void initializeSession() {
        this.config = new KeyBoardConfig();
        this.nextGameState = GameStateEnum.Settings;
        serializer.loadKeyboardConfig(this.config);
        this.inputEligible = false;

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

        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (elapsedTime) -> {
            if (!waitingForKeyInput) {
                waitingForKeyInput = true;
            }
        });

    }


    @Override
    public void initializeSession(int level) {

    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        if (inputEligible) {
            inputKeyboard.update(elapsedTime);
        }

        // Normal input logic goes here, now safe
        return nextGameState;
    }

    @Override
    public int processInputLevel(double elapsedTime) {
        return 0;
    }

    @Override
    public void update(double elapsedTime) {
        if (!inputEligible){
            checkInput();
        }
        if (inputEligible) {
            selectNewInput();
        }
    }


    @Override
    public void render(double elapsedTime) {

        final float HEIGHT_MENU_ITEM = 0.075f;
        float top = -0.5f;

        if (this.config.initialized) {
            // render top elements
            renderMenuItem(
                    currentSelection == SettingsView.MenuState.Up ? fontSelected : fontMenu,
                    "Up",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Up ?
                            Color.YELLOW : Color.BLUE);

            top = renderMenuItem(currentSelection == SettingsView.MenuState.Up ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.up),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Up ?
                            Color.GREEN : Color.BLUE);


            // render left and right elements
            renderMenuItem(
                    currentSelection == MenuState.Left ? fontSelected : fontMenu,
                    "Left",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Left ?
                            Color.YELLOW : Color.BLUE);


            top = renderMenuItem(currentSelection == SettingsView.MenuState.Left ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.left),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Left ?
                            Color.GREEN : Color.BLUE);

            renderMenuItem(
                    currentSelection == MenuState.Right ? fontSelected : fontMenu,
                    "Right",
                    top,
                    HEIGHT_MENU_ITEM,

                    currentSelection == SettingsView.MenuState.Right ?
                            Color.YELLOW : Color.BLUE);


            top = renderMenuItem(currentSelection == SettingsView.MenuState.Left ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.right),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Right ?
                            Color.GREEN : Color.BLUE);

            // render down elements
            renderMenuItem(
                    currentSelection == SettingsView.MenuState.Down ? fontSelected : fontMenu,
                    "Down",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Down ?
                            Color.YELLOW : Color.BLUE);


            top = renderMenuItem(currentSelection == MenuState.Down ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.down),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Down ?
                            Color.GREEN : Color.BLUE);

            renderMenuItem(currentSelection == MenuState.Undo ? fontSelected : fontMenu,
                    "Undo",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == SettingsView.MenuState.Undo ?
                            Color.YELLOW : Color.BLUE);

            top = renderMenuItem(currentSelection == MenuState.Undo ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.undo),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Undo ?
                            Color.GREEN : Color.BLUE);

            renderMenuItem(currentSelection == MenuState.Restart ? fontSelected : fontMenu,
                    "Restart",
                    top,
                    HEIGHT_MENU_ITEM,
                    currentSelection == MenuState.Restart ?
                            Color.YELLOW : Color.BLUE);

            renderMenuItem(currentSelection == MenuState.Restart ? fontSelected : fontMenu,
                    "" + getKeyName(this.config.restart),
                    top,
                    HEIGHT_MENU_ITEM,
                    0.25f,
                    currentSelection == SettingsView.MenuState.Restart ?
                            Color.GREEN : Color.BLUE);
        }
    }

    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - 0.3f, top, height, color);

        return top + height;
    }

    private float renderMenuItem(Font font, String text, float top, float height, float left, Color color) {
        float width = font.measureTextWidth(text, height);

        graphics.drawTextByHeight(font, text, left - 0.3f, top, height, color);

        return top + height;
    }

    private String getKeyName(int key) {
        // Handle alphabetic and number keys as characters
        if ((key >= GLFW_KEY_A && key <= GLFW_KEY_Z) || (key >= GLFW_KEY_1 && key <= GLFW_KEY_9) || key == GLFW_KEY_0) {
            return String.valueOf((char) key);
        }

        // Handle special keys with custom strings
        switch (key) {
            case GLFW_KEY_UP:
                return "Arrow Up";
            case GLFW_KEY_DOWN:
                return "Arrow Down";
            case GLFW_KEY_LEFT:
                return "Arrow Left";
            case GLFW_KEY_RIGHT:
                return "Arrow Right";
            case GLFW_KEY_SPACE:
                return "Space";
            case GLFW_KEY_TAB:
                return "Tab";
            case 0:
                return "_";
            // Add more special keys as needed
            default:
                return "Unknown Key";
        }
    }


    private void selectNewInput() {
        if (!waitingForKeyInput) return;

        int old_input = 0;
        switch(currentSelection) {
            case Up -> {
                old_input = config.up;
                config.up = 0;
            }
            case Left -> {
                old_input = config.left;
                config.left = 0;
            }
            case Right -> {
                old_input = config.right;
                config.right = 0;
            }
            case Down -> {
                old_input = config.down;
                config.down = 0;
            }
            case Undo -> {
                old_input = config.undo;
                config.undo = 0;
            }
            case Restart ->{
                old_input = config.restart;
                config.restart = 0;
            }
        }

        long window = graphics.getWindow(); // or however you access the GLFW window

        int finalOld_input = old_input;
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {

                if (key != 0) {
                    switch (currentSelection) {
                        case Up -> config.up = key;
                        case Left -> config.left = key;
                        case Right -> config.right = key;
                        case Down -> config.down = key;
                        case Undo -> config.undo = key;
                        case Restart -> config.restart = key;
                    }
                } else {
                    switch (currentSelection) {
                        case Up -> config.up = finalOld_input;
                        case Left -> config.left = finalOld_input;
                        case Right -> config.right = finalOld_input;
                        case Down -> config.down = finalOld_input;
                        case Undo -> config.undo = finalOld_input;
                        case Restart -> config.restart = finalOld_input;
                    }
                }

                if (currentSelection != MenuState.Up && config.up == key) config.up = 0;
                if (currentSelection != MenuState.Left && config.left == key) config.left = 0;
                if (currentSelection != MenuState.Right && config.right == key) config.right = 0;
                if (currentSelection != MenuState.Down && config.down == key) config.down = 0;
                if (currentSelection != MenuState.Undo && config.undo == key) config.undo = 0;
                if (currentSelection != MenuState.Restart && config.restart == key) config.restart = 0;


                waitingForKeyInput = false;

                this.serializer.saveKeyboardConfig(this.config);

                // After setting, optionally reset the key callback to the default input system
                glfwSetKeyCallback(window, null);
            }
        });
    }

    private void checkInput () {
        glfwSetKeyCallback(graphics.getWindow(), (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                inputEligible = true;
            }
        });
    }
}
