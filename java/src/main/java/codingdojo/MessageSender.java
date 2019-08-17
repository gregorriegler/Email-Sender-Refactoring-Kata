package codingdojo;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

public interface MessageSender {
    void sendMessage(Message newMessage, Session session, InternetAddress[] toList, String smtpHost, String user, String password) throws MessagingException;
}
