package ecs.Systems;

import ecs.Components.Movable;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInput extends System {

    private final long window;

    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingUp;
    private boolean pressingDown;


    public KeyboardInput(long window) {
        //super(ecs.Components.KeyboardControlled.class, ecs.Components.Appearance.class);
        super(ecs.Components.Appearance.class);

        this.window = window;
    }

    @Override
    public void update(double gameTime) {
        for (var entity : entities.values()) {
            if (entity.contains(ecs.Components.KeyboardControlled.class)) {
                var movable = entity.get(ecs.Components.Movable.class);
                var input = entity.get(ecs.Components.KeyboardControlled.class);


                if (glfwGetKey(window, input.lookup.get(Movable.Direction.Up)) == GLFW_PRESS) {
                    pressingUp = true;
                    if (movable.input != Movable.Direction.Down) {
                        movable.input = Movable.Direction.Up;
                    }
                } if (pressingUp){
                    if (glfwGetKey(window, input.lookup.get(Movable.Direction.Up)) == GLFW_RELEASE) {
                        movable.input = Movable.Direction.Stopped;
                        pressingUp = false;
                    }
                }

                if (glfwGetKey(window, input.lookup.get(Movable.Direction.Down)) == GLFW_PRESS) {
                    if (movable.input != Movable.Direction.Up) {
                        movable.input = Movable.Direction.Down;
                        pressingDown = true;
                    }
                } if (pressingDown) {
                    if (glfwGetKey(window, input.lookup.get(Movable.Direction.Down)) == GLFW_RELEASE) {
                        movable.input = Movable.Direction.Stopped;
                        pressingDown = false;
                    }
                }

                if (glfwGetKey(window, input.lookup.get(Movable.Direction.Left)) == GLFW_PRESS) {
                    if (movable.input != Movable.Direction.Right) {
                        movable.input = Movable.Direction.Left;
                        pressingLeft = true;
                    }
                } if (pressingLeft) {
                    if (glfwGetKey(window, input.lookup.get(Movable.Direction.Left)) == GLFW_RELEASE) {
                        movable.input = Movable.Direction.Stopped;
                        pressingLeft = false;
                    }
                }

                if (glfwGetKey(window, input.lookup.get(Movable.Direction.Right)) == GLFW_PRESS) {
                    if (movable.input != Movable.Direction.Left) {
                        movable.input = Movable.Direction.Right;
                        pressingRight = true;
                    }
                } if (pressingRight) {
                    if (glfwGetKey(window, input.lookup.get(Movable.Direction.Right)) == GLFW_RELEASE) {
                        movable.input = Movable.Direction.Stopped;
                        pressingRight = false;
                    }
                }



            }
        }
    }
}
