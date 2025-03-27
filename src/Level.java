import java.util.ArrayList;

public class Level {
    public String level_name;
    public int height;
    public int width;
    public ArrayList<String[][]> content;

    public Level(String level_name, int height, int width, ArrayList<String[][]> content){
        this.level_name = level_name;
        this.height = height;
        this.width = width;
        this.content = content;
    }
}
