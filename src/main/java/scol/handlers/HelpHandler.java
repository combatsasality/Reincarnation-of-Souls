package scol.handlers;

import java.util.Random;

public class HelpHandler {
    public static int getRandomInRange(int min, int max) {

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
