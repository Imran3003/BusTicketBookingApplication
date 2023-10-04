package com.tn76.BusTicketBooking.controller;

import com.atm.scanAndcash.impl.ScanAndCashImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.tn76.BusTicketBooking.Email.SendTicket;
import com.tn76.BusTicketBooking.entity.Bookings;
import com.tn76.BusTicketBooking.entity.Bus;
import com.tn76.BusTicketBooking.entity.PassengerDetails;
import com.tn76.BusTicketBooking.entity.user.BookingTicketList;
import com.tn76.BusTicketBooking.entity.user.MyTickets;
import com.tn76.BusTicketBooking.entity.user.UserDetails;
import com.tn76.BusTicketBooking.repo.BusRepo;
import com.tn76.BusTicketBooking.repo.PassengerRepo;
import com.tn76.BusTicketBooking.repo.UserRepo;
import com.tn76.BusTicketBooking.utils.Constants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LogInPageController.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module com.tn76.BusTicketBooking.controller
 * @created Jun 09, 2023
 */
@Controller
public class MyController {
    @Autowired
    AddNewBus addNewBus;
    @Autowired
    UserRepo userRepo;

    @Autowired
    BusRepo busRepo;

    @Autowired
    PassengerRepo passengerRepo;

    String userName = "";
    String email = "sample@gmail.com";

    int busNO = 0;

    private static final int TICKET_PRICE = 500;

    Map<String, String> bookingTickets = new HashMap<>();

    @RequestMapping("/")
    public ModelAndView bookingHome() {
        return new ModelAndView("bookingHome");
    }

    @RequestMapping("/showBusses")
    public ModelAndView showBusses(Model model) {

        List<Bus> allBuses = (List<Bus>) busRepo.findAll();
        model.addAttribute("busses", allBuses);

        return new ModelAndView("Busses.html");
    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, Model model) {
        ModelAndView modelAndView = new ModelAndView();

        StringBuilder buttonHtml = new StringBuilder();

        List<UserDetails> admins = (List<UserDetails>) userRepo.findAll();

        boolean userValid = false;

        for (UserDetails user : admins) {
            if (user.getUserName().equals(username)) {
                if (user.getPassword().equals(password)) {
                    userValid = true;
                    email = user.getEmail();
                }
            }
        }
        if (!userValid) {
            model.addAttribute("errorMessage", "Please enter valid Username or password.");
            modelAndView.setViewName("bookingHome.html");
            return modelAndView;
        }

        List<Bus> allBuses = (List<Bus>) busRepo.findAll();
        userName = username;
        model.addAttribute("busses", allBuses);
        model.addAttribute("username", username);
        if (username.equals("imran") && password.equals("imran")) {
            buttonHtml.append("<a class=\"btn1\"\" href=\"/createNewBusPage\">CreateBus</a>"); //createNewBus
            buttonHtml.append("<a class=\"btn1 delete-button\" href=\"/deleteBusPage\">Delete Bus</a>"); // Delete Bus
            model.addAttribute("buttonHtml", buttonHtml);

        }
        modelAndView.setViewName("Busses.html");

        return modelAndView;
    }

    @RequestMapping("/selectedBus")
    public ModelAndView bookSelectedBus(@RequestParam("busNo") Integer busNo, Model model) {
        List<Bus> busses = (List<Bus>) busRepo.findAll();

        ModelAndView modelAndView = new ModelAndView();

        StringBuilder buttonHtml = new StringBuilder();

        StringBuilder buttonHtml1 = new StringBuilder();

        for (Bus buss : busses) {
            if (Objects.equals(buss.getBusNo(), busNo)) {
                List<Bookings> seats = buss.getBookings();
                for (int i = 0; i < seats.size(); i++) {
                    Bookings seat = seats.get(i);
                    int seatVal = i + 1;
                    if (i < 20) {
                        if (seat.getBookingStatus().equals(Constants.BookingStatus.BOOKED))
                            buttonHtml.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} style=\"background-color: red;\"disabled = true value = " + seatVal + " \" >").append(seatVal).append("</button>\n");
                        else
                            buttonHtml.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} \" value = " + seatVal + " \" >").append(seatVal).append("</button>\n");
                    } else {
                        if (seat.getBookingStatus().equals(Constants.BookingStatus.BOOKED))
                            buttonHtml1.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle} style=\"background-color: red;\"disabled = true value = " + seatVal + " \" >").append(seatVal).append("</button>\n");
                        else
                            buttonHtml1.append("<button class=\"myButton\" onclick=\"changeColor(this)\" data-value = ").append(seatVal).append("th:style=\"${buttonStyle}\" value = " + seatVal + " \" >").append(seatVal).append("</button>\n");
                    }
                }
                model.addAttribute("busno", busNo);
                model.addAttribute("buttonHtml", buttonHtml.toString());
                model.addAttribute("buttonHtml1", buttonHtml1.toString());
            }
            modelAndView.setViewName("sample.html");
        }
        return modelAndView;
    }

    @PostMapping("/submitMapData")
    public ModelAndView submitMapData(@RequestBody String json, Model model) throws IOException, WriterException {
        // Handle the received map data
        //
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(json, Map.class);

        bookingTickets = map;
        busNO = Integer.parseInt(map.get("busNo"));
        map.remove("busNo");

        int amount = TICKET_PRICE * map.size();

        String qrCodeData = ScanAndCashImpl.generateQRCode(amount);

        model.addAttribute("qrCodeData", qrCodeData);

        model.addAttribute("amount", amount);

        return new ModelAndView("qrCodePage", model.asMap());
        //return "redirect:/success-page"; // Redirect to a success page after processing the data
    }

    @RequestMapping("/successfullyTicketBooked")
    public ModelAndView ticketBooked(Model model) {
        String tickets = "";
        List<Bus> allBusses = (List<Bus>) busRepo.findAll();

        for (Bus bus : allBusses) {
            if (bus.getBusNo() == busNO) {
                createAndSendTicketAsPdf(bus);
                int seatAvail = bus.getAvailableSeat();
                List<Bookings> bookings = bus.getBookings();
                for (Bookings booking : bookings) {
                    if (bookingTickets.containsKey(String.valueOf(booking.getSeatNo()))) {
                        booking.setBookingStatus(Constants.BookingStatus.BOOKED);
                        tickets = tickets + booking.getSeatNo() + ",";
                        seatAvail--;
                    }
                }
                bus.setAvailableSeat(seatAvail);
            }
        }

        busRepo.saveAll(allBusses);
        savePassenger();
        String modifiedTickets = tickets.replaceAll(",\\s*$", "");
        model.addAttribute("tickets", modifiedTickets);
        return new ModelAndView("succefullyPaid");
    }

    private void savePassenger() {
        List<PassengerDetails> passengerDetailsList = new ArrayList<>();
        bookingTickets.forEach((key, value) ->
        {
            PassengerDetails passengerDetails = new PassengerDetails();
            passengerDetails.setName(userName);
            passengerDetails.setBusNo(busNO);
            passengerDetails.setSeatNo(Integer.parseInt(key));
            passengerDetailsList.add(passengerDetails);
        });
        passengerRepo.saveAll(passengerDetailsList);
    }

    private void createAndSendTicketAsPdf(Bus bus) {
        String busName = bus.getBusName();
        Integer busNo = bus.getBusNo();
        String startingFrom = bus.getStartingFrom();
        String destination = bus.getDestination();
        String busStartingTime = bus.getBusStartingTime();
        String user = userName;

        List<String> seats = new ArrayList<>();
        bookingTickets.forEach((seatNo, val) -> seats.add(seatNo));


        try {

            String yourSeats = "";
            for (int i = 0; i < seats.size(); i++) {
                String seat = seats.get(i);
                if (i == seats.size() - 1)
                    yourSeats = yourSeats + seat;
                else
                    yourSeats = yourSeats + seat + ",";
            }

            String pdfFilePath = "/home/admin/IMRAN/pdf/busTicket.pdf";

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A6);

            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Calculate the width and height of the page
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            float leftMargin = 50; // Adjust this value
            float topMargin = 50; // Adjust this value
            float contentWidth = 300;
            float contentHeight = 500;
            float rightMargin = pageWidth - leftMargin - contentWidth; // Adjust this value based on content width
            float bottomMargin = pageHeight - topMargin - contentHeight;

            // Calculate the available width and height within the margins
            float availableWidth = pageWidth - leftMargin * 2;
            float availableHeight = pageHeight - topMargin * 2;

            // Draw a border around the entire page
            contentStream.setLineWidth(1); // Set the border line width
            contentStream.setStrokingColor(Color.GRAY);
            contentStream.addRect(leftMargin, topMargin, availableWidth, availableHeight);
            contentStream.stroke();

            // Adjust the starting Y position to accommodate top margin
            float yPosition = pageHeight - topMargin;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Add an underline before the text "Your Ticket"
            float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Your Ticket") / 1000 * 12;
            contentStream.setLineWidth(1);
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.moveTo(leftMargin + 70, yPosition - 52);
            contentStream.lineTo(leftMargin + 70 + textWidth, yPosition - 52);
            contentStream.stroke();

            // Write text to the PDF
            contentStream.beginText();
            contentStream.newLineAtOffset(leftMargin + 70, yPosition - 50);
            contentStream.showText("Your Ticket");
            contentStream.setFont(PDType1Font.COURIER, 8);
            contentStream.newLineAtOffset(-40, -30); // Move to the next line
            contentStream.showText("User Name  : " + user);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Bus No     : " + busNo);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Bus Name   : " + busName);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("From       : " + startingFrom);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("To         : " + destination);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Time       : " + busStartingTime);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Your Seats : " + yourSeats);
            contentStream.endText();

            contentStream.close();

            document.save(pdfFilePath);
            document.close();

            SendTicket.sendTicketToEmail(email, pdfFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Create a new Bus

    @RequestMapping("/createNewBusPage")
    public ModelAndView createBusPage() {
        return new ModelAndView("createbus");
    }

    @RequestMapping("/createNewBus")
    public ModelAndView createNewBus(@RequestParam String busName, @RequestParam int busNum,
                                     @RequestParam String startingFrom, @RequestParam String destination,
                                     @RequestParam String startingTime, @RequestParam String reachingTime,
                                     @RequestParam int noOfSeats, Model model) {

        ModelAndView modelAndView = new ModelAndView();
        Bus bus = new Bus();
        bus.setBusName(busName);
        bus.setBusNo(busNum);
        bus.setStartingFrom(startingFrom);
        bus.setBusStartingTime(startingTime);
        bus.setDestination(destination);
        bus.setBusReachingTime(reachingTime);


        List<Bookings> bookingsList = new ArrayList<>();
        for (int i = 1; i <= noOfSeats; i++) {
            Bookings bookings = new Bookings();
            bookings.setSeatNo(i);
            bookings.setBookingStatus(Constants.BookingStatus.AVAILABLE);
            bookingsList.add(bookings);
        }
        bus.setBookings(bookingsList);
        bus.setAvailableSeat(noOfSeats);
        busRepo.save(bus);
        model.addAttribute("busNum", busNum);
        modelAndView.setViewName("successfullyCreateNewBus.html");
        return modelAndView;
    }

    //DeleteBus
    @RequestMapping("/deleteBusPage")
    public ModelAndView DeleteBusPage() {
        return new ModelAndView("deletebus");
    }


    @RequestMapping("/deleteBus")
    public ModelAndView confirmDelete(@RequestParam(name = "busNo") int busNo, Model model) {
        List<Bus> allBuses = (List<Bus>) busRepo.findAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("deletebus");
        for (Bus bus : allBuses) {
            if (bus.getBusNo() == busNo) {
                busRepo.delete(bus);
                model.addAttribute("message", "BUS " + "'" + busNo + "'" + " SuccessFully Deleted");
                return modelAndView;
            }
        }
        model.addAttribute("message", "Bus " + "'" + busNo + "'" + "Not Found");
        return modelAndView;
    }

    @RequestMapping("/showMyTickets")
    public ModelAndView showMyTickets(@RequestParam("userName") String  username, Model model)
//    public void showMyTickets(@RequestParam("busNo") Integer busNo)
    {
        List<MyTickets> myTicketList = new ArrayList<>();
        List<BookingTicketList> bookingTicketLists = new ArrayList<>();

        List<PassengerDetails> passengers = (List<PassengerDetails>) passengerRepo.findAll();

        for (PassengerDetails passenger : passengers) {
            if (passenger.getName().equals(userName))
            {
                MyTickets myTickets = new MyTickets();
                myTickets.setBusNo(passenger.getBusNo());
                myTickets.setSeatNo(passenger.getSeatNo());
                myTicketList.add(myTickets);
            }
        }

        Map<Integer, List<Integer>> groupedTickets = myTicketList.stream()
                .collect(Collectors.groupingBy(MyTickets::getBusNo,
                        Collectors.mapping(MyTickets::getSeatNo, Collectors.toList())));
        List<Bus> allBusses = (List<Bus>) busRepo.findAll();
        for (Bus bus : allBusses) {
            if (groupedTickets.containsKey(bus.getBusNo()))
            {
                List<Integer> tickets = groupedTickets.get(bus.getBusNo());
                for (int i = 0 ; i< tickets.size();i++)
                {
                    BookingTicketList bookingTicketList = new BookingTicketList();
                    bookingTicketList.setBusName(bus.getBusName());
                    bookingTicketList.setBusNo(bus.getBusNo());
                    bookingTicketList.setStartingFrom(bus.getStartingFrom());
                    bookingTicketList.setDestination(bus.getDestination());
                    bookingTicketList.setBusStartingTime(bus.getBusStartingTime());
                    bookingTicketList.setSeatNo(tickets.get(i));
                    bookingTicketLists.add(bookingTicketList);
                    bookingTicketList.setPname(userName);
                }
            }
        }
        model.addAttribute("busses",bookingTicketLists);

        return new ModelAndView("CancelPage.html");
    }

    @RequestMapping("/cancelTicket")
     public ModelAndView cancelTicket(@RequestParam("busNo") int busNum, @RequestParam("seatNo") int seatNo,@RequestParam("passengerName") String passengerName)    {
        List<Bus> all = (List<Bus>) busRepo.findAll();
        for (Bus bus : all) {
            int seatAvail = bus.getAvailableSeat();
            if (bus.getBusNo() == busNum)
            {
                List<Bookings> bookings = bus.getBookings();
                for (Bookings booking : bookings) {
                    if (seatNo == booking.getSeatNo())
                    {
                        booking.setBookingStatus(Constants.BookingStatus.CANCELED);
                        seatAvail ++;
                        bus.setAvailableSeat(seatAvail);
                        List<PassengerDetails> allPassengers = (List<PassengerDetails>) passengerRepo.findAll();
                        for (PassengerDetails passenger : allPassengers) {
                            if (passenger.getName().equals(userName) && passenger.getBusNo() == busNum && passenger.getSeatNo() == seatNo)
                            {
                                passengerRepo.delete(passenger);
                            }
                        }
                    }
                }
            }
        }
        busRepo.saveAll(all);
        return new ModelAndView("succefullyCanceled.html");
    }
}
