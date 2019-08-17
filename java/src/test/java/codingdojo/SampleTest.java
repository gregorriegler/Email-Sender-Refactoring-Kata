package codingdojo;

import org.junit.jupiter.api.Test;

public class SampleTest {
    @Test
    void sample() throws Exception {
        ActualMailServer server = new ActualMailServer();
        server.something(new FileEmailListReader(), "smtp.host.com", "pop3.host.com", "user", "password", "file.txt", "1");
    }

}
