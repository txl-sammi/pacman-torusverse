// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import src.items.Item;
import src.items.ItemType;
import src.moving.BFSMoving;
import src.moving.MovingStrategy;
import src.moving.DirectMoving;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.*;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private List<String> propertyMoves = new ArrayList<>();
  private int propertyMoveIndex = 0;
  private final int listLength = 10;
  private int seed;
  private Location previous;
  private Random randomiser = new Random();
  public Random getRandom() {
    return this.randomiser;
  }

  private MovingStrategy movingStrategy;


  public PacActor(Game game)
  {
    super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
    this.game = game;
    movingStrategy = new BFSMoving();
  }
  private boolean isAuto = false;

  public void setAuto(boolean auto) {
    isAuto = auto;
  }


  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void setPropertyMoves(String propertyMoveString) {
    if (propertyMoveString != null) {
      this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }
  }

  public void keyRepeated(int keyCode)
  {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode)
    {
      case KeyEvent.VK_LEFT:
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
        break;
      case KeyEvent.VK_UP:
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
        break;
      case KeyEvent.VK_RIGHT:
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
        break;
      case KeyEvent.VK_DOWN:
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
        break;
    }
    if (next != null && canMove(next))
    {
      this.previous = getLocation();
      this.previous = getLocation();
      setLocation(next);
      eatPill(next);
    }
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
      Location next = movingStrategy.moveInAutoMode(this, game);
      setLocation(next);
      eatPill(next);
      addVisitedList(next);
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }



  private void followPropertyMoves() {
    String currentMove = propertyMoves.get(propertyMoveIndex);
    switch(currentMove) {
      case "R":
        turn(90);
        break;
      case "L":
        turn(-90);
        break;
      case "M":
        Location next = getNextMoveLocation();
        if (canMove(next)) {
          setLocation(next);
          eatPill(next);
        }
        break;
    }
    propertyMoveIndex++;
  }


  public void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  public boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }

  public boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if ( c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  public int getNbPills() {
    return nbPills;
  }
  public Location getPrevious() {
    return previous;
  }
  public void setPrevious(Location previous) {
    this.previous = previous;
  }
  public void setNbPills(int nbPills) {
    this.nbPills = nbPills;
  }

  public void eatPill(Location location)
  {
    HashMap<ItemType, ArrayList<Item>> pillsAndItems = game.getPillsAndItems();

    for (ItemType itemType: pillsAndItems.keySet()) {
      for (Item item: pillsAndItems.get(itemType)) {
        if ((item.getLocation().getX() == location.getX()) && (item.getLocation().getY() == location.getY()) && !item.getEaten()) {
          score += item.getValue();
          getBackground().fillCell(location, Color.lightGray);
          game.getGameCallback().pacManEatPillsAndItems(location, item.getType().name());
          item.removeItem(game, this);
          break;
        }
      }
    }

    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }

  public void teleportPacActor(Location location) {
    this.setLocation(location);
  }


}
