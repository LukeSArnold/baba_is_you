package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Lava {
    public static Entity create(Texture square, int x, int y) {
        var lava = new Entity();

        lava.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        lava.add(new ecs.Components.Position(x, y));
        lava.add(new ecs.Components.IsLava());

        return lava;
    }
}
