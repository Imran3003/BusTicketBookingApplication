package com.tn76.BusTicketBooking.movieTicket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TicketAvailabilityMonitor {

    public static void main(String[] args) {
        String url = "https://www.redbus.in/bus-tickets/bangalore-to-chennai?fromCityName=Bangalore&fromCityId=122&toCityName=Chennai&toCityId=123&onward=09-Aug-2023&busType=Any&srcCountry=IND&destCountry=IND";

        try {
            Document doc = Jsoup.connect(url).get();

            System.out.println("doc = " + doc);
            // Replace the selector with the actual CSS selector for the availability status element
            Elements availabilityElements = doc.select(".your-selector-for-availability");

            System.out.println("availabilityElements = " + availabilityElements);
            if (!availabilityElements.isEmpty()) {
                System.out.println("Tickets are available!");
            } else {
                System.out.println("Tickets are not available.");
            }
        } catch (IOException e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
    }
}
