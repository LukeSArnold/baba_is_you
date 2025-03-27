package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Water {
    public static Entity create(Texture square, int x, int y) {
        var water = new Entity();

        water.add(new ecs.Components.Appearance(square, Color.WHITE));
        water.add(new ecs.Components.Position(x, y));
        //water.add(new ecs.Components.Collision());

        return water;
    }
}
