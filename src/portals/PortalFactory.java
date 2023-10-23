package src.portals;

import ch.aplu.jgamegrid.Location;

public class PortalFactory {

    private static PortalFactory instance;

    public Portal createPortal(String colour, Location location) {
        switch (colour) {

            case "white":
                return new WhitePortal(location);
            case "gold":
                return new GoldPortal(location);
            case "gray":
                return new GrayPortal(location);
            case "yellow":
                return new YellowPortal(location);

            default: {
                assert false;
            }
        return null;
        }
    }

    public static PortalFactory getInstance() {
        if (instance == null) {
            instance = new PortalFactory();
        }
        return instance;
    }

}
