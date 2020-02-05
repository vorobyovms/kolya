package Mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTP_Auth
  extends Authenticator
{
  String user;
  String password;
  PasswordAuthentication authentication;
  
  SMTP_Auth(String user, String password)
  {
    this.user = user;
    this.password = password;
    this.authentication = new PasswordAuthentication(user, password);
  }
  
  @Override
  public PasswordAuthentication getPasswordAuthentication()
  {
    return this.authentication;
  }
}
