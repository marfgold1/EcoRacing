package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.State;

public class Car {
    @SerializedName("id")
    public int id;

    @SerializedName("position")
    public Position position;

    @SerializedName("speed")
    public int speed;

    @SerializedName("state")
    public State state;

    @SerializedName("damage")
    public int damage;

    @SerializedName("powerups")
    public PowerUps[] powerups;

    @SerializedName("boosting")
    public Boolean boosting;

    @SerializedName("boostCounter")
    public int boostCounter;

    private final static int[] speedDamage = { 9, 9, 8, 6, 3, 0 };

    public int getMaxSpeed() {
        return speedDamage[damage];
    }

    public int getNextSpeed() {
        int nextSpeed = 0;
        for (int k = 1; k < Car.speedDamage.length; k++) {
            if (Car.speedDamage[k] <= this.speed) {
                nextSpeed = Math.max(Car.speedDamage[k - 1] - this.speed, 0);
                break;
            }
        }
        return nextSpeed;
    }

    public int getSpeed() {
        if (this.boostCounter == 1) {
            this.speed = Car.speedDamage[this.damage];
        }
        return this.speed;
    }

    public int getPrevSpeed() {
        int prevSpeed = this.speed;
        for (int k = 1; k < Car.speedDamage.length - 1; k++) {
            if (Car.speedDamage[k] < this.speed) {
                prevSpeed = Car.speedDamage[k];
                break;
            }
        }
        return prevSpeed;
    }
}
