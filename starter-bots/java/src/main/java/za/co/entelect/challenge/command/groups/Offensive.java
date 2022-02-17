package za.co.entelect.challenge.command.groups;

import java.util.ArrayList;
import java.util.List;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.utils.Extras;

public class Offensive implements CommandGroups {
    ArrayList<Command> commands;
    private int lastCheckBlock;

    private final static Command OIL = new OilCommand();
    private final static Command EMP = new EmpCommand();
    private final static TweetCommand TWEET = new TweetCommand();

    // Initialize commands array and lastcheckblock
    public Offensive() {
        this.commands = new ArrayList<>();
        this.lastCheckBlock = 1;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    /*  Generate any strategic position for cybertruck.
        Strategic positions defined by:
            Terrains in the same x position, or formally (block, lane_i) where 0 <= i < lane_length
            that have 2 or 3 terrain that are detected as obstacle, have a strategic position of the
            last checked block that is not obstacle.
        It will check from block 0 until the end of the block, where each block
        will be checked from lane 4 until lane 1 to find one strategic position, if any.
        If there is a strategic position, it will add that position into the TWEET command.
    */
    private void generateCybertruckPosition(GameState state) {
        List<Lane[]> map = state.lanes;
        // Scan for any candidate position for cybertruck
        // Get starting block position
        final int startBlock = map.get(0)[0].position.block;
        // Get length of the lane
        final int laneLen = map.get(0).length;
        // Obstacle counter and free lane index getter.
        int countObstacle, freeLane;
        // Searching schema using boolean.
        boolean isEnd = false;
        int j;
        // Starts from block that is not checked until to the end of the block in the map (laneLen).
        for (j = Math.max(lastCheckBlock - startBlock, 0) + 1; j < laneLen; j++) {
            // Reset the candidate lane, obstacle counter, and last checked block
            freeLane = -1;
            countObstacle = 0;
            lastCheckBlock = startBlock + j;
            // Check each lane in the same x position (i)
            for (int i = map.size() - 1; i >= 0; i--) {
                // Get every block in lane i
                Lane[] lane = map.get(i);
                // Check if (i, j) terrain is the end. If the end, it will stop the check.
                if (Extras.isEndOfLane(lane[j])) {
                    isEnd = true;
                    break;
                }
                // Check if (i, j) terrain is obstacle. If obstacle, increment the obstacle counter.
                if (Extras.isObstacle(lane[j])) {
                    countObstacle++;
                } else {
                    // If not obstacle, set the free lane index to i + 1.
                    freeLane = i + 1;
                }
            }
            // If loop breaks because of the end, it will stop the check.
            if (isEnd)
                break;
            // If loop continue and there are 2 or 3 obstacles,
            else if (countObstacle == 2 || countObstacle == 3) {
                // add (freeLane, lastCheckBlock) as the strategic position in TWEET command.
                TWEET.addPosition(freeLane, lastCheckBlock);
            }
        }
        if (isEnd)
            lastCheckBlock--;
    }

    public void update(GameState state) {
        Car player = state.player;
        Car opponent = state.opponent;
        commands.clear();
        
        // EMP strategy
        // Use EMP if we are behind the opponent and opponent speed is over 6
        if (player.position.block < opponent.position.block && opponent.speed > 6) {
            if (Extras.hasPowerUp(PowerUps.EMP, player))
                commands.add(EMP);
        }

        // TWEET strategy
        // Placed here because we always check for new position
        generateCybertruckPosition(state);
        // Place dat cybertruck onegai
        // Use cybertruck if one of these condition met:
        // 1. We are behind the opponent and opponent speed is over 9 (boosting).
        //    Will place cybertruck in (opponent lane, opponent block + speed + 3).
        // 2. Opponents are behind us and there is a strategic position.
        //    Will place the cybertruck in the nearest strategic position.
        if (Extras.hasPowerUp(PowerUps.TWEET, player)) {
            // First condition
            if (player.position.block < opponent.position.block) {
                TWEET.setPosition(
                    opponent.position.lane,
                    opponent.position.block + opponent.speed + 3
                );
                commands.add(TWEET);
            // Second condition
            } else if (TWEET.placeCybertruck(opponent.position, player.position)) {
                commands.add(TWEET);
            }
        }

        // OIL strategy
        if (player.position.block > opponent.position.block) {
            if (Extras.hasPowerUp(PowerUps.OIL, player))
                commands.add(OIL);
        }

    }
}
