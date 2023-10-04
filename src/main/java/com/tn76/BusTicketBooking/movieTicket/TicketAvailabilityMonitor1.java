package com.tn76.BusTicketBooking.movieTicket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TicketAvailabilityMonitor1 {

    public static void main(String[] args) {
        String url = "https://www.redbus.in/bus-tickets/bangalore-to-chennai?fromCityName=Bangalore&fromCityId=122&toCityName=Chennai&toCityId=123&onward=09-Aug-2023&busType=Any&srcCountry=IND&destCountry=IND";

        try {
            String html = fetchHtmlContent(url);
            System.out.println("html = " + html);
            Document doc = Jsoup.parse(html);

            System.out.println("doc = " + doc);
            Elements availabilityElements = doc.select(".your-selector-for-availability");

            if (!availabilityElements.isEmpty()) {
                System.out.println("Tickets are available!");
            } else {
                System.out.println("Tickets are not available.");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String fetchHtmlContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }
}
