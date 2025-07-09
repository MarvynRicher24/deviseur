package fr.musclegarage.deviseur.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailUtils {
    private static final Session session;

    static {
        try (InputStream in = new FileInputStream("src/main/resources/config.properties")) {
            Properties cfg = new Properties();
            cfg.load(in);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", cfg.getProperty("smtp.host"));
            props.put("mail.smtp.port", cfg.getProperty("smtp.port"));

            final String user = cfg.getProperty("smtp.user");
            final String pass = cfg.getProperty("smtp.pass");

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement config SMTP", e);
        }
    }

    public static void sendMailWithAttachment(
            String to, String subject, String body, byte[] pdfBytes, String filename) throws Exception {

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(session.getProperty("mail.smtp.user")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);

        // Corps du message
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "utf-8");

        // Pièce jointe
        MimeBodyPart filePart = new MimeBodyPart();
        DataSource ds = new ByteArrayDataSource(pdfBytes, "application/pdf");
        filePart.setDataHandler(new DataHandler(ds));
        filePart.setFileName(filename);

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(textPart);
        mp.addBodyPart(filePart);
        msg.setContent(mp);

        Transport.send(msg);
    }
}