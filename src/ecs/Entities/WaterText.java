package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class WaterText {
    public static Entity create(Texture square, int x, int y) {
        var waterText = new Entity();

        waterText.add(new ecs.Components.IsWaterText());
        waterText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        waterText.add(new ecs.Components.Position(x, y));
        waterText.add(new ecs.Components.Pushable());
        waterText.add(new ecs.Components.Text());

        return waterText;
    }
}
