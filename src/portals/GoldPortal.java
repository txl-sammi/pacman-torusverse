package src.portals;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.portals.Portal;

import java.awt.*;

public class GoldPortal extends Portal {

    public GoldPortal(Location location) {

        super(Color.ORANGE, "pacman/sprites/portal_gold.png", location);
    }
    public void put(GGBackground bg, Game game) {
        bg.setPaintColor(getColour());
        bg.fillCircle(game.toPoint(getLocation()), 5);
        game.addActor(this, getLocation());
    }

}
