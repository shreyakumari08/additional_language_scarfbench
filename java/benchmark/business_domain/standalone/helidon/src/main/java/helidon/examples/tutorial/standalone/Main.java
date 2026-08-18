package helidon.examples.tutorial.standalone;

import io.helidon.microprofile.server.Server;

public final class Main {
    private Main() {}

    public static void main(final String[] args) {
        Server.create().start();
    }
}
