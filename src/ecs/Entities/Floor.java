package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Floor {
    public static Entity create(Texture square, int x, int y) {
        var floor = new Entity();

        floor.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        floor.add(new ecs.Components.Position(x, y));

        return floor;
    }
}
