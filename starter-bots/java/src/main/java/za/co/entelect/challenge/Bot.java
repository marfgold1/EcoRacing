package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.utils.LaneFlagger;
import za.co.entelect.challenge.commandGroups.*;
import java.util.*;

public class Bot {
    private final static Command ACCELERATE = new AccelerateCommand();

    // Utils
    private LaneFlagger laneFlagger = new LaneFlagger();

    // Command Groups
    private Fix fix = new Fix();
    private Dodge dodge = new Dodge();
    private Accel accel = new Accel();
    private Offensive offensive = new Offensive();

    public void update(GameState gameState) {
        // Utils
        this.laneFlagger.update(gameState.lanes, gameState.player, gameState.opponent, gameState.currentRound);

        // Command Groups
        this.fix.update(gameState.player, gameState.opponent);
        this.dodge.update(gameState.player, laneFlagger.getFlags(), laneFlagger.getDeccelFlags());
        this.accel.update(laneFlagger.getBoostFlags(), gameState.player);
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
