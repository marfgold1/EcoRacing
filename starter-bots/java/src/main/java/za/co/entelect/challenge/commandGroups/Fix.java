package za.co.entelect.challenge.commandGroups;

import java.util.ArrayList;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;

public class Fix {
    final static Command FIX = new FixCommand();
    ArrayList<Command> commands;

    // Constructor for Fix
    public Fix() {
        this.commands = new ArrayList<>();
    }

    // Add command to the list of commands
    public void update(Car player, Car opponent) {
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
