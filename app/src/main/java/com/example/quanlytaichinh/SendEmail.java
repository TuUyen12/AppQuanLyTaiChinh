package com.example.quanlytaichinh;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {

    // Phương thức gửi OTP qua email
    public static void sendOTP(String recipientEmail, String otp) {
        String senderEmail = "carogame2024@gmail.com";  // Thay bằng email của bạn
        String senderPassword = "mows abux zpeu jnhu"; // Thay bằng mật khẩu email của bạn

        // Cấu hình kết nối SMTP của Gmail
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Tạo một session với các thuộc tính và thông tin đăng nhập
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Trả về thông tin đăng nhập
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Tạo đối tượng MimeMessage để gửi email
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp);

            // Gửi email
            Transport.send(message);
            System.out.println("OTP sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Error while sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức tạo OTP ngẫu nhiên (6 chữ số)
    public static String generateOTP() {
        int otp = (int)(Math.random() * 900000) + 100000; // Tạo OTP 6 chữ số
        return String.valueOf(otp);
    }
}
