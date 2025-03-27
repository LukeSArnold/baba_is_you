package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class BabaText {
    public static Entity create(Texture square, int x, int y) {
        var babaText = new Entity();

        babaText.add(new ecs.Components.IsBabaText());
        babaText.add(new ecs.Components.Appearance(square, Color.WHITE));
        babaText.add(new ecs.Components.Position(x, y));
        babaText.add(new ecs.Components.Collision());
        babaText.add(new ecs.Components.Pushable());

        return babaText;
    }
}
