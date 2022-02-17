package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.entities.Car;
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
}
