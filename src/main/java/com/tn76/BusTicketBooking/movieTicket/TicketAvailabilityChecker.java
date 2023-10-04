package com.tn76.BusTicketBooking.movieTicket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class TicketAvailabilityChecker {

    public static void main(String[] args) {
//        final String targetUrl = "https://www.redbus.in/bus-tickets/bangalore-to-chennai?fromCityName=Bangalore&fromCityId=122&toCityName=Chennai&toCityId=123&onward=09-Aug-2023&busType=Any&srcCountry=IND&destCountry=IND"; // Replace with the actual URL
        final String targetUrl = "https://www.redbus.in/bus-tickets/bangalore-to-chennai?fromCityName=Bangalore&fromCityId=122&toCityName=Chennai&toCityId=123&onward=15-Sep-2023&busType=Any&srcCountry=IND&destCountry=IND&routeId=14157307"; // Replace with the actual URL
        final int checkInterval = 5 * 1000; // 5 minutes in milliseconds

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Boolean b = checkAndNotify(targetUrl);
                if (b)
                {
                    System.out.println(" Ticket Available ... !!!");
                    timer.cancel();
                }
                else
                    System.out.println(" Ticket Not Available ... !!!");
            }
        }, 0, checkInterval);
    }

    private static Boolean checkAndNotify(String targetUrl) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    System.out.println("line = " + line);
                    content.append(line);
                }
//                content.append("Tickets available");
            } finally {
                connection.disconnect();
            }

//            System.out.println("content = " + content);
            return  (content.toString().contains("Tickets available")) ;
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return false;
        }
    }
}
