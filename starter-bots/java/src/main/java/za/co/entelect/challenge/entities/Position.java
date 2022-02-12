package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("y")
    public int lane;

    @SerializedName("x")
    public int block;

    public Position() {
        this(-1, -1);
    }

    public Position(int lane, int block) {
        this.lane = lane;
        this.block = block;
    }
}
