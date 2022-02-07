package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import java.util.*;

public class Bot {

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    // private final static Command OIL = new OilCommand();
    // private final static Command BOOST = new BoostCommand();
    // private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    private GameState gameState;
    private Car myCar;
    private Car opponent;

    private final static int[] speedDamage = { 15, 9, 8, 6, 3, 0 };

    public void update(GameState gameState) {
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
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

        availableCommands.add(fix());
        availableCommands.add(dodge());
        // availableCommands.add(accel());
        // availableCommands.add(offensive());

        // Iterate and return command with the higher priority
        for (ArrayList<Command> commands : availableCommands) {
            if (!commands.isEmpty()) {
                return commands.get(0);
            }
        }

        // If there's no available commands, Accelerate
        return ACCELERATE;
    }

    private ArrayList<Command> fix() {
        ArrayList<Command> res = new ArrayList<Command>();
        // If the opponent is faster than our current max speed, fix first
        if (opponent.speed > speedDamage[myCar.damage] && myCar.damage > 0) {
            res.add(FIX);
        } else {
            if (myCar.damage > 1) {
                res.add(FIX);
            }
        }
        return res;
    }

    private ArrayList<Command> dodge() {
        ArrayList<Command> res = new ArrayList<Command>();

        // Check laneflags for player
        Terrain[] flags = LaneFlags(true);

        // Car will try to aim for the powerups
        if (flags[1].equals(Terrain.BOOST)) {
            return res;
        } else if (flags[0].equals(Terrain.BOOST)) {
            res.add(TURN_LEFT);
            return res;
        } else if (flags[2].equals(Terrain.BOOST)) {
            res.add(TURN_RIGHT);
            return res;
        } else {
            // If there's an obstacle, avoid it
            if (!flags[0].equals(Terrain.EMPTY)) {
                // If has LIZARD, use it
                if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                    res.add(LIZARD);
                }
                // Prioritize to stay on middle (lane 2/3)
                if (myCar.position.lane < 3 && flags[1].equals(Terrain.EMPTY)) {
                    res.add(TURN_RIGHT);
                } else if (myCar.position.lane > 1 && flags[0].equals(Terrain.EMPTY)) {
                    res.add(TURN_LEFT);
                // If there's an obstacle on the middle, move to the edge
                } else if (flags[1].equals(Terrain.EMPTY)) {
                    res.add(TURN_RIGHT);
                } else if (flags[0].equals(Terrain.EMPTY)) {
                    res.add(TURN_LEFT);
                }
            }
        }

        return res;
    };


    /* 
    Flags the nearby lane with the highest priority terrain type
    WALL > MUD > BOOST(all powerups) > EMPTY

    Where:
    - anything isCyberTruck = WALL
    - OIL_SPILLS = MUD
    - TWEET, BOOST, EMP, LIZARD, OIL_POWER = BOOST
    */
    private Terrain[] LaneFlags(boolean forPlayer) {
        Car car;
        if (forPlayer) {
            car = myCar;
        } else {
            car = opponent;
        }
        List<Lane[]> map = gameState.lanes;
        Terrain[] flags = new Terrain[3];

        // Flags out of bounds as WALL
        if (myCar.position.lane == 4){
            flags[2] = Terrain.WALL;
        } else if (myCar.position.lane == 0){
            flags[0] = Terrain.WALL;
        }

        List<Terrain> terrainPowerups = new ArrayList<Terrain>(){{
            add(Terrain.TWEET); 
            add(Terrain.BOOST);
            add(Terrain.EMP);
            add(Terrain.LIZARD);
            add(Terrain.OIL_POWER);
        }};
        final int startBlock = map.get(0)[0].position.block;

        // When isBoosting, car will prioritize dodging than ramming through mud
        boolean isBoosting = forPlayer ? car.boosting : true;

        // Iterate through possible lanes beside the car
        for (int i = Math.max(0, car.position.lane - 1); i < Math.min(4, car.position.lane); i++){
            Lane[] laneList = map.get(i);
            boolean mudFlag = true && !isBoosting;
            for (int j = Math.max(car.position.block - startBlock, 0); j <= car.position.block - startBlock + car.speed; j++){
                // If there's a wall, flag it as a wall
                if (laneList[i].terrain.equals(Terrain.WALL)){
                    flags[i - car.position.lane-1] = Terrain.WALL;
                    break;
                }
                // If there's only 1 mud, ignore it
                if (mudFlag){
                    if (laneList[i].terrain.equals(Terrain.MUD) || laneList[i].terrain.equals(Terrain.OIL_SPILL)){
                        mudFlag = false;
                        continue;
                    }
                }

                // If there's more mud, flag it as a mud
                if (laneList[i].terrain.equals(Terrain.MUD) || laneList[i].terrain.equals(Terrain.OIL_SPILL)){
                    flags[i - car.position.lane-1] = Terrain.MUD;
                    continue;
                }

                // If there's a powerup on an empty lane, flag it as a boost powerup
                if (flags[i - car.position.lane-1] == null && terrainPowerups.contains(laneList[i].terrain)){
                    flags[i - car.position.lane-1] = Terrain.BOOST;
                }
            }

            // If there's nothing, flag it as empty
            if (flags[i] == null){
                flags[i] = Terrain.EMPTY;
            }
        }

        // Position carPos = car.position;
        // int lane = carPos.lane;
        // int startBlock = map.get(0)[0].position.block;
        // int viewingDistance = 15;
        // if (lane == 1) {
        //     // Check for obstacles in lane 1 and 2

        //     for (int i = Math.max(carPos.block - startBlock, 0) + 1; i < carPos.block - startBlock
        //             + viewingDistance; i++) {

        //     }
        // } else if (lane == map.size()) {
        //     // Check for obstacles in last lane and the lane before the last lane
        //     for (int i = Math.max(carPos.block - startBlock, 0) + 1; i < carPos.block - startBlock
        //             + viewingDistance; i++) {

        //     }
        // }

        return flags;

    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }
}
