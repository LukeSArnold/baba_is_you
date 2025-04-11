package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class WallText {
    public static Entity create(Texture square, int x, int y) {
        var wallText = new Entity();

        wallText.add(new ecs.Components.IsWallText());
        wallText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        wallText.add(new ecs.Components.Position(x, y));
        wallText.add(new ecs.Components.Pushable());
        wallText.add(new ecs.Components.Text());

        return wallText;
    }
}
