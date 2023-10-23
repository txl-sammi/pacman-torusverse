package src.items;

import java.awt.*;

public enum ItemType {
    pills,
    ice,
    gold;


    public int getValue() {
        switch (this) {
            case pills: return 1;
            case ice: return 0;
            case gold: return 5;
            default: {
                assert false;
            }
        }
        return -1;
    }

    public String getImageName() {
        switch (this) {
            case pills: return null;
            case ice: return "sprites/ice.png";
            case gold: return "sprites/gold.png";
            default: {
                assert false;
            }
        }
        return null;
    }

    public Color getColour() {
        switch (this) {
            case pills: return Color.WHITE;
            case ice: return Color.BLUE;
            case gold: return Color.YELLOW;
            default: {
                assert false;
            }
        }
        return null;
    }
}