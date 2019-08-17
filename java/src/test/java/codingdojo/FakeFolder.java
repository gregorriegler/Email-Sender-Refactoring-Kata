package codingdojo;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class FakeFolder extends Folder {

    private final Message[] messages;

    public FakeFolder(Message[] messages) {
        super(FakeStore.create(null));
        this.messages = messages;
    }

    @Override
    public synchronized Message[] getMessages() throws MessagingException {
        return messages;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public Folder getParent() throws MessagingException {
        return null;
    }

    @Override
    public boolean exists() throws MessagingException {
        return false;
    }

    @Override
    public Folder[] list(String pattern) throws MessagingException {
        return new Folder[0];
    }

    @Override
    public char getSeparator() throws MessagingException {
        return 0;
    }

    @Override
    public int getType() throws MessagingException {
        return 0;
    }

    @Override
    public boolean create(int type) throws MessagingException {
        return false;
    }

    @Override
    public boolean hasNewMessages() throws MessagingException {
        return false;
    }

    @Override
    public Folder getFolder(String name) throws MessagingException {
        return this;
    }

    @Override
    public boolean delete(boolean recurse) throws MessagingException {
        return false;
    }

    @Override
    public boolean renameTo(Folder f) throws MessagingException {
        return false;
    }

    @Override
    public void open(int mode) throws MessagingException {

    }

    @Override
    public void close(boolean expunge) throws MessagingException {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Flags getPermanentFlags() {
        return null;
    }

    @Override
    public int getMessageCount() throws MessagingException {
        return messages.length;
    }

    @Override
    public Message getMessage(int msgnum) throws MessagingException {
        return null;
    }

    @Override
    public void appendMessages(Message[] msgs) throws MessagingException {

    }

    @Override
    public Message[] expunge() throws MessagingException {
        return new Message[0];
    }
}
