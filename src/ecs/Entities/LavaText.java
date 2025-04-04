package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class LavaText {
    public static Entity create(Texture square, int x, int y) {
        var lavaText = new Entity();

        lavaText.add(new ecs.Components.IsLavaText());
        lavaText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        lavaText.add(new ecs.Components.Position(x, y));
        lavaText.add(new ecs.Components.Collision());
        lavaText.add(new ecs.Components.Pushable());
        lavaText.add(new ecs.Components.Text());

        return lavaText;
    }
}
