package codingdojo;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class FileEmailListReader implements EmailListReader {
    @Override
    public Vector readEmailListFromFile(String emailListFile) throws IOException, AddressException {
        Vector vList = new Vector(10);
        BufferedReader listFile = new BufferedReader(new FileReader(emailListFile));
        String line = null;
        while ((line = listFile.readLine()) != null) {
            vList.addElement(new InternetAddress(line));
        }
        listFile.close();
        return vList;
    }
}