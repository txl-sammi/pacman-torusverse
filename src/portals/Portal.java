package src.portals;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;
import src.monsters.Monster;

import java.awt.*;

public abstract class Portal extends Actor {

    private Color colour;
    private Location location;

    private Portal pairPortal;

    public boolean used;
    public Portal(Color colour, String sprite, Location location) {
        super(sprite);
        this.colour = colour;
        this.location = location;
        this.used = false;

    }

    public Color getColour() {
        return this.colour;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPairPortal(Portal portal) {
        this.pairPortal = portal;
    }

    public Portal getPairPortal() {
        return this.pairPortal;
    }

    public abstract void put(GGBackground bg, Game game);
}
