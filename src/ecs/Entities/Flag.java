package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Flag {
    public static Entity create(Texture square, int x, int y) {
        var flag = new Entity();

        flag.add(new ecs.Components.IsFlag());
        flag.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        flag.add(new ecs.Components.Position(x, y));
//        flag.add(new ecs.Components.Collision());

        return flag;
    }
}
