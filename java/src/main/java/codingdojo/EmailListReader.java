package codingdojo;

import javax.mail.internet.AddressException;
import java.io.IOException;
import java.util.Vector;

public interface EmailListReader {
    Vector readEmailListFromFile(String emailListFile) throws IOException, AddressException;
}
