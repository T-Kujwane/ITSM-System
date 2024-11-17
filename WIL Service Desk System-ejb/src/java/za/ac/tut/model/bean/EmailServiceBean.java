/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.tut.model.bean;

import java.util.Properties;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailServiceBean implements EmailService {

    private final int port = 587;  // Add this to properties if required
    private final String host = "smtp.gmail.com"; // Use this for Gmail
    private final String sender = "thandekabrad@gmail.com";// Fetch from properties
    private final Boolean mustAuthenticate = true;
    private final String username = this.sender;  // Fetch from properties
    private final String password = "pbtj obaj ywtg jvrn"; // Fetch from properties
    private final Boolean debug = true;
    private final String protocol = "SMTP";

    @Override
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.enable", false);
        props.put("mail.smtp.socketFactory.fallback", true);

        Authenticator authenticator = null;
        if (mustAuthenticate) {
            authenticator = new Authenticator() {
                private final PasswordAuthentication pa = new PasswordAuthentication(username, password);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }

        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setSentDate(new java.util.Date());
        message.setText(body);

        Transport.send(message);
    }

    /*private void bypassSSLCheck() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
        }
    }*/
}
