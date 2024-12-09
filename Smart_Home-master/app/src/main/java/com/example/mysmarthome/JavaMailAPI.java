package com.example.mysmarthome;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class JavaMailAPI {
    final String username = "yourEmail@gmail.com"; // Your Gmail username
    final String password = "yourPassword"; // Your Gmail password

    public JavaMailAPI() {
    }

    public void sendmsg(String mail, String sub, String msg){
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setStartTLSEnabled(true);
        try {
            email.setFrom("yourEmail@gmail.com");
            email.setSubject("TestMail");
            email.setMsg("This is a test mail ... :-)");
            email.addTo("recipientEmail@gmail.com");
            email.send();
            System.out.println("Email sent successfully!");
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

}