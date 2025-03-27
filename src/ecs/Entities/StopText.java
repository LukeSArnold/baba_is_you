package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class StopText {
    public static Entity create(Texture square, int x, int y) {
        var stopText = new Entity();

        stopText.add(new ecs.Components.IsStopText());
        stopText.add(new ecs.Components.Appearance(square, Color.WHITE));
        stopText.add(new ecs.Components.Position(x, y));
        stopText.add(new ecs.Components.Collision());
        stopText.add(new ecs.Components.Pushable());

        return stopText;
    }
}
