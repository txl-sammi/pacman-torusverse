package src.moving;

import ch.aplu.jgamegrid.*;
import src.Game;
import src.PacActor;

public interface MovingStrategy {
    Location moveInAutoMode(PacActor pacman, Game game);
}
