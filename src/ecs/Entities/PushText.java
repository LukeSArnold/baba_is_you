package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class PushText {
    public static Entity create(Texture square, int x, int y) {
        var pushText = new Entity();

        pushText.add(new ecs.Components.IsPushText());
        pushText.add(new ecs.Components.Appearance(square, Color.WHITE));
        pushText.add(new ecs.Components.Position(x, y));
        pushText.add(new ecs.Components.Collision());
        pushText.add(new ecs.Components.Pushable());

        return pushText;
    }
}
