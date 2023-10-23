// Troll.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.monsters.Monster;
import src.monsters.MonsterType;

import java.util.Random;


public class Alien extends Monster {
    public Alien(Game game, MonsterType type)
    {
        super(game, type);
    }

    /**
     * Method that enacts the walking approach of alien: shortest distance finder to pacman
     */
    @Override
    protected void walkApproach()
    {
        Game game = getGame();
        Location pacLocation = game.pacActor.getLocation();

        // initialise direction and shortest distance
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        int shortest_dist = 100;

        // iterate through 8-direction compass direction values
        for (Location.CompassDirection c : Location.CompassDirection.values()) {
            Location next = getLocation().getNeighbourLocation(c);
            next = scaleNextLocation((next));

            // if alien can move to the direction
            if (canMove(next)) {

                // calculate distance from pacman
                int dist = next.getDistanceTo(pacLocation);

                // if distance equals recorded shortest distance, randomly select one of the two
                if (dist == shortest_dist) {
                    boolean randomOfTwo = new Random().nextBoolean();
                    if (!randomOfTwo) {
                        compassDir = c;
                    }
                    else {
                        continue;
                    }
                }

                // if distance is shorter than the recorded shortest distance
                // assign shortest distance as new distance and set that compass direction
                if (dist < shortest_dist) {
                    shortest_dist = dist;
                    compassDir = c;
                }
            }
        }

        // set the direction and location of next movement
        Location next = getLocation().getNeighbourLocation(compassDir);
        next = scaleNextLocation((next));
        if (canMove(next)) {
            next = scaleNextLocation((next));
            setDirection(compassDir);

            setLocation(next);
        }
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}
