package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Hedge {
    public static Entity create(Texture square, int x, int y) {
        var hedge = new Entity();

        hedge.add(new ecs.Components.Appearance(square, Color.WHITE));
        hedge.add(new ecs.Components.Position(x, y));
        //hedge.add(new ecs.Components.Collision());

        return hedge;
    }
}
