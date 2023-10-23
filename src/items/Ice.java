package src.items;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;
import src.monsters.Monster;

import java.util.ArrayList;

public class Ice extends Item {

    public Ice(ItemType type, Location location) {
        super(type.getValue(), type, type.getColour(), location, type.getImageName());
    }

    /**
     * Method that freezes all monsters
     * @param game
     */
    private void doEffect(Game game) {
        ArrayList<Monster> monsters = game.getMonsters();
        for (Monster monster: monsters) {
            monster.stopMoving(3);
        }
    }
    @Override
    public void removeItem(Game game, PacActor pacActor) {
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
