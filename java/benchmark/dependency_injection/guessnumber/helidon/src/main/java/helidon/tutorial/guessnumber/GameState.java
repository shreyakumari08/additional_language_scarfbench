package helidon.tutorial.guessnumber;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class GameState {
    private static final int MAX = 100;
    private final Random random = new Random();
    private int number;
    private int minimum;
    private int maximum;
    private int remainingGuesses;

    public GameState() { reset(); }

    public synchronized void reset() {
        this.number = random.nextInt(MAX + 1);
        this.minimum = 0; this.maximum = MAX; this.remainingGuesses = 10;
    }

    public synchronized String tryGuess(int userNumber) {
        if (userNumber < minimum || userNumber > maximum) return "Invalid guess";
        if (userNumber > number) maximum = userNumber - 1;
        else if (userNumber < number) minimum = userNumber + 1;
        if (remainingGuesses > 0) remainingGuesses--;
        return userNumber == number ? "Correct!" : null;
    }

    public int getMinimum() { return minimum; }
    public int getMaximum() { return maximum; }
    public int getRemainingGuesses() { return remainingGuesses; }
}
