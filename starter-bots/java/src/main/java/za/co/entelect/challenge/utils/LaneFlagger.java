package za.co.entelect.challenge.utils;

import java.util.*;

import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.Terrain;

/*
* Flags the nearby lane with the highest priority terrain type
* WALL > MUD > BOOST(all powerups) > EMPTY
* 
* Where:
* - anything isCyberTruck = WALL
* - OIL_SPILLS = MUD
* - TWEET, BOOST, EMP, LIZARD, OIL_POWER = BOOST
*/
public class LaneFlagger {
    Terrain[] flags;
    Terrain[] deccel_flags;
    Terrain[] boost_flags;

    private static List<Terrain> TerrainPowerups;

    public LaneFlagger() {
        // Initialize Flags and powerups
        flags = new Terrain[3];
        deccel_flags = new Terrain[3];
        boost_flags = new Terrain[3];
        TerrainPowerups = new ArrayList<Terrain>() {
            {
                add(Terrain.TWEET);
                add(Terrain.BOOST);
                add(Terrain.EMP);
                add(Terrain.LIZARD);
                add(Terrain.OIL_POWER);
            }
        };
    }

    // Update all flags
    public void update(List<Lane[]> map, Car car, Car opponent, int currentRound) {
        flags = updateFlags(map, car, opponent, car.getSpeed(), currentRound);
        deccel_flags = updateFlags(map, car, opponent, car.getPrevSpeed(), currentRound);
        boost_flags = updateFlags(map, car, opponent, 15, currentRound);
    }

    // Get flags
    public Terrain[] getFlags() {
        return flags;
    }

    public Terrain[] getDeccelFlags() {
        return deccel_flags;
    }

    public Terrain[] getBoostFlags() {
        return boost_flags;
    }

    // Update flags
    private Terrain[] updateFlags(List<Lane[]> map, Car car, Car opponent, int distance, int currentRound) {
        int modifier = -1;
        Lane[] lane;
        Terrain[] flags = new Terrain[3];

        // Get the first block in view
        final int StartBlock = map.get(0)[0].position.block;

        // Flags out of bounds as WALL
        if (car.position.lane == 4) {
            flags[2] = Terrain.WALL;
        } else if (car.position.lane == 1) {
            flags[0] = Terrain.WALL;
        }

        // Iterate through possible lanes on the side of car
        for (int i = Math.max(0, car.position.lane - 2); i <= Math.min(3, car.position.lane); i++) {
            lane = map.get(i);

            // If already flagged out of bounds, skip it
            if (flags[i - car.position.lane + 2] == Terrain.WALL) {
                continue;
            }

            // Get a modifier for turning and accelerating
            modifier = -1;
            // If checking the lane in front of the car
            if (i - car.position.lane + 2 == 1) {
                // If car is not going to accel, set modifier as 0
                if (distance == car.getSpeed()) {
                    modifier = Math.max(car.getNextSpeed() - car.getSpeed(), 0);
                } else {
                    modifier = 0;
                }

            }

            // Iterate through blocks in the lane
            for (int j = Math.max(car.position.block - StartBlock, 0); j <= car.position.block - StartBlock
                    + distance + modifier; j++) {

                // No need to check player position
                if (i - car.position.lane + 2 == 1 && j == 5) {
                    continue;
                }

                // If out of bounds, skip it
                if (Extras.isEndOfLane(lane[j])) {
                    break;
                }

                // Flag the lane as oil spills if it cannot be lizard'd
                if (i - car.position.lane + 2 == 1 && j == car.position.block - StartBlock + car.getSpeed()) {
                    if (lane[j].terrain == Terrain.OIL_SPILL || lane[j].terrain == Terrain.WALL
                            || lane[j].terrain.equals(Terrain.MUD)) {
                        flags[i - car.position.lane + 2] = Terrain.OIL_SPILL;
                        break;
                    }
                }

                // If there's a wall or anything occupied by cybertruck or opponent that slower
                // than player, flag it as a wall
                if (lane[j].terrain.equals(Terrain.WALL) || lane[j].isOccupiedByCyberTruck
                        || (lane[j].occupiedByPlayerId > 0 && lane[j].occupiedByPlayerId != car.id
                                && (opponent.position.block
                                        - car.position.block < (car.getSpeed() - opponent.speed)))) {
                    flags[i - car.position.lane + 2] = Terrain.WALL;
                }

                // If there's mud/oil spills, flag it as a mud
                if (flags[i - car.position.lane + 2] != Terrain.WALL) {
                    if ((lane[j].terrain.equals(Terrain.MUD) || lane[j].terrain.equals(Terrain.OIL_SPILL))
                            && lane[j].occupiedByPlayerId != car.id) {
                        flags[i - car.position.lane + 2] = Terrain.MUD;
                        continue;
                    }
                }

                // If there's a powerup on an empty lane, flag it as a boost powerup
                if (flags[i - car.position.lane + 2] == null && TerrainPowerups.contains(lane[j].terrain)) {
                    flags[i - car.position.lane + 2] = Terrain.BOOST;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            // If there's nothing, flag it as empty
            if (flags[i] == null) {
                flags[i] = Terrain.EMPTY;
            }
        }

        return flags;
    }
}
