package scol.handlers;

import java.util.Random;

public class HelpHandler {
    public static int getRandomInt(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static double getRandomDouble(Random random, int min, int max) {
        return random.nextDouble() *  (max - min) + min;
    }

}
