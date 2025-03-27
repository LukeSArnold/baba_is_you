package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class RockText {
    public static Entity create(Texture square, int x, int y) {
        var rockText = new Entity();

        rockText.add(new ecs.Components.IsRockText());
        rockText.add(new ecs.Components.Appearance(square, Color.WHITE));
        rockText.add(new ecs.Components.Position(x, y));
        rockText.add(new ecs.Components.Collision());
        rockText.add(new ecs.Components.Pushable());

        return rockText;
    }
}
