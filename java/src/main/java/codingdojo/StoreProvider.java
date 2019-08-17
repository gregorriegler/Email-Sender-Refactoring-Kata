package codingdojo;

import javax.mail.NoSuchProviderException;
import javax.mail.Store;

public interface StoreProvider {
    Store getStore(boolean debugOn) throws NoSuchProviderException;
}
