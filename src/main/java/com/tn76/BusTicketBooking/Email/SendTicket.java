package com.tn76.BusTicketBooking.Email;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

/**
 * EmailSender.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.example.testingspringboot.Email
 * @created Aug 22, 2023
 */

public class SendTicket {
    public static void sendTicketToEmail(String recieverEmail, String pdfFilePath) {
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

            // Create the PDF file
//            createPDF(pdfFilePath);

            // Create a new MimeMessage
            Message message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recieverEmail));

            // Set the subject and content
            message.setSubject("Ticket");
            message.setText("Your Ticket");

            // Create the attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(pdfFilePath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("BusTicket.pdf");

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(attachmentPart);

            // Set the message content to be multipart (including the attachment)
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void createPDF(String filePath) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a PDPageContentStream to write content to the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Add some content to the PDF (you can customize this part)
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("<------------Your Ticket--------------->");
            contentStream.showText("Bus No     : ");
            contentStream.showText("Bus Name   : ");
            contentStream.showText("Seat No : ");
            contentStream.showText("Bus No : ");
            contentStream.showText("Bus No : ");
            contentStream.endText();

            // Close the content stream
            contentStream.close();

            // Save the PDF to the specified file
            document.save(filePath);
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
