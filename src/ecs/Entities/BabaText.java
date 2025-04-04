package ecs.Entities;

import ecs.Components.Movable;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class BabaText {
    public static Entity create(Texture spriteSheet, int x, int y) {
        var babaText = new Entity();

        babaText.add(new ecs.Components.IsBabaText());
        babaText.add(new ecs.Components.Appearance(spriteSheet, new float[]{0.2f, 0.2f, 0.2f}));
        babaText.add(new ecs.Components.Position(x, y));
        babaText.add(new ecs.Components.Collision());
        babaText.add(new ecs.Components.Pushable());
        babaText.add(new ecs.Components.Text());

        return babaText;
    }
}
