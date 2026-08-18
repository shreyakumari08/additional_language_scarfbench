package micronaut.tutorial.websocketbot;
import jakarta.inject.Singleton;
@Singleton
public class BotService {
    public String respond(String input) {
        if (input == null) return "?";
        String i = input.toLowerCase();
        if (i.contains("hello") || i.contains("hi")) return "Hi there!";
        if (i.contains("bye")) return "Goodbye!";
        if (i.contains("how are you")) return "I am well, thanks!";
        return "I heard: " + input;
    }
}
