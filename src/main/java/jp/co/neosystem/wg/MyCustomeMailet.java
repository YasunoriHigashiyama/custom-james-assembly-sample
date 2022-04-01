package jp.co.neosystem.wg;

import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;

public class MyCustomeMailet extends GenericMailet {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomeMailet.class);

	@Override
	public void service(Mail mail) throws MessagingException {
		var sender = mail.getMaybeSender();
		LOGGER.info("sender {}", sender.toString());

		var message = mail.getMessage();
		try {
			Object content = message.getContent();
			LOGGER.info("message: {}", content.toString());
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return;
	}
}
