package com.tn76.BusTicketBooking.samples;

import com.atm.scanAndcash.impl.ScanAndCashImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.tn76.BusTicketBooking.entity.Bookings;
import com.tn76.BusTicketBooking.entity.Bus;
import com.tn76.BusTicketBooking.repo.BusRepo;
import com.tn76.BusTicketBooking.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

@RestController
public class YourController {
    // ...
    @Autowired
    BusRepo busRepo;
    private static final int TICKET_PRICE = 500;

    Map<String,String> bookingTickets = new HashMap<>();
    @RequestMapping("/default")
    public void def() {
        System.out.println("2Time = ");
        Bus bus = storeDefault();
//
        System.out.println("Inside def bus = " + bus);

    }

    int busNO = 0;
    @RequestMapping("/sample")
    public ModelAndView home()
    {
        return new ModelAndView("sample");
    }

//    @PostMapping("/submitMapData")
//    public ModelAndView submitMapData(@RequestBody String json, Model model) throws IOException, WriterException {
//        // Handle the received map data
//        //
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String,String> map = objectMapper.readValue(json, Map.class);
//
//        System.out.println("map = " + map);
//
//        bookingTickets = map;
//        busNO = Integer.parseInt( map.get("busNo"));
//        map.remove("busNo");
//        System.out.println("map = " + map);
//        System.out.println("busNO = " + busNO);
//
//        int amount = TICKET_PRICE * map.size();
//        System.out.println("amount = " + amount);
//
//        String qrCodeData = ScanAndCashImpl.generateQRCode(amount);
//
//        model.addAttribute("qrCodeData", qrCodeData);
//
//        model.addAttribute("amount",amount);
//
//        return new ModelAndView("qrCodePage", model.asMap());
//        //return "redirect:/success-page"; // Redirect to a success page after processing the data
//    }
//
//    @RequestMapping("/successfullyTicketBooked")
//    public ModelAndView ticketBooked(Model model)
//    {
//        System.out.println("inside sucb");
//        String tickets = "";
//        List<Bus> allBusses = (List<Bus>) busRepo.findAll();
//
//        for (Bus bus : allBusses) {
//            System.out.println("bus.getBusNo() = " + bus.getBusNo());
//            System.out.println("busNO = " + busNO);
//            if (bus.getBusNo() == busNO)
//            {
//                System.out.println("inside matched bus = " );
//                int seatAvail = bus.getAvailableSeat();
//                System.out.println("seatAvail = " + seatAvail);
//                List<Bookings> bookings = bus.getBookings();
//                for (Bookings booking : bookings) {
//                    System.out.println("bookingTickets = " + bookingTickets);
//                    if (bookingTickets.containsKey(String.valueOf( booking.getSeatNo())))
//                    {
//                        System.out.println("seatAvail = " + seatAvail);
//                        booking.setBookingStatus(Constants.BookingStatus.BOOKED);
//                        seatAvail --;
//                    }
//                }
//                bus.setAvailableSeat(seatAvail);
//            }
//        }
//        System.out.println("tickets = " + tickets);
//        System.out.println("allBusses = " + allBusses);
//        System.out.println("allBusses = " + allBusses.get(0).getAvailableSeat());
//        System.out.println("allBusses = " + allBusses.get(1).getAvailableSeat());
//        busRepo.saveAll(allBusses);
//
//        model.addAttribute("tickets",tickets);
//        return new ModelAndView("succefullyPaid");
//    }
    // ...

    public static Bus storeDefault()
    {
        System.out.println("inside storeDefault");
        Bus bus = new Bus();
        bus.setBusName("Hail Trip");
        bus.setBusNo(1234);
        bus.setStartingFrom("Tenkasi");
        bus.setBusStartingTime("09-Jun-2023 : 7.00 PM");
        bus.setBusReachingTime("10-Jun-2023 : 6.30 AM");
        bus.setDestination("Chennai");

        List<Bookings> bookingsList = new ArrayList<>();
        for (int i = 1 ; i <= 50 ; i++)
        {
            Bookings bookings = new Bookings();
            bookings.setSeatNo(i);
            bookings.setBookingStatus(Constants.BookingStatus.AVAILABLE);
            bookingsList.add(bookings);
        }
        bus.setBookings(bookingsList);
        System.out.println("bus = " + bus);
        return bus;
    }
}
