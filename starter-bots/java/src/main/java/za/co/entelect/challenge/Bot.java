package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import java.util.*;
import java.lang.Exception;
import java.io.File;
import java.io.FileWriter;

import static java.lang.Math.max;

public class Bot {
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static TweetCommand TWEET = new TweetCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    private GameState gameState;
    private Car myCar;
    private Car opponent;

    public void update(GameState gameState) {
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
    }

    public Command run() throws Exception {
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
        if (myCar.speed > 0) {
            availableCommands.add(dodge());
        }
        availableCommands.add(accel());
        availableCommands.add(offensive());

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
        if (opponent.speed > myCar.getMaxSpeed() && myCar.damage > 0) {
            res.add(FIX);
        } else {
            if (myCar.damage > 1) {
                res.add(FIX);
            }
        }
        return res;
    }

    static boolean isMudOrWall(Terrain[] flags, int lane) {
        return flags[lane] == Terrain.MUD || flags[lane] == Terrain.WALL;
    }

    private ArrayList<Command> dodge() throws Exception {
        ArrayList<Command> res = new ArrayList<Command>();
        int myCarLane = myCar.position.lane;

        // Check laneflags for player
        Terrain[] flags = LaneFlags(true);
        // special case: boosting
        // the algorithm will check for all lanes, and decide whether the car should
        // decelerate or dodge

        // if (myCar.boosting && isMudOrWall(flags, 0) && isMudOrWall(flags, 1) && isMudOrWall(flags, 2)) {
        //     // if all lane contains mud or wall, consider to decelerate
        //     Terrain[] flagsNine = LaneFlags(true, 9);
        //     if (myCarLane == 1 && isMudOrWall(flagsNine, 1) && !isMudOrWall(flags, 2)) {
        //         res.add(DECELERATE);
        //     } else if (myCarLane == 4 && !isMudOrWall(flagsNine, 0) && isMudOrWall(flags, 1)) {
        //         res.add(DECELERATE);
        //     } else {
        //         if (!isMudOrWall(flagsNine, 1)) {
        //             res.add(DECELERATE);
        //         }
        //     }
        // }

        // Car will try to aim for the powerups
        if (myCarLane == 1 && flags[2] == Terrain.EMPTY && !flags[1].equals(Terrain.BOOST)) {
            res.add(TURN_RIGHT);
        } else if (myCarLane == 4 && flags[0] == Terrain.EMPTY && !flags[1].equals(Terrain.BOOST)) {
            res.add(TURN_LEFT);
        } else {
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
                if (!flags[1].equals(Terrain.EMPTY)) {
                    // If has LIZARD, use it
                    if (hasPowerUp(PowerUps.LIZARD) && !flags[1].equals(Terrain.OIL_SPILL)) {
                        res.add(LIZARD);
                    }
                    // If all those 3 lanes have an obstacle, choose the least critical one
                    if (isMudOrWall(flags, 0) && isMudOrWall(flags, 1) && isMudOrWall(flags, 2)) {
                        if (flags[0] == Terrain.WALL && flags[1] == Terrain.WALL && flags[2] == Terrain.MUD) {
                            res.add(TURN_RIGHT);
                        } else if (flags[0] == Terrain.MUD && flags[1] == Terrain.MUD && flags[2] == Terrain.MUD) {
                            res.add(TURN_LEFT);
                        }
                    }

                    // Prioritize to stay on middle (lane 2/3)
                    if (myCarLane < 3 && flags[2].equals(Terrain.EMPTY)) {
                        res.add(TURN_RIGHT);
                    } else if (myCarLane > 1 && flags[0].equals(Terrain.EMPTY)) {
                        res.add(TURN_LEFT);
                        // If there's an obstacle on the middle, move to the edge
                    } else if (flags[2].equals(Terrain.EMPTY)) {
                        res.add(TURN_RIGHT);
                    } else if (flags[0].equals(Terrain.EMPTY)) {
                        res.add(TURN_LEFT);
                    }
                }
            }
        }

        return res;
    };

    private ArrayList<Command> accel() {
        boolean useBoost = true;
        ArrayList<Command> res = new ArrayList<Command>();

        Terrain[] flags = LaneFlags(true, 15);

        // check if the car has damage or the car is already boosting
        if (myCar.damage > 0 || myCar.boosting) {
            useBoost = false;
        } else {
            if (flags[1].equals(Terrain.MUD) || flags[1].equals(Terrain.WALL) || flags[1].equals(Terrain.OIL_SPILL)) {
                useBoost = false;
            }
        }

        if (useBoost && hasPowerUp(PowerUps.BOOST))
            res.add(BOOST);

        if (myCar.speed < myCar.getMaxSpeed()) {
            res.add(ACCELERATE);
        }
        return res;
    }

    int lastCheckBlock = 1;

    private ArrayList<Command> offensive() {
        ArrayList<Command> res = new ArrayList<Command>();
        if (myCar.position.block < opponent.position.block && opponent.speed > 6) {
            if (hasPowerUp(PowerUps.EMP))
                res.add(EMP);
        }

        // Scan for any candidate position for cybertruck
        int startBlock = gameState.lanes.get(0)[0].position.block;
        int laneLen = gameState.lanes.get(0).length;
        int countObstacle, freeLane;
        boolean isEnd = false;
        int j;
        for (j = max(lastCheckBlock - startBlock, 0) + 1; j < laneLen; j++) {
            freeLane = -1;
            countObstacle = 0;
            lastCheckBlock = startBlock + j;
            for (int i = gameState.lanes.size() - 1; i >= 0; i--) {
                Lane[] lane = gameState.lanes.get(i);
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
        if (hasPowerUp(PowerUps.TWEET)) {
            if (TWEET.placeCybertruck(opponent.position, myCar.position))
                res.add(TWEET);
        }

        if (myCar.position.block > opponent.position.block) {
            if (hasPowerUp(PowerUps.OIL))
                res.add(OIL);
        }

        return res;
    }

    /*
     * Flags the nearby lane with the highest priority terrain type
     * WALL > MUD > BOOST(all powerups) > EMPTY
     * 
     * Where:
     * - anything isCyberTruck = WALL
     * - OIL_SPILLS = MUD
     * - TWEET, BOOST, EMP, LIZARD, OIL_POWER = BOOST
     */

    private Terrain[] LaneFlags(boolean forPlayer) {
        return LaneFlags(forPlayer, -1);
    }

    private Terrain[] LaneFlags(boolean forPlayer, int forwardDistance) {
        Car car;

        if (forPlayer) {
            car = myCar;
        } else {
            car = opponent;
        }

        if (forwardDistance == -1) {
            forwardDistance = car.speed;
        }

        List<Lane[]> map = gameState.lanes;

        Terrain[] flags = new Terrain[3];

        // Flags out of bounds as WALL
        if (car.position.lane == 4) {
            flags[2] = Terrain.WALL;
        } else if (car.position.lane == 1) {
            flags[0] = Terrain.WALL;
        }

        List<Terrain> terrainPowerups = new ArrayList<Terrain>() {
            {
                add(Terrain.TWEET);
                add(Terrain.BOOST);
                add(Terrain.EMP);
                add(Terrain.LIZARD);
                add(Terrain.OIL_POWER);
            }
        };
        final int startBlock = map.get(0)[0].position.block;

        String debug = "";

        // Iterate through possible lanes beside the car
        for (int i = Math.max(0, car.position.lane - 2); i <= Math.min(3, car.position.lane); i++) {
            Lane[] laneList = map.get(i);

            if (flags[i - car.position.lane + 2] == Terrain.WALL) {
                continue;
            }
            int modifier = -1;
            if (i - car.position.lane + 2 == 1) {
                modifier = 0;
                for (int k = 1; k < Car.speedDamage.length; k++) {
                    if (Car.speedDamage[k] == car.speed) {
                        modifier = Math.max(Car.speedDamage[k - 1] - myCar.speed, 0);
                        break;
                    }
                }
            }

            // Iterate from car position to car position + speed
            for (int j = Math.max(car.position.block - startBlock, 0); j <= car.position.block - startBlock
                    + forwardDistance + modifier; j++) {
                debug += String.format("\nlane %d block %d: ", i, j);
                if (laneList[j] == null || laneList[j].terrain.equals(Terrain.FINISH)) {
                    break;
                }

                // Flag the lane as oil spills if it cannot be lizard'd
                if (i - car.position.lane + 2 == 1 && j == car.position.block - startBlock + car.speed) {
                    if (laneList[j].terrain.equals(Terrain.OIL_SPILL) || laneList[j].terrain.equals(Terrain.WALL)
                            || laneList[j].terrain.equals(Terrain.MUD)) {
                        debug += " OIL_SPILL";
                        flags[i - car.position.lane + 2] = Terrain.OIL_SPILL;
                        break;
                    }
                }

                // If there's a wall or anything occupied by cybertruck, flag it as a wall
                if (laneList[j].terrain.equals(Terrain.WALL) || laneList[j].isOccupiedByCyberTruck
                        || (laneList[j].occupiedByPlayerId > 0 && laneList[j].occupiedByPlayerId != myCar.id
                                && (opponent.position.block - myCar.position.block < (myCar.speed / 2)))) {
                    debug += " WALL";
                    flags[i - car.position.lane + 2] = Terrain.WALL;
                }

                // If there's mud/oil spills, flag it as a mud
                if (flags[i - car.position.lane + 2] != Terrain.WALL) {
                    if ((laneList[j].terrain.equals(Terrain.MUD) || laneList[j].terrain.equals(Terrain.OIL_SPILL)) && laneList[j].occupiedByPlayerId != myCar.id) {
                        debug += " MUD";
                        flags[i - car.position.lane + 2] = Terrain.MUD;
                        continue;
                    }
                }

                // If there's a powerup on an empty lane, flag it as a boost powerup
                if (flags[i - car.position.lane + 2] == null && terrainPowerups.contains(laneList[j].terrain)) {
                    debug += " BOOST->" + laneList[j].terrain.toString();
                    flags[i - car.position.lane + 2] = Terrain.BOOST;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            // If there's nothing, flag it as empty
            if (flags[i] == null) {
                debug += String.format("\nlane %d is empty", i);
                flags[i] = Terrain.EMPTY;
            }
        }

        if (forwardDistance == car.speed) {
            File f = new File("logs_flags.txt");
            try {
                FileWriter fw = new FileWriter(f, true);
                fw.write(Arrays.toString(flags) + '\n');
                fw.write(String.format("Round : %d ", gameState.currentRound) + debug);
                fw.write("\n");
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return flags;

    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck) {
        for (PowerUps powerUp : myCar.powerups) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }
}
