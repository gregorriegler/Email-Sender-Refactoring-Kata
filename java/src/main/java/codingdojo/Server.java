package codingdojo;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;


public class Server {
    private static final String INBOX = "INBOX", POP_MAIL = "pop3",
        SMTP_MAIL = "smtp";
    private boolean debugOn = false;
    private String _smtpHost = null, _pop3Host = null, _user = null,
        _password = null, _listFile = null, _fromName = null;
    private InternetAddress[] toList = null;

    /**
     * main() is used to start an instance of the Server
     */
    public static void main(String args[]) throws Exception {
        // check usage
        //
        if (args.length < 6) {
            System.err.println("Usage: java Server SMTPHost POP3Host user password EmailListFile CheckPeriodFromName");
            System.exit(1);
        }

        // Assign command line arguments to meaningful variable names
        //
        String smtpHost = args[0], pop3Host = args[1], user = args[2], password = args[3], emailListFile = args[4], fromName = null;

        int checkPeriod = Integer.parseInt(args[5]);

        if (args.length > 6)
            fromName = args[6];

        // Process every "checkPeriod" minutes
        //
        Server server = new Server();
        server.doSomething(smtpHost, pop3Host, user, password, emailListFile, fromName, checkPeriod);
        return;
    }


    public void doSomething(String smtpHost, String pop3Host, String user, String password, String emailListFile, String fromName, int checkPeriod) throws IOException, MessagingException, InterruptedException {
        debugOn = false;

        while (keepRunning()) {
            if (debugOn)
                System.out.println(new Date() + "> " + "SESSION START");
            _smtpHost = smtpHost;
            _pop3Host = pop3Host;
            _user = user;
            _password = password;
            _listFile = emailListFile;

            if (fromName != null)
                _fromName = fromName;

            // Read in email list file into java.util.Vector
            //
            Vector vList = new Vector(10);
            BufferedReader listFile = new BufferedReader(createEmailListReader(emailListFile));
            String line = null;
            while ((line = listFile.readLine()) != null) {
                vList.addElement(new InternetAddress(line));
            }
            listFile.close();
            if (debugOn)
                System.out.println(new Date() + "> " + "Found " + vList.size() + " email ids in list");

            toList = new InternetAddress[vList.size()];
            vList.copyInto(toList);
            vList = null;

            //
            // Get individual emails and broadcast them to all email ids
            //

            // Get a Session object
            //
            Session session = createReadingSession();
            session.setDebug(debugOn);

            // Connect to host
            //
            Store store = session.getStore(Server.POP_MAIL);
            store.connect(pop3Host, -1, _user, _password);

            // Open the default folder
            //
            Folder folder = store.getDefaultFolder();
            if (folder == null)
                throw new NullPointerException("No default mail folder");

            folder = folder.getFolder(Server.INBOX);
            if (folder == null)
                throw new NullPointerException("Unable to get folder: " + folder);

            boolean done = false;
            // Get message count
            //
            folder.open(Folder.READ_WRITE);
            int totalMessages = folder.getMessageCount();
            if (totalMessages == 0) {
                if (debugOn)
                    System.out.println(new Date() + "> " + folder + " is empty");
                folder.close(false);
                store.close();
                done = keepRunning();
            }

            if (!done) {
                // Get attributes & flags for all messages
                //
                Message[] messages = folder.getMessages();
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(FetchProfile.Item.FLAGS);
                fp.add("X-Mailer");
                folder.fetch(messages, fp);

                // Process each message
                //
                for (int i = 0; i < messages.length; i++) {
                    if (!messages[i].isSet(Flags.Flag.SEEN)) {
                        Message message = messages[i];
                        String replyTo = _user, subject, xMailer, messageText;
                        Date sentDate;
                        int size;
                        Address[] a = null;

                        // Get Headers (from, to, subject, date, etc.)
                        //
                        if ((a = message.getFrom()) != null)
                            replyTo = a[0].toString();

                        subject = message.getSubject();
                        sentDate = message.getSentDate();
                        size = message.getSize();
                        String[] hdrs = message.getHeader("X-Mailer");
                        if (hdrs != null)
                            xMailer = hdrs[0];
                        String from = _user;

                        // Send message
                        //
                        // create some properties and get the default Session
                        //
                        Properties props = new Properties();
                        props.put("mail.smtp.host", _smtpHost);
                        Session session1 = createWritingSession(props);

                        // create a message
                        //
                        Address replyToList[] = {new InternetAddress(replyTo)};
                        Message newMessage = new MimeMessage(session1);
                        if (_fromName != null)
                            newMessage.setFrom(new InternetAddress(from, _fromName
                                + " on behalf of " + replyTo));
                        else
                            newMessage.setFrom(new InternetAddress(from));
                        newMessage.setReplyTo(replyToList);
                        newMessage.setRecipients(Message.RecipientType.BCC, toList);
                        newMessage.setSubject(subject);
                        newMessage.setSentDate(sentDate);

                        // Set message contents
                        //
                        Object content = message.getContent();
                        String debugText = "Subject: " + subject + ", Sent date: " + sentDate;
                        if (content instanceof Multipart) {
                            if (debugOn)
                                System.out.println(new Date() + "> " + "Sending Multipart message (" + debugText + ")");
                            newMessage.setContent((Multipart) message.getContent());
                        } else {
                            if (debugOn)
                                System.out.println(new Date() + "> " + "Sending Text message (" + debugText + ")");
                            newMessage.setText((String) content);
                        }


                        // Send newMessage
                        //
                        Transport transport = session1.getTransport(Server.SMTP_MAIL);
                        transport.connect(_smtpHost, _user, _password);
                        transport.sendMessage(newMessage, toList);
                    }
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                }

                folder.close(true);
                store.close();
            }
            if (debugOn)
                System.out.println(new Date() + "> " + "SESSION END (Going to sleep for " + checkPeriod
                    + " minutes)");
            Thread.sleep(checkPeriod * 1000 * 60);
        }
    }

    /*test*/ boolean keepRunning() {
        return true;
    }

    /*test*/ Reader createEmailListReader(String emailListFile) throws FileNotFoundException {
        return new FileReader(emailListFile);
    }

    /*test*/ Session createReadingSession() {
        Properties sysProperties = System.getProperties();
        return Session.getDefaultInstance(sysProperties, null);
    }

    /*test*/ Session createWritingSession(Properties props) {
        return Session.getDefaultInstance(props, null);
    }

}
