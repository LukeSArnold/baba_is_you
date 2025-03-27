package ecs.Systems;

import ecs.Components.*;
import ecs.Entities.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class RuleManager extends System {

    final double MOVE_INTERVAL = .250; // seconds

    String[][] board;

    Class<? extends Component> isPush;
    Class<? extends Component> isWin;
    Class<? extends Component> isYou;
    Class<? extends Component> isStop;

    HashMap<String, Class<? extends Component>> componentHashMap = new HashMap<>();

    public RuleManager(int width, int height){
        // rule manager cares about every component type
        super(ecs.Components.Appearance.class);


        this.componentHashMap.put("B", ecs.Components.IsBaba.class);
        this.componentHashMap.put("R", ecs.Components.IsRock.class);
        this.componentHashMap.put("V", ecs.Components.IsLava.class);
        this.componentHashMap.put("A", ecs.Components.IsWater.class);
        this.componentHashMap.put("F", ecs.Components.IsFlag.class);

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
                        if ((right_element != null) && (left_element != null)){
                            switch (right_element) {
                                case "Y" -> isYou = this.componentHashMap.get(left_element);
                                case "P" -> isPush = this.componentHashMap.get(left_element);
                                case "X" -> isWin = this.componentHashMap.get(left_element);
                                case "S" -> isStop = this.componentHashMap.get(left_element);
                            }
                        }
                    }

                    if ((row - 1 >= 0) && (row+1 < board[0].length)){
                        String top_element = board[row-1][col];
                        String bottom_element = board[row+1][col];
                        if ((top_element != null) && (bottom_element != null)){
                            switch (bottom_element) {
                                case "Y" -> isYou = this.componentHashMap.get(top_element);
                                case "P" -> isPush = this.componentHashMap.get(top_element);
                                case "X" -> isWin = this.componentHashMap.get(top_element);
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyRules(){
        for (var entity: entities.values()){
            if (entity.contains(isYou)){
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
            }
        }
    }


    @Override
    public void update(double elapsedTime) {
        updateBoardAll();
        updateRules();
        applyRules();
    }
}
