package za.co.entelect.challenge.command.groups;

import java.util.ArrayList;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;

public class Fix implements CommandGroups {
    ArrayList<Command> commands;

    final static Command FIX = new FixCommand();

    // Constructor for Fix
    public Fix() {
        this.commands = new ArrayList<>();
    }

    // Add command to the list of commands
    public void update(GameState state) {
        Car player = state.player;
        Car opponent = state.opponent;
        commands.clear();
        if (opponent.speed > player.getMaxSpeed() && player.damage > 0) {
            commands.add(FIX);
        } else {
            if (player.damage > 1) {
                commands.add(FIX);
            }
        }
    }

    // Return the list of commands
    public ArrayList<Command> getCommands() {
        return commands;
    }
}
