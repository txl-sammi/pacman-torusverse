package src;

import ch.aplu.jgamegrid.Location;
import src.monsters.*;
import src.utility.GameCallback;

import java.util.Properties;

public class GameExtended extends Game{

    private Monster orion = new Orion(this, MonsterType.Orion); // probably for the orion make a stream to filter the list for only gold values
    private Monster alien = new Alien(this, MonsterType.Alien);
    private Monster wizard = new Wizard(this, MonsterType.Wizard);

    public GameExtended(GameCallback gameCallback, Properties properties)
    {
        //Setup game
        super(gameCallback, properties);
        super.getMonsters().add(orion);
        super.getMonsters().add(alien);
        super.getMonsters().add(wizard);
    }


    @Override
    /**
     * Method that sets up the actors locations in the extendable multiverse version
     */
    public void setupActorLocations() {
        super.setupActorLocations();
        String[] orionLocations = super.getProperties().getProperty("Orion.location").split(",");
        String[] alienLocations = super.getProperties().getProperty("Alien.location").split(",");
        String[] wizardLocations = super.getProperties().getProperty("Wizard.location").split(",");


        int orionX = Integer.parseInt(orionLocations[0]);
        int orionY = Integer.parseInt(orionLocations[1]);

        int alienX = Integer.parseInt(alienLocations[0]);
        int alienY = Integer.parseInt(alienLocations[1]);

        int wizardX = Integer.parseInt(wizardLocations[0]);
        int wizardY = Integer.parseInt(wizardLocations[1]);

        addActor(orion, new Location(orionX, orionY), Location.NORTH);
        addActor(alien, new Location(alienX, alienY), Location.NORTH);
        addActor(wizard, new Location(wizardX, wizardY), Location.NORTH);
    }
}

