package codingdojo;

import org.junit.jupiter.api.Test;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SampleTest {

    int countCalls = 0;

    @Test
    void tbd() throws InterruptedException, MessagingException, IOException {

        Store readingStore = mock(Store.class);
        Folder defaultFolder = mock(Folder.class);
        when(readingStore.getDefaultFolder()).thenReturn(defaultFolder);
        Folder inboxFolder = mock(Folder.class);
        when(defaultFolder.getFolder("INBOX")).thenReturn(inboxFolder);
        Message message = mock(Message.class);
        when(inboxFolder.getMessages()).thenReturn(new Message[]{ message });
        when(message.getContent()).thenReturn("MessageContent");
        Transport sendTransport = mock(Transport.class);

        Server server = new Server() {
            @Override
            boolean keepRunning() {
                return countCalls++ == 0;
            }

            @Override
            Reader createEmailListReader(String emailListFile) throws FileNotFoundException {
                return new StringReader("peter@biz.com\n");
            }

            @Override
            Session createReadingSession() {
                return null;
            }

            @Override
            Store createReadingStore(Session session) throws NoSuchProviderException {
                return readingStore;
            }

            @Override
            Session createWritingSession(Properties props) {
                return null;
            }

            @Override
            Transport createSendTransport(Session session1) throws NoSuchProviderException {
                return sendTransport;
            }
        };
        server.doSomething("smtphost", "pop3Host", "user", "password", "listFileName", "fromName", 0);
    }

}
