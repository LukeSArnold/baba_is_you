package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class WinText {
    public static Entity create(Texture square, int x, int y) {
        var winText = new Entity();

        winText.add(new ecs.Components.IsWinText());
        winText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        winText.add(new ecs.Components.Position(x, y));
        winText.add(new ecs.Components.Collision());
        winText.add(new ecs.Components.Pushable());
        winText.add(new ecs.Components.Text());

        return winText;
    }
}
