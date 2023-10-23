package src.checking;

import ch.aplu.jgamegrid.Location;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LevelCheckingStrategy implements ICheckingStrategy{


    private String[][] currentLevelMap;
    private String currentLevelName;
    private Properties currentProperties;


    private List<File> xmlFiles;

    public LevelCheckingStrategy(List<File> xmlFiles) {
        this.xmlFiles = xmlFiles;
    }



    public boolean check() {

        for (File level: xmlFiles) {
            setLevelName(level.getName());
            setProperties(loadFromXML(level));
            boolean validLevel = checkLevel();
            if (!validLevel) {
                return false;
            }
        }
        return true;
    }

     public boolean checkLevel() {
         boolean valid = true;
         try {
             int pillAndGoldLocations = 0;
             FileWriter fileWriter = new FileWriter("log.txt");
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

             // check pacman cases
             if (!hasPacMan(bufferedWriter)) {
                 valid = false;
             }
             hasAllPortals(bufferedWriter);

             for (String key: this.currentProperties.stringPropertyNames()) {
                 String value = this.currentProperties.getProperty(key);

                 // check if there are more than 1 pacman locations
                 if (key.contains("PacTile")) {
                     if (value.split(";").length > 1) {
                         bufferedWriter.write("level " + this.currentLevelName +
                                 " - more than one start for Pacman: " + stringifyLocations(value));
                         bufferedWriter.close();
                         valid = false;
                     }
                 }

                 // check if there are 2 locations of each portal
                 if (key.contains("Portal")) {
                     String colourTile = key.replace("Portal", "");
                     String colour = colourTile.replace("Tile", "");
                     if (value.split(";").length != 2) {
                         bufferedWriter.write("level " + this.currentLevelName +
                                 " portal " + colour.replace(".location", "") + " count is not 2: " + stringifyLocations(value));
                         bufferedWriter.newLine();
                     }
                 }

                 // add the amount of gold and pills to get the total and check if there are at least 2 in total
                 if (key.contains("Gold") || key.contains("Pill")) {
                     pillAndGoldLocations += key.split(";").length;
                 }

             }

             if (pillAndGoldLocations < 2) {
                 bufferedWriter.write("level " + this.currentLevelName + " - less than 2 Gold and Pill");
                 bufferedWriter.newLine();
                 valid = false;
             }

             if (pillAndGoldLocations > 0 && !checkAccessibility(bufferedWriter)) {
                 valid = false;
             }

             bufferedWriter.close();

         } catch (IOException e) {
             System.out.println(e.getMessage());
         }

         return valid;
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
                int heightNum = Integer.parseInt(height);
                int widthNum = Integer.parseInt(width);
                properties.setProperty("height", height);
                properties.setProperty("width", width);

                this.currentLevelMap = new String[widthNum][heightNum];


                List<Element> rowElements = rootElement.getChildren("row");
                for (int rowIndex = 0; rowIndex < rowElements.size(); rowIndex++) {
                    Element row = rowElements.get(rowIndex);
                    List<Element> cellElements = row.getChildren("cell");
                    for (int cellIndex = 0; cellIndex < cellElements.size(); cellIndex++) {
                        Element cell = cellElements.get(cellIndex);
                        String cellValue = cell.getText();

                        this.currentLevelMap[cellIndex][rowIndex] = cellValue;
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

    private boolean hasPacMan(BufferedWriter bufferedWriter) {
        try {
            if (!this.currentProperties.containsKey("PacTile.location")) {
                bufferedWriter.write("level " + this.currentLevelName + " - no start for PacMan");
                bufferedWriter.close();
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private void hasAllPortals(BufferedWriter bufferedWriter) {
        try {
            if (!this.currentProperties.containsKey("PortalDarkGrayTile.location")) {
                bufferedWriter.write("level " + this.currentLevelName + " - portal DarkGray count is not 2: ");
                bufferedWriter.newLine();

            }
            if (!this.currentProperties.containsKey("PortalWhiteTile.location")) {
                bufferedWriter.write("level " + this.currentLevelName + " - portal White count is not 2: ");
                bufferedWriter.newLine();

            }
            if (!this.currentProperties.containsKey("PortalDarkGoldTile.location")) {
                bufferedWriter.write("level " + this.currentLevelName + " - portal DarkGold count is not 2: ");
                bufferedWriter.newLine();

            }
            if (!this.currentProperties.containsKey("PortalYellowTile.location")) {
                bufferedWriter.write("level " + this.currentLevelName + " - portal Yellow count is not 2: ");
                bufferedWriter.newLine();

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean canVisit(int i, int j, boolean[][] visited) {
        if (i < 0 || j < 0 || i > this.currentLevelMap.length - 1 || j > this.currentLevelMap[0].length - 1) {
            return false;
        }
        if (this.currentLevelMap[i][j].contains("WallTile") || visited[i][j]) {

            return false;
        }
        return true;

    }

    private boolean equalLocations(Location a, Location b) {
        if ((a.getY() == b.getY()) && (a.getX() == b.getX())) {
            return true;
        }
        return false;
    }

    private boolean checkInaccessible(BufferedWriter bufferedWriter, boolean[][] visited) {
        String inaccessibleGold = "";
        String inaccessiblePill = "";
        for (int i = 0; i < this.currentLevelMap.length; i ++) {
            for (int j = 0; j < this.currentLevelMap[0].length;j++) {
                if (this.currentLevelMap[i][j].contains("Gold") && !visited[i][j]) {
                    inaccessibleGold += "(" + (i + 1) + "," + (j + 1) + ");";
                }
                if (this.currentLevelMap[i][j].contains("Pill") && !visited[i][j]) {
                    inaccessiblePill += "(" + (i + 1) + "," + (j + 1) + ");";
                }

            }
        }

        try {
            if (!inaccessiblePill.isEmpty()) {
                String result = inaccessiblePill.substring(0, inaccessiblePill.length() - 1);
                bufferedWriter.write("Level " + this.currentLevelName + " - Pill not accessible: " + result);
                bufferedWriter.newLine();
            }
            if (!inaccessibleGold.isEmpty()) {
                String result = inaccessibleGold.substring(0, inaccessibleGold.length() - 1);
                bufferedWriter.write("Level " + this.currentLevelName + " - Gold not accessible: " + result);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return !inaccessibleGold.isEmpty() || !inaccessiblePill.isEmpty();
    }

    private boolean checkPillOrGold(Location pacManLocation, HashMap<String, ArrayList<Location>> portalLocations,
                                    BufferedWriter bufferedWriter) throws IOException {

        int rowDirection[] = {-1,0,1,0};
        int colDirection[] = {0,1,0,-1};
        int currX = pacManLocation.getX();
        int currY = pacManLocation.getY();
        boolean[][] visited = new boolean[this.currentLevelMap.length][this.currentLevelMap[0].length];
        for (boolean[] row: visited) {
            Arrays.fill(row, false);
        }

        Queue<Location> q = new LinkedList<>();

        q.add(new Location(currX, currY));
        visited[currX][currY] = true;

        while (!q.isEmpty()) {

            Location currLocation = q.peek();


            // check if the current location is a portal and store the pair location in the queue
            String tileType = this.currentLevelMap[currLocation.getX()][currLocation.getY()] + ".location";

            if (tileType.contains("Portal")) {
                if (!visited[currLocation.getX()][currLocation.getY()]) {
                    ArrayList<Location> pairLocations = portalLocations.get(tileType);
                    Location teleportLocation;

                    if (equalLocations(currLocation, pairLocations.get(0))) {
                        teleportLocation = pairLocations.get(1);
                    }
                    else {
                        teleportLocation = pairLocations.get(0);
                    }
                    if (!visited[teleportLocation.getX()][teleportLocation.getY()]) {

                        q.add(teleportLocation);
                    }
                }
            }

            visited[currLocation.getX()][currLocation.getY()] = true;
            q.remove();

            // add adjacent locations to the queue if they can be visited
            for (int i = 0; i < rowDirection.length; i++) {
                int adjX = currLocation.getX() + rowDirection[i];
                int adjY = currLocation.getY() + colDirection[i];

                if (canVisit(adjX, adjY, visited)) {
                    q.add(new Location(adjX, adjY));
                }
            }
        }
        if (checkInaccessible(bufferedWriter, visited)) {
            return false;
        }
        return true;
    }

    private boolean checkAccessibility(BufferedWriter bufferedWriter) throws IOException {
        String[] pacManLocationString = this.currentProperties.getProperty("PacTile.location").split(",");
        Location pacManLocation = new Location(Integer.parseInt(pacManLocationString[0]), Integer.parseInt(pacManLocationString[1]));

        HashMap<String, ArrayList<Location>> portalLocations = new HashMap<>();
        ArrayList<Location> pillLocations = new ArrayList<>();
        ArrayList<Location> goldLocations = new ArrayList<>();

        for (String key: this.currentProperties.stringPropertyNames()) {
            String[] locations = this.currentProperties.getProperty(key).split(";");
            for (String location: locations) {

                String[] coordinates = location.split(",");

                if (key.contains("Pill")) {
                    pillLocations.add(new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                }

                else if (key.contains("Portal")) {
                    if (!portalLocations.containsKey(key)) {
                        portalLocations.put(key, new ArrayList<>());
                    }
                    portalLocations.get(key).add(new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                }

                else if (key.contains("Gold")) {
                    goldLocations.add(new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                }


            }
        }
        if (!checkPillOrGold(pacManLocation, portalLocations, bufferedWriter)) {
            return false;
        }
        return true;

    }

    private String stringifyLocations(String locations) {
        String result = "";
        String[] locationList = locations.split(";");
        for (String location : locationList) {
            String[] coordinates = location.split(",");
            int coordX = Integer.parseInt(coordinates[0]) + 1;
            int coordY = Integer.parseInt(coordinates[1]) + 1;
            String nonZeroLocation = "(" + coordX + "," + coordY + ")";
            if (location.equals(locationList[locationList.length - 1])) {
                result = result + nonZeroLocation;
            } else {
                result = result + nonZeroLocation + "; ";
            }
        }
        return result;
    }

    public void setLevelName(String name) {
        this.currentLevelName = name;
    }

    public void setProperties(Properties properties) {
        this.currentProperties = properties;
    }

    public Properties getCurrentProperties() {
        return this.currentProperties;
    }

}
