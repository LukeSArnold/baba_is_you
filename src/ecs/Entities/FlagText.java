package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class FlagText {
    public static Entity create(Texture square, int x, int y) {
        var flagText = new Entity();

        flagText.add(new ecs.Components.IsFlagText());
        flagText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        flagText.add(new ecs.Components.Position(x, y));
        flagText.add(new ecs.Components.Pushable());
        flagText.add(new ecs.Components.Text());

        return flagText;
    }
}
