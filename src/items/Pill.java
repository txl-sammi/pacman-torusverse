package src.items;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;
import src.items.Item;
import src.items.ItemType;

public class Pill extends Item {

    public Pill(ItemType type, Location location) {
        super(type.getValue(), type, type.getColour(), location);
    }

    public void put(GGBackground bg, Game game) {
        bg.setPaintColor(getColour());
        bg.fillCircle(game.toPoint(super.getLocation()), 5);
    }

    @Override
    public void removeItem(Game game, PacActor pacActor) {
        pacActor.setNbPills(pacActor.getNbPills() + 1);
        setEaten(true);
    }
}
