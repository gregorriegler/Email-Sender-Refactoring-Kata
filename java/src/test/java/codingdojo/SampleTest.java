package codingdojo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Vector;

import static java.util.Arrays.asList;

public class SampleTest {
    @Test
    void noMessages() throws Exception {
        ActualMailServer server = new ActualMailServer(new EmailMessageSender());
        server.inLoop(
            new StubEmailListReader(),
            new StubStoreProvider(FakeStore.create(new FakeFolder(new Message[]{}))),
            "smtp.host.com",
            "pop3.host.com",
            "user",
            "password",
            "file.txt",
            "1",
            new Server()
        );
    }

    @Test
    void withSeenMessage() throws Exception {
        ActualMailServer server = new ActualMailServer(new EmailMessageSender());
        server.inLoop(
            new StubEmailListReader(),
            new StubStoreProvider(FakeStore.create(new FakeFolder(new Message[]{
                new FakeMessage(true)
            }))),
            "smtp.host.com",
            "pop3.host.com",
            "user",
            "password",
            "file.txt",
            "1",
            new Server()
        );
    }

    @Test
    void withUnseenMessage() throws Exception {
        StubMessageSender messageSender = new StubMessageSender();
        ActualMailServer server = new ActualMailServer(messageSender);
        server.inLoop(
            new StubEmailListReader(),
            new StubStoreProvider(FakeStore.create(new FakeFolder(new Message[]{
                new FakeMessage(false)
            }))),
            "smtp.host.com",
            "pop3.host.com",
            "user",
            "password",
            "file.txt",
            "1",
            new Server()
        );
        Assertions.assertEquals(
            asList(new StubMessageSender.Sent(
                null,
                null,
                null,
                null,
                null,
                null
            )
        ), messageSender.sents);
    }

}

class StubEmailListReader implements EmailListReader {

    @Override
    public Vector readEmailListFromFile(String emailListFile) throws AddressException {
        Vector<InternetAddress> vector = new Vector(3);
        vector.add(new InternetAddress("my@mail.com"));
        return vector;
    }
}

class StubStoreProvider implements StoreProvider {

    private final Store store;

    public StubStoreProvider(Store store) {
        this.store = store;
    }

    @Override
    public Store getStore(boolean debugOn) throws NoSuchProviderException {
        return store;
    }

}