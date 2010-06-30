/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.email;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.constants.Constants;

/**
 * This class can be used to send emails based on parameters in the configuration file
 *
 * @author Eric Osisek
 */
public class Emailer
{

	/**
	 * A reference to the logger for this class
	 */
	static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

	/**
	 * The SMTP configuration from the database
	 */
	private EmailConfig emailConfig = null;

	/**
	 * Configures the Emailer to use the specified SMTP server
	 *
	 * @param config An EmailConfig Object which points to the SMTP server to use
	 */
	public void configure(EmailConfig config)
	{
		this.emailConfig = config;
	}

	/**
	 * Returns true iff the mailer has been configured.  This can be used to check
	 * whether or not the configuration was found in the database when the Emailer
	 * was constructed.  If this returns false, the configure method must be
	 * called before any emails can be sent.
	 *
	 * @return true iff the Emailer has been configured.
	 */
	public boolean isConfigured()
	{
		return emailConfig != null;
	}

	/**
	 * Sends an email using the configuration taken from the database or setup manually
	 * using the configure method.  The configure method takes priority over the configuration
	 * from the database.
	 *
	 * @param to The email address the message will be sent to
	 * @param subject The subject of the email
	 * @param messageBody The body (content) of the email
	 * @return true if the email was sent successfully, false otherwise
	 */
	public boolean sendEmail(String to, String subject, String messageBody)
	{
		// Confirm that the Emailer has been configured, either automatically by the constructor
		// or manually by the calling code.
		if(!isConfigured())
		{
			log.error("Cannot send email because the Emailer has not been configured correctly.  Either we could not find any emailconfig entries in the database or the configure method was called with incorrect values.");
			return false;
		}

		try
        {
			Message msg;

			Properties props = System.getProperties();
			props.put("mail.host", emailConfig.getEmailServerAddress());
			if(emailConfig.getPassword() != null)
			{
				props.put("mail.smtp.auth", "true");

				Authenticator auth = new SMTPAuthenticator(emailConfig.getFromAddress().substring(0, emailConfig.getFromAddress().indexOf('@')), emailConfig.getPassword());
				Session session = Session.getDefaultInstance(props, auth);

				msg = new MimeMessage(session);
			}
			else
				msg = new MimeMessage(Session.getInstance(props, null));

	        msg.setSubject(subject);
	        msg.setSentDate(new Date());

	        msg.setFrom(InternetAddress.parse(emailConfig.getFromAddress(), false)[0]);
	        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
	        msg.setText(messageBody);

	        Transport.send(msg);

	        return true;
        }
        catch(MessagingException e)
        {
        	log.error("An MessagingException occurred while trying to send the email.", e);
        	return false;
        }
        catch(Exception e)
        {
        	log.error("An error occurred while trying to send the email.", e);
        	return false;
        }
	}

    public boolean sendEmail(String to, String subject,String messageBody,String attachedFile)
    {
        try
        {
            Message msg;

            Properties props = System.getProperties();
            props.put("mail.host", emailConfig.getEmailServerAddress());
            if(emailConfig.getPassword() != null)
            {
                props.put("mail.smtp.auth", "true");

                Authenticator auth = new SMTPAuthenticator(emailConfig.getFromAddress().substring(0, emailConfig.getFromAddress().indexOf('@')), emailConfig.getPassword());
                Session session = Session.getDefaultInstance(props, auth);

                msg = new MimeMessage(session);
            }
            else
                msg = new MimeMessage(Session.getInstance(props, null));

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            msg.setFrom(InternetAddress.parse(emailConfig.getFromAddress(), false)[0]);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(messageBody);

            // create the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();

            // attach the file to the message
            FileDataSource fds = new FileDataSource(attachedFile);
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(fds.getName());

            // create the Multipart and add its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);

            // add the Multipart to the message
            msg.setContent(mp);
            Transport.send(msg);

	        return true;
        }
        catch(MessagingException e)
        {
        	log.error("An MessagingException occurred while trying to send the email.", e);         
        	return false;
        }
       
    }
	/**
	 * SimpleAuthenticator is used to do simple authentication
	 * when the SMTP server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator
	{
		private String username;
		private String password;

		public SMTPAuthenticator(String username, String password)
		{
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(username, password);
		}
	}
}
