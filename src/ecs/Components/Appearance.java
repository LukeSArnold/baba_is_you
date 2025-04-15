package ecs.Components;

import edu.usu.graphics.AnimatedSprite;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

public class Appearance extends Component {
    public Texture spriteSheet;
    public int subImageWidth;
    public float[] spriteTime;
    public int subImageIndex = 0;
    public double animationTime = 0;
    public Vector2f center;

    public Color color;

    public Appearance(Texture spriteSheet, float[] spriteTime) {
        this.spriteSheet = spriteSheet;
        this.spriteTime = spriteTime;
        this.subImageWidth = spriteSheet.getWidth() / spriteTime.length;
    }

    public Appearance(Texture spriteSheet, float[] spriteTime, int subImageIndex) {
        this.spriteSheet = spriteSheet;
        this.spriteTime = spriteTime;
        this.subImageWidth = spriteSheet.getWidth() / spriteTime.length;
        this.subImageIndex = subImageIndex;
    }
}
