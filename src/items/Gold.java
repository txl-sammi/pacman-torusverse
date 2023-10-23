package src.items;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;
import src.monsters.Monster;

import java.util.ArrayList;

public class Gold extends Item {


    public Gold(ItemType type, Location location) {
        super(type.getValue(), type, type.getColour(), location, type.getImageName());

    }

    /**
     * Method that enacts enraged state to all monsters
     * @param game current game
     */
    private void doEffect(Game game) {
        ArrayList<Monster> monsters = game.getMonsters();
        for (Monster monster: monsters) {
            monster.enrage(3);
        }
    }

    /**
     * Method that removes the item from game
     * @param game
     * @param pacActor pacman
     */
    @Override
    public void removeItem(Game game, PacActor pacActor) {
        pacActor.setNbPills(pacActor.getNbPills() + 1);
        setEaten(true);
        doEffect(game);
        this.hide();
    }

    public void put(GGBackground bg, Game game) {
        bg.setPaintColor(getColour());
        bg.fillCircle(game.toPoint(getLocation()), 5);
        game.addActor(this, getLocation());
    }


}
