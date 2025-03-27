package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class FlagText {
    public static Entity create(Texture square, int x, int y) {
        var flagText = new Entity();

        flagText.add(new ecs.Components.IsFlagText());
        flagText.add(new ecs.Components.Appearance(square, Color.WHITE));
        flagText.add(new ecs.Components.Position(x, y));
        flagText.add(new ecs.Components.Collision());
        flagText.add(new ecs.Components.Pushable());

        return flagText;
    }
}
