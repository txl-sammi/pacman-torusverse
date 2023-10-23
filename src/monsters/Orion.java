// Troll.java
// Used for PacMan
package src.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.items.Item;
import src.items.ItemType;

import java.util.ArrayList;
import java.util.Random;


public class Orion extends Monster {
    public Orion(Game game, MonsterType type)
    {
        super(game, type);
    }

    Location targetGold = null;
    ArrayList<Item> visitedGolds = new ArrayList<Item>();

    /**
     * Method that enacts the walking approach of Orion: walks to random gold pieces, even if they have been eaten
     * but favors locations with uneaten gold pieces
     */
    @Override
    protected void walkApproach()
    {
        Game game = getGame();
        double oldDirection = getDirection();
        ArrayList<Item> goldItems = game.getPillsAndItems().get(ItemType.gold);
        Location goldLocation;

        // sets the target gold to a random gold if uninitialised
        if (this.targetGold == null) {
            goldLocation = getRandomGold(visitedGolds, goldItems);
            targetGold = goldLocation;
        }

        // determine direction to gold and next location
        Location.CompassDirection compassDir =
                getLocation().get4CompassDirectionTo(targetGold);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        next = scaleNextLocation((next));

        // move to next location or random walk towards gold
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

        // if orion had reached target gold, set new target gold
        if (next.equals(targetGold)) {
            targetGold = getRandomGold(visitedGolds, goldItems);
        }

        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }

    /**
     * Method that calculates appropriate target gold to set for orion
     * @param visitedGolds list of visited golds
     * @param goldItems array list of all gold items on board (eaten and uneaten)
     * @return new random target gold
     */
    Location getRandomGold(ArrayList<Item> visitedGolds, ArrayList<Item> goldItems) {
        int goldCount = goldItems.size();
        Item gold;
        Location goldLocation;

        // loop until a suitable gold is found
        while (true) {

            // if number of visited golds equals total number of golds on board
            // reset visited gold list and return new random gold location
            if (visitedGolds.size() == goldCount) {
                visitedGolds.clear();
                gold = goldItems.get(new Random().nextInt(goldItems.size()));
                visitedGolds.add(gold);
                goldLocation = gold.getLocation();
                return goldLocation;
            }

            // create new random gold and check if suitable
            else {
                gold = goldItems.get(new Random().nextInt(goldItems.size()));

                // check if gold has not been visited, and if it has not been eaten
                if (!visitedGolds.contains(gold) && !gold.getEaten()) {
                    visitedGolds.add(gold);
                    goldLocation = gold.getLocation();
                    return goldLocation;
                }

                // check if gold has not been visited, and if it has been eaten
                else if (!visitedGolds.contains(gold) && gold.getEaten()) {

                    // check if there are any uneaten golds, if not then set as gold location
                    boolean eatens = false;
                    for (Item goldItem : goldItems) {
                        if (!goldItem.getEaten()) {
                            eatens = true;
                            break;
                        }
                    }
                    if (!eatens) {
                        visitedGolds.add(gold);
                        goldLocation = gold.getLocation();
                        return goldLocation;
                    }
                }
            }
        }
    }
}
