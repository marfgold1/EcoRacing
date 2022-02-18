package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

public class Extras {
    public static Boolean hasPowerUp(PowerUps powerUpToCheck, Car car) {
        for (PowerUps powerUp : car.powerups) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMudOrWall(Terrain[] flags, int lane) {
        return flags[lane] == Terrain.MUD || flags[lane] == Terrain.WALL;
    }

    public static boolean isObstacle(Lane lane) {
        return (
            lane.terrain == Terrain.MUD
            || lane.terrain == Terrain.WALL
            || lane.terrain == Terrain.OIL_SPILL
            || lane.isOccupiedByCyberTruck
        );
    }

    public static boolean isEndOfLane(Lane lane) {
        return (lane == null || lane.terrain == Terrain.FINISH);
    }
}
