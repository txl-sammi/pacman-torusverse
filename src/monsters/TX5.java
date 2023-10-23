// Troll.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.monsters.Monster;
import src.monsters.MonsterType;


public class TX5 extends Monster {
    public TX5(Game game, MonsterType type)
    {
        super(game, type);
    }

    /**
     * Method that enacts the walking approach of TX5: Determine direction to pacActor and try to move in
     * that direction. Otherwise, random walk.
     */
    @Override
    protected void walkApproach()
    {
        Game game = getGame();
        Location pacLocation = game.pacActor.getLocation();
        double oldDirection = getDirection();
        this.setPrevious(this.getLocation());

        // determine direction to pacman
        Location.CompassDirection compassDir =
          getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);

        // if can move then move else randomly turn left or right, or backwards
        if (!isVisited(next) && canMove(next))
        {
          setLocation(next);
        }

        else
        {
          // Random walk
          int sign = getRandomiser().nextDouble() < 0.5 ? 1 : -1;
          setDirection(oldDirection);
          turn(sign * 90);  // Try to turn left/right
          next = getNextMoveLocation();
          next = scaleNextLocation((next));
          if (canMove(next))
          {
            setLocation(next);
          }
          else
          {
            setDirection(oldDirection);
            next = getNextMoveLocation();
            next = scaleNextLocation((next));
            if (canMove(next)) // Try to move forward
            {
              setLocation(next);
            }
            else
            {
              setDirection(oldDirection);
              turn(-sign * 90);  // Try to turn right/left
              next = getNextMoveLocation();
              next = scaleNextLocation((next));
              if (canMove(next))
              {
                setLocation(next);
              }
              else
              {
                setDirection(oldDirection);
                turn(180);  // Turn backward
                next = getNextMoveLocation();
                next = scaleNextLocation((next));
                setLocation(next);
              }
            }
          }
        }

        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}
