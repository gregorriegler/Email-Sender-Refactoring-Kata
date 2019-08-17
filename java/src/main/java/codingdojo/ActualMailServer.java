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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class ActualMailServer {

    private final MessageSender messageSender;

    public ActualMailServer(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    void something(EmailListReader emailListReader, StoreProvider storeProvider, String... args) throws IOException, MessagingException, InterruptedException {
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
        server.debugOn = false;

        while (true) {
            if (server.debugOn)
                System.out.println(new Date() + "> " + "SESSION START");
            inLoop(emailListReader, storeProvider, smtpHost, pop3Host, user, password, emailListFile, fromName, server);
            if (server.debugOn)
                System.out.println(new Date() + "> " + "SESSION END (Going to sleep for " + checkPeriod
                    + " minutes)");
            Thread.sleep(checkPeriod * 1000 * 60);
        }
    }

    public void inLoop(EmailListReader emailListReader, StoreProvider storeProvider, String smtpHost, String pop3Host, String user, String password, String emailListFile, String fromName, Server server) throws IOException, MessagingException {
        server._smtpHost = smtpHost;
        server._pop3Host = pop3Host;
        server._user = user;
        server._password = password;
        server._listFile = emailListFile;

        if (fromName != null)
            server._fromName = fromName;

        // Read in email list file into java.util.Vector
        //
        Vector vList = emailListReader.readEmailListFromFile(emailListFile);
        if (server.debugOn)
            System.out.println(new Date() + "> " + "Found " + vList.size() + " email ids in list");

        server.toList = new InternetAddress[vList.size()];
        vList.copyInto(server.toList);
        vList = null;

        //
        // Get individual emails and broadcast them to all email ids
        //

        // Get a Store
        Store store = storeProvider.getStore(server.debugOn);

        // Connect to host
        store.connect(pop3Host, -1, server._user, server._password);

        // Open the default folder
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
            if (server.debugOn)
                System.out.println(new Date() + "> " + folder + " is empty");
            folder.close(false);
            store.close();
            done = true;
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
                    String replyTo = server._user, subject, xMailer, messageText;
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
                    String from = server._user;

                    // Send message
                    //
                    // create some properties and get the default Session
                    //
                    Properties props = new Properties();
                    props.put("mail.smtp.host", server._smtpHost);
                    Session session1 = Session.getDefaultInstance(props, null);

                    // create a message
                    //
                    Address replyToList[] = {new InternetAddress(replyTo)};
                    Message newMessage = new MimeMessage(session1);
                    if (server._fromName != null)
                        newMessage.setFrom(new InternetAddress(from, server._fromName
                            + " on behalf of " + replyTo));
                    else
                        newMessage.setFrom(new InternetAddress(from));
                    newMessage.setReplyTo(replyToList);
                    newMessage.setRecipients(Message.RecipientType.BCC, server.toList);
                    newMessage.setSubject(subject);
                    newMessage.setSentDate(sentDate);

                    // Set message contents
                    //
                    Object content = message.getContent();
                    String debugText = "Subject: " + subject + ", Sent date: " + sentDate;
                    if (content instanceof Multipart) {
                        if (server.debugOn)
                            System.out.println(new Date() + "> " + "Sending Multipart message (" + debugText + ")");
                        newMessage.setContent((Multipart) message.getContent());
                    } else {
                        if (server.debugOn)
                            System.out.println(new Date() + "> " + "Sending Text message (" + debugText + ")");
                        newMessage.setText((String) content);
                    }


                    // Send newMessage
                    //
                    messageSender.sendMessage(newMessage, session1, server.toList, server._smtpHost, server._user, server._password);
                }
                messages[i].setFlag(Flags.Flag.DELETED, true);
            }

            folder.close(true);
            store.close();
        }
    }

}