package micronaut.tutorial.guessnumber.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserNumberBean {
    private int number;
    private Integer userNumber;
    private int minimum;
    private int maximum;
    private int remainingGuesses;

    public UserNumberBean() {}
    public UserNumberBean(int number, int maxNumber) { reset(number, maxNumber); }

    public void reset(int number, int maxNumber) {
        this.minimum = 0; this.userNumber = 0; this.remainingGuesses = 10;
        this.maximum = maxNumber; this.number = number;
    }

    public void check() {
        if (userNumber > number) maximum = userNumber - 1;
        else if (userNumber < number) minimum = userNumber + 1;
        if (remainingGuesses > 0) remainingGuesses--;
    }

    public int getMinimum() { return minimum; }
    public int getMaximum() { return maximum; }
    public int getNumber() { return number; }
    public int getRemainingGuesses() { return remainingGuesses; }
    public Integer getUserNumber() { return userNumber; }
    public void setUserNumber(Integer userNumber) { this.userNumber = userNumber; }
}
