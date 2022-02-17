package za.co.entelect.challenge.command.groups;

import java.util.ArrayList;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.utils.Extras;
import za.co.entelect.challenge.utils.LaneFlagger;

public class Dodge implements CommandGroups {
    private ArrayList<Command> commands;
    private LaneFlagger flagger;

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    private final static Command LIZARD = new LizardCommand();
    private final static Command DECELERATE = new DecelerateCommand();

    // Initialize Array Commands
    public Dodge(LaneFlagger flagger) {
        this.flagger = flagger;
        this.commands = new ArrayList<>();
    }

    // Get Commands
    public ArrayList<Command> getCommands() {
        return commands;
    }

    // Update commands
    public void update(GameState state) {
        commands.clear();
        Car car = state.player;
        Terrain[] flags = flagger.getFlags();
        Terrain[] deccel_flags = flagger.getDeccelFlags();
        if (car.getSpeed() > 0) {
            int carLane = car.position.lane;

            // If the lane in the middle (lane 2/3) is empty, it will prioritize to stay on
            // the middle
            if (carLane == 1 && flags[2] == Terrain.EMPTY && !flags[1].equals(Terrain.BOOST)) {
                commands.add(TURN_RIGHT);
            } else if (carLane == 4 && flags[0] == Terrain.EMPTY && !flags[1].equals(Terrain.BOOST)) {
                commands.add(TURN_LEFT);
            } else {
                // Player will prioritize getting powerups
                if (flags[1].equals(Terrain.BOOST)) {
                    return;
                } else if (flags[0].equals(Terrain.BOOST)) {
                    commands.add(TURN_LEFT);
                    return;
                } else if (flags[2].equals(Terrain.BOOST)) {
                    commands.add(TURN_RIGHT);
                    return;
                } else {
                    // If there's an obstacle, avoid it
                    if (!flags[1].equals(Terrain.EMPTY)) {
                        // Prioritize to stay on middle (lane 2/3)
                        if (carLane < 3 && flags[2].equals(Terrain.EMPTY)) {
                            commands.add(TURN_RIGHT);
                        } else if (carLane > 1 && flags[0].equals(Terrain.EMPTY)) {
                            commands.add(TURN_LEFT);
                            // If there's an obstacle on the middle, move to the edge
                        } else if (flags[2].equals(Terrain.EMPTY)) {
                            commands.add(TURN_RIGHT);
                        } else if (flags[0].equals(Terrain.EMPTY)) {
                            commands.add(TURN_LEFT);
                        }

                        // If has LIZARD, use it
                        if (Extras.hasPowerUp(PowerUps.LIZARD, car) && !flags[1].equals(Terrain.OIL_SPILL)) {
                            commands.add(LIZARD);
                        }
                    }
                }
            }
            // If the obstacle is dodgeable using decelerate, use it
            if (deccel_flags[1] == Terrain.EMPTY && !(flags[1] == Terrain.EMPTY)) {
                commands.add(DECELERATE);
            }

            // If it's undodgeable, choose the best lane to go
            if (Extras.isMudOrWall(flags, 0) && Extras.isMudOrWall(flags, 1) && Extras.isMudOrWall(flags, 2)) {
                if (flags[0] == Terrain.WALL && flags[1] == Terrain.WALL && flags[2] == Terrain.MUD) {
                    commands.add(TURN_RIGHT);
                } else if (flags[0] == Terrain.MUD && flags[1] == Terrain.WALL && flags[2] == Terrain.WALL) {
                    commands.add(TURN_LEFT);
                }
            }
        }
    }
}
