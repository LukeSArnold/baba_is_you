import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files


public class LevelMapper {
    public ArrayList<Level> levels;

    public LevelMapper(String bbiy_file){
        this.levels = new ArrayList<Level>();

        try {
            // open bbiy file for parsing
            File myObj = new File(bbiy_file);
            Scanner myReader = new Scanner(myObj);

            // keep line count for tracking file contents
            int line_count = 0;

            // declare name, height, width, and layers for Level object instantiation
            String name = "";
            int height = 0;
            int width = 0;
            int layers_parsed = 0;
            ArrayList<String[][]> layers = new ArrayList<>();

            boolean running = true;
            // read through whole file
            while (myReader.hasNextLine()) {
                // get information for name and grid size
                if (line_count < 2) {
                    String data = myReader.nextLine();

                    if (line_count == 0) {
                        name = data;
                    } else if (line_count == 1) {
                        String[] split_data = data.split("x");
                        width = Integer.parseInt(split_data[0].trim());
                        height = Integer.parseInt(split_data[1].trim());
                    }
                    line_count++;
                } else {
                    // parse layers
                    if (layers_parsed < 2) {

                        // assuming only two layers, get all content and store to array
                        String[][] layer = new String[width][height];
                        for (int row = 0; row < height; row++) {
                            String row_content = myReader.nextLine();
                            for (int column = 0; column < row_content.length(); column++) {
                                layer[row][column] = String.valueOf(row_content.charAt(column));
                            }
                        }
                        layers.add(layer);
                        layers_parsed++;
                    } else{
                        // all layers parsed, save new Level object and reset for more levels
                        Level newLevel = new Level(name, height, width, layers);
                        levels.add(newLevel);

                        layers = new ArrayList<>();
                        line_count = 0;
                        layers_parsed = 0;
                    }
                }
            }
            Level newLevel = new Level(name, height, width, layers);
            levels.add(newLevel);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading files");
        }
    }

    public ArrayList<String> GetLevelNames(){
        ArrayList<String> names = new ArrayList<>();
        for (Level level: levels){
            names.add(level.level_name);
        }
        return names;
    }
}

