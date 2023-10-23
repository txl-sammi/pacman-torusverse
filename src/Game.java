// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.items.*;
import src.monsters.Monster;
import src.monsters.MonsterType;
import src.monsters.TX5;
import src.monsters.Troll;
import src.portals.Portal;
import src.portals.PortalFactory;
import src.utility.GameCallback;

import java.awt.*;
import java.util.*;


public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  public PacActor pacActor = new PacActor(this);
  private ArrayList<Monster> monsters = new ArrayList<>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private HashMap<ItemType, ArrayList<Item>> pillsAndItems = new HashMap<>();
  private HashMap<String, ArrayList<Portal>> portals = new HashMap<>();
  private int maxPillsAndItems;
  private GGBackground bg;
  private boolean pacDead;

  public Game(GameCallback gameCallback, Properties properties)
  {
    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    this.pacDead = false;
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");

    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    loadPillAndItemsLocations();
    loadPortalLocations();

    bg = getBg();
    this.maxPillsAndItems = setupAndCountPillsAndItems();
    drawGrid(bg);

  }

  public GameCallback getGameCallback() {
    return gameCallback;
  }

  /**
   * Method that sets the actors locations in the simple version
   */
  public void setupActorLocations() {
    String trollLocationString = properties.getProperty("TrollTile.location");
    if (trollLocationString != null) {
      String[] singleTrollLocationStrings = trollLocationString.split(";");
      for (String singleTrollLocationString: singleTrollLocationStrings) {
        String[] locationStrings = singleTrollLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        Monster troll = new Troll(this, MonsterType.Troll);
        addActor(troll, location, Location.NORTH);
        this.monsters.add(troll);
      }
    }

    String TX5LocationString = properties.getProperty("TX5Tile.location");
    if (TX5LocationString != null) {
      String[] singleTX5LocationStrings = TX5LocationString.split(";");
      for (String singleTX5LocationString: singleTX5LocationStrings) {
        String[] locationStrings = singleTX5LocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        Monster tx5 = new TX5(this, MonsterType.TX5);
        addActor(tx5, location, Location.NORTH);
        this.monsters.add(tx5);
      }
    }
    String[] pacManLocations = this.properties.getProperty("PacTile.location").split(",");
    int pacManX = Integer.parseInt(pacManLocations[0]);
    int pacManY = Integer.parseInt(pacManLocations[1]);
    addActor(pacActor, new Location(pacManX, pacManY));
  }

  public HashMap<ItemType, ArrayList<Item>> getPillsAndItems() {
    return pillsAndItems;
  }

  private void loadPillAndItemsLocations() {
    String pillsLocationString = properties.getProperty("PillTile.location");
    if (pillsLocationString != null) {
      String[] singlePillLocationStrings = pillsLocationString.split(";");
      for (String singlePillLocationString: singlePillLocationStrings) {
        String[] locationStrings = singlePillLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPillAndItemList(ItemType.pills, location);
      }
    }

    String goldLocationString = properties.getProperty("GoldTile.location");
    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPillAndItemList(ItemType.gold, location);
      }
    }

    String iceLocationString = properties.getProperty("IceTile.location");
    if (iceLocationString != null) {
      String[] singleIceLocationStrings = iceLocationString.split(";");
      for (String singleIceLocationString: singleIceLocationStrings) {
        String[] locationStrings = singleIceLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPillAndItemList(ItemType.ice, location);
      }
    }
  }

  private int setupAndCountPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (ItemType itemType: pillsAndItems.keySet()) {
      if (!itemType.equals(ItemType.ice)) {
        pillsAndItemsCount += pillsAndItems.get(itemType).size();
      }
    }

    return pillsAndItemsCount;
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.lightGray);

    String wallLocationString = properties.getProperty("WallTile.location");
    if (wallLocationString != null) {
      String[] singleGoldLocationStrings = wallLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        bg.fillCell(location, Color.gray);
      }
    }
    for (ItemType itemType : pillsAndItems.keySet()) {
      for (Item item: pillsAndItems.get(itemType)) {
        bg.setPaintColor(Color.white);
        item.put(bg, this);
      }
    }
    for (String colour : portals.keySet()) {
      for (Portal portal: portals.get(colour)) {
        bg.setPaintColor(Color.white);
        portal.put(bg, this);
      }
    }

  }

  private void loadPortalLocations() {
    String whiteLocationString = properties.getProperty("PortalWhiteTile.location");
    if (whiteLocationString != null) {
      String[] singleWhiteLocationStrings = whiteLocationString.split(";");
      for (String singleWhiteLocationString: singleWhiteLocationStrings) {
        String[] locationStrings = singleWhiteLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPortalList("white", location);
      }
    }

    String goldLocationString = properties.getProperty("PortalDarkGoldTile.location");
    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPortalList("gold", location);
      }
    }


    String grayLocationString = properties.getProperty("PortalDarkGrayTile.location");
    if (grayLocationString != null) {
      String[] singleGrayLocationStrings = grayLocationString.split(";");
      for (String singleGrayLocationString: singleGrayLocationStrings) {
        String[] locationStrings = singleGrayLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPortalList("gray", location);
      }
    }

    String yellowLocationString = properties.getProperty("PortalYellowTile.location");
    if (yellowLocationString != null) {
      String[] singleYellowLocationStrings = yellowLocationString.split(";");
      for (String singleYellowLocationString: singleYellowLocationStrings) {
        String[] locationStrings = singleYellowLocationString.split(",");
        Location location = new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1]));
        addToPortalList("yellow", location);
      }
    }


  }

  private void addToPortalList(String color, Location location) {
    PortalFactory factory = PortalFactory.getInstance();
    if (!portals.containsKey(color)) {
      portals.put(color, new ArrayList<Portal>());
      portals.get(color).add(factory.createPortal(color, location));
    }
    else {
      portals.get(color).add(factory.createPortal(color, location));
      Portal pair = portals.get(color).get(0);
      Portal instance = portals.get(color).get(1);
      pair.setPairPortal(instance);
      instance.setPairPortal(pair);
    }
  }


  public ArrayList<Monster> getMonsters() {
    return this.monsters;
  }
  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }

  private Item createPillAndItem(ItemType itemType, Location location) {
    switch (itemType) {
      case pills: return new Pill(itemType, location);
      case gold: return new Gold(itemType, location);
      case ice: return new Ice(itemType, location);
      default: {
        assert false;
      }
    }
    return null;
  }

  // add the item to the hashmap
  private void addToPillAndItemList(ItemType itemType, Location location) {
    if (!pillsAndItems.containsKey(itemType)) {
      pillsAndItems.put(itemType, new ArrayList<Item>());
    }
    pillsAndItems.get(itemType).add(createPillAndItem(itemType, location));
  }

  public void setupSeed() {
    seed = Integer.parseInt(properties.getProperty("seed"));
    pacActor.setSeed(seed);
    for (Monster monster: this.monsters) {
      monster.setSeed(seed);
    }

    addKeyRepeatListener(pacActor);
    setKeyRepeatPeriod(150);
    pacActor.setSlowDown(3);

    for (Monster monster: this.monsters) {
      monster.setSlowDown(3);
      if (monster.getType() == MonsterType.TX5) {
        monster.stopMoving(5);
      }
    }
  }

  public void runGame() {
    doRun();
    show();
    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    boolean hasPacmanBeenHit;
    boolean hasPacmanEatAllPills;

    do {
      hasPacmanBeenHit = checkCollision();
      checkTeleport();
      hasPacmanEatAllPills = pacActor.getNbPills() >= this.maxPillsAndItems;
      delay(10);
    } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
    delay(120);

    Location loc = pacActor.getLocation();
    for (Monster monster: this.monsters) {
      monster.setStopMoving(true);
    }
    pacActor.removeSelf();

    String title = "";
    if (hasPacmanBeenHit) {
      pacDead = true;
      bg.setPaintColor(Color.red);
      title = "GAME OVER";
      addActor(new Actor("sprites/explosion3.gif"), loc);
    } else if (hasPacmanEatAllPills) {
      bg.setPaintColor(Color.yellow);
      title = "YOU WIN";
    }
    setTitle(title);
    gameCallback.endOfGame(title);

    doPause();

    Timer timer = new Timer();
    int SECONDS_TO_MILLISECONDS = 3000;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        hide();
      }
    }, SECONDS_TO_MILLISECONDS);
  }

  // check collision between pacman and monsters
  private boolean checkCollision() {
    for (Monster monster: this.monsters) {
      if (monster.getLocation().equals(this.pacActor.getLocation())) {
        return true;
      }
    }
    return false;
  }

  private void checkTeleport() {
    for (String colour : portals.keySet()) {
      Portal first = portals.get(colour).get(0);
      Portal second = portals.get(colour).get(1);

      if (first.getLocation().equals(this.pacActor.getLocation())) {
        Location pairLocation = second.getLocation();
        Location previous = this.pacActor.getPrevious();
        if (previous != first.getLocation()){
          this.pacActor.teleportPacActor(pairLocation);
          this.pacActor.setPrevious(pairLocation);
        }
      }
      else if (second.getLocation().equals(this.pacActor.getLocation())) {
        Location pairLocation = first.getLocation();
        Location previous = this.pacActor.getPrevious();
        if (previous != second.getLocation()){
          this.pacActor.teleportPacActor(pairLocation);
          this.pacActor.setPrevious(pairLocation);
        }
      }

      for (Monster monster: monsters) {
        if (first.getLocation().equals(monster.getLocation())) {
          Location pairLocation = second.getLocation();
          Location previous = monster.getPrevious();
          if (previous != first.getLocation()){
            monster.teleportMonster(pairLocation);
            monster.setPrevious(pairLocation);
          }
        }
        else if (second.getLocation().equals(monster.getLocation())) {
          Location pairLocation = first.getLocation();
          Location previous = monster.getPrevious();
          if (previous != second.getLocation()){
            monster.teleportMonster(pairLocation);
            monster.setPrevious(pairLocation);
          }
        }
      }

    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public boolean getPacStatus() {
    return this.pacDead;
  }

  public HashMap<String, ArrayList<Portal>> getPortals() {
    return this.portals;
  }
}
