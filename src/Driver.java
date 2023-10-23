package src;

import src.mapeditor.editor.Controller;
import src.utility.PropertiesLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.File;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test1.properties";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) { // is either file or folder
            String path = args[0];
            File file = new File(path);

            if (file.isFile()) { // argument is xml file
                new Controller(file);
            }

            else if (file.isDirectory()) {
                File files[] = file.listFiles();
                Arrays.sort(files);
                List<File> xmlFiles = new ArrayList<>();
                for (File a: files) {
                    if (a.getName().endsWith(".xml")) {
                        xmlFiles.add(a);
                    }
                }

                final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
                GamePlayer gamePlayer = new GamePlayer(file, xmlFiles, properties.getProperty("PacMan.isAuto"),
                        properties.getProperty("seed"));


                gamePlayer.playGames();

            }
        }
        else { // no argument
            new Controller();
        }
    }
}


