package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Water {
    public static Entity create(Texture square, int x, int y) {
        var water = new Entity();

        water.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        water.add(new ecs.Components.Position(x, y));
        water.add(new ecs.Components.IsWater());

        return water;
    }
}
