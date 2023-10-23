// Monster.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.*;
import src.Game;

import java.awt.Color;
import java.util.*;

public abstract class Monster extends Actor
{
  private Game game;
  private final MonsterType type;
  private ArrayList<Location> visitedList = new ArrayList<Location>();
  private final int listLength = 20;
  private boolean stopMoving = false;
  private int seed = 0;
  private int speedScale = 1;
  private Location previous;
  private Random randomiser = new Random(0);

  public Monster(Game game, MonsterType type)
  {
    super("sprites/" + type.getImageName());
    this.game = game;
    this.type = type;
  }

  /**
   * Method that stop the movement of monster
   * @param seconds
   */
  public void stopMoving(int seconds) {
    this.stopMoving = true;
    Timer timer = new Timer(); // Instantiate Timer Object
    int SECOND_TO_MILLISECONDS = 1000;
    final Monster monster = this;
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        monster.stopMoving = false;
      }
    }, seconds * SECOND_TO_MILLISECONDS);
  }

  /**
   * Method that enacts enraged stste of monster
   * @param seconds
   */
  public void enrage(int seconds) {
    if (!this.stopMoving) {
      setSpeedScale(2);
      Timer time = new Timer();
      int SECOND_TO_MILLISECONDS = 1000;
      time.schedule(new TimerTask() {
        @Override
        public void run() {
          setSpeedScale(1);
        }
      }, seconds * SECOND_TO_MILLISECONDS);
    }
  }

  public MonsterType getType() {
    return type;
  }

  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void setStopMoving(boolean stopMoving) {
    this.stopMoving = stopMoving;
  }

  public void act()
  {
    if (stopMoving) {
      return;
    }
    walkApproach();
    if (getDirection() > 150 && getDirection() < 210)
      setHorzMirror(false);
    else
      setHorzMirror(true);
  }

  void addVisitedList(Location location)
  {
    visitedList.add(location);
    if (visitedList.size() == listLength)
      visitedList.remove(0);
  }

  boolean isVisited(Location location)
  {
    for (Location loc : visitedList)
      if (loc.equals(location))
        return true;
    return false;
  }

  /**
   * Method that checks if monster is able to move to location
   * @param location desired location
   * @return true or false
   */
  boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    if (c.equals(Color.gray) || location.getX() >= game.getNumHorzCells()
            || location.getX() < 0 || location.getY() >= game.getNumVertCells() || location.getY() < 0)
      return false;
    else
      return true;
  }

  /**
   * Method that scales the desired location to enact the enraged state of monster
   * @param location
   * @return new location
   */
  public Location scaleNextLocation(Location location) {
    if (canMove(location)) {
      int directionX = (location.getX() - this.getLocation().getX()) * getSpeedScale();
      int directionY = (location.getY() - this.getLocation().getY()) * getSpeedScale();

      return new Location(this.getLocation().getX() + directionX, this.getLocation().getY() + directionY);
    }
    return location;
  }


  public int getSpeedScale() {
    return this.speedScale;
  }

  private void setSpeedScale(int speedScale) {
    this.speedScale = speedScale;
  }

  Game getGame() { return this.game; }

  Random getRandomiser() { return this.randomiser; }

  protected abstract void walkApproach();

  public void teleportMonster(Location location) {
    this.setLocation(location);
  }
  public Location getPrevious() {
    return previous;
  }
  public void setPrevious(Location previous) {
    this.previous = previous;
  }
}

