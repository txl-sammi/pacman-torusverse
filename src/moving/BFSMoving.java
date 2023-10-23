package src.moving;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;
import src.portals.Portal;

import java.awt.*;
import java.util.*;
import java.util.List;

class Node {
    int teleported;
    int x, y;
    Node parent;

    public Node(int x, int y, Node parent, int teleported) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.teleported = teleported;
    }
}
public class BFSMoving implements MovingStrategy{
    private String map[][];
    private int height;
    private int width;
    private Game game;
    private HashMap<String, ArrayList<Portal>> portals;
    private List<Location> eatenPills = new ArrayList<>();

    @Override
    public Location moveInAutoMode(PacActor pacman, Game game) {
        Properties properties = game.getProperties();
        this.height = Integer.parseInt(properties.getProperty("height"));
        this.width = Integer.parseInt(properties.getProperty("width"));
        this.map = new String[height][width];
        this.game = game;
        generateMap(properties);
        this.portals = game.getPortals();

        pacman.setPrevious(pacman.getLocation());
        List<Node> path = BFSSearch(pacman);
        Location newLocation = new Location(path.get(1).x, path.get(1).y);
        Location.CompassDirection compassDir =
                pacman.getLocation().get4CompassDirectionTo(newLocation);
        pacman.setDirection(compassDir);
        return newLocation;
    }

    private void generateMap(Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (value != null) {
                String[] singleLocationStrings = value.split(";");
                for (String singleLocation : singleLocationStrings) {
                    if (singleLocation != null) {
                        String[] locationStrings = singleLocation.split(",");
                        if (locationStrings.length == 1){
                            break;
                        }
                        int x = Integer.parseInt(locationStrings[1]);
                        int y = Integer.parseInt(locationStrings[0]);
                        this.map[x][y] = key;

                    }
                }
            }
        }
    }

    private List<Node> BFSSearch(PacActor pacman) {
        boolean[][] visited = new boolean[height][width];
        Location currentLocation = pacman.getLocation();
        Queue<Node> queue = new LinkedList<>();

        List<String> portalList = Arrays.asList("PortalDarkGoldTile.location", "PortalDarkGrayTile.location"
                , "PortalWhiteTile.location", "PortalYellowTile.location");

        queue.add(new Node(currentLocation.x, currentLocation.y, null, 0));
        visited[currentLocation.y][currentLocation.x] = true;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int[] dx = { -1, 1, 0, 0 };
            int[] dy = { 0, 0, -1, 1 };

            if (Objects.equals(map[current.y][current.x], "PillTile.location") ||
                    Objects.equals(map[current.y][current.x],"GoldTile.location")) {
                Location pill = new Location(current.x, current.y);
                if (Objects.equals(pacman.getLocation(), pill)){
                    eatenPills.add(pill);
                }
                if (!eatenPills.contains(pill)) {

                    List<Node> path = new ArrayList<>();
                    while (current != null) {
                        path.add(0, current);
                        current = current.parent;
                    }
                    return path;
                }
            }

            // next move should be to teleport
            if (stringEqualsAny(map[current.y][current.x], portalList) &&
                    current.parent != null && current.teleported == 0) {

                Location portalLocation = new Location(current.x, current.y);
                if (map[current.y][current.x].equals("PortalDarkGoldTile.location")){
                    List<Portal> portalPairs = portals.get("gold");
                    Location pairLocation;
                    if (portalPairs.get(0).getLocation().equals(portalLocation)){
                        pairLocation = portalPairs.get(1).getLocation();
                    }
                    else {
                        pairLocation = portalPairs.get(0).getLocation();
                    }

                    int newX = pairLocation.x;
                    int newY = pairLocation.y;
                    visited[newY][newX] = true;
                    Node neighbour = new Node(newX, newY, current, 1);
                    queue.add(neighbour);
                }
                if (map[current.y][current.x].equals("PortalDarkGrayTile.location")){
                    List<Portal> portalPairs = portals.get("gray");
                    Location pairLocation;
                    if (portalPairs.get(0).getLocation().equals(portalLocation)){
                        pairLocation = portalPairs.get(1).getLocation();
                    }
                    else {
                        pairLocation = portalPairs.get(0).getLocation();
                    }

                    int newX = pairLocation.x;
                    int newY = pairLocation.y;
                    visited[newY][newX] = true;
                    Node neighbour = new Node(newX, newY, current, 1);
                    queue.add(neighbour);
                }
                if (map[current.y][current.x].equals("PortalWhiteTile.location")){
                    List<Portal> portalPairs = portals.get("white");
                    Location pairLocation;
                    if (portalPairs.get(0).getLocation().equals(portalLocation)){
                        pairLocation = portalPairs.get(1).getLocation();
                    }
                    else {
                        pairLocation = portalPairs.get(0).getLocation();
                    }

                    int newX = pairLocation.x;
                    int newY = pairLocation.y;
                    visited[newY][newX] = true;
                    Node neighbour = new Node(newX, newY, current, 1);
                    queue.add(neighbour);
                }
                if (map[current.y][current.x].equals("PortalYellowTile.location")){
                    List<Portal> portalPairs = portals.get("yellow");
                    Location pairLocation;
                    if (portalPairs.get(0).getLocation().equals(portalLocation)){
                        pairLocation = portalPairs.get(1).getLocation();
                    }
                    else {
                        pairLocation = portalPairs.get(0).getLocation();
                    }

                    int newX = pairLocation.x;
                    int newY = pairLocation.y;
                    visited[newY][newX] = true;
                    Node neighbour = new Node(newX, newY, current, 1);
                    queue.add(neighbour);
                }
                continue;
            }

            // look at neighbours
            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                if (isValid(pacman, newX, newY) && !visited[newY][newX]) {

                    visited[newY][newX] = true;
                    Node neighbour = new Node(newX, newY, current, 0);
                    queue.add(neighbour);
                }
            }
        }
        return null;
    }

    private boolean stringEqualsAny(String input, List<String> stringList) {
        for (String str : stringList) {
            if (str.equals(input)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValid(PacActor pacman, int x, int y) {
        Location location = new Location(x, y);
        Color c = pacman.getBackground().getColor(location);
        if ( c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
                || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0) {
            return false;
        }
        return true;
    }


}
