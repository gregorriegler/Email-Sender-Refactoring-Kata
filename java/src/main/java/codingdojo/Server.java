package codingdojo;

import javax.mail.internet.InternetAddress;


public class Server {
	protected static final String INBOX = "INBOX", POP_MAIL = "pop3",
			SMTP_MAIL = "smtp";
	protected boolean debugOn = false;
	protected String _smtpHost = null, _pop3Host = null, _user = null,
			_password = null, _listFile = null, _fromName = null;
	protected InternetAddress[] toList = null;

	/**
	 * main() is used to start an instance of the Server
	 */
	public static void main(String... args) throws Exception {
		new ActualMailServer(new EmailMessageSender()).something(new FileEmailListReader(), new SessionStoreProvider(), args);
		return;
	}


}
