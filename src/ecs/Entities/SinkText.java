package ecs.Entities;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class SinkText {
    public static Entity create(Texture square, int x, int y) {
        var sinkText = new Entity();

        sinkText.add(new ecs.Components.IsSinkText());
        sinkText.add(new ecs.Components.Appearance(square, new float[]{0.2f, 0.2f, 0.2f}));
        sinkText.add(new ecs.Components.Position(x, y));
        sinkText.add(new ecs.Components.Pushable());
        sinkText.add(new ecs.Components.Text());

        return sinkText;
    }
}
