package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class IsText {
    public static Entity create(Texture square, int x, int y) {
        var isText = new Entity();

        isText.add(new ecs.Components.IsIsText());
        isText.add(new ecs.Components.Appearance(square, Color.WHITE));
        isText.add(new ecs.Components.Position(x, y));
        isText.add(new ecs.Components.Collision());
        isText.add(new ecs.Components.Pushable());

        return isText;
    }
}
