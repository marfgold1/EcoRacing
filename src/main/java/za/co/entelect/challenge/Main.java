package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";

    /**
     * Read the current state, feed it to the bot, get the output and print it to
     * stdout
     *
     * @param args the args
     **/
    public static void main(String[] args) {
        Gson gson = new Gson();
        Bot bot = new Bot();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                try {
                    int roundNumber = sc.nextInt();
                    String statePath = String.format("./%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
                    String state = new String(Files.readAllBytes(Paths.get(statePath)));
                    bot.update(gson.fromJson(state, GameState.class));
                    Command command = bot.run();
                    System.out.println(String.format("C;%d;%s", roundNumber, command.render()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
