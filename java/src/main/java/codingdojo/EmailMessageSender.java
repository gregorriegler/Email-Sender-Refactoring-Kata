package codingdojo;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

public class EmailMessageSender implements MessageSender {
    public EmailMessageSender() {
    }

    @Override
    public void sendMessage(Message newMessage, Session session, InternetAddress[] toList, String smtpHost, String user, String password) throws MessagingException {
        Transport transport = session.getTransport(Server.SMTP_MAIL);
        transport.connect(smtpHost, user, password);
        transport.sendMessage(newMessage, toList);
    }
}