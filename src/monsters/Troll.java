// Troll.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.monsters.Monster;
import src.monsters.MonsterType;


public class Troll extends Monster {
    public Troll(Game game, MonsterType type)
    {
        super(game, type);
    }

    /**
     * Method that enacts the walking approach of Troll: Random walk
     */
    @Override
    protected void walkApproach()
    {
        Game game = getGame();
        Location pacLocation = game.pacActor.getLocation();
        double oldDirection = getDirection();
        this.setPrevious(this.getLocation());
        // get random direction and new location
        Location.CompassDirection compassDir =
                getLocation().get4CompassDirectionTo(pacLocation);
        setDirection(compassDir);
        int sign = getRandomiser().nextDouble() < 0.5 ? 1 : -1;
        setDirection(oldDirection);
        turn(sign * 90);  // Try to turn left/right
        Location next = getNextMoveLocation();
        next = scaleNextLocation((next));

        // if can move then move else randomly turn left or right, or backwards
        if (canMove(next))
        {
            setLocation(next);
        }
        else
        {
            setDirection(oldDirection);
            next = getNextMoveLocation();
            next = scaleNextLocation((next));
            if (canMove(next)){ // Try to move forward
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

        game.getGameCallback().monsterLocationChanged(this);
    }
}
