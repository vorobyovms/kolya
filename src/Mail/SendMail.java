package Mail;


import javax.mail.Authenticator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import static javax.xml.transform.OutputKeys.ENCODING;

public class SendMail implements Runnable{
    
    String subject;
    String content;
    String smtpServer = "smtp.gmail.com";
    String login;
    String password;
    String rec_email;
    
    Properties props;
    Session session;
    Authenticator authenticator;
    
    public SendMail(String login,String password,String subject,String content,String rec_email) {
        this.login = login;
        this.password = password;
        this.subject = subject;
        this.content = content;
        this.rec_email = rec_email;
    }

    @Override
    public void run() {
        props = new Properties();
        props.put("mail.smtp.starttls.enable" , "true" );
        //props.put("mail.smtp.ssl.enable", "true");
	props.put("mail.smtp.port", "587");
	props.put("mail.smtp.host", smtpServer);
	props.put("mail.smtp.auth", "true");
	props.put("mail.mime.charset", ENCODING);
        
        //Authentication, Session, MimeSession
	authenticator = new SMTP_Auth(login, password);
	session = Session.getDefaultInstance(props, authenticator);
	session.setDebug(true);
		        
	//Message
	Message msg = new MimeMessage(session);
   
        try {
            System.out.println("rec_email = '"+rec_email+"'");
            msg.setFrom(new InternetAddress(this.login));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(rec_email));
            msg.setSubject(subject);
            msg.setText(content);
            Transport.send(msg);

        } catch (MessagingException ex) {
            Logger.getLogger(SendMail.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
        }

    }
}
