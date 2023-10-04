package com.tn76.BusTicketBooking.Email;

/**
 * EmailSender.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.example.testingspringboot.Email
 * @created Aug 22, 2023
 */

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class EmailSender
{
    public static void sendEmail(String recieverEmail, String subject, List<String> texts) {
        // Sender's email credentials
        String senderEmail = "imranmohamed783@gmail.com";
        String senderPassword = "tfyfixdbquclfmks";

        // Recipient's email address
//        String recipientEmail = "kadeepi820@gmail.com";
        // Setup properties for the Gmail SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a Session object with authentication
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

        try {
            // Create a new MimeMessage
            Message message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recieverEmail));

            // Set the subject and content
            message.setSubject(subject);

            StringBuilder content = new StringBuilder();

            for (String text : texts) {
                content.append(text).append("\n");
            }
            message.setText(content.toString());

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

