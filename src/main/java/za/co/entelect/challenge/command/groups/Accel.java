package za.co.entelect.challenge.command.groups;

import java.util.ArrayList;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.utils.Extras;
import za.co.entelect.challenge.utils.LaneFlagger;

public class Accel implements CommandGroups {
    private ArrayList<Command> commands;
    private LaneFlagger flagger;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command BOOST = new BoostCommand();

    // Initialize commands array
    public Accel(LaneFlagger flagger) {
        this.commands = new ArrayList<>();
        this.flagger = flagger;
    }

    // Get Commands
    @Override
    public ArrayList<Command> getCommands() {
        return commands;
    }

    // Update commands
    @Override
    public void update(GameState state) {
        Terrain[] flags = flagger.getBoostFlags();
        Car car = state.player;
        commands.clear();
        boolean useBoost = true;

        // check if the car has damage or the car is already boosting
        if (car.damage > 0 || car.boosting) {
            useBoost = false;
        } else {
            if (flags[1].equals(Terrain.MUD) || flags[1].equals(Terrain.WALL) || flags[1].equals(Terrain.OIL_SPILL)) {
                useBoost = false;
            }
        }

        if (useBoost && Extras.hasPowerUp(PowerUps.BOOST, car))
            commands.add(BOOST);

        if (car.getSpeed() < car.getMaxSpeed()) {
            commands.add(ACCELERATE);
        }
    }
}
