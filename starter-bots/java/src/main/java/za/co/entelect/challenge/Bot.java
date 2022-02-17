package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.command.groups.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.utils.LaneFlagger;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Bot {
    private final static Command ACCELERATE = new AccelerateCommand();

    // Utils
    private LaneFlagger laneFlagger = new LaneFlagger();

    // Command Groups
    private CommandGroups fix = new Fix();
    private CommandGroups dodge = new Dodge(laneFlagger);
    private CommandGroups accel = new Accel(laneFlagger);
    private CommandGroups offensive = new Offensive();
    private GameState gameState;

    public void update(GameState gameState) {
        // Logging
        this.gameState = gameState;

        // Utils
        this.laneFlagger.update(gameState.lanes, gameState.player, gameState.opponent, gameState.currentRound);

        // Command Groups
        this.fix.update(gameState);
        this.dodge.update(gameState);
        this.accel.update(gameState);
        this.offensive.update(gameState);
    }

    public Command run() {
        // Initialize Available Command
        /*
         * Available command initialized from index 0-3, where
         * index 0 = FIX
         * index 1 = LIZARD, TURN
         * index 2 = BOOST, ACCELERATE
         * index 3 = EMP, OIL, TWEET
         */
        // NOTES: NOTHING and DECELERATE will not be used, because it is redundant
        List<ArrayList<Command>> availableCommands = new ArrayList<>();

        availableCommands.add(fix.getCommands());
        availableCommands.add(dodge.getCommands());
        availableCommands.add(accel.getCommands());
        availableCommands.add(offensive.getCommands());

        File f = new File("logs_commands.txt");
        try {
            FileWriter fw = new FileWriter(f, true);
            fw.write(gameState.currentRound + " ");
            for (ArrayList<Command> command : availableCommands) {
                for (Command c : command) {
                    fw.write(c.render() + " ");
                }
                fw.write("\n");
            }
            fw.write("\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Iterate and return command with the higher priority
        for (ArrayList<Command> commands : availableCommands) {
            if (!commands.isEmpty()) {
                return commands.get(0);
            }
        }

        // If there's no available commands, Accelerate
        return ACCELERATE;
    }

}
