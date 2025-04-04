package ecs.Systems;

import ecs.Components.*;
import ecs.Entities.Entity;
import ecs.Particle;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class RuleManager extends System {

    final double MOVE_INTERVAL = .1; // seconds

    private final MyRandom random = new MyRandom();

    private final float CELL_SIZE;
    private final float OFFSET_X;
    private final float OFFSET_Y;

    private String[][] board;

    private Class<? extends Component> isPush;
    private Class<? extends Component> isWin;
    private Class<? extends Component> isYou;
    private Class<? extends Component> isStop;
    private Class<? extends Component> isKill;
    private Class<? extends Component> isSink;

    // textures
    Texture blankTexture = new Texture("resources/images/blank.png");

    int width;
    int height;



    private HashMap<String, Class<? extends Component>> componentHashMap = new HashMap<>();

    public RuleManager(int width, int height, int gridSize){
        // rule manager cares about every component type
        super(ecs.Components.Appearance.class);

        this.width = width;
        this.height = height;

        this.componentHashMap.put("B", ecs.Components.IsBaba.class);
        this.componentHashMap.put("R", ecs.Components.IsRock.class);
        this.componentHashMap.put("V", ecs.Components.IsLava.class);
        this.componentHashMap.put("A", ecs.Components.IsWater.class);
        this.componentHashMap.put("F", ecs.Components.IsFlag.class);
        this.componentHashMap.put("W", ecs.Components.IsWall.class);

        OFFSET_X = 0.1f;
        OFFSET_Y = 0.1f;
        CELL_SIZE = (1.0f - OFFSET_X * 2) / gridSize;

        board = new String[width][height];

    }

    private void updateRules(){
        for (int col = 0; col < board.length; col++){
            for (int row =0 ; row < board[0].length; row++){
                if ((board[row][col] != null) && (board[row][col].equals("I"))){
                    // "is" text exists here, check and update rules
                    if ((col - 1 >= 0) && (col+1 < board.length)){
                        String right_element = board[row][col+1];
                        String left_element = board[row][col-1];
                        if ((right_element != null)){
                            switch (right_element) {
                                case "Y" -> isYou = this.componentHashMap.get(left_element);
                                case "P" -> isPush = this.componentHashMap.get(left_element);
                                case "X" -> isWin = this.componentHashMap.get(left_element);
                                case "S" -> isStop = this.componentHashMap.get(left_element);
                                case "K" -> isKill = this.componentHashMap.get(left_element);
                                case "N" -> isSink = this.componentHashMap.get(left_element);
                            }
                        }
                    }

                    if ((row - 1 >= 0) && (row+1 < board[0].length)){
                        String top_element = board[row-1][col];
                        String bottom_element = board[row+1][col];
                        if (bottom_element != null){
                            switch (bottom_element) {
                                case "Y" -> isYou = this.componentHashMap.get(top_element);
                                case "P" -> isPush = this.componentHashMap.get(top_element);
                                case "X" -> isWin = this.componentHashMap.get(top_element);
                                case "S" -> isStop = this.componentHashMap.get(top_element);
                                case "K" -> isKill = this.componentHashMap.get(top_element);
                                case "N" -> isSink = this.componentHashMap.get(top_element);
                            }
                        }
                    }
                }

                // update is stop is moved
                if ((board[row][col] != null) && (board[row][col].equals("S"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isStop = null;
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isStop = null;
                        }
                    }
                }

                // update is you
                if ((board[row][col] != null) && (board[row][col].equals("Y"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isYou = null;
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isYou = null;
                        }
                    }
                }

                // update is push

                if ((board[row][col] != null) && (board[row][col].equals("P"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isPush = null;
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isPush = null;
                        }
                    }
                }

                // update is win
                if ((board[row][col] != null) && (board[row][col].equals("X"))) {
                    if ((board[row][col - 1] == null) && (board[row - 1][col] == null)) {
                        isWin = null;
                    } else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col - 1].equals("I")) && (!board[row - 1][col].equals("I"))) {
                            isWin = null;
                        }
                    }
                }

                // update is defeat
                if ((board[row][col] != null) && (board[row][col].equals("K"))){
                    if ((board[row][col-1] == null) && (board[row-1][col] == null)){
                        isKill = null;
                    }

                    else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col-1].equals("I")) && (!board[row-1][col].equals("I"))){
                                isKill = null;
                            }
                        }
                }

                // update is sink
                if ((board[row][col] != null) && (board[row][col].equals("N"))){
                    if ((board[row][col-1] == null) && (board[row-1][col] == null)){
                        isSink = null;
                    }

                    else if ((board[row][col - 1] != null) && (board[row-1][col] != null)) {
                        if ((!board[row][col-1].equals("I")) && (!board[row-1][col].equals("I"))){
                            isSink = null;
                        }
                    }
                }
            }
        }
    }

    private void applyRules(){
        for (var entity: entities.values()){
            if (isYou != null) {
                if (entity.contains(isYou)) {
                    if (!entity.contains(ecs.Components.Movable.class)) {
                        entity.add(new ecs.Components.Movable(Movable.Direction.Stopped, MOVE_INTERVAL));
                    }
                    if (!entity.contains(ecs.Components.KeyboardControlled.class)) {
                        entity.add(new ecs.Components.KeyboardControlled(Map.of(
                                GLFW_KEY_UP, ecs.Components.Movable.Direction.Up,
                                GLFW_KEY_DOWN, ecs.Components.Movable.Direction.Down,
                                GLFW_KEY_LEFT, ecs.Components.Movable.Direction.Left,
                                GLFW_KEY_RIGHT, ecs.Components.Movable.Direction.Right
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
            if (isStop != null) {
                if (entity.contains(isStop)) {
                    if (!entity.contains(ecs.Components.Stoppable.class)) {
                        entity.add(new ecs.Components.Particles());
                        addParticles(entity);
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

            if (isPush != null) {
                if (entity.contains(isPush)) {
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

            if (isWin != null) {
                if (entity.contains(isWin)) {
                    if (!entity.contains(ecs.Components.Winnable.class)){
                        entity.add(new ecs.Components.Winnable());
                    }
                } else {
                    if (entity.contains(ecs.Components.Winnable.class)) {
                        entity.remove(ecs.Components.Winnable.class);
                    }
                }
            }

            if (isKill != null) {
                if (entity.contains(isKill)) {
                    if (!entity.contains(ecs.Components.Killing.class)){
                        entity.add(new ecs.Components.Killing());
                    }
                } else {
                    if (entity.contains(ecs.Components.Killing.class)) {
                        entity.remove(ecs.Components.Killing.class);
                    }
                }
            }
            if (isSink != null) {
                if (entity.contains(isSink)) {
                    if (!entity.contains(ecs.Components.Sinking.class)){
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
                    // GAME WON!
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
                    destroyEntity(entity);
                }
            }
        }
    }

    private void destroyEntity(Entity entity){
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
                        destroyEntity(sinkable);
                        destroyEntity(entity);
                    }
                }
            }
        }
    }

    private void addParticles(Entity entity){
        Position position = entity.get(ecs.Components.Position.class);
        Particles particles = entity.get(ecs.Components.Particles.class);

        int x_pos = position.getX();
        int y_pos = position.getY();

        int x = 2 + 2;

        for (int i = 0; i < 20; i++) {
            float size = (float) random.nextGaussian(0.015f, 0.004f);
            var p = new Particle(
                    new Vector2f(-0.5f + OFFSET_X + position.getX() * CELL_SIZE,-0.5f + OFFSET_Y + position.getY() * CELL_SIZE),
                    this.random.nextCircleVector(),
                    (float) this.random.nextGaussian(0.07f, 0.05f),
                    new Vector2f(size, size),
                    this.random.nextGaussian(3, 1) / 10);

            p.area.left = -0.5f + OFFSET_X + position.getX() * CELL_SIZE;
            p.area.top =  -0.5f + OFFSET_Y + position.getY() * CELL_SIZE;

            particles.particles.put(p.name, p);
        }
    }

    @Override
    public void update(double elapsedTime) {
        updateBoardAll();
        updateRules();
        applyRules();
        checkWin();
        checkKill();
        checkSink();
    }
}
