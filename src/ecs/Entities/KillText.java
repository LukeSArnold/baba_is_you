package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class KillText {
    public static Entity create(Texture square, int x, int y) {
        var killText = new Entity();

        killText.add(new ecs.Components.IsKillText());
        killText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        killText.add(new ecs.Components.Position(x, y));
        killText.add(new ecs.Components.Collision());
        killText.add(new ecs.Components.Pushable());
        killText.add(new ecs.Components.Text());

        return killText;
    }
}
