package ecs.Systems;
import ecs.Components.*;
import ecs.Entities.Entity;
import ecs.ParticleSystem;
import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;

import java.util.*;

import utils.*;


import static org.lwjgl.glfw.GLFW.*;

public class RuleManager extends System {

    // keyboard control map for newly configuration "isYou" objects
    private final KeyBoardConfig keyBoardConfig;

    // settings for updating new Keyboard controlled elements after rule changes
    final double MOVE_INTERVAL = .15; // seconds
    private double intervalElapsed = 0;

    // window needed to detect keyboard inputs
    private final long window;

    private boolean won = false;
    private boolean isYouChange = false;

    private ArrayList<Class<? extends Component>> isWinConfig = new ArrayList<>();

    // particles systems declared here due to requirement to expose certain methods
    private final ParticleSystemRenderer particleSystemRenderer;
    private final ParticleSystem particleSystem;
    private final Graphics2D graphics;

    // configurations to keep track of board changes
    private String[][] board;
    private String[][] previousBoard;

    // logs for undo and beginning states
    private Stack<HashMap<Long, ArrayList<Component>>> undoStack = new Stack<>();
    private final HashMap<Long, ArrayList<Component>> initialState = new HashMap<>();

    // component logs for rule changes
    private final ArrayList<Class<? extends Component>> isWall = new ArrayList<>() ;
    private final ArrayList<Class<? extends Component>> isRock = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isWater = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isFlag = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isLava = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isBaba = new ArrayList<>();

    private final ArrayList<Class<? extends Component>> isPush = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isWin = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isYou = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isStop = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isKill = new ArrayList<>();
    private final ArrayList<Class<? extends Component>> isSink = new ArrayList<>();

    // textures
    private final Texture wallTexture = new Texture("resources/images/wall.png");
    private final Texture rockTexture = new Texture("resources/images/rock.png");
    private final Texture flagTexture = new Texture("resources/images/flag.png");
    private final Texture babaTexture = new Texture("resources/images/big-blue.png");
    private final Texture waterTexture = new Texture("resources/images/water.png");
    private final Texture lavaTexture = new Texture("resources/images/lava.png");
    private final Texture blankTexture = new Texture("resources/images/blank.png");

    int width;
    int height;

    private final HashMap<String, Class<? extends Component>> componentHashMap = new HashMap<>();

    private final Sound popSound;
    private final Sound xylophoneSound;


    public RuleManager(int width, int height,
                       int gridSize, long window,
                       KeyBoardConfig keyBoardConfig, SoundManager audio,
                       Graphics2D graphics){

        // rule manager cares about every component type
        super(ecs.Components.Appearance.class);

        this.graphics = graphics;

        popSound = audio.load("pop", "resources/audio/Pop.ogg", false);
        Sound cheerSound = audio.load("cheer", "resources/audio/Cheer.ogg", false);
        xylophoneSound = audio.load("xylophone", "resources/audio/Xylophone.ogg", false);

        this.width = width;
        this.height = height;

        this.componentHashMap.put("B", ecs.Components.IsBaba.class);
        this.componentHashMap.put("R", ecs.Components.IsRock.class);
        this.componentHashMap.put("V", ecs.Components.IsLava.class);
        this.componentHashMap.put("A", ecs.Components.IsWater.class);
        this.componentHashMap.put("F", ecs.Components.IsFlag.class);
        this.componentHashMap.put("W", ecs.Components.IsWall.class);

        float OFFSET_X = 0.1f;
        float OFFSET_Y = 0.1f;
        // logs for particle system renderer updates
        float CELL_SIZE = (1.0f - OFFSET_X * 2) / gridSize;

        board = new String[width][height];
        previousBoard = board;

        this.window = window;

        this.keyBoardConfig = keyBoardConfig;

        this.particleSystem = new ParticleSystem(gridSize);

        this.particleSystemRenderer = new ParticleSystemRenderer();
        this.particleSystemRenderer.initialize("resources/images/sparkle.png");
    }

    @Override
    public void update(double elapsedTime) {
        if (this.keyBoardConfig != null && this.keyBoardConfig.initialized) {

            intervalElapsed += elapsedTime;

            isYou.clear();
            isSink.clear();
            isPush.clear();
            isKill.clear();
            isStop.clear();
            isWin.clear();

            isFlag.clear();
            isRock.clear();
            isBaba.clear();
            isWall.clear();
            isWater.clear();
            isLava.clear();

            updateBoardAll();
            updateRules();
            applyRules();
            updateEntityTypes();
            checkWin();
            checkKill();
            checkSink();
            updateUndoStack();

            for (var component: isWin){
                if (!isWinConfig.contains(component)){
                    for (var entity: entities.values()){
                        if (entity.contains(component)){
                            Position position = entity.get(ecs.Components.Position.class);
                            particleSystem.isWinChange(position.getX(), position.getY());
                        }
                    }
                }
            }
            isWinConfig = (ArrayList<Class<? extends Component>>) isWin.clone();

            particleSystem.update(elapsedTime);

            particleSystemRenderer.render(graphics, particleSystem);

            if (isYouChange) {
                xylophoneSound.play();
                isYouChange = false;
            }

            if (intervalElapsed > MOVE_INTERVAL) {
                checkUndo();
                intervalElapsed -= MOVE_INTERVAL;
            }
        }

        if (undoStack.size() < 2){
            for (var entity : entities.values()) {
                storeCopy(entity, initialState);
            }
        }
    }

    private void updateEntityTypes(){
        for (var entity: entities.values()){
            Appearance entityAppearance = entity.get(ecs.Components.Appearance.class);
            if (!isWall.isEmpty()) {
                for (var isWallComponent: isWall) {
                    if (entity.contains(isWallComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsWall());
                        entityAppearance.spriteSheet = wallTexture;
                    }
                }
            }

            if (!isRock.isEmpty()) {
                for (var isRockComponent: isRock) {
                    if (entity.contains(isRockComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsRock());
                        entityAppearance.spriteSheet = rockTexture;
                    }
                }
            }

            if (!isWater.isEmpty()) {
                for (var isWaterComponent: isWater) {
                    if (entity.contains(isWaterComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsWater());
                        entityAppearance.spriteSheet = waterTexture;
                    }
                }
            }

            if (!isLava.isEmpty()) {
                for (var isLavaComponent: isLava) {
                    if (entity.contains(isLavaComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsLava());
                        entityAppearance.spriteSheet = lavaTexture;
                    }
                }
            }

            if (!isFlag.isEmpty()) {
                for (var isFlagComponent: isFlag) {
                    if (entity.contains(isFlagComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsFlag());
                        entityAppearance.spriteSheet = flagTexture;
                    }
                }
            }

            if (!isBaba.isEmpty()) {
                for (var isBabaComponent: isBaba) {
                    if (entity.contains(isBabaComponent)) {
                        removeCurrentType(entity);
                        entity.add(new ecs.Components.IsBaba());
                        entityAppearance.spriteSheet = babaTexture;
                    }
                }
            }
        }

        isLava.clear();
        isWall.clear();
        isRock.clear();
        isWater.clear();
        isBaba.clear();
    }

    private void removeCurrentType(Entity entity){
        if (entity.contains(ecs.Components.IsWall.class)){
            entity.remove(ecs.Components.IsWall.class);
        }
        if (entity.contains(ecs.Components.IsFlag.class)){
            entity.remove(ecs.Components.IsFlag.class);
        }
        if (entity.contains(ecs.Components.IsHedge.class)){
            entity.remove(ecs.Components.IsHedge.class);
        }
        if (entity.contains(ecs.Components.IsLava.class)){
            entity.remove(ecs.Components.IsLava.class);
        }
        if (entity.contains(ecs.Components.IsRock.class)){
            entity.remove(ecs.Components.IsRock.class);
        }
        if (entity.contains(ecs.Components.IsWater.class)){
            entity.remove(ecs.Components.IsWater.class);
        }
        if (entity.contains(ecs.Components.IsBaba.class)){
            entity.remove(ecs.Components.IsBaba.class);
        }
    }

    private void updateRules(){

        for (int col = 0; col < board.length; col++){
            for (int row =0 ; row < board[0].length; row++){
                if ((board[row][col] != null) && (board[row][col].equals("I"))){
                    // "is" text exists here, check and update rules
                    if ((col - 1 >= 0) && (col+1 < board.length)){
                        String right_element = board[row][col+1];
                        String left_element = board[row][col-1];
                        if ((right_element != null) && (left_element != null)){
                            switch (right_element) {
                                case "Y" -> {
                                    if (!isYou.contains(this.componentHashMap.get(left_element))) {
                                        isYou.add(this.componentHashMap.get(left_element));
                                    }
                                }
                                case "P" -> {
                                    if (!isPush.contains(this.componentHashMap.get(left_element))) {
                                        isPush.add(this.componentHashMap.get(left_element));
                                    }
                                }
                                case "X" -> {
                                    if (!isWin.contains(this.componentHashMap.get(left_element))) {
                                        isWin.add(this.componentHashMap.get(left_element));
                                    }
                                }
                                case "S" -> {
                                    if (!isStop.contains(this.componentHashMap.get(left_element))) {
                                        isStop.add(this.componentHashMap.get(left_element));
                                    }
                                }
                                case "K" -> {
                                    if (!isKill.contains(this.componentHashMap.get(left_element))) {
                                        isKill.add(this.componentHashMap.get(left_element));
                                    }
                                }
                                case "N" -> {
                                    if (!isSink.contains(this.componentHashMap.get(left_element))) {
                                        isSink.add(this.componentHashMap.get(left_element));
                                    }
                                }

                                case "W" -> isWall.add(this.componentHashMap.get(left_element));
                                case "R" -> isRock.add(this.componentHashMap.get(left_element));
                                case "F" -> isFlag.add(this.componentHashMap.get(left_element));
                                case "A" -> isWater.add(this.componentHashMap.get(left_element));
                                case "V" -> isLava.add(this.componentHashMap.get(left_element));
                                case "B" -> isBaba.add(this.componentHashMap.get(left_element));

                            }
                        }
                    }

                    if ((row - 1 >= 0) && (row+1 < board[0].length)){
                        String top_element = board[row-1][col];
                        String bottom_element = board[row+1][col];
                        if ((bottom_element != null) && (top_element != null)){
                            switch (bottom_element) {
                                case "Y" -> {
                                    if (!isYou.contains(this.componentHashMap.get(top_element))) {
                                        isYou.add(this.componentHashMap.get(top_element));
                                    }
                                }
                                case "P" -> {
                                    if (!isPush.contains(this.componentHashMap.get(top_element))) {
                                        isPush.add(this.componentHashMap.get(top_element));
                                    }
                                }
                                case "X" -> {
                                    if (!isWin.contains(this.componentHashMap.get(top_element))) {
                                        isWin.add(this.componentHashMap.get(top_element));
                                    }
                                }
                                case "S" -> {
                                    if (!isStop.contains(this.componentHashMap.get(top_element))) {
                                        isStop.add(this.componentHashMap.get(top_element));
                                    }
                                }
                                case "K" -> {
                                    if (!isKill.contains(this.componentHashMap.get(top_element))) {
                                        isKill.add(this.componentHashMap.get(top_element));
                                    }
                                }
                                case "N" -> {
                                    if (!isSink.contains(this.componentHashMap.get(top_element))) {
                                        isSink.add(this.componentHashMap.get(top_element));
                                    }
                                }

                                case "W" -> isWall.add(this.componentHashMap.get(top_element));
                                case "R" -> isRock.add(this.componentHashMap.get(top_element));
                                case "F" -> isFlag.add(this.componentHashMap.get(top_element));
                                case "A" -> isWater.add(this.componentHashMap.get(top_element));
                                case "L" -> isLava.add(this.componentHashMap.get(top_element));
                                case "B" -> isBaba.add(this.componentHashMap.get(top_element));
                            }
                        }
                    }
                }

                // update is stop is moved
                if ((board[row][col] != null) && (board[row][col].equals("S"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isStop.clear();
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isStop.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isStop.clear();
                        } else {
                            if (board[row][col - 2] == null){
                                isStop.clear();
                            }
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isStop.clear();
                        } else {
                            if (board[row-2][col] == null){
                                isStop.clear();
                            }
                        }
                    }
                }

                // update is you
                if ((board[row][col] != null) && (board[row][col].equals("Y"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isYou.clear();
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isYou.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isYou.clear();
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isYou.clear();
                        }
                    }
                }

                // update is push

                if ((board[row][col] != null) && (board[row][col].equals("P"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isPush.clear();
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isPush.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isPush.clear();
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isPush.clear();
                        }
                    }
                }

                // update is win
                if ((board[row][col] != null) && (board[row][col].equals("X"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isWin.clear();
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isWin.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isWin.clear();
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isWin.clear();
                        }
                    }
                }

                // update is defeat
                if ((board[row][col] != null) && (board[row][col].equals("K"))){
                    if ((board[row][col-1] == null) && (board[row-1][col] == null)){
                        isKill.clear();
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col-1].equals("I")) && (!board[row-1][col].equals("I"))){
                            isKill.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isKill.clear();
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isKill.clear();
                        }
                    }
                }

                // update is sink
                if ((board[row][col] != null) && (board[row][col].equals("N"))){
                    if ((board[row][col-1] == null) && (board[row-1][col] == null)){
                        isSink.clear();
                    }

                    else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col-1].equals("I")) && (!board[row-1][col].equals("I"))){
                            isSink.clear();
                        }
                    } else if ((board[row][col - 1] != null)) {
                        if ((!board[row][col - 1].equals("I"))) {
                            isSink.clear();
                        }
                    } else if ((board[row - 1][col] != null)) {
                        if ((!board[row-1][col].equals("I"))) {
                            isSink.clear();
                        }
                    }
                }
            }
        }

        if (isYou != isYou){
            isYouChange = true;
        }
    }

    private void applyRules(){
        for (var entity: entities.values()) {
            if (!isYou.isEmpty()) {
                boolean isYouing = false;
                for (var isYouComponent : isYou) {
                    if (entity.contains(isYouComponent)) {
                        isYouing = true;
                    }
                }
                if (isYouing) {
                    if (!entity.contains(ecs.Components.Movable.class)) {
                        entity.add(new ecs.Components.Movable(Movable.Direction.Stopped, MOVE_INTERVAL));

                        Position position = entity.get(ecs.Components.Position.class);
                        if (!won) {
                            particleSystem.isYouChange(position.getX(), position.getY());
                        }
                    }

                    if (!entity.contains(ecs.Components.KeyboardControlled.class)) {
                        entity.add(new ecs.Components.KeyboardControlled(Map.of(
                                this.keyBoardConfig.up, Movable.Direction.Up,
                                this.keyBoardConfig.left, Movable.Direction.Left,
                                this.keyBoardConfig.right, Movable.Direction.Right,
                                this.keyBoardConfig.down, Movable.Direction.Down
                        )));
                    }
                } else {
                    if (entity.contains(ecs.Components.Movable.class)) {
                        entity.remove(ecs.Components.Movable.class);
                    }
                    if (entity.contains(ecs.Components.KeyboardControlled.class)) {
                        entity.remove(ecs.Components.KeyboardControlled.class);
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Movable.class)) {
                    entity.remove(ecs.Components.Movable.class);
                }
                if (entity.contains(ecs.Components.KeyboardControlled.class)) {
                    entity.remove(ecs.Components.KeyboardControlled.class);
                }

            }

            if (!isStop.isEmpty()) {
                boolean isStopping = false;
                for (var isStopComponent : isStop) {
                    if (entity.contains(isStopComponent)) {
                        isStopping = true;
                    }
                }
                if (isStopping) {
                    if (!entity.contains(ecs.Components.Stoppable.class)) {
                        entity.add(new ecs.Components.Stoppable());
                    }
                } else {
                    if (entity.contains(ecs.Components.Stoppable.class)) {
                        if (!entity.contains(ecs.Components.IsHedge.class)) {
                            entity.remove(ecs.Components.Stoppable.class);
                        }
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Stoppable.class)) {
                    if (!entity.contains(ecs.Components.IsHedge.class)) {
                        entity.remove(ecs.Components.Stoppable.class);
                    }
                }
            }

            if (!isPush.isEmpty()) {
                boolean isPushing = false;
                for (var isPushComponent: isPush) {
                    if (entity.contains(isPushComponent)) {
                        isPushing = true;
                    }
                }
                if (isPushing) {
                    if (!entity.contains(ecs.Components.Pushable.class)) {
                        entity.add(new ecs.Components.Pushable());
                    }
                } else {
                    if (entity.contains(ecs.Components.Pushable.class)) {
                        if (!entity.contains(ecs.Components.Text.class)) {
                            entity.remove(ecs.Components.Pushable.class);
                        }
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Pushable.class)) {
                    if (!entity.contains(ecs.Components.Text.class)) {
                        entity.remove(ecs.Components.Pushable.class);
                    }
                }
            }

            if (!isWin.isEmpty()) {
                boolean isWinning = false;
                for (var isWinComponent: isWin) {
                    if (entity.contains(isWinComponent)) {
                        isWinning = true;
                    }
                }
                if (isWinning) {
                    if (!entity.contains(ecs.Components.Winnable.class)) {
                        entity.add(new ecs.Components.Winnable());
                    }
                } else {
                    if (entity.contains(ecs.Components.Winnable.class)) {
                        entity.remove(ecs.Components.Winnable.class);
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Winnable.class)) {
                    entity.remove(ecs.Components.Winnable.class);
                }
            }

            if (!isKill.isEmpty()) {
                boolean isKilling = false;
                for (var isKillComponent: isKill) {
                    if (entity.contains(isKillComponent)) {
                        isKilling = true;
                    }
                }

                if (isKilling) {
                    if (!entity.contains(ecs.Components.Killing.class)) {
                        entity.add(new ecs.Components.Killing());
                    }
                } else {
                    if (entity.contains(ecs.Components.Killing.class)) {
                        entity.remove(ecs.Components.Killing.class);
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Killing.class)) {
                    entity.remove(ecs.Components.Killing.class);
                }
            }

            if (!isSink.isEmpty()) {
                boolean isSinking = false;
                for (var isSinkComponent: isSink) {
                    if (entity.contains(isSinkComponent)) {
                        isSinking = true;
                    }
                }
                if (isSinking) {
                    if (!entity.contains(ecs.Components.Sinking.class)) {
                        entity.add(new ecs.Components.Sinking());
                    }
                } else {
                    if (entity.contains(ecs.Components.Sinking.class)) {
                        entity.remove(ecs.Components.Sinking.class);
                    }
                }
            } else {
                if (entity.contains(ecs.Components.Sinking.class)){
                    entity.remove(ecs.Components.Sinking.class);
                }
            }
        }
    }

    private void updateBoard(Entity entity, String characterMarker){
        Position entityPosition = entity.get(Position.class);
        int x_pos = entityPosition.getX();
        int y_pos = entityPosition.getY();

        board[y_pos][x_pos] = characterMarker;
    }

    private void updateBoardAll(){
        board = new String[width][height];
        for (var entity : entities.values()) {
            if (entity.contains(IsWall.class)) {
                updateBoard(entity, "w");
            } else if (entity.contains(IsRock.class)) {
                updateBoard(entity, "r");
            } else if (entity.contains(IsBaba.class)) {
                updateBoard(entity, "b");
            } else if (entity.contains(IsWater.class)) {
                updateBoard(entity, "a");
            } else if (entity.contains(IsLava.class)) {
                updateBoard(entity, "v");
            } else if (entity.contains(IsFlag.class)) {
                updateBoard(entity, "f");
            } else if (entity.contains(IsHedge.class)) {
                updateBoard(entity, "h");
            }
            // update text items
            else if (entity.contains(IsWallText.class)) {
                updateBoard(entity, "W");

            } else if (entity.contains(IsRockText.class)) {
                updateBoard(entity, "R");

            } else if (entity.contains(IsBabaText.class)) {
                updateBoard(entity, "B");

            } else if (entity.contains(IsWaterText.class)) {
                updateBoard(entity, "A");

            } else if (entity.contains(IsLavaText.class)) {
                updateBoard(entity, "V");

            } else if (entity.contains(IsFlagText.class)) {
                updateBoard(entity, "F");

            } else if (entity.contains(IsYouText.class)) {
                updateBoard(entity, "Y");
            } else if (entity.contains(IsIsText.class)) {
                updateBoard(entity, "I");
            } else if (entity.contains(IsWinText.class)) {
                updateBoard(entity, "X");
            } else if (entity.contains(IsSinkText.class)) {
                updateBoard(entity, "N");
            } else if (entity.contains(IsKillText.class)) {
                updateBoard(entity, "K");
            } else if (entity.contains(IsStopText.class)) {
                updateBoard(entity, "S");
            } else if (entity.contains(IsPushText.class)) {
                updateBoard(entity, "P");
            }
        }
    }

    private void checkWin(){
        // checking win condition
        ArrayList<Entity> winningEntities = new ArrayList<>();
        ArrayList<Entity> isYouEntities = new ArrayList<>();
        for (var entity: entities.values()){
            if (entity.contains(Winnable.class)){
                winningEntities.add(entity);
            }

            if (entity.contains(KeyboardControlled.class)){
                isYouEntities.add(entity);
            }
        }

        for (var entity: winningEntities){
            Position winningPosition = entity.get(ecs.Components.Position.class);
            int winningX = winningPosition.getX();
            int winningY = winningPosition.getY();
            for (var isYouEntity: isYouEntities){
                Position isYouPosition = isYouEntity.get(ecs.Components.Position.class);
                int isYouX = isYouPosition.getX();
                int isYouY = isYouPosition.getY();

                if ((winningX == isYouX) && (winningY == isYouY)){
                    // GAME WON

                    if (!this.won) {
                        SoundManager audio = new SoundManager();
                        Sound cheer = audio.load("win", "resources/audio/Cheer.ogg", false);
                        cheer.play();

                        for (Entity winningEntity: entities.values()){
                            for (var isYouComponent: isYou) {
                                if (winningEntity.contains(isYouComponent)) {
                                    Position position = winningEntity.get(ecs.Components.Position.class);
                                    particleSystem.objectIsWin(position.getX(), position.getY());
                                }
                            }
                        }
                        this.won = true;
                    }
                    for (var allEntities: entities.values()){
                        if (allEntities.contains(ecs.Components.Movable.class)){
                            allEntities.remove(ecs.Components.Movable.class);
                        }
                        if (allEntities.contains(ecs.Components.KeyboardControlled.class)){
                            allEntities.remove(ecs.Components.KeyboardControlled.class);
                        }
                    }
                }
            }
        }
    }

    private void checkKill(){
        // checking win condition
        ArrayList<Entity> killingEntities = new ArrayList<>();
        ArrayList<Entity> isYouEntities = new ArrayList<>();
        for (var entity: entities.values()){
            if (entity.contains(Killing.class)){
                killingEntities.add(entity);
            }

            if (entity.contains(KeyboardControlled.class)){
                isYouEntities.add(entity);
            }
        }

        for (var entity: killingEntities){
            Position killingPosition = entity.get(ecs.Components.Position.class);
            int killingX = killingPosition.getX();
            int killingY = killingPosition.getY();
            for (var isYouEntity: isYouEntities){
                Position isYouPosition = isYouEntity.get(ecs.Components.Position.class);
                int isYouX = isYouPosition.getX();
                int isYouY = isYouPosition.getY();

                if ((killingX == isYouX) && (killingY == isYouY)){
                    // destroy component
                    particleSystem.objectDeath(isYouX, isYouY);
                    destroyEntity(isYouEntity);
                }
            }
        }
    }

    private void destroyEntity(Entity entity){
        Position position = entity.get(ecs.Components.Position.class);

        if (entity.contains(ecs.Components.Movable.class)){
            entity.remove(ecs.Components.Movable.class);
        }
        if (entity.contains(ecs.Components.KeyboardControlled.class)){
            entity.remove(ecs.Components.KeyboardControlled.class);
        }
        if (entity.contains(ecs.Components.Appearance.class)){
            Appearance entityAppearance = entity.get(ecs.Components.Appearance.class);
            entityAppearance.spriteSheet = blankTexture;
        }
        if (entity.contains(ecs.Components.Pushable.class)){
            entity.remove(ecs.Components.Pushable.class);
        }
        if (entity.contains(ecs.Components.Killing.class)){
            entity.remove(ecs.Components.Killing.class);
        }
        if (entity.contains(ecs.Components.Winnable.class)){
            entity.remove(ecs.Components.Winnable.class);
        }
        if (entity.contains(ecs.Components.Sinking.class)){
            entity.remove(ecs.Components.Sinking.class);
        }
        if (entity.contains(ecs.Components.IsWater.class)){
            entity.remove(ecs.Components.IsWater.class);
        }
        if (entity.contains(ecs.Components.IsRock.class)){
            entity.remove(ecs.Components.IsRock.class);
        }
        if (entity.contains(ecs.Components.IsLava.class)){
            entity.remove(ecs.Components.IsLava.class);
        }
        if (entity.contains(ecs.Components.IsBaba.class)){
            entity.remove(ecs.Components.IsBaba.class);
        }
        if (entity.contains(ecs.Components.IsFlag.class)){
            entity.remove(ecs.Components.IsFlag.class);
        }
        if (entity.contains(ecs.Components.IsWall.class)){
            entity.remove(ecs.Components.IsWall.class);
        }
    }

    private void checkSink(){
        ArrayList<Entity> sinkComponents = new ArrayList<>();
        for (var entity: entities.values()){
            if (entity.contains(ecs.Components.Sinking.class)){
                sinkComponents.add(entity);
            }
        }

        for (var entity: entities.values()){
            if (!entity.contains(ecs.Components.Sinking.class) && (!entity.contains(ecs.Components.Text.class))) {
                Position entityPosition = entity.get(ecs.Components.Position.class);
                for (var sinkable : sinkComponents) {
                    Position sinkablePosition = sinkable.get(ecs.Components.Position.class);
                    if ((entityPosition.getX() == sinkablePosition.getX()) && (entityPosition.getY() == sinkablePosition.getY())) {
                        particleSystem.objectDeath(entityPosition.getX(), entityPosition.getY());
                        destroyEntity(sinkable);
                        destroyEntity(entity);
                    }
                }
            }
        }
    }

    private void updateUndoStack() {

        if (!boardsMatch()) {
            popSound.play();

            java.lang.System.out.println("NEW BOARD CONFIG");
            HashMap<Long, ArrayList<Component>> storingHashMap = new HashMap<>();
            for (var entity : entities.values()) {
                storeCopy(entity, storingHashMap);
            }
            undoStack.push(storingHashMap);
        }
        previousBoard = board;
    }

    private void undo(){
        isYou.clear();
        isSink.clear();
        isPush.clear();
        isKill.clear();
        isStop.clear();
        isWin.clear();
        if (!undoStack.isEmpty()){
            // get previous gameStates from stack
            var previousState = undoStack.pop();
            for (var entity: entities.values()){
                if (!entity.contains(ecs.Components.Text.class)) {
                    destroyEntity(entity);
                }
                // previous states for each entity stored in the hash map
                var previousEntityStates = previousState.get(entity.getId());
                for (Component component: previousEntityStates){
                    if (entity.contains(component.getClass())){
                        entity.remove(component.getClass());
                        entity.add(component);
                    }
                    else {
                        entity.add(component);
                    }
                }
            }
        }
    }

    private void restart(){
        for (var entity: entities.values()){
            // previous states for each entity stored in the hash map
            var previousEntityStates = initialState.get(entity.getId());
            for (Component component: previousEntityStates){
                if (entity.contains(component.getClass())){
                    entity.remove(component.getClass());
                    entity.add(component);
                }
                else {
                    entity.add(component);
                }
            }
        }

        for (var entity: entities.values()){

            if (entity.contains(ecs.Components.KeyboardControlled.class)){
                entity.remove(ecs.Components.KeyboardControlled.class);
            }
            if (entity.contains(ecs.Components.Movable.class)){
                entity.remove(ecs.Components.Movable.class);
            }
        }
        undoStack = new Stack<>();
    }

    private boolean boardsMatch(){
        for (int row = 0; row <width; row ++){
            for (int col = 0; col < height; col ++){
                if (!Objects.equals(board[row][col], previousBoard[row][col])){
                    return false;
                }
            }
        }
        return true;
    }

    private void storeCopy
            (Entity entity, HashMap<Long, ArrayList<Component>> hashMap){

        ArrayList<ecs.Components.Component> componentsList = new ArrayList<>();

        if (entity.contains(ecs.Components.Appearance.class)){
            Appearance entityAppearance = entity.get(ecs.Components.Appearance.class);
            Appearance newAppearance = new ecs.Components.Appearance(entityAppearance.spriteSheet, entityAppearance.spriteTime);
            componentsList.add(newAppearance);
        }

        if (entity.contains(ecs.Components.IsBaba.class)){
            componentsList.add(new IsBaba());
        }

        if (entity.contains(ecs.Components.IsBabaText.class)){
            componentsList.add(new IsBabaText());
        }

        if (entity.contains(ecs.Components.IsFlag.class)){
            componentsList.add(new IsFlag());
        }

        if (entity.contains(ecs.Components.IsHedge.class)){
            componentsList.add(new IsHedge());
        }

        if (entity.contains(ecs.Components.IsLava.class)){
            componentsList.add(new IsLava());
        }

        if (entity.contains(ecs.Components.IsRock.class)){
            componentsList.add(new IsRock());
        }

        if (entity.contains(ecs.Components.IsWall.class)){
            componentsList.add(new IsWall());
        }

        if (entity.contains(ecs.Components.IsWater.class)){
            componentsList.add(new IsWater());
        }

        if (entity.contains(ecs.Components.KeyboardControlled.class)){
            KeyboardControlled entityKeyboardControlled = entity.get(ecs.Components.KeyboardControlled.class);
            KeyboardControlled keyboardControlledCopy = new KeyboardControlled(entityKeyboardControlled.keys);
            componentsList.add(keyboardControlledCopy);
        }

        if (entity.contains(ecs.Components.Killing.class)){
            componentsList.add(new Killing());
        }


        if (entity.contains(ecs.Components.Movable.class)){
            Movable entityMovable = entity.get(ecs.Components.Movable.class);
            Movable movableCopy = new Movable(entityMovable.input, entityMovable.moveInterval);
            componentsList.add(movableCopy);
        }

        if (entity.contains(ecs.Components.Position.class)){
            Position entityPosition = entity.get(ecs.Components.Position.class);
            Position positionCopy = new Position(entityPosition.getX(), entityPosition.getY());
            componentsList.add(positionCopy);
        }

        hashMap.put(entity.getId(), componentsList);

    }

    private void checkUndo(){

        if (!won) {
            if (glfwGetKey(window, keyBoardConfig.undo) == GLFW_PRESS) {
                undo();
            }

            if (glfwGetKey(window, keyBoardConfig.restart) == GLFW_PRESS) {
                restart();
            }
        }
    }
}
