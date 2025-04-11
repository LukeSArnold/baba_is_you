package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class StopText {
    public static Entity create(Texture square, int x, int y) {
        var stopText = new Entity();

        stopText.add(new ecs.Components.IsStopText());
        stopText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        stopText.add(new ecs.Components.Position(x, y));
        stopText.add(new ecs.Components.Pushable());
        stopText.add(new ecs.Components.Text());

        return stopText;
    }
}
