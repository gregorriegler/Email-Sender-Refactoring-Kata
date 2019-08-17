package codingdojo;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StubMessageSender implements MessageSender {

    public final List<Sent> sents = new ArrayList<>();

    @Override
    public void sendMessage(Message newMessage, Session session, InternetAddress[] toList, String smtpHost, String user, String password) throws MessagingException {
        this.sents.add(new Sent(newMessage, session, toList, smtpHost, user, password));
    }

    public static class Sent {
        public final Message newMessage;
        public final Session session;
        public final InternetAddress[] toList;
        public final String smtpHost;
        public final String user;
        public final String password;

        public Sent(Message newMessage, Session session, InternetAddress[] toList, String smtpHost, String user, String password) {
            this.newMessage = newMessage;
            this.session = session;
            this.toList = toList;
            this.smtpHost = smtpHost;
            this.user = user;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Sent sent = (Sent) o;
            return Objects.equals(newMessage, sent.newMessage) &&
                Objects.equals(session, sent.session) &&
                Arrays.equals(toList, sent.toList) &&
                Objects.equals(smtpHost, sent.smtpHost) &&
                Objects.equals(user, sent.user) &&
                Objects.equals(password, sent.password);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(newMessage, session, smtpHost, user, password);
            result = 31 * result + Arrays.hashCode(toList);
            return result;
        }

        @Override
        public String toString() {
            return "Sent{" +
                "newMessage=" + newMessage +
                ", session=" + session +
                ", toList=" + Arrays.toString(toList) +
                ", smtpHost='" + smtpHost + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
        }
    }
}
