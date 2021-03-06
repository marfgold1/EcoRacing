package za.co.entelect.challenge.command;

import java.util.ArrayList;

import za.co.entelect.challenge.entities.Position;

public class TweetCommand implements Command {
    Position pos = new Position();

    ArrayList<Position> candidatePositions = new ArrayList<>();

    // Try to place the cybertruck if there are any strategic positions
    // that are more or equal than the enemy block + 10 and return true.
    // If there are no strategic position available, return false.
    public boolean placeCybertruck(Position enemyPos, Position playerPos) {
        // Delete all candidates that was behind the enemy
        candidatePositions.removeIf(f -> f.block < enemyPos.block + 10);
        // Place cybertruck if:
        // 1. There's a candidate position
        if (!candidatePositions.isEmpty()) {
            // 2. First candidate position is behind the player
            if (candidatePositions.get(0).block < playerPos.block) {
                // 3. Last cybertruck passed by enemy
                if (pos.block < enemyPos.block) {
                    // Set cybertruck position to first candidate position and return true
                    // (cybertruck ready to be placed)
                    pos = candidatePositions.get(0);
                    return true;
                }
            }
        }
        // If any of those condition is not met, return false
        return false;
    }
    
    public void setPosition(int lane, int block) {
        pos.lane = lane;
        pos.block = block;
    }

    public void addPosition(int lane, int block) {
        candidatePositions.add(new Position(lane, block));
    }

    @Override
    public String render() {
        return String.format("USE_TWEET %d %d", pos.lane, pos.block);
    }
}
