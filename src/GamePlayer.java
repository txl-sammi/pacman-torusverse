package src;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import src.checking.CompositeCheckingStrategy;
import src.checking.GameCheckingStrategy;
import src.checking.ICheckingStrategy;
import src.checking.LevelCheckingStrategy;
import src.mapeditor.editor.Controller;
import src.utility.GameCallback;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GamePlayer {
    int seed;
    private boolean autoPlayerSwitch;
    private boolean pacDead;
    private boolean pacWin;
    private List<File> xmlFiles;
    private File directory;

    private ICheckingStrategy checkingStrategy;


    public GamePlayer (File directory , List<File> xmlFiles, String auto, String seed) {
        this.directory = directory;
        this.xmlFiles = xmlFiles;
        this.seed = Integer.parseInt(seed);
        this.autoPlayerSwitch = auto.equals("true");
        this.pacDead = false;
        this.pacWin = false;
        ArrayList<ICheckingStrategy> checkingStrategies = new ArrayList<>();
        checkingStrategies.add(new GameCheckingStrategy(directory, xmlFiles));
        checkingStrategies.add(new LevelCheckingStrategy(xmlFiles));


        this.checkingStrategy = new CompositeCheckingStrategy(checkingStrategies);

    }

    public GamePlayer (List<File> xmlFiles, String auto, String seed) {
        this.xmlFiles = xmlFiles;
        this.seed = Integer.parseInt(seed);
        this.autoPlayerSwitch = auto.equals("true");
        this.pacDead = false;
        this.pacWin = false;
        ArrayList<ICheckingStrategy> checkingStrategies = new ArrayList<>();
        checkingStrategies.add(new LevelCheckingStrategy(xmlFiles));
        this.checkingStrategy = new CompositeCheckingStrategy(checkingStrategies);
    }

    public void playGames() {
        // check game folder
        if (!this.checkingStrategy.check()) {
            JOptionPane.showMessageDialog(null, "Game Folder is invalid. Check Log", "error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        for (File a: this.xmlFiles) {
            Properties properties = loadFromXML(a);

            boolean gameWon = playLevel(properties);

            if (!gameWon) {
                break;
            }

            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        new Controller();
    }

    public boolean playLevel(Properties properties) {

        properties.setProperty("PacMan.isAuto", Boolean.toString(this.autoPlayerSwitch));
        properties.setProperty("seed", Integer.toString(this.seed));
        GameCallback gameCallback = new GameCallback();
        Game game = new Game(gameCallback, properties);
        game.setupActorLocations();
        game.setupSeed();
        game.runGame();
        return !game.getPacStatus(); // pacstatus: false if alive  and true if dead, therefore return true if continue and false if no
    }


    public Properties loadFromXML(File file) {
        Properties properties = new Properties();
        if (file.canRead() && file.exists()) {
            try {
                SAXBuilder saxBuilder = new SAXBuilder();

                Document document = saxBuilder.build(file);

                Element rootElement = document.getRootElement();
                Element dimensions = rootElement.getChild("size");


                String height = dimensions.getChild("height").getText();
                String width = dimensions.getChild("width").getText();

                properties.setProperty("height", height);
                properties.setProperty("width", width);

                List<Element> rowElements = rootElement.getChildren("row");
                for (int rowIndex = 0; rowIndex < rowElements.size(); rowIndex++) {
                    Element row = rowElements.get(rowIndex);
                    List<Element> cellElements = row.getChildren("cell");
                    for (int cellIndex = 0; cellIndex < cellElements.size(); cellIndex++) {
                        Element cell = cellElements.get(cellIndex);
                        String cellValue = cell.getText();

                        // Generate the key using the cell value

                        String key = cellValue + ".location";
                        String location = cellIndex + "," + rowIndex;
                        if (properties.containsKey(key)) {
                            String existingValue = properties.getProperty(key);
                            properties.setProperty(key, existingValue + ";" + location);
                        } else {
                            properties.setProperty(key, location);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

}
