package codingdojo;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class SessionStoreProvider implements StoreProvider {
    public SessionStoreProvider() {
    }

    @Override
    public Store getStore(boolean debugOn) throws NoSuchProviderException {
        Properties sysProperties = System.getProperties();
        Session session = Session.getDefaultInstance(sysProperties, null);
        session.setDebug(debugOn);
        return session.getStore(Server.POP_MAIL);
    }
}