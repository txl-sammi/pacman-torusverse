package src.portals;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.portals.Portal;

import java.awt.*;

public class GrayPortal extends Portal {

    public GrayPortal(Location location) {
        super(Color.DARK_GRAY, "pacman/sprites/portal_gray.png", location);

    }

    public void put(GGBackground bg, Game game) {
        bg.setPaintColor(getColour());
        bg.fillCircle(game.toPoint(getLocation()), 5);
        game.addActor(this, getLocation());
    }

}
