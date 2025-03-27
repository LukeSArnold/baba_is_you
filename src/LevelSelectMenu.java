import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.*;

public class LevelSelectMenu extends GameStateView {

    LevelMapper mapper = new LevelMapper("levels-all.bbiy");

    private int currentSelection = 0;

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.MainMenu;
    private int nextLevel;
    private Font fontMenu;
    private Font fontSelected;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        fontMenu = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        fontSelected = new Font("resources/fonts/Roboto-Bold.ttf", 48, false);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // Arrow keys to navigate the menu
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            if (currentSelection == 0) {
                currentSelection = mapper.levels.size() - 1;
            } else {
                currentSelection--;
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            if (currentSelection == mapper.levels.size() - 1) {
                currentSelection = 0;
            } else {
                currentSelection++;
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
        // When Enter is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.GamePlay;
            nextLevel = currentSelection;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.LevelSelect;
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
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return currentSelection;
    }



    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void render(double elapsedTime) {
        final float HEIGHT_MENU_ITEM = 0.075f;
        float top = -0.25f;
        for (int levelIndex = 0; levelIndex < mapper.levels.size(); levelIndex++){
            String level_name = mapper.levels.get(levelIndex).level_name;
            top = renderMenuItem(currentSelection == levelIndex ? fontSelected : fontMenu, level_name, top, HEIGHT_MENU_ITEM, currentSelection == levelIndex ? Color.YELLOW : Color.BLUE);
        }
    }

    /**
     * Centers the text horizontally, at the specified top position.
     * It also returns the vertical position to draw the next menu item
     */
    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, color);

        return top + height;
    }
}
