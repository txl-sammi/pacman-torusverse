package src.items;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.PacActor;

import java.awt.*;

public abstract class Item extends Actor {

    private final int value;
    private final ItemType type;
    private boolean eaten;
    private Color colour;
    private Location location;

    public Item(int value, ItemType type, Color colour, Location location) {
        this.value = value;
        this.type = type;
        eaten = false;
        this.colour = colour;
        this.location = location;
    }

    public Item(int value, ItemType type, Color colour, Location location, String sprite) {
        super(sprite);
        this.value = value;
        this.type = type;
        eaten = false;
        this.colour = colour;
        this.location = location;
    }


    public boolean getEaten() {
        return this.eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    public int getValue() {
        return this.value;
    }

    public ItemType getType() {
        return this.type;
    }

    public abstract void removeItem(Game game, PacActor pacActor);

    public Color getColour() {
        return this.colour;
    }

    public Location getLocation() {
        return this.location;
    }

    public abstract void put(GGBackground bg, Game game);
}
