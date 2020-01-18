package codingdojo;

import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {

    int countCalls = 0;

    @Test
    void tbd() throws InterruptedException, MessagingException, IOException {

        Server server = new Server() {
            @Override
            boolean keepRunning() {
                return countCalls++ == 0;
            }
        };
        server.doSomething("smtphost", "pop3Host", "user", "password", "listFileName", "fromName", 0);
    }

}
