package ecs.Systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;

public class Renderer extends System {

    private final int GRID_SIZE;
    private final float CELL_SIZE;
    private final float OFFSET_X;
    private final float OFFSET_Y;

    private final Graphics2D graphics;

    public Renderer(Graphics2D graphics, int gridSize) {
        super(ecs.Components.Appearance.class,
                ecs.Components.Position.class);

        OFFSET_X = 0.1f;
        OFFSET_Y = 0.1f;
        GRID_SIZE = gridSize;
        CELL_SIZE = (1.0f - OFFSET_X * 2) / gridSize;
        this.graphics = graphics;
    }

    @Override
    public void update(double elapsedTime) {

        // Draw a blue background for the gameplay area
        Rectangle area = new Rectangle(-0.5f + OFFSET_X, -0.5f + OFFSET_Y, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        graphics.draw(area, Color.BLACK);

        // Draw each of the game entities!
        for (var entity : entities.values()) {
            renderEntity(entity);
        }
    }

    private void renderEntity(ecs.Entities.Entity entity) {
        var appearance = entity.get(ecs.Components.Appearance.class);
        var position = entity.get(ecs.Components.Position.class);

        Rectangle area = new Rectangle(0, 0, 0, 0);
        area.left = -0.5f + OFFSET_X + position.getX() * CELL_SIZE;
        area.top = -0.5f + OFFSET_Y + position.getY() * CELL_SIZE;
        area.width = CELL_SIZE;
        area.height = CELL_SIZE;

        var color = Color.WHITE;

        Rectangle subImage;
        if (entity.contains(ecs.Components.IsFloor.class)){
            subImage = new Rectangle(
                    appearance.subImageWidth * appearance.subImageIndex,
                    0,
                    appearance.subImageWidth,
                    appearance.spriteSheet.getHeight());
        } else {
            subImage = new Rectangle(
                    appearance.subImageWidth * appearance.subImageIndex,
                    0,
                    appearance.subImageWidth,
                    appearance.spriteSheet.getHeight(),
                    -1);
        }

        Vector2f center = new Vector2f(area.left + (float) appearance.subImageWidth / 2, area.top + (float) appearance.subImageWidth / 2);
        graphics.draw(appearance.spriteSheet, area, subImage, 0, center, color);
    }
}
