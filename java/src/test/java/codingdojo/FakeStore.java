package codingdojo;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.util.Properties;

public class FakeStore extends Store {

    private final FakeFolder folder;

    public static Store create(FakeFolder folder) {
        Session session = Session.getInstance(new Properties());
        URLName urlname = null;
        return new FakeStore(session, urlname, folder);
    }

    public FakeStore(Session session, URLName urlname, FakeFolder folder) {
        super(session, urlname);
        this.folder = folder;
    }

    @Override
    public Folder getDefaultFolder() throws MessagingException {
        return folder;
    }

    @Override
    public Folder getFolder(String s) throws MessagingException {
        return null;
    }

    @Override
    public Folder getFolder(URLName urlName) throws MessagingException {
        return null;
    }

    @Override
    public synchronized void connect(String host, int port, String user, String password) throws MessagingException {
    }
}
