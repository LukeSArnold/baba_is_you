package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Hedge {
    public static Entity create(Texture square, int x, int y) {
        var hedge = new Entity();

        hedge.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        hedge.add(new ecs.Components.Position(x, y));
        hedge.add(new ecs.Components.Stoppable());
        hedge.add(new ecs.Components.IsHedge());

        return hedge;
    }
}
