// Wizard.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.monsters.Monster;
import src.monsters.MonsterType;

import java.util.Random;


public class Wizard extends Monster {
    public Wizard(Game game, MonsterType type)
    {
        super(game, type);
    }

    /**
     * Method that enacts the walking approach of Wizard: random walk - but can walk through 1-cell thick walls
     */
    @Override
    protected void walkApproach()
    {
        Game game = getGame();
        Location.CompassDirection directions[] = Location.CompassDirection.values();

        // loop until movable random direction is found
        while (true) {

            // randomly select 8-direction and set location
            int rnd = new Random().nextInt(directions.length);
            Location.CompassDirection compassDir = directions[rnd];
            setDirection(compassDir);
            Location next = getNextMoveLocation();
            next = scaleNextLocation((next));

            // move to new location if applicable
            if (canMove(next))
            {
                setLocation(next);
                break;
            }

            // check if the next adjacent cell is movable, which means that wizard is able to jump over wall cell
            else {
                Location adjNext = getLocation().getAdjacentLocation(getDirection(), 2);
                adjNext = enragedAdjacent(adjNext);
                if (canMove(adjNext)) {
                    setLocation(adjNext);
                    break;
                }
            }

        }

        game.getGameCallback().monsterLocationChanged(this);
    }

    /**
     * Method that enacts the enraged movement of Wizard when moving to adjacent cell
     * @param adjNext next location
     * @return new enraged location
     */
    Location enragedAdjacent(Location adjNext) {
        if (getSpeedScale() == 2) {
            Location newNext = getLocation().getAdjacentLocation(getDirection(), 3);
            if (canMove(newNext)){
                return newNext;
            }
        }
        return adjNext;
    }
}
