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

    private final static int[] speedDamage = {15,9,8,6,3,0}; 

    public void update(GameState gameState) {
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
    }
    
    public Command run() {
        // Initialize Available Command
        /* 
            Available command initialized from index 0-3, where
            index 0 = FIX
            index 1 = LIZARD, TURN
            index 2 = BOOST, ACCELERATE
            index 3 = EMP, OIL, TWEET
        */
        // NOTES: NOTHING and DECELERATE will not be used, because it is redundant
        List<ArrayList<Command>> availableCommands = new ArrayList<>();
        
        availableCommands.add(fix());
        availableCommands.add(dodge());
        availableCommands.add(accel());
        availableCommands.add(offensive());

        // Iterate and return command with the higher priority
        for (ArrayList<Command> commands : availableCommands) {
            if (!commands.isEmpty()){
                return commands.get(0);
            }
        }   

        // If there's no available commands, Accelerate
        return ACCELERATE;
    }

    private ArrayList<Command> fix(){
        ArrayList<Command> res = new ArrayList<Command>(); 
        if (opponent.speed > speedDamage[myCar.damage]) {
            res.add(FIX);
        } else {
            if (myCar.damage > 1){
                res.add(FIX);
            }
        }
        return res;
    }

    private ArrayList<Command> dodge() {
        ArrayList<Command> res = new ArrayList<Command>();

        // Check laneflags for player
        Terrain[] flags = LaneFlags(true);

        // If there's only powerup in front of the car
        if (flags[1].equals(Terrain.BOOST)){
            return res;
        // If there's only powerup in left of the car
        } else if (flags[0].equals(Terrain.BOOST)){
            res.add(TURN_LEFT);
            return res;
        // If there's only powerup in right of the car
        } else if (flags[2].equals(Terrain.BOOST)) {
            res.add(TURN_RIGHT);
            return res;
        } else {
            
        }
        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
            res.add(LIZARD);
        }

        
        
        
        return ;
    };

    private Terrain[] LaneFlags(boolean forPlayer) {
        Car car;
        if (forPlayer){
            car = myCar;
        } else {
            car = opponent;
        }
        List<Lane[]> map = gameState.lanes;
        Terrain[] flags = new Terrain[3];
        
        Position carPos = car.position;        
        int lane = carPos.lane;
        int startBlock = map.get(0)[0].position.block;
        int viewingDistance = 15;
        if (lane == 1) {
            // Check for obstacles in lane 1 and 2
            
            for (int i = Math.max(carPos.block - startBlock, 0) + 1; i < carPos.block - startBlock + viewingDistance; i++) {
            
            }
        }
        else if (lane == map.size()) {
            // Check for obstacles in last lane and the lane before the last lane
            for (int i = Math.max(carPos.block - startBlock, 0) + 1; i < carPos.block - startBlock + viewingDistance; i++) {
            
            }
        }

        

        return flags;
        
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
      for (PowerUps powerUp: available) {
          if (powerUp.equals(powerUpToCheck)) {
              return true;
          }
      }
      return false;
    }
}
