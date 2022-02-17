package za.co.entelect.challenge.commandGroups;

import java.util.ArrayList;
import java.util.List;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.utils.Extras;

public class Offensive {
    private ArrayList<Command> commands;
    private int lastCheckBlock;

    private final static Command OIL = new OilCommand();
    private final static Command EMP = new EmpCommand();
    private final static TweetCommand TWEET = new TweetCommand();

    // Initialize commands array and lastcheckblock
    public Offensive() {
        commands = new ArrayList<>();
        lastCheckBlock = 1;
    }

    // Get Commands
    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void update(Car player, Car opponent, List<Lane[]> map) {
        commands.clear();
        if (player.position.block < opponent.position.block && opponent.speed > 6) {
            if (Extras.hasPowerUp(PowerUps.EMP, player))
                commands.add(EMP);
        }

        // Scan for any candidate position for cybertruck
        int startBlock = map.get(0)[0].position.block;
        int laneLen = map.get(0).length;
        int countObstacle, freeLane;
        boolean isEnd = false;
        int j;
        for (j = Math.max(lastCheckBlock - startBlock, 0) + 1; j < laneLen; j++) {
            freeLane = -1;
            countObstacle = 0;
            lastCheckBlock = startBlock + j;
            for (int i = map.size() - 1; i >= 0; i--) {
                Lane[] lane = map.get(i);
                if (lane[j] == null || lane[j].terrain == Terrain.FINISH) {
                    isEnd = true;
                    break;
                }
                if (lane[j].terrain == Terrain.MUD
                        || lane[j].terrain == Terrain.WALL
                        || lane[j].terrain == Terrain.OIL_SPILL
                        || lane[j].isOccupiedByCyberTruck) {
                    countObstacle++;
                } else {
                    freeLane = i + 1;
                }
            }
            if (isEnd)
                break;
            else if (countObstacle == 2 || countObstacle == 3) {
                TWEET.addPosition(freeLane, lastCheckBlock);
            }
        }
        if (isEnd)
            lastCheckBlock--;

        // Place dat cybertruck onegai
        if (Extras.hasPowerUp(PowerUps.TWEET, player)) {
            if (TWEET.placeCybertruck(opponent.position, player.position))
                commands.add(TWEET);
        }

        if (player.position.block > opponent.position.block) {
            if (Extras.hasPowerUp(PowerUps.OIL, player))
                commands.add(OIL);
        }

    }
}
