package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class LavaText {
    public static Entity create(Texture square, int x, int y) {
        var lavaText = new Entity();

        lavaText.add(new ecs.Components.IsLavaText());
        lavaText.add(new ecs.Components.Appearance(square, Color.WHITE));
        lavaText.add(new ecs.Components.Position(x, y));
        lavaText.add(new ecs.Components.Collision());
        lavaText.add(new ecs.Components.Pushable());

        return lavaText;
    }
}
