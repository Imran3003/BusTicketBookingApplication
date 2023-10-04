package com.tn76.BusTicketBooking.otp;

/**
 * OtpGenerator.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.otp
 * @created Sep 14, 2023
 */

import java.util.Random;

public class OTPGenerator {
    public static String generateOTP() {
        // Define the length of the OTP
        int otpLength = 4;

        // Create a StringBuilder to store the OTP
        StringBuilder otp = new StringBuilder();

        // Create an instance of Random class
        Random random = new Random();

        // Generate random digits and append them to the OTP
        for (int i = 0; i < otpLength; i++) {
            int digit = random.nextInt(10); // Generates a random digit between 0 and 9
            otp.append(digit);
        }

        return otp.toString();
    }

    public static void main(String[] args) {
        String otp = generateOTP();
        System.out.println("Generated OTP: " + otp);
    }

}
