package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Grass {
    public static Entity create(Texture square, int x, int y) {
        var grass = new Entity();

        grass.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        grass.add(new ecs.Components.Position(x, y));

        return grass;
    }
}
