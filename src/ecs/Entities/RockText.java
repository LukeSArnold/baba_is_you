package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class RockText {
    public static Entity create(Texture square, int x, int y) {
        var rockText = new Entity();

        rockText.add(new ecs.Components.IsRockText());
        rockText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        rockText.add(new ecs.Components.Position(x, y));
        rockText.add(new ecs.Components.Pushable());
        rockText.add(new ecs.Components.Text());


        return rockText;
    }
}
